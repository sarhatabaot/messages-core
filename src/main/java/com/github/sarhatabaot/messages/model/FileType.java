package com.github.sarhatabaot.messages.model;

/**
 * @author sarhatabaot
 */
public enum FileType {
    YAML(".yml"),
    JSON(".json");
    private final String extension;

    FileType(final String extension) {
        this.extension = extension;
    }
    
    /**
     * @return the full file extension. Including the ".".
     */
    public String getExtension() {
        return extension;
    }
}
