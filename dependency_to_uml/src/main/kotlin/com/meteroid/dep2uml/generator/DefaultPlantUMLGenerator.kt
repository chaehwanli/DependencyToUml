package com.meteroid.dep2uml.generator

import com.meteroid.dep2uml.model.DependencyInfo
import java.io.File

class DefaultPlantUMLGenerator : PlantUMLGenerator {
    override fun generateDiagram(dependencies: List<DependencyInfo>, outputPath: String) {
        val plantUMLContent = buildPlantUMLContent(dependencies)
        File(outputPath).writeText(plantUMLContent)
    }

    fun buildPlantUMLContent(dependencies: List<DependencyInfo>): String {
        val sb = StringBuilder()
        sb.append("@startuml\n")
        val packageMap = mutableMapOf<String, MutableList<String>>()

        // Collecting package and class information
        dependencies.forEach { dep ->
            packageMap.computeIfAbsent(dep.group) { mutableListOf() }.add(dep.name)
        }

        // Package and class definitions
        packageMap.forEach { (group, names) ->
            sb.append("package $group {\n")
            names.forEach { name ->
                sb.append("    class $name {\n")
                sb.append("        version : ${dependencies.find { it.name == name }?.version}\n")
                sb.append("    }\n")
            }
            sb.append("}\n")
        }

        // 의존성 관계 추가
        dependencies.forEach { dep ->
            // Add relationships using actual dependency names
            // For example, let dep.dependencies be a list of names of other packages that the dependency depends on.
            dep.dependencies.forEach { dependencyName ->
                sb.append("${dep.group}.${dep.name} --> $dependencyName\n")
            }
        }

        sb.append("@enduml")
        return sb.toString()
    }
}