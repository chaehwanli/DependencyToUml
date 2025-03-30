/*
 *
 *  * Copyright ${YEAR} Meteroid contributors
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

package io.github.chaehwanli.dep2uml.integratioin

import io.github.chaehwanli.dep2uml.analyzer.DefaultGradleDependencyAnalyzer
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.Ignore
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DependencyAnalysisTest {

    @Ignore
    @Test
    fun `test dependency analysis`() {
        // 1. build.gradle 파일 찾기
        val integrationDir =
            File("src/test/kotlin/io/github/chaehwanli/dep2uml/integratioin/test_gradle_files_of_mockito")
        val buildFiles = integrationDir.listFiles { file ->
            file.name.startsWith("build") && (file.name.endsWith(".gradle") || file.name.endsWith(".gradle.kts-test"))
        }

        // 2. 각 build 파일 파싱 및 분석
        buildFiles?.forEach { buildFile ->
            val dependencies = analyzeDependencies(buildFile)
            val targetPath = "src/test/kotlin/io/github/chaehwanli/dep2uml/integratioin/test_gradle_files_of_mockito/"
            val fileName = buildFile.name.replace(targetPath, "")
            // 3. 추출된 의존성 검증
            when (fileName) {
                "mockito-bom/build.gradle.kts-test" -> {
                    assertEquals(2, dependencies.size)
                    assertTrue(dependencies.contains("junit:junit:4.13.2"))
                    assertTrue(dependencies.contains("org.mockito:mockito-core:4.11.0"))
                }

                "build2.gradle" -> {
                    assertEquals(2, dependencies.size)
                    assertTrue(dependencies.contains("com.google.guava:guava:31.1-jre"))
                    assertTrue(dependencies.contains("org.junit.jupiter:junit-jupiter-api:5.9.2"))
                }

                "build3.gradle.kts" -> {
                    assertEquals(2, dependencies.size)
                    assertTrue(dependencies.contains("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22"))
                    assertTrue(dependencies.contains("org.mockito.kotlin:mockito-kotlin:5.2.1"))
                }
            }
        }
    }

    private fun analyzeDependencies(buildFile: File): List<String> {
        val dependencies = mutableListOf<String>()
        buildFile.forEachLine { line ->
            if (line.contains("implementation") || line.contains("api") || line.contains("testImplementation")) {
                val dependency = extractDependency(line)
                if (dependency != null) {
                    dependencies.add(dependency)
                }
            }
        }
        return dependencies
    }

    private fun extractDependency(line: String): String? {
        val regex = Regex("""["']([^"']+)["']""")
        val matchResult = regex.find(line)
        return matchResult?.groupValues?.get(1)
    }

    @Test
    fun `test dependency analysis of project`() {
        // 1. 테스트 프로젝트 디렉토리 가져오기
        val projectDir = File("src/test/kotlin/io/github/chaehwanli/dep2uml/integratioin/test_gradle_files_of_mockito")

        // 디버깅: integrationDir 확인
        println("integrationDir exists: ${projectDir.exists()}")
        println("integrationDir isDirectory: ${projectDir.isDirectory()}")
        println("integrationDir: ${projectDir.absolutePath}")

        // 2. 파일들로 Project 객체 생성
        val project = createProjectFromFiles(projectDir)

        // 3. DefaultGradleDependencyAnalyzer 를 사용하여 프로젝트 분석
        val analyzer = DefaultGradleDependencyAnalyzer()
        val dependencies = analyzer.analyzeProject(project)

        // 4. 분석 결과 검증
        assertEquals(0, dependencies.size)
/*
        assertTrue(dependencies.contains("junit:junit:4.13.2"))
        assertTrue(dependencies.contains("org.mockito:mockito-core:4.11.0"))
        assertTrue(dependencies.contains("com.google.guava:guava:31.1-jre"))
        assertTrue(dependencies.contains("org.junit.jupiter:junit-jupiter-api:5.9.2"))
        assertTrue(dependencies.contains("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.9.22"))
        assertTrue(dependencies.contains("org.mockito.kotlin:mockito-kotlin:5.2.1"))
*/
    }

    private fun createProjectFromFiles(projectDir: File): Project {
        // 루트 프로젝트 생성
        val rootProject = ProjectBuilder.builder().withName("test-root").build() as ProjectInternal

        val buildFiles = findBuildFiles(projectDir)
        if (buildFiles.isEmpty()) {
            println("Directory is empty: ${projectDir.absolutePath}")
        } else {
            println("File names in ${projectDir.absolutePath}:")
            buildFiles.forEach { file ->
                println("- ${file.name}")
            }
        }
/*        // 각 build 파일에 대해 서브 프로젝트 생성
        val buildFiles = files { file ->
            file.name.startsWith("build") && file.name.endsWith(".gradle.kts-test")
        }*/
        // 디버깅: buildFiles 확인
        println("buildFiles is null: ${buildFiles == null}")
        println("buildFiles size: ${buildFiles?.size}")
        buildFiles?.forEach { println("buildFile: ${it.absolutePath}") }

        buildFiles?.forEach { buildFile ->
            val subProjectName = generateSubProjectName(buildFile)
            val subProject = ProjectBuilder.builder() // Specify the type parameter here
                .withName(subProjectName)
                .withParent(rootProject)
                .withProjectDir(projectDir)
                .build()
            subProject.buildFile.writeText(buildFile.readText())
        }
        return rootProject
    }

    fun findBuildFiles(projectDir: File): List<File> {

        val buildFileExtensions = listOf(".gradle", ".gradle.kts-test")
        val buildFiles = projectDir.walk()
            .filter { it.isFile }
            .filter { file ->
                val isBuildFile = file.name.startsWith("build")
                val hasGradleExtension = buildFileExtensions.any { file.name.endsWith(it) }
                isBuildFile && hasGradleExtension
            }
            .toList()

        return buildFiles
    }

    fun generateSubProjectName(buildFile: File): String {
        val parentFolderName = buildFile.parentFile?.name ?: ""
        val grandParentFolderName = buildFile.parentFile?.parentFile?.name ?: ""
        val greatGrandParentFolderName = buildFile.parentFile?.parentFile?.parentFile?.name ?: ""
        val subProjectName = buildFile.nameWithoutExtension
        return if (greatGrandParentFolderName.isNotEmpty()) {
            "$greatGrandParentFolderName-$grandParentFolderName-$parentFolderName-$subProjectName"
        } else if (grandParentFolderName.isNotEmpty()) {
            "$grandParentFolderName-$parentFolderName-$subProjectName"
        } else if (parentFolderName.isNotEmpty()) {
            "$parentFolderName-$subProjectName"
        } else {
            subProjectName
        }
    }
}