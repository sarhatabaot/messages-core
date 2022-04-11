package com.github.sarhatabaot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author sarhatabaot
 */
class UtilTest {

    @Test
    void getAsFileName() {
        final String fileTestName = "test-name.java";
        final String expectedResult = "TestName.java";
        assertEquals(expectedResult,Util.getAsFileName(fileTestName));
    }

    @Test
    void getAsClassName() {
        final String testName = "test-name";
        final String fileTestName = "test-name.java";
        final String expectedResult = "TestName";

        assertEquals(expectedResult, Util.getAsClassName(testName));
        assertEquals(expectedResult,Util.getAsClassName(fileTestName));
        assertNotEquals(testName, Util.getAsClassName(testName));
    }

    @Test
    void getAsVariableName() {
        final String testVariable = "test-variable";
        final String expectedResult = "TEST_VARIABLE";

        assertEquals(expectedResult,Util.getAsVariableName(testVariable));
        assertNotEquals(testVariable,Util.getAsVariableName(testVariable));

        final String testVariableSingle = "test";
        final String expectedResultSingle = "TEST";

        assertEquals(expectedResultSingle,Util.getAsVariableName(testVariableSingle));
        assertNotEquals(testVariableSingle,Util.getAsVariableName(testVariableSingle));
    }
}