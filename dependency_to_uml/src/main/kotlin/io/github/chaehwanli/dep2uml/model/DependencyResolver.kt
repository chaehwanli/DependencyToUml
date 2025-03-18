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
package io.github.chaehwanli.dep2uml.model

enum class DependencyType(val type: String) {
    API("api"),
    IMPLEMENTATION("implementation"),
    COMPILE_ONLY("compileOnly"),
    COMPILE_ONLY_API("compileOnlyApi"),
    RUNTIME_ONLY("runtimeOnly"),
    RUNTIME_CLASS_PATH("runtimeClassPath"),
    TEST_IMPLEMENTATION("testImplementation"),
    TEST_RUNTIME_ONLY("testRuntimeOnly"),
    TEST_RUNTIME_CLASS_PATH("testRuntimeClassPath"),
    ANNOTATION_PROCESSOR("annotationProcessor"),
    KAPT("kapt");
}

class DependencyResolver {

    companion object {
        private val MAPPINGS = listOf(
            "api" to DependencyType.API,
            "implementation" to DependencyType.IMPLEMENTATION,
            "compileOnly" to DependencyType.COMPILE_ONLY,
            "compileOnlyApi" to DependencyType.COMPILE_ONLY_API,
            "runtimeOnly" to DependencyType.RUNTIME_ONLY,
            "runtimeClassPath" to DependencyType.RUNTIME_CLASS_PATH,
            "testImplementation" to DependencyType.TEST_IMPLEMENTATION,
            "testRuntimeOnly" to DependencyType.TEST_RUNTIME_ONLY,
            "testRuntimeClassPath" to DependencyType.TEST_RUNTIME_CLASS_PATH,
            "annotationProcessor" to DependencyType.ANNOTATION_PROCESSOR,
            "kapt" to DependencyType.KAPT
        )

        fun resolve(configurationName: String): DependencyType {
            return MAPPINGS.firstOrNull {(keyword, _) ->
                configurationName.equals(keyword, ignoreCase = true)/* || configurationName.endsWith(keyword, ignoreCase = true)*/
            }?.second
                ?: throw IllegalArgumentException("Unknown configuration: $configurationName")
        }
        fun getKeywords(): List<String> {
            return MAPPINGS.map { it.first }
        }
    }
}