package com.data.converters;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Log4j2
@UtilityClass
public class CsvToSqlGenerators {

    /**
     * Generates SQL from CSV file
     *
     * @param csvFile input CSV file name
     * @param tableName table name in sql
     * @param destinationFile output file name
     * @param generateMergeQuery generates merge query e.g. INSERT ... ON DUPLICATE KEY ...
     * @param mergekey merge key
     * @return List of Sql
     */
    public static List<String> generateSQLFromCSV(String csvFile, String tableName, String destinationFile, boolean generateMergeQuery, String mergekey) {
        List<String> sqlStatements = new ArrayList<>();
        String line;
        String csvSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(destinationFile))) {
            String[] headers = br.readLine().split(csvSplitBy);

            while ((line = br.readLine()) != null) {
                String[] values = line.split(csvSplitBy);
                StringBuilder columns = new StringBuilder();
                StringBuilder valuesSQL = new StringBuilder();
                StringBuilder keyValuePair = new StringBuilder();

                IntStream.range(0, Math.min(headers.length, values.length))
                        .filter(i -> !values[i].isEmpty())
                        .boxed()
                        .forEach(i -> {

                            if (!columns.isEmpty()) {
                                columns.append(", ");
                                valuesSQL.append(", ");
                                if (generateMergeQuery && !headers[i].equalsIgnoreCase(mergekey) && !keyValuePair.isEmpty())
                                    keyValuePair.append(", ");
                            }
                            columns.append(headers[i]);
                            valuesSQL.append("'").append(values[i]).append("'");
                            if (generateMergeQuery && !headers[i].equalsIgnoreCase(mergekey))
                                keyValuePair.append(headers[i]).append("=").append("'").append(values[i]).append("'");
                        });

                String sql =
                        generateMergeQuery ?
                                (keyValuePair.isEmpty() ? String.format("INSERT INTO %s (%s) VALUES (%s);", tableName, columns, valuesSQL)
                                        : String.format("INSERT INTO %s (%s) VALUES (%s) ON DUPLICATE KEY UPDATE %s;", tableName, columns, valuesSQL, keyValuePair))
                                : String.format("INSERT INTO %s (%s) VALUES (%s);", tableName, columns, valuesSQL);
                bw.write(sql);
                bw.newLine();
                sqlStatements.add(sql);
            }
            bw.flush();

        } catch (IOException e) {
            log.error("Exception occurred ", e);
        }
        return sqlStatements;
    }
}
