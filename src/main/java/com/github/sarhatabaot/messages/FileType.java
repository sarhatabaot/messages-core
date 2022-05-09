package com.github.sarhatabaot.messages;

/**
 * @author sarhatabaot
 */
public enum FileType {
    YAML(".yml"),
    JSON(".json");
    private String extension;

    FileType(final String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
