@file:Suppress("SpellCheckingInspection")

plugins {
    java
    `java-library`
    id("maven-publish")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "2.1.0"
}

group = "com.github.Choi-JinHwan"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.7")

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    archiveFileName.set("AfreecatvLib.jar")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

kotlin {
    jvmToolchain(21)
}

publishing {
    publications {
        create("maven-public", MavenPublication::class) {
            groupId = rootProject.group.toString()
            artifactId = rootProject.name
            version = rootProject.version.toString()
            from(components.getByName("java"))
        }
    }
}
