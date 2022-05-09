package com.github.sarhatabaot.messages;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.Set;

/**
 * @author sarhatabaot
 */
public class WriteJsonClass extends WriteClass<JsonElement>{

    public WriteJsonClass(final String targetPackage, final String basePath, final String privateConstructor, final boolean overwriteClasses) {
        super(targetPackage, basePath, privateConstructor, overwriteClasses);
    }


    @Override
    public FileType getFileType() {
        return FileType.JSON;
    }

    @Override
    public boolean noChildren(final @NotNull JsonElement element) {
        return element.isJsonPrimitive();
    }

    @Override
    public Set<Map.Entry<String, JsonElement>> getRootEntrySet(File file) throws FileNotFoundException {
        //json stuff
        JsonReader reader = new JsonReader(new FileReader(file));

        //root element
        JsonElement rootElement = JsonParser.parseReader(reader);
        return rootElement.getAsJsonObject().entrySet();
    }

    @Override
    public Set<Map.Entry<String, JsonElement>> getEntrySetFromValue(@NotNull JsonElement jsonElement) {
        return jsonElement.getAsJsonObject().entrySet();
    }

    @Override
    public String getEntryValueAsString(final @NotNull JsonElement value) {
        return value.getAsString();
    }
}
