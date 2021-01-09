plugins {
    application
    kotlin("jvm") version "1.4.10"
    java
    `maven-publish`
}

group = "com.github.ajsnarr98"
version = "1.0"
description = "Alexa Smart Home Door Closer - Device"
java.sourceCompatibility = JavaVersion.VERSION_1_8

application {
    mainClassName = "com.github.ajsnarr98.main.MainKt"
}

repositories {
    jcenter()
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    // kotlin libs
    implementation(kotlin("stdlib"))
    // gson
    implementation("com.google.code.gson:gson:2.8.6")
    // logging
    implementation(group="org.apache.logging.log4j", name="log4j-api", version="2.14.0")
    implementation(group="org.apache.logging.log4j", name="log4j-core", version="2.14.0")
    // aws
    implementation("com.amazonaws:aws-lambda-java-core:1.2.0")
    implementation("com.amazonaws:aws-java-sdk-dynamodb:1.11.327")
    implementation("software.amazon.awssdk.iotdevicesdk:aws-iot-device-sdk:1.2.11")

    // testing
    testImplementation("junit:junit:4.13.1")
}

sourceSets {
    main {
        java {
            srcDirs(
                    "src/main/kotlin/"
            )
        }
    }
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "com.github.ajsnarr98.smartdoorcloser.MainKt")
    }

    // To add all of the dependencies
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from ({
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    })
}
