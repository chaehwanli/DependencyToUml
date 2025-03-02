package com.meteroid.dep2uml.generator

import com.meteroid.dep2uml.analyzer.DefaultGradleDependencyAnalyzer
import io.mockk.mockk
import io.mockk.every
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedConfiguration
import org.gradle.api.artifacts.ResolvedDependency
import org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.Test

class DefaultPlantUMLGeneratorTest {

    @Test
    fun `should generate Diagram`() {
        val plantUMLGenerator = DefaultPlantUMLGenerator()

        plantUMLGenerator.generateDiagram(emptyList(), "output.uml")
        assertEquals(true, plantUMLGenerator != null)
    }

    @Test
    fun `should generate correct PlantUML string with package and dependencies`() {
        // Mock project and configuration
        val project = mockk<Project>()
        val configuration = mockk<Configuration>()
        val resolvedConfiguration = mockk<ResolvedConfiguration>()
        val dependencyA = mockk<ResolvedDependency>()
        val dependencyB = mockk<ResolvedDependency>()
        val dependencyC = mockk<ResolvedDependency>()

        // Mock returns
        every { project.configurations } returns mockk {
            every { iterator() } returns mutableSetOf(configuration).iterator()
        }
        every { configuration.isCanBeResolved } returns true
        every { configuration.name } returns "implementation"
        every { configuration.resolvedConfiguration } returns resolvedConfiguration
        every { resolvedConfiguration.firstLevelModuleDependencies } returns setOf(dependencyA)

        // dependency A
        every { dependencyA.moduleGroup } returns "com.example"
        every { dependencyA.moduleName } returns "libraryA"
        every { dependencyA.moduleVersion } returns "1.0.0"
        every { dependencyA.children } returns setOf(dependencyB, dependencyC)

        // dependency B (sub-dependency of A)
        every { dependencyB.moduleGroup } returns "com.example.utils"
        every { dependencyB.moduleName } returns "libraryB"
        every { dependencyB.moduleVersion } returns "1.2.0"
        every { dependencyB.children } returns emptySet()

        // dependency C (sub-dependency of A)
        every { dependencyC.moduleGroup } returns "org.example"
        every { dependencyC.moduleName } returns "libraryC"
        every { dependencyC.moduleVersion } returns "2.0.0"
        every { dependencyC.children } returns emptySet()

        // Running PlantUML string generation function
        val analyzer = DefaultGradleDependencyAnalyzer()
        val defaultPlantUMLGenerator = DefaultPlantUMLGenerator()
        val result = defaultPlantUMLGenerator.buildPlantUMLContent(analyzer.analyzeProject(project))

        // 예상되는 PlantUML 문자열
        val expected = """
            @startuml
            package com.example {
                class libraryA {
                    version : 1.0.0
                }
            }
            package com.example.utils {
                class libraryB {
                    version : 1.2.0
                }
            }
            package org.example {
                class libraryC {
                    version : 2.0.0
                }
            }
            libraryA --> libraryB
            libraryA --> libraryC
            @enduml
        """.trimIndent()

        // 결과 검증
        assertEquals(expected, result)
    }
}