/*
 *
 *  * Copyright 2025 Meteroid contributors
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.meteroid.dep2uml.generator

import com.meteroid.dep2uml.analyzer.DefaultGradleDependencyAnalyzer

import java.io.File

import io.mockk.mockk
import io.mockk.every
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedConfiguration
import org.gradle.api.artifacts.ResolvedDependency
import org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.Test

private const val testOutputUmlFile = "test_output.uml"

class DefaultPlantUMLGeneratorTest {

    @Test
    fun `should generate Diagram`() {
        // prepare mockk Project class
        val project = getMockkProject()

        // Running PlantUML string generation function
        val analyzer = DefaultGradleDependencyAnalyzer()
        val defaultPlantUMLGenerator = DefaultPlantUMLGenerator()
        defaultPlantUMLGenerator.generateDiagram(
            analyzer.analyzeProject(project),
            testOutputUmlFile
        )

        assertEquals(getExpectedPlantUml(), testOutputUmlFile.readTextFile())
    }

    @Test
    fun `should generate correct PlantUML string with package and dependencies`() {
        // prepare mockk Project class
        val project = getMockkProject()

        // Running PlantUML string generation function
        val analyzer = DefaultGradleDependencyAnalyzer()
        val defaultPlantUMLGenerator = DefaultPlantUMLGenerator()
        val result = defaultPlantUMLGenerator.buildPlantUMLContent(analyzer.analyzeProject(project))

        // verify with expected PlantUML string
        assertEquals(getExpectedPlantUml(), result)
    }

    private fun getExpectedPlantUml(): String {
        // Expected PlantUML string
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
                com.example.libraryA --> com.example.utils.libraryB
                com.example.libraryA --> org.example.libraryC
                @enduml
            """.trimIndent()
        return expected
    }

    private fun getMockkProject(): Project {
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
        return project
    }

    private fun String.readTextFile(): String {
        return File(this).readText()
    }
}