plugins {
    id("java-library")
    id("maven-publish")
}

version = "1.6.0"
description = "Core library for plugins to generate data classes from json and yaml files."


repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.annotations)
    compileOnly(libs.slf4j.api)

    //todo not sure we need this to be exposed
    api(libs.roaster.api)
    api(libs.roaster.jdt)
    api(libs.gson)
    api(libs.snakeyaml)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform)
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    test {
        useJUnitPlatform()
    }
}