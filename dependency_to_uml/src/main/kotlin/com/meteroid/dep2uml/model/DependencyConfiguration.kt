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

package com.meteroid.dep2uml.model

enum class DependencyConfiguration(val type: DependencyType) {
    // Compilation
    IMPLEMENTATION(DependencyType.IMPLEMENTATION),
    API(DependencyType.API),
    COMPILE_ONLY(DependencyType.COMPILE_ONLY),
    COMPILE_ONLY_API(DependencyType.COMPILE_ONLY_API),

    // Runtime
    RUNTIME_ONLY(DependencyType.RUNTIME_ONLY),
    RUNTIME_CLASS_PATH(DependencyType.RUNTIME_CLASS_PATH),

    // Testing
    TEST_IMPLEMENTATION(DependencyType.TEST_IMPLEMENTATION),
    TEST_RUNTIME_ONLY(DependencyType.TEST_RUNTIME_ONLY),
    TEST_RUNTIME_CLASS_PATH(DependencyType.TEST_RUNTIME_CLASS_PATH),

    // Annotation
    ANNOTATION_PROCESSOR(DependencyType.ANNOTATION_PROCESSOR),
    KAPT(DependencyType.KAPT);

    companion object {
        fun fromConfigurationName(name: String): DependencyType {
            return when {
                name.contains("api", ignoreCase = true) -> API.type
                name.contains("implementation", ignoreCase = true) -> IMPLEMENTATION.type
                name.contains("compileOnly", ignoreCase = true) -> COMPILE_ONLY.type
                name.contains("compileOnlyApi", ignoreCase = true) -> COMPILE_ONLY_API.type
                name.contains("runtimeOnly", ignoreCase = true) -> RUNTIME_ONLY.type
                name.contains("runtimeClassPath", ignoreCase = true) -> RUNTIME_CLASS_PATH.type
                name.contains("testImplementation", ignoreCase = true) -> TEST_IMPLEMENTATION.type
                name.contains("testRuntimeOnly", ignoreCase = true) -> TEST_RUNTIME_ONLY.type
                name.contains(
                    "testRuntimeClassPath",
                    ignoreCase = true
                ) -> TEST_RUNTIME_CLASS_PATH.type

                name.contains("annotationProcessor", ignoreCase = true) -> ANNOTATION_PROCESSOR.type
                name.contains("kapt", ignoreCase = true) -> KAPT.type
                else -> throw IllegalArgumentException("Unknown configuration: $name")
            }
        }
    }
}