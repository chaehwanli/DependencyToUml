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

        dependencies.forEach { dep ->
            packageMap.computeIfAbsent(dep.group) { mutableListOf() }.add(dep.name)
        }

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
            // 예시: 의존성 관계를 추가하는 로직
            sb.append("${dep.name} --> ${dep.name}\n") // 실제 의존성 이름을 사용
        }

        sb.append("@enduml")
        return sb.toString()
    }
}