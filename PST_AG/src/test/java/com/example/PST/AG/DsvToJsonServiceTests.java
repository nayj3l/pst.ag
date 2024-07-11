package com.example.PST.AG;

import com.example.PST_AG.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.PST_AG.model.Employee;
import com.example.PST_AG.service.DsvToJsonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class DsvToJsonServiceTests {

    @Autowired
    private DsvToJsonService dsvToJsonService;

    @Test
    public void testConversionFromInput() throws IOException {
        String inputFilePath = "src/test/resources/DSV_input_1.txt";
        String outputFilePath = "src/test/resources/output/JSONL_output_expected.json";

        Files.createDirectories(Paths.get("src/test/resources/output"));

        dsvToJsonService.convertDSVToJSONL(inputFilePath, outputFilePath);
        assertTrue(Files.exists(Paths.get(outputFilePath)));
    }

}
