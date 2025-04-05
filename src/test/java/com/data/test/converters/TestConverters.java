package com.data.test.converters;


import com.data.converters.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class TestConverters {

    @Test
    public void testCsvToSqlGenerator_withInsertQuery(){
        String csvFile ="src/test/java/com/data/test/converters/test.csv";
        String sqlFile ="src/test/java/com/data/test/converters/test.sql";

        List<String> sqlStatements = CsvToSqlGenerators.generateSQLFromCSV(csvFile,"my_table",sqlFile,false, null);
        assertNotNull(sqlStatements);
        assertEquals(3, sqlStatements.size());

        String expectedSQL1 = "INSERT INTO my_table (name, age) VALUES ('Alice', '30');";
        String expectedSQL2 = "INSERT INTO my_table (name, age) VALUES ('Bob', '25');";
        String expectedSQL3 = "INSERT INTO my_table (name) VALUES ('Foo');";
        assertEquals(expectedSQL1, sqlStatements.get(0));
        assertEquals(expectedSQL2, sqlStatements.get(1));
        assertEquals(expectedSQL3, sqlStatements.get(2));
    }

    @Test
    public void testCsvToSqlGenerator_withMergeQuery(){
        String csvFile ="src/test/java/com/data/test/converters/test.csv";
        String sqlFile ="src/test/java/com/data/test/converters/test_merge.sql";

        List<String> sqlStatements = CsvToSqlGenerators.generateSQLFromCSV(csvFile,"my_table",sqlFile,true, "name");
        assertNotNull(sqlStatements);
        assertEquals(3, sqlStatements.size());

        String expectedSQL1 = "INSERT INTO my_table (name, age) VALUES ('Alice', '30') ON DUPLICATE KEY UPDATE age='30';";
        String expectedSQL2 = "INSERT INTO my_table (name, age) VALUES ('Bob', '25') ON DUPLICATE KEY UPDATE age='25';";
        String expectedSQL3 = "INSERT INTO my_table (name) VALUES ('Foo');";
        assertEquals(expectedSQL1, sqlStatements.get(0));
        assertEquals(expectedSQL2, sqlStatements.get(1));
        assertEquals(expectedSQL3, sqlStatements.get(2));
    }

    @Test
    public void testCsvToCurlGenerator(){
        String csvFile ="src/test/java/com/data/test/converters/test.csv";
        String sqlFile ="src/test/java/com/data/test/converters/test.sh";

        List<String> sqlStatements =
                CsvToCurlGenerators.generateCurlsFromCSV(
                        csvFile,"GET","https://x.com","a:b",sqlFile);
        assertNotNull(sqlStatements);
        assertEquals(3, sqlStatements.size());

        String expectedSQL1 = "curl -X GET \"https://x.com?name=Alice&age=30\" -h a:b;";
        String expectedSQL2 = "curl -X GET \"https://x.com?name=Bob&age=25\" -h a:b;";
        String expectedSQL3 = "curl -X GET \"https://x.com?name=Foo\" -h a:b;";
        assertEquals(expectedSQL1, sqlStatements.get(0));
        assertEquals(expectedSQL2, sqlStatements.get(1));
        assertEquals(expectedSQL3, sqlStatements.get(2));
    }

    @Test
    public void testCsvToJsonGenerator(){
        String csvFile ="src/test/java/com/data/test/converters/test.csv";
        String sqlFile ="src/test/java/com/data/test/converters/test.json";

        String jsonString =
                CsvToJsonGenerators.generateJsonFromCSV(
                        csvFile,  sqlFile, Pojo.class);
        assertNotNull(jsonString);

        String expectedJson = "[{\"name\":\"Alice\",\"age\":\"30\"},{\"name\":\"Bob\",\"age\":\"25\"},{\"name\":\"Foo\"}]";
        assertEquals(expectedJson.trim(), jsonString.trim());
    }

    @Test
    public void testJsonToJavaClassGenerator(){
        String jsonFile ="src/test/java/com/data/test/converters/generatedClass/Input.json";
        String classFile ="src/test/java/com/data/test/converters/generatedClass/";

        String jsonString =
                JsonToJavaClassGenerators.generateJavaClassFromJson(
                        jsonFile,  classFile, "com.data.test.converters.generatedClass");
        assertNotNull(jsonString);

       assertEquals("SUCCESS", jsonString);
    }

    @Test
    public void testJsonToXmlGenerator(){
        String jsonFile ="src/test/java/com/data/test/converters/generatedClass/Input.json";
        String classFile ="src/test/java/com/data/test/converters/generatedClass/output.xml";

        String outputString =
                JsonToXmlGenerators.generateXmlFromJson(
                        jsonFile,  classFile);
        assertNotNull(outputString);

        assertEquals("SUCCESS", outputString);
    }

    @Test
    public void testXmlToJsonGenerator(){
        String jsonFile ="src/test/java/com/data/test/converters/test.xml";
        String classFile ="src/test/java/com/data/test/converters/test_xml_output.json";

        String output =
                XmlToJsonGenerator.generateJsonFromXml(
                        jsonFile,  classFile);
        assertNotNull(output);

        assertEquals("SUCCESS", output);
    }


    @Test
    public void testYamlToJsonGenerator(){
        String jsonFile ="src/test/java/com/data/test/converters/test.yaml";
        String classFile ="src/test/java/com/data/test/converters/test_yaml_output.json";

        String output =
                YamlToJsonGenerator.generateYamlFromJson(
                        jsonFile,  classFile);
        assertNotNull(output);

        assertEquals("SUCCESS", output);
    }
}
