/*
 *
 * Copyright 2025 Meteroid contributors
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

package com.meteroid.dep2uml.plugin

import com.meteroid.dep2uml.analyzer.DefaultGradleDependencyAnalyzer
import com.meteroid.dep2uml.generator.DefaultPlantUMLGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GenerateDependencyToUMLTask : DefaultTask() {
    @TaskAction
    fun generateDiagram() {
        // 의존성 분석 및 다이어그램 생성 로직
        val project = project
        val analyzer = DefaultGradleDependencyAnalyzer()
        val generator = DefaultPlantUMLGenerator()

        // 다이어그램 생성
        generator.generateDiagram(analyzer.analyzeProject(project), "${project.buildDir}/dependency-diagram.puml")
    }
}