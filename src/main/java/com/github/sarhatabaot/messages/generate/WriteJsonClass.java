package com.github.sarhatabaot.messages.generate;

import com.github.sarhatabaot.messages.model.FileType;
import com.github.sarhatabaot.messages.model.TypeKeyValue;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
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
    public boolean isPrimitive(final @NotNull JsonElement element) {
        return element.isJsonPrimitive();
    }

    @Override
    public Set<Map.Entry<String, JsonElement>> getRootEntrySet(File file) throws FileNotFoundException {
        JsonReader reader = new JsonReader(new FileReader(file));
        JsonElement rootElement = JsonParser.parseReader(reader);
        return rootElement.getAsJsonObject().entrySet();
    }

    @Override
    public Set<Map.Entry<String, JsonElement>> getEntrySetFromValue(@NotNull JsonElement jsonElement) {
        return jsonElement.getAsJsonObject().entrySet();
    }

    @Override
    public TypeKeyValue getEntryValue(final @NotNull JsonElement value) {
        JsonPrimitive primitive = value.getAsJsonPrimitive();

        if(primitive.isNumber()) {
            try {
                return new TypeKeyValue(Double.class, String.valueOf(primitive.getAsDouble()));
            } catch (NumberFormatException e){
                return new TypeKeyValue(Integer.class, String.valueOf(primitive.getAsInt()));
            }
        }

        return new TypeKeyValue(String.class, value.getAsString());
    }

    @Override
    public boolean isArray(final @NotNull JsonElement element) {
        return element.isJsonArray();
    }

    @Override
    public String[] getAsStringArray(final @NotNull JsonElement value) {
        List<String> stringList = new ArrayList<>();
        JsonArray jsonElements = value.getAsJsonArray();
        for(JsonElement element: jsonElements) {
            stringList.add(element.getAsString());
        }
        return stringList.toArray(new String[0]);
    }
}
