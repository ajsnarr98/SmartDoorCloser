plugins {
    application
    kotlin("jvm") version "1.3.70"
    java
}

// version = "0.1.0"
group = "com.github.ajsnarr98"

application {
    mainClassName = "com.github.ajsnarr98.main.MainKt"
}

dependencies {
    implementation(kotlin("stdlib"))
}

repositories {
    jcenter()
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
        attributes("Main-Class" to "com.stuffhere.main.MainKt")
    }

    // To add all of the dependencies
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from { 
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    }
}
