package com.meteroid.dep2uml.analyzer

import io.mockk.every
import io.mockk.mockk
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.ResolvedConfiguration
import org.gradle.api.artifacts.ResolvedDependency
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

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
        every { resolvedDependency.moduleGroup } returns "org.apache.commons"
        every { resolvedDependency.moduleName } returns "commons-lang3"
        every { resolvedDependency.moduleVersion } returns "3.12.0"
        every { resolvedDependency.children } returns setOf()

        // When
        val result = analyzer.analyzeProject(project)

        // Then
        assertEquals(1, result.size)
        with(result.first()) {
            assertEquals("org.apache.commons", group)
            assertEquals("commons-lang3", name)
            assertEquals("3.12.0", version)
        }
    }

    @Test
    fun `should transition dependency analysis`() {
        // Given
        val analyzer = DefaultGradleDependencyAnalyzer()
        val project = mockk<Project>()
        val configurations = mockk<ConfigurationContainer>()
        val configuration = mockk<Configuration>()
        val resolvedConfiguration = mockk<ResolvedConfiguration>()
        val parentDependency = mockk<ResolvedDependency>()
        val transitiveDependency = mockk<ResolvedDependency>()

        // Mock setup
        every { project.configurations } returns configurations
        every { configurations.iterator() } returns mutableSetOf(configuration).iterator()
        every { configuration.isCanBeResolved } returns true
        every { configuration.resolvedConfiguration } returns resolvedConfiguration
        every { resolvedConfiguration.firstLevelModuleDependencies } returns setOf(parentDependency)

        // set parent dependency (Guava)
        every { parentDependency.moduleGroup } returns "com.google.guava"
        every { parentDependency.moduleName } returns "guava"
        every { parentDependency.moduleVersion } returns "31.0.1-jre"
        every { parentDependency.children } returns setOf(transitiveDependency)

        // set transition dependency (ListenableFuture)
        every { transitiveDependency.moduleGroup } returns "com.google.guava"
        every { transitiveDependency.moduleName } returns "listenablefuture"
        every { transitiveDependency.moduleVersion } returns "9999.0-empty-to-avoid-conflict"
        every { transitiveDependency.children } returns setOf()

        // When
        val result = analyzer.analyzeProject(project)

        // Then
        assertEquals(2, result.size)
        with(result.first()) {
            assertEquals("com.google.guava", group)
            assertEquals("guava", name)
            assertEquals("31.0.1-jre", version)
        }
        with(result.last()) {
            assertEquals("com.google.guava", group)
            assertEquals("listenablefuture", name)
            assertEquals("9999.0-empty-to-avoid-conflict", version)
        }
    }

    @Test
    fun `should circular reference detection`() {
        // Given
        val analyzer = DefaultGradleDependencyAnalyzer()
        val project = mockk<Project>()
        val configurations = mockk<ConfigurationContainer>()
        val configuration = mockk<Configuration>()
        val resolvedConfiguration = mockk<ResolvedConfiguration>()
        val dependencyA = mockk<ResolvedDependency>()
        val dependencyB = mockk<ResolvedDependency>()

        // Mock setup
        every { project.configurations } returns configurations
        every { configurations.iterator() } returns mutableSetOf(configuration).iterator()
        every { configuration.isCanBeResolved } returns true
        every { configuration.resolvedConfiguration } returns resolvedConfiguration
        every { resolvedConfiguration.firstLevelModuleDependencies } returns setOf(
            dependencyA,
            dependencyB
        )

        // set circular reference
        every { dependencyA.moduleGroup } returns "com.example"
        every { dependencyA.moduleName } returns "libA"
        every { dependencyA.moduleVersion } returns "1.0.0"
        every { dependencyA.children } returns setOf(dependencyB)

        every { dependencyB.moduleGroup } returns "com.example"
        every { dependencyB.moduleName } returns "libB"
        every { dependencyB.moduleVersion } returns "1.0.0"
        every { dependencyB.children } returns setOf(dependencyA) // circular reference occurs

        // When
        val result = analyzer.analyzeProject(project)

        // Then
        assertEquals(2, result.size)
        with(result.first()) {
            assertEquals("com.example", group)
            assertEquals("libA", name)
            assertEquals("1.0.0", version)
        }
        with(result.last()) {
            assertEquals("com.example", group)
            assertEquals("libB", name)
            assertEquals("1.0.0", version)
        }
    }

    @Test
    fun `should analysis of various dependency types`() {
        // Given
        val analyzer = DefaultGradleDependencyAnalyzer()
        val project = mockk<Project>()
        val configurations = mockk<ConfigurationContainer>()
        val apiConfig = mockk<Configuration>()
        val implementationConfig = mockk<Configuration>()
        val compileOnlyConfig = mockk<Configuration>()
        val runtimeOnlyConfig = mockk<Configuration>()

        // Mock setup with various dependency
        every { project.configurations } returns configurations
        every { configurations.getByName("api") } returns apiConfig
        every { configurations.getByName("implementation") } returns implementationConfig
        every { configurations.getByName("compileOnly") } returns compileOnlyConfig
        every { configurations.getByName("runtimeOnly") } returns runtimeOnlyConfig

        val apiDependency = mockk<ResolvedDependency>()
        val implDependency = mockk<ResolvedDependency>()
        val compileOnlyDependency = mockk<ResolvedDependency>()
        val runtimeDependency = mockk<ResolvedDependency>()

        every { apiConfig.resolvedConfiguration.firstLevelModuleDependencies } returns setOf(
            apiDependency
        )
        every { implementationConfig.resolvedConfiguration.firstLevelModuleDependencies } returns setOf(
            implDependency
        )
        every { compileOnlyConfig.resolvedConfiguration.firstLevelModuleDependencies } returns setOf(
            compileOnlyDependency
        )
        every { runtimeOnlyConfig.resolvedConfiguration.firstLevelModuleDependencies } returns setOf(
            runtimeDependency
        )

        every { apiDependency.moduleName } returns "jackson-databind"
     
        every { implDependency.moduleName } returns "slf4j-api"
     
        every { compileOnlyDependency.moduleName } returns "javax.annotation-api"
     
        every { runtimeDependency.moduleName } returns "logback-classic"

        // When
        val result = analyzer.analyzeProject(project)

        // Then
        assertEquals(4, result.size)
        with(result.first()) {
            assertEquals("com.fasterxml.jackson.core", group)
            assertEquals("jackson-databind", name)
            assertEquals("2.15.2", version)
        }
        with(result.last()) {
            assertEquals("org.slf4j", group)
            assertEquals("slf4j-api", name)
            assertEquals("1.7.36", version)
        }
    }
}