plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // Add a dependency for JSON parsing if needed
    implementation("com.google.code.gson:gson:2.8.9")
    
    // You may also add dependencies for HTTP client libraries, if not using standard Java
    implementation("org.apache.httpcomponents.client5:httpclient5:5.2")
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use JUnit Jupiter test framework
            useJUnitJupiter("5.10.3")
        }
    }
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

javafx {
    version = "22.0.1"
    modules = listOf("javafx.controls")
}

application {
    // Define the main class for the application.
    mainClass = "org.example.App"
}
