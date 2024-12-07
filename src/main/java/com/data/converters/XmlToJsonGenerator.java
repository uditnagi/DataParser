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
public class XmlToJsonGenerator {

    /**
     * Generates JSON from XML
     * uses Jackson's XmlMapper for de-serialise XML and ObjectMapper for serialise JSON
     *
     * @param jsonFile input json file name
     * @param destinationFile output file name
     * @return output
     */
    public static String generateJsonFromXml(String jsonFile, String destinationFile) {
        File input = new File(jsonFile);

        try ( FileWriter fileWriter = new FileWriter(destinationFile)) {
            JsonNode rootNode = new XmlMapper().readTree(input);
            String content = generateJsonFromXml(rootNode);
            fileWriter.write(content);

            log.info("JSON generated successfully: {}", content);
        } catch (Exception e) {
            log.error(" Exception occurred", e);
            return "FAILURE";
        }
        return "SUCCESS";
    }

    private static String generateJsonFromXml(JsonNode xmlNode) throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        StringBuilder json = new StringBuilder();
        if (xmlNode.isArray()) {
            json.append("[\n");
            for (JsonNode node : xmlNode) {
                json.append(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node)).append(",\n");
            }
            json.setLength(json.length() - 1);
            json.append("]\n");
        } else {
            json.append(jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(xmlNode));
        }
        return json.toString();
    }

}
