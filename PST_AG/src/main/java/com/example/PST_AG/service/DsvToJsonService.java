package com.example.PST_AG.service;

import com.example.PST_AG.model.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DsvToJsonService {

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .registerModule(new JavaTimeModule());

    public void convertDSVToJSONL(String inputFilePath, String outputFilePath) throws IOException {
        try (CSVReader reader = createCSVReader(inputFilePath);
             BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {

            List<String[]> allLines = reader.readAll();
            if (allLines.isEmpty()) {
                throw new IOException("Input file is empty");
            }

            allLines.stream().skip(1)
                    .map(line -> mapToEmployee(line))
                    .map(this::convertToJson)
                    .forEach(jsonLine -> {
                        try {
                            writer.write(jsonLine);
                            writer.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (CsvException e) {
            e.printStackTrace();
        }
    }

    private CSVReader createCSVReader(String inputFilePath) throws IOException {
        char separator = detectSeparator(inputFilePath);
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(separator)
                .withQuoteChar('"')
                .build();
        return new CSVReaderBuilder(new FileReader(inputFilePath))
                .withCSVParser(parser)
                .build();
    }

    private LocalDate parseDate(String date) {
        List<DateTimeFormatter> formatters = new ArrayList<>();
        formatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        formatters.add(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        formatters.add(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        formatters.add(DateTimeFormatter.ofPattern("MM/dd/yyyy"));

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(date, formatter);
            } catch (DateTimeParseException e) {
            }
        }

        throw new IllegalArgumentException("Invalid date format: " + date);
    }

    private char detectSeparator(String inputFilePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilePath))) {
            String headerLine = br.readLine();
            if (headerLine.contains(",")) {
                return ',';
            } else if (headerLine.contains("|")) {
                return '|';
            } else {
                throw new IOException("Unsupported delimiter in the input file.");
            }
        }
    }

    private Employee mapToEmployee(String[] fields) {
        String firstName = fields[0].trim();
        String middleName = fields[1].trim();
        String lastName = fields[2].trim();
        String gender = fields[3].trim();
        LocalDate dateOfBirth = parseDate(fields[4].trim());
        int salary = Integer.parseInt(fields[5].trim());

        return new Employee(firstName, middleName, lastName, gender, dateOfBirth, salary);
    }

    private String convertToJson(Employee employee) {
        try {
            return objectMapper.writeValueAsString(employee);
        } catch (IOException e) {
            throw new RuntimeException("Error converting to JSON", e);
        }
    }
}
