package com.data.converters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Log4j2
@UtilityClass
public class JsonToJavaClassGenerators {

    private static final Set<String> generatedClasses = new HashSet<>();

    /**
     * Generates pojo from json.
     *
     * @param jsonFile input json file name
     * @param destinationDir output destination directory
     * @param packageName package name
     * @return output
     */
    public static String generateJavaClassFromJson(String jsonFile, String destinationDir, String packageName) {
        File input = new File(jsonFile);

        try {
            JsonNode rootNode = new ObjectMapper().readTree(input);
            rootNode = rootNode.isArray() ? rootNode.elements().next() : rootNode;
            String classContent = generatePojoFromJson(rootNode, packageName, destinationDir, "GeneratedClass");

            log.info("POJO class generated successfully: {}", classContent);
        } catch (Exception e) {
            log.error(" Exception occurred", e);
            return "FAILURE";
        }
        return "SUCCESS";
    }

    private static String generatePojoFromJson(JsonNode node, String packageName, String destinationDir, String className) {
        StringBuilder _classContent = new StringBuilder("package ");
        _classContent.append(packageName).append(";\n\n").append("import java.util.List;\n");
        if (containsNestedObject(node)) {
            _classContent.append("import ").append(packageName).append(".*;\n");
        }

        _classContent.append("\npublic class ").append(className).append(" {\n\n");
        StringBuilder _fieldNames = new StringBuilder();
        StringBuilder _fieldSetterAndGetters = new StringBuilder();
        node.fieldNames().forEachRemaining(fieldName -> {
            JsonNode fieldValue = node.get(fieldName);
            String fieldType = getJavaType(fieldName, fieldValue, packageName, destinationDir);
            _fieldNames.append(" private ").append(fieldType).append(" ").append(fieldName).append(";\n");
            _fieldSetterAndGetters.append(" public ").append(fieldType).append(" get")
                    .append(capitalize(fieldName)).append("() { return ").append(fieldName).append("; }\n");
            _fieldSetterAndGetters.append(" public void set").append(capitalize(fieldName))
                    .append("(").append(fieldType).append(" ").append(fieldName).append(") { this.")
                    .append(fieldName).append(" = ").append(fieldName).append("; }\n");
        });
        _classContent.append(_fieldNames).append("\n\n").append(_fieldSetterAndGetters).append("\n").append("}");

        saveClassToFile(_classContent.toString(), className, destinationDir);
        return _classContent.toString();
    }

    private static boolean containsNestedObject(JsonNode node) {
        Iterator<JsonNode> elements = node.elements();
        while (elements.hasNext()) {
            JsonNode element = elements.next();
            if (element.isObject() || (element.isArray() && containsNestedObject(element))) {
                return true;
            }
        }
        return false;
    }

    private static String getJavaType(String fieldName, JsonNode node, String packageName, String destinationDir) {
        if (node.isTextual()) {
            return "String";
        } else if (node.isInt()) {
            return "int";
        } else if (node.isDouble()) {
            return "double";
        } else if (node.isBoolean()) {
            return "boolean";
        } else if (node.isArray()) {
            JsonNode firstElement = node.elements().next();
            return "List<" + getJavaType(fieldName, firstElement, packageName, destinationDir) + ">";
        } else if (node.isObject()) {
            String nestedClassName = capitalize(fieldName);
            if (!generatedClasses.contains(nestedClassName)) {
                generatePojoFromJson(node, packageName, destinationDir, nestedClassName);
                generatedClasses.add(nestedClassName);
            }
            return nestedClassName;
        } else {
            return "Object";
        }
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    private static void saveClassToFile(String classContent, String className, String destinationDir) {
        File file = new File(destinationDir + "/" + className + ".java");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(classContent);
        } catch (IOException e) {
            log.error("Exception occurred", e);
        }
    }

}
