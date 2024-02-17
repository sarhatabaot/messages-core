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
class MessagesPlugin<T : Exception>(
    var basePath: String = "src/main/java/",
    var sourceFolder: String,
    var baseDir: String? = null,
    var fileType: FileType = FileType.JSON,
    var targetPackage: String,
    var privateConstructor: String? = null,
    var overwriteClasses: Boolean = true
) {

    @Throws(Exception::class)
    fun throwException(message: String?) {
        throw Exception(message)
    }

    @Throws(Exception::class)
    fun runTask() {
        val sourceFolder = File(sourceFolder)
        generateClass(sourceFolder)
    }

    @Throws(Exception::class)
    fun generateClass(sourceFolder: File) {
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
