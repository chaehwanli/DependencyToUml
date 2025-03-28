/*
 *
 * Copyright ${YEAR} chaehwan.li@gmail.com
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

package io.github.chaehwanli.dep2uml.plugin

import org.gradle.api.Project
import org.junit.jupiter.api.Assertions.*
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

class GenerateDependencyToUMLTaskTest {

    @Test
    fun `should be executed GenerateDependencyToUML task`() {
        // Given: 테스트용 Gradle 프로젝트 생성
        val project: Project = ProjectBuilder.builder().build()
        val task = project.tasks.create(
            "GenerateDependencyToUMLTask",
            GenerateDependencyToUMLTask::class.java
        )

        // When: Task 실행
        task.actions.forEach { it.execute(task) }

        // Then: 정상 실행 여부 확인
        assertNotNull(task, "GenerateDependencyToUMLTask 태스크가 생성되어야 합니다.")
    }
}