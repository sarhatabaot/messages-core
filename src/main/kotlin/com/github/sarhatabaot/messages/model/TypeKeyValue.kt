package com.github.sarhatabaot.messages.model

/**
 *
 * @author sarhatabaot
 */

class TypeKeyValue(clazz: Class<*>, value: String) {
    private val clazz: Class<*>
    private val value: String

    init {
        this.clazz = clazz
        this.value = value
    }

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
