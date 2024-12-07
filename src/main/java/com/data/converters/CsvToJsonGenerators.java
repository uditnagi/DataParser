package com.data.converters;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Log4j2
@UtilityClass
public class CsvToJsonGenerators {

    /**
     * Generates JSON from CSV file
     * uses - jackson library. CsvMapper for reading csv data and ObjectMapper for JSON.
     *
     * @param csvFile input csv fileName
     * @param destinationFile output json fileName
     * @param pojo final class which json represents
     * @return json string
     */
    public static <T> String generateJsonFromCSV(String csvFile, String destinationFile, Class<T> pojo) {
        File input = new File(csvFile);
        File output = new File(destinationFile);
        String _jsonString = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<T> data = readObjectsFromCsv(input, pojo, objectMapper);
            _jsonString = writeAsJson(data, output, objectMapper);

        } catch (IOException e) {
            log.error("Exception occurred", e);
        }
        return _jsonString;
    }

    private static <T> List<T> readObjectsFromCsv(File file, Class<T> pojo, ObjectMapper objectMapper) throws IOException {
        CsvSchema csvSchema = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();


        MappingIterator<T> iterator = csvMapper
                .readerFor(pojo)
                .with(csvSchema)
                .readValues(file);
        return iterator.readAll().stream().map((T obj) -> removeEmptyFields(obj, pojo, objectMapper)).toList();
    }

    private static <T> T removeEmptyFields(T obj, Class<T> pojo, ObjectMapper mapper) {

        ObjectNode node = mapper.convertValue(obj, ObjectNode.class);
        node.fieldNames().forEachRemaining(field -> {
            if (node.get(field).asText().isEmpty()) {
                node.remove(field);
            }
        });
        return mapper.convertValue(node, pojo);
    }

    private static <T> String writeAsJson(List<T> node, File file, ObjectMapper objectMapper) throws IOException {

        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, node);

        return objectMapper.writeValueAsString(node);
    }
}
