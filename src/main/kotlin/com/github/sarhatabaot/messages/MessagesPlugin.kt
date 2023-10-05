package com.github.sarhatabaot.messages

import com.github.sarhatabaot.messages.generate.WriteClass
import com.github.sarhatabaot.messages.generate.WriteJsonClass
import com.github.sarhatabaot.messages.generate.WriteYamlClass
import com.github.sarhatabaot.messages.model.FileType
import com.github.sarhatabaot.messages.util.Util
import java.io.File
import java.io.IOException
import java.util.*

/**
 *
 * @author sarhatabaot
 */

abstract class MessagesPlugin<T : Exception> {
    abstract fun getBasePath(): String

    abstract fun getSourceFolder(): String

    abstract fun getBaseDir(): String?

    open fun getFileType(): FileType {
        return FileType.JSON
    }

    abstract fun getTargetPackage(): String
    abstract fun getPrivateConstructor(): String?
    abstract fun isOverwriteClasses(): Boolean

    @Throws(Exception::class)
    abstract fun throwException(message: String?)

    @Throws(Exception::class)
    open fun runTask() {
        val sourceFolder = File(getSourceFolder())
        generateClass(sourceFolder)
    }

    @Throws(Exception::class)
    open fun generateClass(sourceFolder: File) {
        val splitPackage = Util.getPathFromPackage(getTargetPackage())
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


    companion object {
        const val BASE_PATH = "src/main/java/"
    }
}
