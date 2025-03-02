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
        configurationName: String
    ): List<DependencyInfo> {
        val key = "${dependency.moduleGroup}:${dependency.moduleName}"
        if (key in processed) {
            return emptyList()
        }

        processed.add(key)
        val typeOfConfigurationName = DependencyConfiguration.fromConfigurationName(configurationName)

        val childDependencies = dependency.children.map { "${it.moduleGroup}.${it.moduleName}" }

        return listOf(
            DependencyInfo(
                group = dependency.moduleGroup,
                name = dependency.moduleName,
                version = dependency.moduleVersion,
                type = typeOfConfigurationName,
                dependencies = childDependencies
            )
        ) + dependency.children.flatMap { analyzeDependency(it, processed, configurationName) }
    }

}