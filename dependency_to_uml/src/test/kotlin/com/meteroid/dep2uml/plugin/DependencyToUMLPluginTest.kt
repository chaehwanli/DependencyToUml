/*
 *
 * Copyright 2025 Meteroid contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.meteroid.dep2uml.plugin

import org.junit.jupiter.api.Assertions.*
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

class DependencyToUMLPluginTest {

    @Test
    fun `should the plugin is applied properly`() {
        // Given: 테스트용 Gradle 프로젝트 생성
        val project = ProjectBuilder.builder().build()

        // When: 플러그인 적용
        project.pluginManager.apply("com.meteroid.dep2uml")

        // Then: 특정 Task가 등록되었는지 확인
        val task = project.tasks.findByName("GenerateDependencyToUMLTask")
        assertNotNull(task, "GenerateDependencyToUMLTask 태스크가 존재해야 합니다.")
        assertTrue(task is GenerateDependencyToUMLTask, "태스크 타입이 올바르지 않습니다.")
    }
}