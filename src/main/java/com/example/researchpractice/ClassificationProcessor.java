package com.example.researchpractice;

import com.example.researchpractice.model.files.Core;
import com.example.researchpractice.model.response.DOIClassificationResponse;
import com.example.researchpractice.model.basemodel.DxDoi;
import com.example.researchpractice.model.files.Scie_ssci;
import com.example.researchpractice.model.files.Sense;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@AllArgsConstructor
public class ClassificationProcessor {

    public SCIE_SCSIRepository scie_scsiRepository;
    public SenseRepository senseRepository;
    public CoreRepository coreRepository;
    private String xDoi;
    private String yDoi;

    public DOIClassificationResponse process(DxDoi dxDoi, String wos) {

        DOIClassificationResponse.DOIClassificationResponseBuilder response = DOIClassificationResponse.builder();
        response.type(extractType(dxDoi));
        response.year(extractYear(dxDoi));
        String type = extractType(dxDoi);
        boolean isInWos = wos != null && !wos.equals("");
        response.isInWos(isInWos);
        boolean info = true;  // TODO - Assuming that info is always true. In the algorithm description there is no statement about its value.
        response.isInWos(info);
        String classCNATDCU = "";
        String classINFO = "";

        switch (type) {
            case "journal-article" -> { // Tested with "10.1007/s11831-020-09492-4" and "10.1109/ACCESS.2019.2943498"
                String issn = extractISSN(dxDoi);
                String conainterTitle = extractContainerTitle(dxDoi);
                response.issn(issn);
                response.containerTitle(conainterTitle);
                classINFO = "D";
                response.classINFO(classINFO);
                List<Scie_ssci> scieScsi = scie_scsiRepository.findAllByIssnOrderByYear(issn);
                if (!scieScsi.isEmpty()) {  // Article found

                    Comparator<Scie_ssci> compareByJournalImpactFactor = Comparator.comparingDouble(o -> o.journalImpactFactor);
                    Comparator<Scie_ssci> compareByArticleInfluenceScore = Comparator.comparingDouble(o -> o.articleInfluenceScore);

                    // TODO - Logic here might be different. Assuming that I need to parse each entry from the files, when do I stop ???
                    scieScsi.sort(compareByJournalImpactFactor);
                    for (Scie_ssci article : scieScsi) {
                        processMarkerLogic(response, info, classCNATDCU, classINFO, conainterTitle, scieScsi, article);

                        // TODO - If articleInfluenceScore is positive. This might change based on the above assumption.
                        if (article.articleInfluenceScore > 0) {
                            scieScsi.sort(compareByArticleInfluenceScore);
                            processMarkerLogic(response, info, classCNATDCU, classINFO, conainterTitle, scieScsi, article);
                        }
                        break;
                    }
                }

                if (classCNATDCU.equals("") && isInWos) { // Artile not found but has WoS
                    response.classCNATDCU("ISI ESCI");
                }

                // TODO - This will always return a web page, a java script script is processing the request.
                if (info && classINFO.equals("D")) {
                    try {
                        URL obj = new URL("https://plu.mx/plum/a/?doi=" + xDoi + "/" + yDoi);
                        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                        HttpURLConnection.setFollowRedirects(true);
                        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                            response.classINFO("C");
                        }
                    } catch (MalformedURLException e) {
                        // Ignore for now
                    } catch (IOException e) {
                        // Ignore for now
                    }
                }
            } case "paper-conference" -> {  // TODO - Find DOI for this type and test algorithm
                String event = extractEvent(dxDoi);
                String acronim = event;
                String event_title = "";

                if (!event.equals("NONE")) {
                    if (acronim.contains(" ")) {
                        acronim = event.substring(0, event.indexOf(" "));
                        if (acronim.contains("/")) {
                            acronim = event.substring(0, event.indexOf("/"));
                        }
                    }

                    if (event.contains(":")) {
                        event_title = event.substring(event.indexOf(":"));
                        response.event_title(event_title);
                    }
                }

                if (isInWos) {
                    response.classCNATDCU("ISI PROC");
                } else if (xDoi.equals("10.1109")) {
                    response.classCNATDCU("IEEE PROC");
                }

                if (info) {
                    response.classINFO("D");
                    List<Core> conferences = coreRepository.findAllByAcronymOrderByCoreYear(acronim);
                    for (Core conference : conferences) {
                        if (conference.getTitle().equals(event_title)) {
                            response.classINFO(conference.getClassInfo());
                        }
                    }
                }

            } case "chapter", "book" -> {  // Tested with "10.1007/978-3-319-10530-7"
                response.publisher(extractPublisher(dxDoi));
                response.publisherLocation(extractPublisherLocation(dxDoi));
                if (info) {
                    String publisher = extractPublisher(dxDoi);
                    List<Sense> yearExactMatch = new ArrayList<>();
                    if (publisher.contains("Springer")) {
                        yearExactMatch = senseRepository.findAllByNaamAndPlaatsOrderByJaar(publisher.substring(0, publisher.indexOf(" ")), extractPublisherLocation(dxDoi));
                    } else {
                        yearExactMatch = senseRepository.findAllByNaamAndPlaatsOrderByJaar(publisher, extractPublisherLocation(dxDoi));
                    }
                    boolean exactMatch = false;
                    for (Sense sense : yearExactMatch) {
                        if (sense.getJaar().equals(extractYear(dxDoi))) {
                            exactMatch = true;
                            response.classINFO(sense.getWaardering());
                            break;
                        }
                    }

                    if (!exactMatch && yearExactMatch != null && !yearExactMatch.isEmpty()) {
                        response.classINFO(yearExactMatch.get(yearExactMatch.size() - 1).getWaardering());
                    }
                }
            }
        }
        return response.build();
    }

    private void processMarkerLogic(DOIClassificationResponse.DOIClassificationResponseBuilder response, boolean info, String classCNATDCU, String classINFO, String conainterTitle, List<Scie_ssci> scieScsi, Scie_ssci article) {
        double rank = scieScsi.indexOf(article);
        if (rank <= Math.ceil(0.25 * scieScsi.size())) {
            response.classCNATDCU("ISI ROSU");
            classCNATDCU = "ISI ROSU";
            if (conainterTitle.contains("NATURE")) {
                response.classCNATDCU("NATURE");
                classCNATDCU = "NATURE";

            }
        } else if (rank <= Math.ceil(0.5 * scieScsi.size())) {
            if (!classCNATDCU.equals("ISI ROSU")) {
                response.classCNATDCU("ISI GALBEN");
                classCNATDCU = "ISI GALBEN";
            }
        } else {
            if (!classCNATDCU.equals("ISI ROSU") || !classCNATDCU.equals("ISI GALBEN")) {
                response.classCNATDCU("ISI ALB");
                classCNATDCU = "ISI ALB";
            }
        }

        if (info) {
            double x = Math.floor(0.2 * Math.ceil(0.25 * scieScsi.size()));
            if (rank <= x) {
                response.classINFO("A*");
                classINFO = "A*";
            } else if (rank <= Math.ceil(0.25 * scieScsi.size()) + x) {
                if (!classINFO.equals("A*")) {
                    response.classINFO("A");
                    classINFO = "A";
                } else if (rank <= Math.ceil(0.5 * scieScsi.size()) + x) {
                    if (!classINFO.equals("A*") || !classINFO.equals("A")) {
                        response.classINFO("B");
                        classINFO = "B";
                    } else if (!classINFO.equals("B")) {
                        response.classINFO("C");
                        classINFO = "C";
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
                .map(input -> input.containerTitle.toString())
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
