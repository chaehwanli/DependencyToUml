package com.meteroid.dep2uml.generator

import com.meteroid.dep2uml.model.DependencyInfo

interface StarUMLGenerator {
    fun generatePackageDiagram(dependencies: List<DependencyInfo>, outputPath: String)
}