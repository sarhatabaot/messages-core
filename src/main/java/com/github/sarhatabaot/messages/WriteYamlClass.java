package com.github.sarhatabaot.messages;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
    public boolean noChildren(final Object element) {
        return element instanceof String; //not exactly true, we should check if it's primitive or string
    }

    @Override
    public Set<Map.Entry<String, Object>> getRootEntrySet(final File file) throws IOException {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = new FileInputStream(file)) {
            Map<String, Object> yamlFile = yaml.load(inputStream);
            return yamlFile.entrySet();
        }
    }

    @Override
    public Set<Map.Entry<String, Object>> getEntrySetFromValue(final Object value) {
        return ((Map<String,Object>) value).entrySet();
    }

    @Override
    public String getEntryValueAsString(final Object value) {
        return (String) value;
    }
}
