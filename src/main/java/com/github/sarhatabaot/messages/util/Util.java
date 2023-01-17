package com.github.sarhatabaot.messages.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sarhatabaot
 */
public class Util {
    private Util() {
        throw new UnsupportedOperationException();
    }

    public static @NotNull String getAsClassName(final String sourceFileName) {
        return getAsFileName(sourceFileName).replace(".java", "");
    }

    public static @NotNull String getAsFileName(final @NotNull String sourceFileName) {
        List<String> names = new ArrayList<>();
        for(String string: sourceFileName.split("-")) {
            names.add(capitalize(string));
        }
        return String.join("",names);
    }
    
    public static String capitalize(String str) {
        if(str == null)
            return null;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


}
