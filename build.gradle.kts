import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version(libs.versions.kotlin)
    id("java-library")
    id("maven-publish")
    jacoco
}

group = "com.github.sarhatabaot.messages"
version = "1.6.0"
description = "Core library for plugins to generate data classes from json and yaml files."


repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.slf4j.api)
    compileOnly(libs.roaster.api)
    compileOnly(libs.roaster.jdt)
    compileOnly(libs.gson)
    compileOnly(libs.snakeyaml)

    implementation(libs.kotlin.stdlib8)

    api(libs.annotations)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform)
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf("-Xjvm-default=all")
}

publishing {

    publications.create<MavenPublication>("maven") {
        groupId = groupId
        artifactId = artifactId
        version = version

        from(components["java"])

        pom {
            name = "Messages Core"
            description = project.description
            url = "https://github.com/sarhatabaot/messages-core"

            licenses {
                license {
                    name.set("MIT License")
                    url.set("http://www.opensource.org/licenses/mit-license.php")
                }
            }

            developers {
                developer {
                    name = "Omer Oreg"
                    email = "omeroreg@gmail.com"
                    url = "https://sarhatabaot.net/"
                }
            }

            scm {
                name = "GitHub sarhatabaot Apache Maven Packages"
                url = "https://maven.pkg.github.com/sarhatabaot/messages-core"
            }
        }


    }
}

tasks {

    jar {
        manifest {
            attributes["Implementation-Title"] = rootProject.name
            attributes["Implementation-Version"] = rootProject.version
        }
    }

    test {
        useJUnitPlatform()
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}