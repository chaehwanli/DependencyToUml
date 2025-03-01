package com.meteroid.dep2uml.analyzer

import io.mockk.every
import io.mockk.mockk
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.ResolvedConfiguration
import org.gradle.api.artifacts.ResolvedDependency
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.gradle.testfixtures.ProjectBuilder

class DefaultGradleDependencyAnalyzerTest {

    @Test
    fun `should analyze project dependencies`() {
        // Given
        val analyzer = DefaultGradleDependencyAnalyzer()
        val project = mockk<Project>()
        val configurations = mockk<ConfigurationContainer>()
        val configuration = mockk<Configuration>()
        val resolvedConfiguration = mockk<ResolvedConfiguration>()
        val resolvedDependency = mockk<ResolvedDependency>()

        // Mock setup
        every { project.configurations } returns configurations
        every { configurations.iterator() } returns mutableSetOf(configuration).iterator()
        every { configuration.isCanBeResolved } returns true
        every { configuration.resolvedConfiguration } returns resolvedConfiguration
        every { resolvedConfiguration.firstLevelModuleDependencies } returns setOf(
            resolvedDependency
        )
        every { resolvedDependency.moduleGroup } returns "org.springframework"
        every { resolvedDependency.moduleName } returns "spring-core"
        every { resolvedDependency.moduleVersion } returns "5.3.0"
        every { resolvedDependency.children } returns setOf()

        // When
        val result = analyzer.analyzeProject(project)

        // Then
        assertEquals(1, result.size)
        with(result.first()) {
            assertEquals("org.springframework", group)
            assertEquals("spring-core", name)
            assertEquals("5.3.0", version)
        }
    }

    @Test
    fun `should basic dependency analysis`() {
        val analyzer = DefaultGradleDependencyAnalyzer()
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("java")

        project.dependencies.add("implementation", "org.apache.commons:commons-lang3:3.12.0")

        val config = project.configurations.getByName("implementation")
        val dependencies = config.dependencies.map { it.group + ":" + it.name + ":" + it.version }

        analyzer.analyzeProject(project)

        assertTrue(dependencies.contains("org.apache.commons:commons-lang3:3.12.0"))
    }

    @Test
    fun `should transition dependency analysis`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("java")

        project.repositories.mavenCentral()
        project.dependencies.add("implementation", "com.google.guava:guava:31.0.1-jre")

        val config = project.configurations.getByName("implementation")
        config.resolve() // 전이 의존성 분석 수행

        assertTrue(config.resolvedConfiguration.firstLevelModuleDependencies.any {
            it.name == "guava"
        })
    }

    @Test
    fun `should circular reference detection`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("java")

        val config = project.configurations.create("customConfig")
        val dep1 = project.dependencies.create("com.example:libA:1.0.0")
        val dep2 = project.dependencies.create("com.example:libB:1.0.0")

        config.dependencies.add(dep1)
        config.dependencies.add(dep2)

        // 인위적으로 순환 참조 생성
        config.dependencies.add(dep1)

        val exception = assertThrows<IllegalStateException> {
            config.resolve()
        }
        assertTrue(exception.message?.contains("Circular dependency") == true)
    }

    @Test
    fun `should analysis of various dependency types`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("java-library")

        project.dependencies.apply {
            add("api", "com.fasterxml.jackson.core:jackson-databind:2.13.0")
            add("implementation", "org.slf4j:slf4j-api:1.7.30")
            add("compileOnly", "javax.annotation:javax.annotation-api:1.3.2")
            add("runtimeOnly", "ch.qos.logback:logback-classic:1.2.3")
        }

        val apiDeps = project.configurations.getByName("api").dependencies
        val implDeps = project.configurations.getByName("implementation").dependencies
        val compileOnlyDeps = project.configurations.getByName("compileOnly").dependencies
        val runtimeDeps = project.configurations.getByName("runtimeOnly").dependencies

        assertTrue(apiDeps.any { it.name == "jackson-databind" })
        assertTrue(implDeps.any { it.name == "slf4j-api" })
        assertTrue(compileOnlyDeps.any { it.name == "javax.annotation-api" })
        assertTrue(runtimeDeps.any { it.name == "logback-classic" })
    }

}