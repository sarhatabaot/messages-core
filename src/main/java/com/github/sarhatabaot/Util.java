package com.github.sarhatabaot;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sarhatabaot
 */
public class Util {
    private Util() {
        throw new UnsupportedOperationException();
    }

    public static String getAsClassName(final String sourceFileName) {
        return getAsFileName(sourceFileName).replace(".java", "");
    }

    public static String getAsFileName(final String sourceFileName) {
        List<String> names = new ArrayList<>();
        for(String string: sourceFileName.split("-")) {
            names.add(capitalize(string));
        }
        return String.join("",names);
    }

    public static String getAsVariableName(final String sourceKey) {
        return sourceKey.replace("-","_").toUpperCase();
    }

    public static String capitalize(String str) {
        if(str == null)
            return null;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
