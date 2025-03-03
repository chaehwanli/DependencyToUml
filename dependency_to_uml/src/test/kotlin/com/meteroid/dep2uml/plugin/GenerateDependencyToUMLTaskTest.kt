package com.meteroid.dep2uml.plugin

import org.gradle.api.Project
import org.junit.jupiter.api.Assertions.*
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

class GenerateDependencyToUMLTaskTest {

    @Test
    fun `should be executed GenerateDependencyToUML task`() {
        // Given: 테스트용 Gradle 프로젝트 생성
        val project: Project = ProjectBuilder.builder().build()
        val task = project.tasks.create("GenerateDependencyToUMLTask", GenerateDependencyToUMLTask::class.java)

        // When: Task 실행
        task.actions.forEach { it.execute(task) }

        // Then: 정상 실행 여부 확인
        assertNotNull(task, "generateDependencyUML 태스크가 생성되어야 합니다.")
    }
}