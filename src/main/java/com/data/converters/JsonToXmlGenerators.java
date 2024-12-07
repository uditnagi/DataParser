package com.data.converters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


@Log4j2
@UtilityClass
public class JsonToXmlGenerators {

    /**
     * Generates CML from JSON
     * uses jackson's ObjectMapper for de-serialising JSON and XmlMapper for serialising XML
     *
     * @param jsonFile input json file name
     * @param destinationFile output xml file name
     * @return output
     */
    public static String generateXmlFromJson(String jsonFile, String destinationFile) {
        File input = new File(jsonFile);

        try (FileWriter fileWriter = new FileWriter(destinationFile)) {
            JsonNode rootNode = new ObjectMapper().readTree(input);
            String content = generateXmlFromJson(rootNode);
            fileWriter.write(content);

            log.info("XML generated successfully: {}", content);
        } catch (Exception e) {
            log.error(" Exception occurred", e);
            return "FAILURE";
        }
        return "SUCCESS";
    }

    private static String generateXmlFromJson(JsonNode rootNode) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        StringBuilder xml = new StringBuilder(); // Check if the root node is an array
        if (rootNode.isArray()) {
            xml.append("<ArrayNode>\n");
            for (JsonNode node : rootNode) {
                xml.append("<item>\n");
                xml.append(xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node));
                xml.append("</item>\n");
            }
            xml.append("</ArrayNode>\n");
        } else {
            xml.append(xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode));
        }
        return xml.toString();
    }
}
