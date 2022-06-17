package com.example.researchpractice;

import com.example.researchpractice.model.files.Core;
import com.example.researchpractice.model.files.Scie_ssci;
import com.example.researchpractice.model.files.Sense;
import com.example.researchpractice.model.preloader.DBPreloaderChecker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class ResearchPracticeApplication {

    @Autowired
    public SCIE_SCSIRepository scie_scsiRepository;

    @Autowired
    public SenseRepository senseRepository;

    @Autowired
    public CoreRepository coreRepository;

    @Autowired
    public DBInitCheckerRepository dbChecker;

    public static void main(String[] args) {
        SpringApplication.run(ResearchPracticeApplication.class, args);
    }

    @PostConstruct
    public void init() {
//       boolean dbWasInitialized = dbChecker.findAll().stream().map(dbPreloaderChecker -> dbPreloaderChecker.skipDBInit).findFirst().orElse(false);
//       if (!dbWasInitialized) {
//           loadSCIEData();
//           loadSSCIData();
//           loadSENSEData();
           loadCOREData();
//           dbChecker.save(new DBPreloaderChecker(true));
//       }
    }

    public void loadSCIEData() {
        for (int i = 0; i < 23; i++) {
            Integer year = 1997 + i;
            String inputFile = "SCIE/journals-SCIE-year-" + year +".json";
            processFile(inputFile);
        }
    }

    public void loadSSCIData() {
        for (int i = 0; i < 23; i++) {
            Integer year = 1997 + i;
            String inputFile = "SSCI/journals-SSCI-year-" + year +".json";
            processFile(inputFile);
        }
    }

    public void loadSENSEData() {
        String inputFromFile = getResourceFileAsString("SENSE/SENSE.csv");
        CSVReader csvReader = new CSVReader(new StringReader(inputFromFile));
        boolean skipFirstLine = true;
        String[] values;
        while (true) {
            try {
                if (skipFirstLine) {
                    skipFirstLine = false;
                    continue;
                }
                if ((values = csvReader.readNext()) == null) break;
                Sense senseData = Sense.builder()
                        .isn(values[0])
                        .prefixMetCode(values[1])
                        .naam(values[2])
                        .plaats(values[3])
                        .jaar(values[4])
                        .waardering(values[5])
                        .build();

                senseRepository.save(senseData);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void loadCOREData() {

        List<String> coreYears = Arrays.asList("2008", "2010", "2013", "2014", "2017", "2018", "2020", "2021");
        List<Character> separators = Arrays.asList('\t', ' ');
        for(String year : coreYears) {
            String inputFromFile = getResourceFileAsString("CORE/CORE-" + year + ".csv");
            char separator = year.equals("2021") ? separators.get(1) : separators.get(0);
            CSVReader csvReader = new CSVReader(new StringReader(inputFromFile), separator);

            String[] values;
            while (true) {
                try {
                    if ((values = csvReader.readNext()) == null) break;
                    Core.CoreBuilder core = Core.builder();
                    core.id(getOrNullFromArray(values, 0));
                    core.title(getOrNullFromArray(values, 1));
                    core.acronym(getOrNullFromArray(values, 2));
                    core.coreYear(getOrNullFromArray(values, 3));
                    core.classInfo(getOrNullFromArray(values, 4));
                    core.isSomething(getOrNullFromArray(values, 5));
                    core.subId(getOrNullFromArray(values, 6));
                    core.subSubId(getOrNullFromArray(values, 7));

                    if (values.length > 8) {
                        core.extras(Arrays.asList(values));
                    }

                    coreRepository.save(core.build());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void processFile(String inputFile) {
        String journalsExample = getResourceFileAsString(inputFile);
        ObjectMapper mapper = new ObjectMapper();

        try {
            List<Scie_ssci> ssciData = mapper.readValue(journalsExample, mapper.getTypeFactory().constructCollectionType(List.class, Scie_ssci.class));
            insertDataIntoDB(ssciData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return;
    }

    public void insertDataIntoDB(List<Scie_ssci> inputData) {
        scie_scsiRepository.saveAll(inputData);
    }

    public static String getResourceFileAsString(String fileName) {
        InputStream is = getResourceFileAsInputStream(fileName);
        if (is != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } else {
            throw new RuntimeException("resource not found");
        }
    }

    public static InputStream getResourceFileAsInputStream(String fileName) {
        ClassLoader classLoader = ResearchPracticeApplication.class.getClassLoader();
        return classLoader.getResourceAsStream(fileName);
    }

    private String getOrNullFromArray(String[] values, int index) {
        try {
            String value = values[index];
            return value;
        } catch (Exception e) {
            return null;
        }
    }

}
