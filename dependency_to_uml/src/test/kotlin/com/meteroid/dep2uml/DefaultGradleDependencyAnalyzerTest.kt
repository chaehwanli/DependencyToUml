/*
 *
 * Copyright 2025 Meteroid contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.meteroid.dep2uml.analyzer

import com.meteroid.dep2uml.model.DependencyType
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
        every { configuration.name } returns "implementation"
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
        every { configuration.name } returns "implementation"
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
        every { configuration.name } returns "implementation"
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
        every { configuration.name } returns "implementation"
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
        assertEquals(4, result.size)
        with(result.first()) {
            assertEquals("com.example", group)
            assertEquals("libA", name)
            assertEquals("1.0.0", version)
        }
        with(result.last()) {
            assertEquals("com.example", group)
            assertEquals("libA", name)
            assertEquals("1.0.0", version)
        }
        /*        result.forEach { dependencyInfo ->
                    assertEquals("com.example", dependencyInfo.group)
                    assertEquals("libA", dependencyInfo.name)
                    assertEquals("1.0.0", dependencyInfo.version)

                }*/
        with(result[0]) {
            assertEquals("com.example", group)
            assertEquals("libA", name)
            assertEquals("1.0.0", version)
        }
        with(result[1]) {
            assertEquals("com.example", group)
            assertEquals("libB", name)
            assertEquals("1.0.0", version)
        }
        with(result[2]) {
            assertEquals("com.example", group)
            assertEquals("libB", name)
            assertEquals("1.0.0", version)
        }
        with(result[3]) {
            assertEquals("com.example", group)
            assertEquals("libA", name)
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
        every { configurations.iterator() } returns mutableSetOf(
            apiConfig,
            implementationConfig,
            compileOnlyConfig,
            runtimeOnlyConfig
        ).iterator()
        every { configurations.getByName("api") } returns apiConfig
        every { configurations.getByName("implementation") } returns implementationConfig
        every { configurations.getByName("compileOnly") } returns compileOnlyConfig
        every { configurations.getByName("runtimeOnly") } returns runtimeOnlyConfig

        val apiResolvedConfiguration = mockk<ResolvedConfiguration>()
        val implementationResolvedConfiguration = mockk<ResolvedConfiguration>()
        val compileOnlyResolvedConfiguration = mockk<ResolvedConfiguration>()
        val runtimeOnlyResolvedConfiguration = mockk<ResolvedConfiguration>()

        val apiDependency = mockk<ResolvedDependency>()
        val implDependency = mockk<ResolvedDependency>()
        val compileOnlyDependency = mockk<ResolvedDependency>()
        val runtimeOnlyDependency = mockk<ResolvedDependency>()

        // set dependency
        every { apiConfig.resolvedConfiguration } returns apiResolvedConfiguration
        every { apiConfig.isCanBeResolved } returns true
        every { apiConfig.name } returns "api"
        every { apiResolvedConfiguration.firstLevelModuleDependencies } returns setOf(apiDependency)
        every { apiDependency.moduleName } returns "jackson-databind"
        every { apiDependency.moduleGroup } returns "com.fasterxml.jackson.core"
        every { apiDependency.moduleVersion } returns "2.15.2"
        every { apiDependency.children } returns setOf()

        every { implementationConfig.resolvedConfiguration } returns implementationResolvedConfiguration
        every { implementationConfig.isCanBeResolved } returns true
        every { implementationConfig.name } returns "implementation"
        every { implementationResolvedConfiguration.firstLevelModuleDependencies } returns setOf(
            implDependency
        )
        every { implDependency.moduleName } returns "slf4j-api"
        every { implDependency.moduleGroup } returns "org.slf4j"
        every { implDependency.moduleVersion } returns "1.7.36"
        every { implDependency.children } returns setOf()

        every { compileOnlyConfig.resolvedConfiguration } returns compileOnlyResolvedConfiguration
        every { compileOnlyConfig.isCanBeResolved } returns true
        every { compileOnlyConfig.name } returns "compileOnly"
        every { compileOnlyResolvedConfiguration.firstLevelModuleDependencies } returns setOf(
            compileOnlyDependency
        )
        every { compileOnlyDependency.moduleName } returns "javax.annotation-api"
        every { compileOnlyDependency.moduleGroup } returns "org.javax"
        every { compileOnlyDependency.moduleVersion } returns "1.1.1"
        every { compileOnlyDependency.children } returns setOf()

        every { runtimeOnlyConfig.resolvedConfiguration } returns runtimeOnlyResolvedConfiguration
        every { runtimeOnlyConfig.isCanBeResolved } returns true
        every { runtimeOnlyConfig.name } returns "runtimeOnly"
        every { runtimeOnlyResolvedConfiguration.firstLevelModuleDependencies } returns setOf(
            runtimeOnlyDependency
        )
        every { runtimeOnlyDependency.moduleName } returns "logback-classic"
        every { runtimeOnlyDependency.moduleGroup } returns "org.logback"
        every { runtimeOnlyDependency.moduleVersion } returns "1.1.2"
        every { runtimeOnlyDependency.children } returns setOf()

        // When
        val result = analyzer.analyzeProject(project)

        // Then
        assertEquals(4, result.size)
        with(result.first()) {
            assertEquals("com.fasterxml.jackson.core", group)
            assertEquals("jackson-databind", name)
            assertEquals("2.15.2", version)
            assertEquals(DependencyType.API, type)
        }
        with(result.last()) {
            assertEquals("org.logback", group)
            assertEquals("logback-classic", name)
            assertEquals("1.1.2", version)
            assertEquals(DependencyType.RUNTIME_ONLY, type)
        }
    }
}