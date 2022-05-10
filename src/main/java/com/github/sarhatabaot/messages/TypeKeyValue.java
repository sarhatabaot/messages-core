package com.github.sarhatabaot.messages;

/**
 * @author sarhatabaot
 */
public class TypeKeyValue {
    private Class<?> clazz;
    private final String value;

    public TypeKeyValue(final Class<?> clazz, final String value) {
        this.clazz = clazz;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
