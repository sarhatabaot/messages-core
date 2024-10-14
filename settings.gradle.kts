rootProject.name = "messages-core"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("gson", "com.google.code.gson:gson:2.10.1")
            library("snakeyaml", "org.yaml:snakeyaml:2.2")
            library("annotations", "org.jetbrains:annotations:26.0.1")
            library("slf4j-api", "org.slf4j:slf4j-api:2.0.12")

            version("kotlin", "1.9.22")
            library("kotlin-stdlib8","org.jetbrains.kotlin","kotlin-stdlib-jdk8").versionRef("kotlin")

            version("roaster", "2.22.2.Final")
            library("roaster-api", "org.jboss.forge.roaster","roaster-api").versionRef("roaster")
            library("roaster-jdt", "org.jboss.forge.roaster","roaster-jdt").versionRef("roaster")

            library("junit-jupiter","org.junit.jupiter:junit-jupiter:5.10.2")
            library("junit-platform", "org.junit.platform","junit-platform-launcher").withoutVersion()
        }
    }
}