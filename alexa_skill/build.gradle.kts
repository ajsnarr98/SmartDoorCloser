plugins {
    kotlin("jvm") version "1.4.10"
    java
    `maven-publish`
}

group = "com.github.ajsnarr98"
version = "1.0"
description = "Alexa Smart Home Door Closer"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    jcenter()
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.json:json:20180130")
    implementation("com.amazonaws:aws-lambda-java-core:1.2.0")
    implementation("com.amazonaws:aws-java-sdk-dynamodb:1.11.327")
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

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}
