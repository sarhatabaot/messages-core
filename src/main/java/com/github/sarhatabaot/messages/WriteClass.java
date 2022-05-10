package com.github.sarhatabaot.messages;

import org.jetbrains.annotations.Contract;
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

    protected void writePrimitiveValue(@NotNull Writer fileWriter, final String key, final @NotNull TypeKeyValue value, final String tab) throws IOException {
        final String variableName = Util.getAsVariableName(key);
        final String finalVariableName = getFinalVariableName(value.getClazz().getTypeName().replace("java.lang.", ""), variableName);
        fileWriter.write(tab + finalVariableName + " = "  + getWrapped(value) + ";");
    }

    /**
     * @return Returns a wrapped string if the class is a string.
     */
    private String getWrapped(@NotNull TypeKeyValue value) {
        if(value.getClazz().equals(String.class)) {
            return '"'+value.getValue()+'"';
        }
        return value.getValue();
    }

    @Contract(pure = true)
    private @NotNull String getFinalVariableName(final String typeName, final String variableName) {
        return String.format("public static final %s %s",typeName,variableName);
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
                writeValue(fileWriter,entrySet);
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

    private void writeValue(Writer fileWriter,Map.@NotNull Entry<String, T> entrySet) throws IOException{
        if (noChildren(entrySet.getValue())) {
            writePrimitiveValue(fileWriter, entrySet.getKey(), getEntryValue(entrySet.getValue()), "\t");
            fileWriter.write("\n");
        } else {
            final String innerClassName = Util.getAsClassName(entrySet.getKey());
            fileWriter.write("\t public static class " + innerClassName + " {");
            fileWriter.write("\n");
            for (Map.Entry<String, T> elementSet : getEntrySetFromValue(entrySet.getValue())) {
                if(noChildren(elementSet.getValue())) {
                    writePrimitiveValue(fileWriter, elementSet.getKey(), getEntryValue(elementSet.getValue()), "\t\t");
                    fileWriter.write("\n");
                } else {
                    writeValue(fileWriter,elementSet);
                }
            }
            if (privateConstructor != null && !privateConstructor.isEmpty()) {
                writePrivateUtilConstructor(fileWriter, innerClassName);
            }
            fileWriter.write("}");
        }
    }


    public abstract FileType getFileType();

    public abstract boolean noChildren(T element);

    public abstract Set<Map.Entry<String, T>> getRootEntrySet(File file) throws IOException;

    public abstract Set<Map.Entry<String, T>> getEntrySetFromValue(T value);

    public abstract TypeKeyValue getEntryValue(T value);

}
