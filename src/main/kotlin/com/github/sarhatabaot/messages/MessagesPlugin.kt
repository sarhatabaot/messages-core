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
abstract class MessagesPlugin<T : Exception>(
    private val basePath: String = "src/main/java/",
    private val sourceFolder: String,
    private val baseDir: String? = null,
    private val fileType: FileType = FileType.JSON,
    private val targetPackage: String,
    private val privateConstructor: String? = null,
    private val overwriteClasses: Boolean = true
) {

    @Throws(Exception::class)
    open fun throwException(message: String?) {
        throw Exception(message)
    }

    @Throws(Exception::class)
    open fun runTask() {
        val sourceFolder = File(sourceFolder)
        generateClass(sourceFolder)
    }

    @Throws(Exception::class)
    open fun generateClass(sourceFolder: File) {
        val splitPackage = Util.getPathFromPackage(targetPackage)
        val targetFolder = File(baseDir, basePath + splitPackage)
        if (!sourceFolder.exists()) {
            throwException("Could not find source folder. ${sourceFolder.name}")
            return
        }
        if (!targetFolder.exists()) {
            throwException("Could not find specified package. $targetPackage ${targetFolder.path}")
            return
        }

        val writeClass: WriteClass<*> = getWriteClass()

        try {
            if (sourceFolder.isDirectory()) {
                for(sourceFile in sourceFolder.listFiles()?.filterNotNull()!!) {
                    writeClass.createJavaClass(sourceFile)
                }
            } else {
                writeClass.createJavaClass(sourceFolder)
            }
        } catch (e: IOException) {
            throwException(e.message)
        }
    }

    private fun getWriteClass(): WriteClass<*> {
        return if(fileType == FileType.JSON) {
            WriteJsonClass(targetPackage, basePath, privateConstructor, overwriteClasses)
        } else {
            WriteYamlClass(targetPackage, basePath, privateConstructor, overwriteClasses)
        }
    }
}
