package com.github.sarhatabaot.messages;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

/**
 * @author sarhatabaot
 */
public abstract class WriteClass<T> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    protected final String targetPackage;
    protected final String basePath;
    protected final String privateConstructor;
    protected final boolean overwriteClasses;

    public WriteClass(final String targetPackage, final String basePath, final String privateConstructor, final boolean overwriteClasses) {
        this.targetPackage = targetPackage;
        this.basePath = basePath;
        this.privateConstructor = privateConstructor;
        this.overwriteClasses = overwriteClasses;
    }

    protected void writePrivateUtilConstructor(@NotNull Writer fileWriter, String parentClassName) throws IOException {
        fileWriter.write("private " + parentClassName + " (){");
        fileWriter.write("throw new UnsupportedOperationException(" + privateConstructor + ");");
        fileWriter.write("}");
    }

    protected void writePrimitiveString(@NotNull Writer fileWriter, final String key, final String value, final String tab) throws IOException {
        final String baseVariableName = "public static final String ";
        String variableName = Util.getAsVariableName(key);
        final String finalVariableName = baseVariableName + variableName;
        fileWriter.write(tab + finalVariableName + " = " + '"' + value + '"' + ";");
    }

    protected @NotNull String getPathFromPackage() {
        return String.join(File.separator, targetPackage.split("\\."));
    }

    public void createJavaClass(@NotNull File file) {
        final String parentFileName = Util.getAsFileName(file.getName()).replace(getFileType().getExtension(), ".java");
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

            for (Map.Entry<String, T> entrySet : getRootEntrySet(file)) {
                if (noChildren(entrySet.getValue())) {
                    writePrimitiveString(fileWriter, entrySet.getKey(), getEntryValueAsString(entrySet.getValue()), "\t");
                    fileWriter.write("\n");
                } else {
                    final String innerClassName = Util.getAsClassName(entrySet.getKey());
                    fileWriter.write("\t public static class " + innerClassName + " {");
                    fileWriter.write("\n");
                    for (Map.Entry<String, T> elementSet : getEntrySetFromValue(entrySet.getValue())) {
                        writePrimitiveString(fileWriter, elementSet.getKey(), getEntryValueAsString(elementSet.getValue()), "\t\t");
                        fileWriter.write("\n");
                    }
                    if (privateConstructor != null && !privateConstructor.isEmpty()) {
                        writePrivateUtilConstructor(fileWriter, innerClassName);
                    }
                    fileWriter.write("}");
                }
            }
            //json end
            fileWriter.write("\n");
            if (privateConstructor != null && !privateConstructor.isEmpty()) {
                writePrivateUtilConstructor(fileWriter, parentClassName);
            }
            fileWriter.write("}");

            logger.info(String.format("Created class: %s for file: %s with type: %s", parentClassName, file.getName(), getFileType().name()));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

    }

    public abstract FileType getFileType();

    public abstract boolean noChildren(T element);

    public abstract Set<Map.Entry<String, T>> getRootEntrySet(File file) throws IOException;

    public abstract Set<Map.Entry<String, T>> getEntrySetFromValue(T value);

    public abstract String getEntryValueAsString(T value);
}
