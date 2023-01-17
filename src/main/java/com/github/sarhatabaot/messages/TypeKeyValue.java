package com.github.sarhatabaot.messages;

/**
 * @author sarhatabaot
 */
public class TypeKeyValue {
    private final Class<?> clazz;
    private final String value;

    public TypeKeyValue(final Class<?> clazz, final String value) {
        this.clazz = clazz;
        this.value = value;
    }
    
    /**
     * Returns the value to set.
     * @return the value.
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Returns the class type to generate.
     * @return The class type.
     */
    public Class<?> getClazz() {
        return clazz;
    }
}
