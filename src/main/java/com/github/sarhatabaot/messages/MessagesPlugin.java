package com.github.sarhatabaot.messages;

import com.github.sarhatabaot.messages.generate.WriteClass;
import com.github.sarhatabaot.messages.generate.WriteJsonClass;
import com.github.sarhatabaot.messages.generate.WriteYamlClass;
import com.github.sarhatabaot.messages.model.FileType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * @author sarhatabaot
 */
public interface MessagesPlugin<T extends Exception> {
    String BASE_PATH = "src/main/java/";
    
    String getBasePath();
    
    File getSourceFolder();
    File getBaseDir();
    default FileType getFileType() {
        return FileType.JSON;
    }
    String getTargetPackage();
    
    String getPrivateConstructor();
    
    boolean isOverwriteClasses();
    void throwException(final String message) throws T;
    
    default void runTask() throws T {
        String splitPackage = getPathFromPackage(getTargetPackage());
        
        final File targetFolder = new File(getBaseDir(), getBasePath()+ splitPackage);
        if (!getSourceFolder().exists()) {
            throwException("Could not find source folder." + getSourceFolder().getName());
        }
        
        if (!targetFolder.exists()) {
            throwException("Could not find specified package. " + getTargetPackage() + " " + targetFolder.getPath());
        }
        
        WriteClass<?> writeClass = null;
        
        if (getFileType() == FileType.JSON)
            writeClass = new WriteJsonClass(getTargetPackage(),getBasePath(), getPrivateConstructor(), isOverwriteClasses());
        
        if (getFileType() == FileType.YAML)
            writeClass = new WriteYamlClass(getTargetPackage(), getBasePath(), getPrivateConstructor(), isOverwriteClasses());
        
        if (writeClass == null) {
            throwException("There was a problem getting the file type");
        }
        
        try {
            if (getSourceFolder().isDirectory()) {
                for (File sourceFile : Objects.requireNonNull(getSourceFolder().listFiles())) {
                    writeClass.createJavaClass(sourceFile);
                }
            } else {
                writeClass.createJavaClass(getSourceFolder());
            }
        } catch (IOException e) {
            throwException(e.getMessage());
        }
    }
    
    default @NotNull String getPathFromPackage(final @NotNull String targetPackage) {
        return String.join(File.separator, targetPackage.split("\\."));
    }
    
}
