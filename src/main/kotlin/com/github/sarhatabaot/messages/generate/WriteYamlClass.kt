package com.github.sarhatabaot.messages.generate

import com.github.sarhatabaot.messages.model.FileType
import com.github.sarhatabaot.messages.model.TypeKeyValue
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.nio.file.Files


/**
 *
 * @author sarhatabaot
 */
class WriteYamlClass(
    targetPackage: String,
    basePath: String,
    privateConstructor: String?,
    overwriteClasses: Boolean
): WriteClass<Any>(
    targetPackage,
    basePath,
    privateConstructor,
    overwriteClasses
) {
    override fun getFileType() = FileType.YAML

    override fun isPrimitive(element: Any): Boolean {
        return element.javaClass.isPrimitive ||
                element is Int ||
                element is Double ||
                element is Float ||
                element is Boolean ||
                element is String
    }

    override fun isArray(element: Any): Boolean {
        return element is Array<*> && element.isArrayOf<String>()
    }

    override fun getRootEntrySet(file: File?): Set<Map.Entry<String, Any>> {
        val yaml = Yaml()
        Files.newInputStream(file!!.toPath()).use { inputStream ->
            val yamlFile =
                yaml.load<Map<String, Any>>(inputStream)
            return yamlFile.entries
        }
    }

    override fun getEntrySetFromValue(value: Any) = (value as Map<String, Any>).entries

    override fun getEntryValue(value: Any): TypeKeyValue {
        if (value is Int) return TypeKeyValue(Int::class.java, value.toString())
        if (value is Double) return TypeKeyValue(Double::class.java, value.toString())
        return if (value is Boolean) TypeKeyValue(
            Boolean::class.java,
            value.toString()
        ) else TypeKeyValue(String::class.java, value.toString())

    }

    override fun getAsStringArray(value: Any): Array<String> {
        return value as Array<String>
    }
}