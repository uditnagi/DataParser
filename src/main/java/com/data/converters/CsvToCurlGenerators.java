package com.data.converters;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Log4j2
@UtilityClass
public class CsvToCurlGenerators {

    /**
     * Generates curl from CSV where each row's represents a curl with field becomes queryParams.
     * e.g.
     * name,age
     * Alice,20
     * -->
     * curl -XGET "https://google.com?name=Alice&age=20";
     *
     * @param csvFile    input csv file
     * @param httpMethod HTTP Method
     * @param uri        URI of curl
     * @param destinationFile   output bash file
     * @return collection of curls
     */
    public static List<String> generateCurlsFromCSV(String csvFile,
                                                    String httpMethod,
                                                    String uri,
                                                    String headers,
                                                    String destinationFile) {
        List<String> curlStatements = new ArrayList<>();
        String line;
        String csvSplitBy = ",";
        String joinBy = "=";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(destinationFile))) {
            String[] csvHeaders = br.readLine().split(csvSplitBy);

            while ((line = br.readLine()) != null) {
                String[] values = line.split(csvSplitBy);
                StringBuilder queryParams = new StringBuilder();

                IntStream.range(0, Math.min(csvHeaders.length, values.length))
                        .filter(i -> !values[i].isEmpty())
                        .boxed()
                        .forEach(i -> {
                            if (!queryParams.isEmpty()) {
                                queryParams.append("&");

                            }
                            queryParams.append(csvHeaders[i])
                                    .append(joinBy)
                                    .append(values[i]);
                        });

                String curl = String.format("curl -X %s \"%s?%s\"", httpMethod, uri, queryParams);
                curl = headers != null && !headers.isEmpty() ? String.format(curl + " -h %s;", headers) : curl + ";";
                bw.write(curl);
                bw.newLine();
                curlStatements.add(curl);
            }
            bw.flush();

        } catch (IOException e) {
            log.error("Exception occurred ", e);
        }
        return curlStatements;
    }

}
