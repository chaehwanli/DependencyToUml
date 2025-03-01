package com.meteroid.dep2uml.analyzer

import com.meteroid.dep2uml.model.DependencyInfo
import org.gradle.api.Project

interface GradleDependencyAnalyzer {
    fun analyzeProject(project: Project): List<DependencyInfo>
}