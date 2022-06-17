package com.example.researchpractice;

import com.example.researchpractice.model.DOIClassificationResponse;
import com.example.researchpractice.model.DxDoi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Controller
public class ClassificationController {

    @Autowired
    public SCIE_SCSIRepository scie_scsiRepository;

    @Autowired
    public SenseRepository senseRepository;

    @GetMapping("/api/dxdoi/get")
    @ResponseBody
    public DxDoi getDxDoiJson() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<DxDoi> response = restTemplate.exchange("https://dx.doi.org/10.1016/j.future.2017.11.006", HttpMethod.GET, entity, DxDoi.class);
        return response.getBody();
    }

    @GetMapping("/api/wos/get")
    @ResponseBody
    public String getWosJson() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<String> response = restTemplate.exchange("http://ws.isiknowledge.com/cps/openurl/service?url_ver=Z39.88-2004&rft_id=info:doi/10.1016/j.future.2017.11.006", HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    @GetMapping("/api/get/{x}/{y}")
    @ResponseBody
    public DOIClassificationResponse getClassification(@PathVariable("x") String firstPart, @PathVariable("y") String secondPart) {
        String doi = firstPart + "/" + secondPart;
        ClassificationProcessor processor = new ClassificationProcessor(scie_scsiRepository, senseRepository, firstPart, secondPart);
        return processor.process(getDxDoiObject(doi), getWos(doi));
    }

    private DxDoi getDxDoiObject(String doi) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<DxDoi> response = restTemplate.exchange("https://dx.doi.org/" + doi, HttpMethod.GET, entity, DxDoi.class);
        return response.getBody();
    }

    private String getWos(String doi) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<String> response = restTemplate.exchange("http://ws.isiknowledge.com/cps/openurl/service?url_ver=Z39.88-2004&rft_id=info:doi/" + doi, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }
}
