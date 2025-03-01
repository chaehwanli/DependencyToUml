package com.meteroid.dep2uml.generator

import com.meteroid.dep2uml.model.DependencyInfo
import java.nio.file.Paths
import kotlin.io.path.writeText

class DefaultStarUMLGenerator : StarUMLGenerator {
    override fun generatePackageDiagram(dependencies: List<DependencyInfo>, outputPath: String) {
        val diagram = createStarUMLDiagram(dependencies)
        Paths.get(outputPath).writeText(diagram)
    }

    private fun createStarUMLDiagram(dependencies: List<DependencyInfo>): String {
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

    private fun generatePackageElements(dependencies: List<DependencyInfo>): String {
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