package com.github.sarhatabaot.messages.util

import com.github.sarhatabaot.messages.util.Util.capitalize
import com.github.sarhatabaot.messages.util.Util.getAsClassName
import com.github.sarhatabaot.messages.util.Util.getAsFileName
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

/**
 *
 * @author sarhatabaot
 */
class UtilTest {

    @Test
    fun getAsClassName() {
        assertEquals("InternalMessages", getAsClassName("internal-messages.java"))

        assertNotEquals("InternalMessages.java", getAsClassName("internal-messages.java"))
    }

    @Test
    fun getAsFileName() {
        assertEquals("Commands", getAsFileName("commands"))
        assertEquals("InternalMessages.java", getAsFileName("internal-messages.java"))

        assertNotEquals("Internal-Messages", getAsFileName("internal-messages"))
    }

    @Test
    fun capitalize() {
        assertEquals("Hello", capitalize("hello"))
        assertNotEquals("hello", capitalize("hello"))

        assertEquals("HEllo", capitalize("hEllo"))

        Assertions.assertNull(capitalize(null))
        Assertions.assertNull(capitalize(""))
    }
}