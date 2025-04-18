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
import java.io.File

class DefaultPlantUMLGenerator : PlantUMLGenerator {
    override fun generateDiagram(dependencies: List<DependencyInfo>, outputPath: String) {
        val plantUMLContent = buildPlantUMLContent(dependencies)
        File(outputPath).writeText(plantUMLContent)
    }

    fun buildPlantUMLContent(dependencies: List<DependencyInfo>): String {
        val sb = StringBuilder()
        sb.append("@startuml\n")
        val packageMap = mutableMapOf<String, MutableSet<String>>()

        // Collecting package and class information
        dependencies.forEach { dep ->
            packageMap.computeIfAbsent(dep.group) { mutableSetOf() }.add(dep.name)
        }

        // Package and class definitions
        packageMap.forEach { (group, names) ->
            sb.append("package $group {\n")
            if (names.isEmpty()) {
                sb.append("        version : ${dependencies.find { it.group == group }?.version}\n")
            } else {
                names.forEach { name ->
                    sb.append("    class $name {\n")
                    sb.append("        version : ${dependencies.find { it.name == name }?.version}\n")
                    sb.append("    }\n")
                }
            }
            sb.append("}\n")
        }

        // 의존성 관계 추가
        sb.append("\n")
        val processedRelations = mutableSetOf<String>()
        dependencies.forEach { dep ->
            // Add relationships using actual dependency names
            // For example, let dep.dependencies be a list of names of other packages that the dependency depends on.
            dep.dependencies.forEach { dependencyName ->
                var sourceName = dep.group + "." + dep.name
                var targetName = dependencyName

                val sourceParts = sourceName.split(".")
                val targetParts = targetName.split(".")
                val isContained = sourceParts.zip(targetParts).all { (source, target) -> source == target }

                if (!isContained) {
                    val quotedSourceName = if (sourceName.contains("-") || sourceName.contains("_")) {
                        "\"$sourceName\""
                    } else {
                        sourceName
                    }
                    val quotedTargetName = if (targetName.contains("-") || targetName.contains("_")) {
                        "\"$targetName\""
                    } else {
                        targetName
                    }
                    val relation = "$quotedSourceName --> $quotedTargetName : ${dep.type}"
                    if (processedRelations.add(relation)) {
                        sb.append("$relation\n")
                    }
                }
            }
        }
        sb.append("@enduml")
        return sb.toString()
    }
}