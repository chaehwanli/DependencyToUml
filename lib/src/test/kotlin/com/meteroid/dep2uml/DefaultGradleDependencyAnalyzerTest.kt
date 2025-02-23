package com.meteroid.dep2uml.analyzer

import io.mockk.every
import io.mockk.mockk
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedConfiguration
import org.gradle.api.artifacts.ResolvedDependency
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultGradleDependencyAnalyzerTest {
    
    @Test
    fun `should analyze project dependencies`() {
        // Given
        val analyzer = DefaultGradleDependencyAnalyzer()
        val project = mockk<Project>()
        val configuration = mockk<Configuration>()
        val resolvedConfiguration = mockk<ResolvedConfiguration>()
        val resolvedDependency = mockk<ResolvedDependency>()
        
        // Mock setup
        every { project.configurations } returns setOf(configuration)
        every { configuration.isCanBeResolved } returns true
        every { configuration.resolvedConfiguration } returns resolvedConfiguration
        every { resolvedConfiguration.firstLevelModuleDependencies } returns setOf(resolvedDependency)
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
}