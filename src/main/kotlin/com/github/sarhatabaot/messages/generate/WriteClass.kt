package com.github.sarhatabaot.messages.generate

import com.github.sarhatabaot.messages.model.FileType
import com.github.sarhatabaot.messages.model.TypeKeyValue
import com.github.sarhatabaot.messages.util.Util
import org.jboss.forge.roaster.Roaster
import org.jboss.forge.roaster.model.source.JavaClassSource
import org.jetbrains.annotations.Contract
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*

/**
 *
 * @author sarhatabaot
 */
abstract class WriteClass<T> protected constructor(
    private val targetPackage: String,
    private val basePath: String,
    private val privateConstructor: String?,
    protected val overwriteClasses: Boolean
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private fun arrayAsArrayInit(values: Array<String>): String {
        val stringBuilder = StringBuilder()
        for (value in values) {
            stringBuilder.append("\"$value\",")
        }
        stringBuilder.deleteCharAt(stringBuilder.length - 1)
        return stringBuilder.toString()
    }

    private fun getPathFromPackage(): String {
        return java.lang.String.join(
            File.separator,
            *targetPackage.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray())
    }

    //Entry Point
    @Throws(IOException::class)
    fun createJavaClass(sourceFile: File) {
        val parentFileName = Util.getAsFileName(sourceFile.getName()).replace(getFileType().extension, ".java")
        val parentClassName = parentFileName.replace(".java", "")
        val classPath = basePath + getPathFromPackage()
        val outputFile = File(classPath, parentFileName)
        val javaClass = createSourceClass(sourceFile, parentClassName)
        FileWriter(outputFile).use { fileWriter ->
            fileWriter.write(javaClass.toString())
            logger.info("Created class: $parentClassName for file: ${sourceFile.getName()} with type: ${getFileType().name}")
        }
    }

    @Contract(pure = true)
    private fun getUnsupportedOperationMessage(): String {
        return "throw new UnsupportedOperationException($privateConstructor);"
    }

    @Throws(IOException::class)
    private fun createSourceClass(sourceFile: File, parentClassName: String): JavaClassSource {
        val parentClass = Roaster.create(JavaClassSource::class.java)
            .setPackage(targetPackage)
            .setName(parentClassName)
            .setFinal(true)
        parentClass.addMethod()
            .setConstructor(true)
            .setPrivate()
            .setBody(getUnsupportedOperationMessage())
        for (entrySet in getRootEntrySet(sourceFile)) {
            writeValue(parentClass, entrySet)
        }
        return parentClass
    }

    private fun writeValue(parentClass: JavaClassSource, entrySet: Map.Entry<String, T>) {
        if (isPrimitive(entrySet.value)) {
            writePrimitiveField(parentClass, entrySet)
            return
        }
        if (isArray(entrySet.value)) {
            writeArrayField(parentClass, entrySet.key, getAsStringArray(entrySet.value))
            return
        }
        val innerClassName = Util.getAsClassName(entrySet.key)
        val innerClass = parentClass.addNestedType(JavaClassSource::class.java)
            .setName(innerClassName)
            .setStatic(true)
            .setFinal(true)
        innerClass.addMethod()
            .setConstructor(true)
            .setPrivate()
            .setBody(getUnsupportedOperationMessage())
        for (elementSet in getEntrySetFromValue(entrySet.value)) {
            if (isPrimitive(elementSet.value)) {
                if (isArray(elementSet.value)) {
                    writeArrayField(innerClass, elementSet.key, getAsStringArray(elementSet.value))
                } else {
                    writePrimitiveField(innerClass, elementSet)
                }
            } else {
                writeValue(innerClass, elementSet)
            }
        }
    }

    private fun writePrimitiveField(javaClass: JavaClassSource, entrySet: Map.Entry<String, T>) {
        val typeKeyValue = getEntryValue(entrySet.value)
        val type = typeKeyValue.getClazz()
        val fieldName = translateToFieldKey(entrySet.key)
        javaClass.addField()
            .setType(type)
            .setPublic()
            .setStatic(true)
            .setFinal(true)
            .setName(fieldName)
        if (type.isAssignableFrom(String::class.java)) {
            javaClass.getField(fieldName).setStringInitializer(typeKeyValue.getValue())
        } else {
            javaClass.getField(fieldName).setLiteralInitializer(typeKeyValue.getValue())
        }
    }

    private fun writeArrayField(javaClass: JavaClassSource, key: String, values: Array<String>) {
        javaClass.addField()
            .setType(Array<String>::class.java)
            .setPublic()
            .setStatic(true)
            .setFinal(true)
            .setName(translateToFieldKey(key))
            .setLiteralInitializer("new String[]{" + arrayAsArrayInit(values) + "}")
    }

    private fun translateToFieldKey(key: String): String {
        return if (key.contains("-")) {
            key.replace("-", "_").uppercase(Locale.getDefault())
        } else key.uppercase(Locale.getDefault())
    }

    /**
     * See #[FileType]
     * @return the file type
     */
    abstract fun getFileType(): FileType

    /**
     * @return true if the element is a primitive
     */
    abstract fun isPrimitive(element: T): Boolean

    /**
     * @return true if the element is an array
     */
    abstract fun isArray(element: T): Boolean

    @Throws(IOException::class)
    abstract fun getRootEntrySet(file: File?): Set<Map.Entry<String, T>>
    abstract fun getEntrySetFromValue(value: T): Set<Map.Entry<String, T>>
    abstract fun getEntryValue(value: T): TypeKeyValue
    abstract fun getAsStringArray(value: T): Array<String>
}
