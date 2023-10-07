package com.github.sarhatabaot.messages

import com.github.sarhatabaot.messages.model.FileType
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.File

/**
 *
 * @author sarhatabaot
 */
class MessagesPluginTest {
    @TempDir
    lateinit var tempOutputDir: File

    private lateinit var messagesJsonNoOverwritePlugin: MessagesPlugin<Exception>
    private lateinit var messagesJsonOverwritePlugin: MessagesPlugin<Exception>

    private lateinit var messagesYamlNoOverwritePlugin: MessagesPlugin<Exception>
    private lateinit var messagesYamlOverwritePlugin: MessagesPlugin<Exception>

    @BeforeEach
    fun setUp() {
        messagesJsonNoOverwritePlugin =
            MessagesPluginTestImpl(tempOutputDir, "src/test/resources/json", FileType.JSON, false)
        messagesJsonOverwritePlugin =
            MessagesPluginTestImpl(tempOutputDir, "src/test/resources/json", FileType.JSON, true)

        messagesYamlNoOverwritePlugin =
            MessagesPluginTestImpl(tempOutputDir, "src/test/resources/yaml", FileType.YAML, false)
        messagesYamlOverwritePlugin =
            MessagesPluginTestImpl(tempOutputDir, "src/test/resources/yaml", FileType.YAML, true)
    }

    @Test
    fun generateClass() {
    }
}

class MessagesPluginTestImpl(
    tempOutputDir: File,
    sourceFolder: String,
    fileType: FileType,
    overwriteClasses: Boolean
) : MessagesPlugin<Exception>(
    basePath = tempOutputDir.path.toString(),
    sourceFolder = sourceFolder,
    fileType = fileType,
    targetPackage = "com.github.sarhatabaot.test",
    overwriteClasses = overwriteClasses
) {

}
