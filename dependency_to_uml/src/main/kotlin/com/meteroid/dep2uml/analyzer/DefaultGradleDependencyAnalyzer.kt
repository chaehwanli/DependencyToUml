package com.meteroid.dep2uml.analyzer

import com.meteroid.dep2uml.model.DependencyConfiguration
import com.meteroid.dep2uml.model.DependencyInfo
import com.meteroid.dep2uml.model.DependencyType
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvedDependency

class DefaultGradleDependencyAnalyzer : GradleDependencyAnalyzer {

    override fun analyzeProject(project: Project): List<DependencyInfo> {
        return project.configurations
            .filter { it.isCanBeResolved }
            .flatMap { configuration ->
                configuration.resolvedConfiguration
                    .firstLevelModuleDependencies
                    .flatMap { analyzeDependency(it, mutableSetOf(), configuration.name) }
            }
    }

    private fun analyzeDependency(
        dependency: ResolvedDependency,
        processed: MutableSet<String> = mutableSetOf(),
    ): List<DependencyInfo> {
        val key = "${dependency.moduleGroup}:${dependency.moduleName}"
        if (key in processed) {
            return emptyList()
        }

        processed.add(key)

        return listOf(
            DependencyInfo(
                group = dependency.moduleGroup,
                name = dependency.moduleName,
                version = dependency.moduleVersion,
                type = DependencyType.IMPLEMENTATION
            )
        ) + dependency.children.flatMap { analyzeDependency(it, processed) }
    }

    private fun analyzeDependency(
        dependency: ResolvedDependency,
        processed: MutableSet<String> = mutableSetOf(),
        configurationName: String,
    ): List<DependencyInfo> {
        val key = "${dependency.moduleGroup}:${dependency.moduleName}"
        if (key in processed) {
            return emptyList()
        }

        processed.add(key)
        val typeOfConfigurationName =
            DependencyConfiguration.fromConfigurationName(configurationName)

        return listOf(
            DependencyInfo(
                group = dependency.moduleGroup,
                name = dependency.moduleName,
                version = dependency.moduleVersion,
                type = typeOfConfigurationName
            )
        ) + dependency.children.flatMap { analyzeDependency(it, processed, configurationName) }
    }

}