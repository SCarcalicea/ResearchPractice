package com.example.researchpractice;

import com.example.researchpractice.model.DOIClassificationResponse;
import com.example.researchpractice.model.DxDoi;
import com.example.researchpractice.model.Scie_ssci;
import com.example.researchpractice.model.Sense;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
@AllArgsConstructor
public class ClassificationProcessor {

    public SCIE_SCSIRepository scie_scsiRepository;
    public SenseRepository senseRepository;

    private String xDoi;
    private String yDoi;

    public DOIClassificationResponse process(DxDoi dxDoi, String wos) {

        DOIClassificationResponse.DOIClassificationResponseBuilder response = DOIClassificationResponse.builder();
        response.type(extractType(dxDoi));
        response.year(extractYear(dxDoi));
        String type = extractType(dxDoi);
        boolean isInWos = wos != null && !wos.equals("");
        response.isInWos(isInWos);
        boolean info = true;  // TODO - Assuming that info is always true. In the algorithm there is no statement about it.
        response.isInWos(info);
        String classCNATDCU = "";
        String classINFO = "";

        if (type.equals("journal-article")) {
            String issn = extractISSN(dxDoi);
            String conainterTitle = extractContainerTitle(dxDoi);
            response.issn(issn);
            response.containerTitle(conainterTitle);
            classINFO = "D";
            response.classINFO(classINFO);

            List<Scie_ssci> scieScsi = scie_scsiRepository.findAllByIssnOrderByYear(issn);
            if (!scieScsi.isEmpty()) {

                Comparator<Scie_ssci> compareByJournalImpactFactor = Comparator.comparingDouble(o -> o.journalImpactFactor);
                Comparator<Scie_ssci> compareByArticleInfluenceScore = Comparator.comparingDouble(o -> o.articleInfluenceScore);
                scieScsi.sort(compareByJournalImpactFactor);

                for (Scie_ssci article : scieScsi) { // TODO - Logic here might be different. Assuming that I need to parse each entry from the files ???
                    /* marker */
                    processMarkerLogic(response, info, classCNATDCU, classINFO, conainterTitle, scieScsi, article);
                    /* end marker */

                    // TODO - If articleInfluenceScore is positive. This might change based on the above assumption.
                    if (article.articleInfluenceScore > 0) {
                        scieScsi.sort(compareByArticleInfluenceScore);

                        /* marker */
                        processMarkerLogic(response, info, classCNATDCU, classINFO, conainterTitle, scieScsi, article);
                        /* end marker */
                    }
                }
            }

            if (classCNATDCU.equals("") && isInWos) {
                response.classCNATDCU("ISI ESCI");
            }

            // TODO - This will always return a web page, a java script script is processing the request
            if (info && classINFO.equals("D")) {
                try {
                    URL obj = new URL("https://plu.mx/plum/a/?doi=" + xDoi + "/" + yDoi);
                    HttpURLConnection conn = (HttpURLConnection) obj.openConnection();;
                    HttpURLConnection.setFollowRedirects(true);
                    if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        response.classINFO("C");
                    }
                } catch (MalformedURLException e) {
                    // Ignore for now
                }  catch (IOException e) {
                    // Ignore for now
                }
            }

        } else if (type.equals("paper-conference")) {
            String event = extractEvent(dxDoi);
            if (!event.equals("NONE")) {
                response.acronim(event.substring(0, event.indexOf(" ")));
                response.event_title(event.substring(event.indexOf(":")));
            }

            if (isInWos) {
                response.classCNATDCU("ISI PROC");
            } else if (xDoi.equals("10.1109")) {
                response.classCNATDCU("IEEE PROC");
            }

            if (info) {
                response.classINFO("D");

                // TODO - Core file data seems to be random, some processing is needed here
//            search exact acronym in CORE with closest year
//            if found
//            if match of event_title in 75% then
//            classINFO=class from CORE (A*, A, B or C)

            }


            // Implementation for paper-conference here
        } else if (type.equals("chapter") || type.equals("book")) { // Tested with "10.1007/978-3-319-10530-7"
            response.publisher(extractPublisher(dxDoi));
            response.publisherLocation(extractPublisherLocation(dxDoi));

            if (info) {
                String publisher = extractPublisher(dxDoi);
                List<Sense> yearExactMatch = senseRepository.findAllByNaamAndPlaatsOrderByJaar(publisher.substring(0, publisher.indexOf(" ")),extractPublisherLocation(dxDoi));
                boolean exactMatch = false;
                for (Sense sense : yearExactMatch) {
                    if (sense.getJaar().equals(extractYear(dxDoi))) {
                        exactMatch = true;
                        response.classINFO(sense.getWaardering());
                        break;
                    }
                }

                if (!exactMatch && yearExactMatch != null && !yearExactMatch.isEmpty()) {
                    response.classINFO(yearExactMatch.get(yearExactMatch.size()-1).getWaardering());
                }
            }
        }
        return response.build();
    }

    private void processMarkerLogic(DOIClassificationResponse.DOIClassificationResponseBuilder response, boolean info, String classCNATDCU, String classINFO, String conainterTitle, List<Scie_ssci> scieScsi, Scie_ssci article) {
        double rank = scieScsi.indexOf(article);
        if (rank <= Math.ceil(0.25 * scieScsi.size())) {
            response.classCNATDCU("ISI ROSU");
            if (conainterTitle.contains("NATURE")) {
                response.classCNATDCU("NATURE");

            }
        } else if (rank <= Math.ceil(0.5 * scieScsi.size())) {
            if (!classCNATDCU.equals("ISI ROSU")) {
                response.classCNATDCU("ISI GALBEN");
            }
        } else {
            if (!classCNATDCU.equals("ISI ROSU") || !classCNATDCU.equals("ISI GALBEN")) {
                response.classCNATDCU("ISI ALB");
            }
        }

        if (info) {
            double x = Math.floor(0.2 * Math.ceil(0.25 * scieScsi.size()));

            if (rank <= x) {
                response.classINFO("A*");
            } else if (rank <= Math.ceil(0.25 * scieScsi.size()) + x) {
                if (!classINFO.equals("A*")) {
                    response.classINFO("A");
                } else if (rank <= Math.ceil(0.5 * scieScsi.size()) + x) {
                    if (!classINFO.equals("A*") || !classINFO.equals("A")) {
                        response.classINFO("B");
                    } else if (!classINFO.equals("A*") || !classINFO.equals("A") || !classINFO.equals("B")) {
                        response.classINFO("C");
                    }
                }
            }
        }
    }

    private String extractType(DxDoi dxDoi) {
        return Optional.ofNullable(dxDoi)
                .map(input -> input.type)
                .orElse("NONE");
    }

    private Integer extractYear(DxDoi dxDoi) {
        return Optional.ofNullable(dxDoi.indexed)
                .map(indexed -> indexed.dateParts)
                .map(dateParts -> dateParts.get(0))
                .map(date -> date.get(0))
                .orElse(0);
    }

    private String extractISSN(DxDoi dxDoi) {
        return Optional.ofNullable(dxDoi)
                .map(input -> input.iSSN)
                .map(issns -> issns.get(0))
                .orElse("NONE");
    }

    private String extractContainerTitle(DxDoi dxDoi) {
        return Optional.ofNullable(dxDoi)
                .map(input -> input.containerTitle)
                .orElse("NONE");
    }

    private String extractPublisher(DxDoi dxDoi) {
        return Optional.ofNullable(dxDoi)
                .map(input -> input.publisher)
                .orElse("NONE");
    }

    private String extractPublisherLocation(DxDoi dxDoi) {
        return Optional.ofNullable(dxDoi)
                .map(input -> input.publisherLocation)
                .orElse("NONE");
    }

    private String extractEvent(DxDoi dxDoi) {
        return Optional.ofNullable(dxDoi)
                .map(input -> input.event)
                .orElse("NONE");
    }
}
