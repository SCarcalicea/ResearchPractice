package com.example.researchpractice;

import com.example.researchpractice.model.DOIClassificationResponse;
import com.example.researchpractice.model.DxDoi;
import com.example.researchpractice.model.Sense;
import lombok.AllArgsConstructor;

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
        boolean info = true;
        response.isInWos(info);

        if (type.equals("journal-article")) {
            response.issn(extractISSN(dxDoi));
            response.containerTitle(extractContainerTitle(dxDoi));
            response.classINFO("D");

            // Implementation for journal-article here
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

//            search exact acronym in CORE with closest year
//            if found
//            if match of event_title in 75% then
//            classINFO=class from CORE (A*, A, B or C)
            }


            // Implementation for paper-conference here
        } else if (type.equals("chapter") || type.equals("book")) {
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
