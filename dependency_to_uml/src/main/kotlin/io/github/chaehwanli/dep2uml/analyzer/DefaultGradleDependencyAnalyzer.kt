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

package io.github.chaehwanli.dep2uml.analyzer

import org.slf4j.LoggerFactory
import io.github.chaehwanli.dep2uml.model.DependencyInfo
import io.github.chaehwanli.dep2uml.model.DependencyResolver
import io.github.chaehwanli.dep2uml.model.DependencyType
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvedDependency

class DefaultGradleDependencyAnalyzer : GradleDependencyAnalyzer {
   private val logger = LoggerFactory.getLogger(DefaultGradleDependencyAnalyzer::class.java)

    override fun analyzeProject(project: Project): List<DependencyInfo> {
        val keywords = DependencyResolver.getKeywords()
        return project.configurations
            .filter { it ->
                it.isCanBeResolved && keywords.any { keyword -> it.name.equals(keyword, ignoreCase = true) }
            }
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
        processedKeys: MutableSet<String> = mutableSetOf(),
        configurationName: String,
    ): List<DependencyInfo> {

        // group name과 module name 로그 출력
        logger.info("#1 Group Name: ${dependency.moduleGroup}, Module Name: ${dependency.moduleName}")

        val moduleName = if (dependency.moduleGroup.equals(dependency.moduleName, ignoreCase=false)) {
            // group name이 module name의 일부인 경우에만 제거
            dependency.moduleName.split(".").last()
        } else {
            dependency.moduleName
        }
        logger.info("#2 Group Name: ${dependency.moduleGroup}, Module Name: ${moduleName}")
        
        val key = "${dependency.moduleGroup}:${moduleName}"
        if (processedKeys.contains(key)) {
            return emptyList()
        }

        processedKeys.add(key)
        val typeOfConfigurationName =
            DependencyResolver.resolve(configurationName)

        val childDependencies = dependency.children.map { it.moduleGroup }.toSet()

        return listOf(
            DependencyInfo(
                group = dependency.moduleGroup,
                name = moduleName,  // name = dependency.moduleName,
                version = dependency.moduleVersion,
                type = typeOfConfigurationName,
                dependencies = childDependencies
            )
        ) + dependency.children.flatMap { analyzeDependency(it, processedKeys, configurationName) }
    }

}