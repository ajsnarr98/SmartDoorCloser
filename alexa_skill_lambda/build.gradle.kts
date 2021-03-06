plugins {
    kotlin("jvm") version "1.4.10"
    java
    `maven-publish`
}

group = "com.github.ajsnarr98.smartdoorcloser"
version = "1.0"
description = "Alexa Smart Home Door Closer - Alexa Skill Lambda"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    jcenter()
    mavenLocal()
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    val retrofitVersion = "2.9.0"
    // kotlin
    implementation(kotlin("stdlib"))
    // gson
    implementation("com.google.code.gson:gson:2.8.6")
    // retrofit
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    // aws libs
    implementation("com.amazonaws:aws-lambda-java-core:1.2.0")
    implementation("com.amazonaws:aws-java-sdk-dynamodb:1.11.327")

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

    // To add all of the dependencies
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from ({
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    })
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}
