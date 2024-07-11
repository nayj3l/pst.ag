package com.example.PST_AG.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DsvToJsonConverter implements CommandLineRunner {

    @Autowired
    private DsvToJsonService dsvToJsonService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Received arguments:");
        for (String arg : args) {
            System.out.println(arg);
        }

        if (args.length < 2) {
            System.out.println("Usage: java -jar dsv-to-json.jar <inputFilePath> <outputFilePath>");
            return;
        }

        String inputFilePath = args[0];
        String outputFilePath = args[1];

        dsvToJsonService.convertDSVToJSONL(inputFilePath, outputFilePath);

        System.out.println("Conversion completed successfully.");
    }
}
