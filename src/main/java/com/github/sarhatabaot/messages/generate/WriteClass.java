package com.github.sarhatabaot.messages.generate;

import com.github.sarhatabaot.messages.util.Util;
import com.github.sarhatabaot.messages.model.FileType;
import com.github.sarhatabaot.messages.model.TypeKeyValue;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    
    protected WriteClass(final String targetPackage, final String basePath, final String privateConstructor, final boolean overwriteClasses) {
        this.targetPackage = targetPackage;
        this.basePath = basePath;
        this.privateConstructor = privateConstructor;
        this.overwriteClasses = overwriteClasses;
    }
    
    private @NotNull String arrayAsArrayInit(final String @NotNull [] values) {
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
    
    
    protected @NotNull String getPathFromPackage() {
        return String.join(File.separator, targetPackage.split("\\."));
    }
    
    //Entry Point
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
    
    @Contract(pure = true)
    private @NotNull String getUnsupportedOperationMessage() {
        return "throw new UnsupportedOperationException(" + privateConstructor + ");";
    }
    
    private @NotNull JavaClassSource createSourceClass(@NotNull File sourceFile, final String parentClassName) throws IOException {
        final JavaClassSource parentClass = Roaster.create(JavaClassSource.class)
            .setPackage(targetPackage)
            .setName(parentClassName)
            .setFinal(true);
        
        parentClass.addMethod()
            .setConstructor(true)
            .setPrivate()
            .setBody(getUnsupportedOperationMessage());
        
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
            .setBody(getUnsupportedOperationMessage());
        
        
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
        Class<?> type = typeKeyValue.getClazz();
        String fieldName = translateToFieldKey(entrySet.getKey());
        javaClass.addField()
            .setType(type)
            .setPublic()
            .setStatic(true)
            .setFinal(true)
            .setName(fieldName);
        
        if(type.isAssignableFrom(String.class)) {
            javaClass.getField(fieldName).setStringInitializer(typeKeyValue.getValue());
        } else {
            javaClass.getField(fieldName).setLiteralInitializer(typeKeyValue.getValue());
        }
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
    
    
    private @NotNull String translateToFieldKey(final @NotNull String key) {
        if (key.contains("-")) {
            return key.replace("-", "_").toUpperCase();
        }
        return key.toUpperCase();
    }
    
    /**
     * See #{@link FileType}
     * @return the file type
     */
    public abstract FileType getFileType();
    
    public abstract boolean isPrimitive(T element);
    
    public abstract boolean isArray(T element);
    
    public abstract Set<Map.Entry<String, T>> getRootEntrySet(File file) throws IOException;
    
    public abstract Set<Map.Entry<String, T>> getEntrySetFromValue(T value);
    
    public abstract TypeKeyValue getEntryValue(T value);
    
    public abstract String[] getAsStringArray(T value);
    
}
