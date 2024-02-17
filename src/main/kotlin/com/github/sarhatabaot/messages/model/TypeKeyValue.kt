package com.github.sarhatabaot.messages.model

/**
 *
 * @author sarhatabaot
 */

class TypeKeyValue(private val clazz: Class<*>, private val value: String) {

    /**
     * Returns the value to set.
     * @return the value.
     */
    fun getValue(): String {
        return value
    }

    /**
     * Returns the class type to generate.
     * @return The class type.
     */
    fun getClazz(): Class<*> {
        return clazz
    }
}
