package com.github.sarhatabaot.messages;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * @author sarhatabaot
 */
public class WriteClass {
    private final Logger logger = LoggerFactory.getLogger(WriteClass.class);
    private final String targetPackage;
    private final String basePath;
    private final String privateConstructor;
    private final boolean overwriteClasses;

    public WriteClass(final String targetPackage, final String basePath, final String privateConstructor, final boolean overwriteClasses) {
        this.targetPackage = targetPackage;
        this.basePath = basePath;
        this.privateConstructor = privateConstructor;
        this.overwriteClasses = overwriteClasses;
    }

    public void createJavaClassFromJsonFile(final @NotNull File file) {
        final String parentFileName = Util.getAsFileName(file.getName()).replace(".json", ".java");
        final String parentClassName = parentFileName.replace(".java", "");
        final String classPath = basePath + getPathFromPackage();

        File outputFile = new File(classPath, parentFileName);

        try (Writer fileWriter = new BufferedWriter(new FileWriter(outputFile, !overwriteClasses))) {
            fileWriter.write("package " + targetPackage + ";");
            fileWriter.write("\n");
            fileWriter.write("\n");
            fileWriter.write("public final class " + parentClassName + " ");
            fileWriter.write("{");
            fileWriter.write("\n");

            //json stuff
            JsonReader reader = new JsonReader(new FileReader(file));

            //root element
            JsonElement rootElement = JsonParser.parseReader(reader);
            logger.debug(rootElement.toString());
            for (Map.Entry<String, JsonElement> entrySet : rootElement.getAsJsonObject().entrySet()) {
                if (entrySet.getValue().isJsonPrimitive()) {
                    writePrimitiveString(fileWriter, entrySet, "\t");
                    fileWriter.write("\n");
                } else {
                    final String innerClassName = Util.getAsClassName(entrySet.getKey());
                    fileWriter.write("\t public static class " + innerClassName + " {");
                    fileWriter.write("\n");
                    for (Map.Entry<String, JsonElement> elementSet : entrySet.getValue().getAsJsonObject().entrySet()) {
                        writePrimitiveString(fileWriter, elementSet, "\t\t");
                        fileWriter.write("\n");
                    }
                    if (privateConstructor!= null && !privateConstructor.isEmpty()) {
                        writePrivateUtilConstructor(fileWriter,innerClassName);
                    }
                    fileWriter.write("}");
                }
            }
            //json end
            fileWriter.write("\n");
            if (privateConstructor!= null && !privateConstructor.isEmpty()) {
                writePrivateUtilConstructor(fileWriter, parentClassName);
            }
            fileWriter.write("}");

            logger.info(String.format("Created class: %s for file: %s",parentClassName, file.getName()));
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
    }

    private void writePrivateUtilConstructor(@NotNull Writer fileWriter, String parentClassName) throws IOException {
        fileWriter.write("private " + parentClassName + " (){");
        fileWriter.write("throw new UnsupportedOperationException(" + privateConstructor + ");");
        fileWriter.write("}");
    }

    private void writePrimitiveString(@NotNull Writer fileWriter, Map.@NotNull Entry<String, JsonElement> entrySet, String tab) throws IOException {
        final String baseVariableName = "public static final String ";
        String variableName = Util.getAsVariableName(entrySet.getKey());
        final String finalVariableName = baseVariableName + variableName;
        final String finalVariableValue = entrySet.getValue().getAsString();
        fileWriter.write(tab + finalVariableName + " = " + '"' + finalVariableValue + '"' + ";");
    }

    private @NotNull String getPathFromPackage() {
        return String.join(File.separator, targetPackage.split("\\."));
    }
}
