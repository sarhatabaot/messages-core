package com.github.sarhatabaot.messages

import com.github.sarhatabaot.messages.generate.WriteClass
import com.github.sarhatabaot.messages.generate.WriteJsonClass
import com.github.sarhatabaot.messages.generate.WriteYamlClass
import com.github.sarhatabaot.messages.model.FileType
import java.io.File
import java.io.IOException
import java.util.*

/**
 *
 * @author sarhatabaot
 */

interface MessagesPlugin<T : Exception> {
    fun getBasePath(): String

    fun getSourceFolder(): String

    fun getBaseDir(): String?

    fun getFileType(): FileType {
        return FileType.JSON
    }

    fun getTargetPackage(): String
    fun getPrivateConstructor(): String?
    fun isOverwriteClasses(): Boolean

    @Throws(Exception::class)
    fun throwException(message: String?)

    @Throws(Exception::class)
    fun runTask() {
        val sourceFolder = File(getSourceFolder())
        generateClass(sourceFolder)
    }

    @Throws(Exception::class)
    fun generateClass(sourceFolder: File) {
        val splitPackage = getPathFromPackage(getTargetPackage())
        val targetFolder = File(getBaseDir(), getBasePath() + splitPackage)
        if (!sourceFolder.exists()) {
            throwException("Could not find source folder. ${sourceFolder.name}")
            return
        }
        if (!targetFolder.exists()) {
            throwException("Could not find specified package. ${getTargetPackage()} ${targetFolder.path}")
            return
        }

        var writeClass: WriteClass<*>? = null
        if (getFileType() === FileType.JSON)
            writeClass = WriteJsonClass(getTargetPackage(), getBasePath(), getPrivateConstructor(), isOverwriteClasses())
        if (getFileType() === FileType.YAML)
            writeClass = WriteYamlClass(getTargetPackage(), getBasePath(), getPrivateConstructor(), isOverwriteClasses())
        if (writeClass == null) {
            throwException("There was a problem getting the file type")
            return
        }

        try {
            if (sourceFolder.isDirectory()) {
                for (sourceFile in Objects.requireNonNull(sourceFolder.listFiles())) {
                    writeClass.createJavaClass(sourceFile!!)
                }
            } else {
                writeClass.createJavaClass(sourceFolder)
            }
        } catch (e: IOException) {
            throwException(e.message)
        }
    }

    fun getPathFromPackage(targetPackage: String): String {
        return java.lang.String.join(
            File.separator,
            *targetPackage.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray())
    }

    companion object {
        const val BASE_PATH = "src/main/java/"
    }
}
