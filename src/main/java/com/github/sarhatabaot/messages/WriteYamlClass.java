package com.github.sarhatabaot.messages;

import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.Set;

/**
 * @author sarhatabaot
 */
public class WriteYamlClass extends WriteClass<Object>{
    public WriteYamlClass(final String targetPackage, final String basePath, final String privateConstructor, final boolean overwriteClasses) {
        super(targetPackage, basePath, privateConstructor, overwriteClasses);
    }

    @Override
    public FileType getFileType() {
        return FileType.YAML;
    }

    @Override
    public boolean isPrimitive(final @NotNull Object element) {
        return element.getClass().isPrimitive() ||
                element instanceof Integer ||
                element instanceof Double ||
                element instanceof Float ||
                element instanceof Boolean ||
                element instanceof String;
    }

    @Override
    public Set<Map.Entry<String, Object>> getRootEntrySet(final File file) throws IOException {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            Map<String, Object> yamlFile = yaml.load(inputStream);
            return yamlFile.entrySet();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Map.Entry<String, Object>> getEntrySetFromValue(final Object value) {
        return ((Map<String,Object>) value).entrySet();
    }

    @Override
    public TypeKeyValue getEntryValue(final Object value) {
        if(value instanceof Integer)
            return new TypeKeyValue(Integer.class, String.valueOf(value));
        if(value instanceof Double)
            return new TypeKeyValue(Double.class, String.valueOf(value));
        if(value instanceof Boolean)
            return new TypeKeyValue(Boolean.class,String.valueOf(value));

        return new TypeKeyValue(String.class, String.valueOf(value));
    }

    @Override
    public boolean isArray(final Object element) {
        return element instanceof String[];
    }

    @Override
    public String[] getAsStringArray(final Object value) {
        return (String[]) value;
    }
}
