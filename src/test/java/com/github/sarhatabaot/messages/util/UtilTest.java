package com.github.sarhatabaot.messages.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author sarhatabaot
 */
class UtilTest {

    @Test
    void getAsClassName() {
        assertEquals("InternalMessages", Util.getAsClassName("internal-messages.java"));

        assertNotEquals("InternalMessages.java", Util.getAsClassName("internal-messages.java"));
    }

    @Test
    void getAsFileName() {
        assertEquals("Commands", Util.getAsFileName("commands"));
        assertEquals("InternalMessages.java", Util.getAsFileName("internal-messages.java"));

        assertNotEquals("Internal-Messages",Util.getAsFileName("internal-messages"));
    }

    @Test
    void capitalize() {
        assertEquals("Hello", Util.capitalize("hello"));
        assertNotEquals("hello", Util.capitalize("hello"));

        assertEquals("HEllo", Util.capitalize("hEllo"));

        assertNull(Util.capitalize(null));
        assertNull(Util.capitalize(""));
    }
}