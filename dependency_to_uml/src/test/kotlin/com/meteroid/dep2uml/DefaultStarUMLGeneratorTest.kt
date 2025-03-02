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

package com.meteroid.dep2uml

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.test.assertTrue
import kotlin.io.path.exists
import com.meteroid.dep2uml.model.DependencyInfo
import com.meteroid.dep2uml.model.DependencyType
import com.meteroid.dep2uml.generator.DefaultStarUMLGenerator

class DefaultStarUMLGeneratorTest {

    @Test
    fun `should generate StarUML diagram file`(@TempDir tempDir: Path) {
        // Given
        val generator = DefaultStarUMLGenerator()
        val dependencies = listOf(
            DependencyInfo(
                group = "org.springframework",
                name = "spring-core",
                version = "5.3.0",
                type = DependencyType.IMPLEMENTATION
            )
        )
        val outputPath = tempDir.resolve("test-diagram.mdj")

        // When
        generator.generatePackageDiagram(dependencies, outputPath.toString())

        // Then
        assertTrue(outputPath.exists())
        // TODO: Add more specific assertions about file content
    }
}