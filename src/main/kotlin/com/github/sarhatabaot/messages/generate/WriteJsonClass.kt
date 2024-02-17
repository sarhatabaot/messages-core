package com.github.sarhatabaot.messages.generate

import com.github.sarhatabaot.messages.model.FileType
import com.github.sarhatabaot.messages.model.TypeKeyValue
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.stream.JsonReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader

/**
 *
 * @author sarhatabaot
 */
class WriteJsonClass(
    targetPackage: String,
    basePath: String,
    privateConstructor: String?,
    overwriteClasses: Boolean
): WriteClass<JsonElement>(
    targetPackage,
    basePath,
    privateConstructor,
    overwriteClasses
) {
    override fun getFileType() = FileType.JSON

    override fun isPrimitive(element: JsonElement) = element.isJsonPrimitive

    override fun isArray(element: JsonElement) = element.isJsonArray

    @Throws(FileNotFoundException::class)
    override fun getRootEntrySet(file: File?): Set<Map.Entry<String, JsonElement>> {
        val reader = JsonReader(file?.let { FileReader(it) })
        val rootElement = JsonParser.parseReader(reader)
        return rootElement.asJsonObject.entrySet()
    }

    override fun getEntrySetFromValue(value: JsonElement): Set<Map.Entry<String, JsonElement>> {
        return value.asJsonObject.entrySet()
    }

    override fun getEntryValue(value: JsonElement): TypeKeyValue {
        val primitive = value.asJsonPrimitive

        return if (primitive.isNumber) {
            try {
                TypeKeyValue(Double::class.java, primitive.asDouble.toString())
            } catch (e: NumberFormatException) {
                TypeKeyValue(Int::class.java, primitive.asInt.toString())
            }
        } else TypeKeyValue(String::class.java, value.asString)

    }

    override fun getAsStringArray(value: JsonElement): Array<String> {
        val stringList: MutableList<String> = ArrayList()
        val jsonElements = value.asJsonArray
        for (element in jsonElements) {
            stringList.add(element.asString)
        }
        return stringList.toTypedArray<String>()
    }
}