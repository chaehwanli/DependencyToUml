/*
 *
 * Copyright 2025 chaehwan.li@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.github.chaehwanli.dep2uml.generator

import io.github.chaehwanli.dep2uml.model.DependencyInfo
import java.nio.file.Paths
import kotlin.io.path.writeText

class DefaultStarUMLGenerator : StarUMLGenerator {
    override fun generatePackageDiagram(dependencies: List<io.github.chaehwanli.dep2uml.model.DependencyInfo>, outputPath: String) {
        val diagram = createStarUMLDiagram(dependencies)
        Paths.get(outputPath).writeText(diagram)
    }

    private fun createStarUMLDiagram(dependencies: List<io.github.chaehwanli.dep2uml.model.DependencyInfo>): String {
        // StarUML MDJ 형식의 기본 템플릿
        return """
        {
            "_type": "Project",
            "name": "DependencyDiagram",
            "ownElements": [
                {
                    "_type": "UMLModel",
                    "name": "Dependencies",
                    "ownElements": [
                        {
                            "_type": "UMLPackageDiagram",
                            "name": "Package Dependencies",
                            "ownElements": ${generatePackageElements(dependencies)}
                        }
                    ]
                }
            ]
        }
        """.trimIndent()
    }

    private fun generatePackageElements(dependencies: List<io.github.chaehwanli.dep2uml.model.DependencyInfo>): String {
        val packages = dependencies.map { dep ->
            """
            {
                "_type": "UMLPackage",
                "name": "${dep.group}:${dep.name}",
                "version": "${dep.version}"
            }
            """.trimIndent()
        }

        return packages.joinToString(",\n", prefix = "[", postfix = "]")
    }
}