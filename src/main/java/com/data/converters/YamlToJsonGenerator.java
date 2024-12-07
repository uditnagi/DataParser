package com.data.converters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Log4j2
@UtilityClass
public class YamlToJsonGenerator {

    /**
     * Generates JSON from YAML
     * uses Jackson's YamlReader for de-serialise XML and ObjectMapper for serialise JSON
     *
     * @param jsonFile input json file name
     * @param destinationFile output file name
     * @return output
     */
    public static String generateYamlFromJson(String jsonFile, String destinationFile) {
        File input = new File(jsonFile);

        try ( FileWriter fileWriter = new FileWriter(destinationFile)) {
            JsonNode rootNode = new YAMLMapper().readTree(input);
            String content = generateJsonFromYaml(rootNode);
            fileWriter.write(content);

            log.info("JSON generated successfully: {}", content);
        } catch (Exception e) {
            log.error(" Exception occurred", e);
            return "FAILURE";
        }
        return "SUCCESS";
    }

    private static String generateJsonFromYaml(JsonNode rootNode) throws IOException {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

}
