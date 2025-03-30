/*
* This file was generated by the Gradle 'init' task.
*
* This generated file contains a sample Java library project to get you started.
* For more details on building Java & JVM projects, please refer to https://docs.gradle.org/8.12.1/userguide/building_java_projects.html in the Gradle documentation.
* This project uses @Incubating APIs which are subject to change.
*/

plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    `kotlin-dsl`
    kotlin("jvm") version "1.9.21"
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.2.1"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // This dependency is exported to consumers, that is to say found on their compile classpath.
    api(libs.commons.math3)

    // This dependency is used internally, and not exposed to consumers on their own compile classpath.
    implementation(libs.guava)
    implementation(kotlin("stdlib"))
    implementation(gradleApi())

    // plantUML : latest 1.2023.7 https://sourceforge.net/projects/plantuml/files/
    implementation("net.sourceforge.plantuml:plantuml:1.2023.7")

    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.11")

    // Kotlin Test 의존성 추가
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))

    // MockK 의존성 추가
    testImplementation("io.mockk:mockk:1.13.8")  // 최신 안정 버전

    // JUnit 의존성
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.11.1")
    testImplementation("org.junit.jupiter:junit-jupiter-migrationsupport:5.11.1")
    testImplementation(libs.junit.jupiter)

    testImplementation(gradleTestKit()) // Gradle TestKit 추가
    testImplementation(kotlin("test"))  // Kotlin Test 추가
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use JUnit Jupiter test framework
            useJUnitJupiter("5.11.1")
        }
    }
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.test {
    useJUnitPlatform()  // JUnit 5 사용 설정
}

group = "io.github.chaehwanli"
version = "0.3.10"

gradlePlugin {
    website = "https://github.com/chaehwanli/DependencyToUml"
    vcsUrl = "https://github.com/chaehwanli/DependencyToUml.git"
    plugins {
        create("dependencyToUMLPlugin") {
            id = "io.github.chaehwanli.dep2uml.dependencyToUMLPlugin"
            implementationClass = "io.github.chaehwanli.dep2uml.plugin.DependencyToUMLPlugin"
            displayName = "Dependency To UML Plugin"
            description =
                "A program that creates a package relationship using uml by referring to the dependency relationship."
            tags.set(listOf("dependencytoPlantUML", "DependencytoUML"))
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GradlePluginPortal"
            url = uri("https://plugins.gradle.org/m2/")
            credentials {
                username = System.getenv("GRADLE_PUBLISH_KEY")
                password = System.getenv("GRADLE_PUBLISH_SECRET")
                //username = providers.gradleProperty("gradle.publish.key").toString()//.orNull ?: System.getenv("GRADLE_PUBLISH_KEY")
                //password = providers.gradleProperty("gradle.publish.secret").toString()//.orNull ?: System.getenv("GRADLE_PUBLISH_SECRET")
            }
        }
    }
}

/*
tasks.withType<com.gradle.publish.PublishTask>().configureEach {
    website.set(providers.gradleProperty("pluginWebsite").orElse("https://github.com/chaehwanli/DependencyToUml"))
    vcsUrl.set(providers.gradleProperty("pluginVcsUrl").orElse("https://github.com/chaehwanli/DependencyToUml.git"))
    tags.set(listOf("gradle", "plugin", "dependency"))
}
*/

/*
pluginBundle {
    website = "https://github.com/chaehwanli/DependencyToUml"
    vcsUrl = "https://github.com/chaehwanli/DependencyToUml.git"
    description = "A program that creates a package relationship using uml by referring to the dependency relationship."
    tags = listOf("gradle", "plugin", "dependency")
}
*/
