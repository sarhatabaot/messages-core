package com.github.sarhatabaot.messages;

import com.github.sarhatabaot.messages.generate.WriteClass;
import com.github.sarhatabaot.messages.generate.WriteJsonClass;
import com.github.sarhatabaot.messages.generate.WriteYamlClass;
import com.github.sarhatabaot.messages.model.FileType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author sarhatabaot
 */
public interface MessagesPlugin<T extends Exception> {
    String BASE_PATH = "src/main/java/";
    
    String getBasePath();
    
    List<String> getSourceFolderPath();
    String getBaseDir();
    default FileType getFileType() {
        return FileType.JSON;
    }
    String getTargetPackage();
    
    String getPrivateConstructor();
    
    boolean isOverwriteClasses();
    void throwException(final String message) throws T;
    
    default void runTask() throws T {
        for(String path: getSourceFolderPath()) {
            final File sourceFolder = new File(path);
            generateClass(sourceFolder);
        }
    }
    
    private void generateClass(final File sourceFolder) throws T {
        String splitPackage = getPathFromPackage(getTargetPackage());
    
        final File targetFolder = new File(getBaseDir(), getBasePath()+ splitPackage);
    
        if (!sourceFolder.exists()) {
            throwException("Could not find source folder." + sourceFolder.getName());
            return;
        }
    
        if (!targetFolder.exists()) {
            throwException("Could not find specified package. " + getTargetPackage() + " " + targetFolder.getPath());
            return;
        }
    
        WriteClass<?> writeClass = null;
    
        if (getFileType() == FileType.JSON)
            writeClass = new WriteJsonClass(getTargetPackage(),getBasePath(), getPrivateConstructor(), isOverwriteClasses());
    
        if (getFileType() == FileType.YAML)
            writeClass = new WriteYamlClass(getTargetPackage(), getBasePath(), getPrivateConstructor(), isOverwriteClasses());
    
        if (writeClass == null) {
            throwException("There was a problem getting the file type");
            return;
        }
    
        try {
            if (sourceFolder.isDirectory()) {
                for (File sourceFile : Objects.requireNonNull(sourceFolder.listFiles())) {
                    writeClass.createJavaClass(sourceFile);
                }
            } else {
                writeClass.createJavaClass(sourceFolder);
            }
        } catch (IOException e) {
            throwException(e.getMessage());
        }
    }
    
    default @NotNull String getPathFromPackage(final @NotNull String targetPackage) {
        return String.join(File.separator, targetPackage.split("\\."));
    }
    
}
