package com.meteroid.dep2uml.generator

import com.meteroid.dep2uml.model.DependencyInfo

interface PlantUMLGenerator {
    fun generateDiagram(dependencies: List<DependencyInfo>, outputPath: String)
}