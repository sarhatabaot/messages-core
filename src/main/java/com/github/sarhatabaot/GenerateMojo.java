package com.github.sarhatabaot;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;


/**
 * This goal aims to generate a static accessor class
 * with string to every internal message
 */
@Mojo(
        name = "generate",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES
)
public class GenerateMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject mavenProject;

    @Parameter(property = "messages.overwrite")
    private boolean overwriteClasses;

    @Parameter(required = true, property = "messages.sourcefolder") //source folder to generate classes from
    private File sourceFolder;

    @Parameter(required = true, property = "messages.targetpackage") //target should be a package
    private String targetPackage;

    @Parameter(property = "messages.privateconstructor")
    private String privateConstructor;

    private static final String BASE_PATH = "src" + File.separator + "main" + File.separator + "java" + File.separator;

    public void execute() throws MojoExecutionException {
        String splitPackage = getPathFromPackage();

        final File targetFolder = new File(mavenProject.getBasedir(), BASE_PATH + splitPackage);
        if (!sourceFolder.exists())
            throw new MojoExecutionException("Could not find source folder." + sourceFolder.getName());

        if (!targetFolder.exists())
            throw new MojoExecutionException("Could not find specified package. " + targetPackage + " " + targetFolder.getPath());


        for (File sourceFile : sourceFolder.listFiles()) {
            createJavaClassFromJsonFile(sourceFile);
        }
        //File targetClassFile = new File
    }

    private void createJavaClassFromJsonFile(final File file) {
        final String parentFileName = Util.getAsFileName(file.getName()).replace(".json", ".java");
        final String parentClassName = parentFileName.replace(".java", "");
        final String classPath = BASE_PATH + getPathFromPackage();

        File outputFile = new File(classPath, parentFileName);

        try (Writer fileWriter = new BufferedWriter(new FileWriter(outputFile, !overwriteClasses))) {
            fileWriter.write("package " + targetPackage + ";");
            fileWriter.write("\n");
            fileWriter.write("\n");
            fileWriter.write("public final class " + parentClassName + " ");
            fileWriter.write("{");
            fileWriter.write("\n");

            //json stuff
            JsonReader reader = new JsonReader(new FileReader(file));

            //root element
            JsonElement rootElement = JsonParser.parseReader(reader);
            getLog().debug(rootElement.toString());
            for (Map.Entry<String, JsonElement> entrySet : rootElement.getAsJsonObject().entrySet()) {
                if (entrySet.getValue().isJsonPrimitive()) {
                    writePrimitiveString(fileWriter, entrySet, "\t");
                    fileWriter.write("\n");
                } else {
                    final String innerClassName = Util.getAsClassName(entrySet.getKey());
                    fileWriter.write("\t public static class " + innerClassName + " {");
                    fileWriter.write("\n");
                    for (Map.Entry<String, JsonElement> elementSet : entrySet.getValue().getAsJsonObject().entrySet()) {
                        writePrimitiveString(fileWriter, elementSet, "\t\t");
                        fileWriter.write("\n");
                    }
                    if (!privateConstructor.isEmpty()) {
                        writePrivateUtilConstructor(fileWriter,innerClassName);
                    }
                    fileWriter.write("}");
                }
            }
            //json end
            fileWriter.write("\n");
            if (!privateConstructor.isEmpty()) {
                writePrivateUtilConstructor(fileWriter, parentClassName);
            }
            fileWriter.write("}");

            getLog().info("Created class: %s for file: %s".formatted(parentClassName, file.getName()));
        } catch (IOException e) {
            getLog().error(e);
        }
    }

    private void writePrivateUtilConstructor(Writer fileWriter, String parentClassName) throws IOException {
        fileWriter.write("private " + parentClassName + " (){");
        fileWriter.write("throw new UnsupportedOperationException(" + privateConstructor + ");");
        fileWriter.write("}");
    }

    private void writePrimitiveString(Writer fileWriter, Map.Entry<String, JsonElement> entrySet, String tab) throws IOException {
        final String baseVariableName = "public static final String ";
        String variableName = Util.getAsVariableName(entrySet.getKey());
        final String finalVariableName = baseVariableName + variableName;
        final String finalVariableValue = entrySet.getValue().getAsString();
        fileWriter.write(tab + finalVariableName + " = " + '"' + finalVariableValue + '"' + ";");
    }

    private String getPathFromPackage() {
        return String.join(File.separator, targetPackage.split("\\."));
    }

}
