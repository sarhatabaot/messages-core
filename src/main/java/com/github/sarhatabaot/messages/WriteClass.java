package com.github.sarhatabaot.messages;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
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
    
    private String arrayAsArrayInit(final String @NotNull [] values) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String value : values) {
            stringBuilder
                .append('"')
                .append(value)
                .append('"')
                .append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
    
    /**
     * @return Returns a wrapped string if the class is a string.
     */
    private String getWrapped(@NotNull TypeKeyValue value) {
        if (value.getClazz().equals(String.class)) {
            return '"' + value.getValue() + '"';
        }
        return value.getValue();
    }
    
    @Contract(pure = true)
    private @NotNull String getFinalVariableName(final String typeName, final String variableName) {
        return String.format("public static final %s %s", typeName, variableName);
    }
    
    protected @NotNull String getPathFromPackage() {
        return String.join(File.separator, targetPackage.split("\\."));
    }
    
    public void createJavaClass(@NotNull File sourceFile) throws IOException {
        final String parentFileName = Util.getAsFileName(sourceFile.getName()).replace(getFileType().getExtension(), ".java");
        final String parentClassName = parentFileName.replace(".java", "");
        final String classPath = basePath + getPathFromPackage();
        
        File outputFile = new File(classPath, parentFileName);
        
        JavaClassSource javaClass = createSourceClass(sourceFile, parentClassName);
        try (FileWriter fileWriter = new FileWriter(outputFile)) {
            fileWriter.write(javaClass.toString());
            logger.info(String.format("Created class: %s for file: %s with type: %s", parentClassName, sourceFile.getName(), getFileType().name()));
        }
    }
    
    private @NotNull JavaClassSource createSourceClass(@NotNull File sourceFile, final String parentClassName) throws IOException {
        final JavaClassSource parentClass = Roaster.create(JavaClassSource.class)
            .setPackage(targetPackage)
            .setName(parentClassName)
            .setFinal(true);
        
        parentClass.addMethod()
            .setConstructor(true)
            .setPrivate()
            .setBody("throw new UnsupportedOperationException(" + privateConstructor + ");");
        
        for (Map.Entry<String, T> entrySet : getRootEntrySet(sourceFile)) {
            writeValue(parentClass, entrySet);
        }
        
        return parentClass;
    }
    
    private void writeValue(final JavaClassSource parentClass, Map.@NotNull Entry<String, T> entrySet) {
        if (isPrimitive(entrySet.getValue())) {
            writePrimitiveField(parentClass, entrySet);
            return;
        }
        
        if (isArray(entrySet.getValue())) {
            writeArrayField(parentClass, entrySet.getKey(), getAsStringArray(entrySet.getValue()));
            return;
        }
        
        final String innerClassName = Util.getAsClassName(entrySet.getKey());
        final JavaClassSource innerClass = parentClass.addNestedType(JavaClassSource.class)
            .setName(innerClassName)
            .setStatic(true)
            .setFinal(true);
        
        
        innerClass.addMethod()
            .setConstructor(true)
            .setPrivate()
            .setBody("throw new UnsupportedOperationException(" + privateConstructor + ");");
        
        
        for (Map.Entry<String, T> elementSet : getEntrySetFromValue(entrySet.getValue())) {
            if (isPrimitive(elementSet.getValue())) {
                if (isArray(elementSet.getValue())) {
                    writeArrayField(innerClass, elementSet.getKey(), getAsStringArray(elementSet.getValue()));
                } else {
                    writePrimitiveField(innerClass, elementSet);
                }
            } else {
                writeValue(innerClass, elementSet);
            }
        }
    }
    
    
    private void writePrimitiveField(final @NotNull JavaClassSource javaClass, Map.@NotNull Entry<String, T> entrySet) {
        TypeKeyValue typeKeyValue = getEntryValue(entrySet.getValue());
        javaClass.addField()
            .setType(typeKeyValue.getClazz())
            .setPublic()
            .setStatic(true)
            .setFinal(true)
            .setName(translateToFieldKey(entrySet.getKey()))
            .setStringInitializer(typeKeyValue.getValue());
    }
    
    private void writeArrayField(final @NotNull JavaClassSource javaClass, final String key, final String @NotNull [] values) {
        javaClass.addField()
            .setType(String[].class)
            .setPublic()
            .setStatic(true)
            .setFinal(true)
            .setName(translateToFieldKey(key))
            .setStringInitializer(arrayAsArrayInit(values));
    }
    
    
    private void writeJavaClass(@NotNull File sourceFile, final String parentClassName, final File outputFile) {
        try (Writer fileWriter = new BufferedWriter(new FileWriter(outputFile, !overwriteClasses))) {
            fileWriter.write("package " + targetPackage + ";");
            fileWriter.write("\n");
            fileWriter.write("public final class " + parentClassName + " ");
            fileWriter.write("{");
            fileWriter.write("\n");
            
            for (Map.Entry<String, T> entrySet : getRootEntrySet(sourceFile)) {
                writeValue(fileWriter, entrySet);
            }
            //json end
            fileWriter.write("\n");
            if (privateConstructor != null && !privateConstructor.isEmpty()) {
                writePrivateUtilConstructor(fileWriter, parentClassName);
            }
            fileWriter.write("}");
            
            logger.info(String.format("Created class: %s for file: %s with type: %s", parentClassName, sourceFile.getName(), getFileType().name()));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    private @NotNull String translateToFieldKey(final @NotNull String key) {
        if (key.contains("-")) {
            return key.replace("-", "_").toUpperCase();
        }
        return key.toUpperCase();
    }
    
    public abstract FileType getFileType();
    
    public abstract boolean isPrimitive(T element);
    
    public abstract boolean isArray(T element);
    
    public abstract Set<Map.Entry<String, T>> getRootEntrySet(File file) throws IOException;
    
    public abstract Set<Map.Entry<String, T>> getEntrySetFromValue(T value);
    
    public abstract TypeKeyValue getEntryValue(T value);
    
    public abstract String[] getAsStringArray(T value);
    
}
