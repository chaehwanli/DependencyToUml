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

/*
 by Gradle 8.x
 */
enum class DependencyType {
    // Compilation
    IMPLEMENTATION,
    API,
    COMPILE_ONLY,
    COMPILE_ONLY_API,

    // Runtime
    RUNTIME_ONLY,
    RUNTIME_CLASS_PATH,

    // Testing
    TEST_IMPLEMENTATION,
    TEST_RUNTIME_ONLY,
    TEST_RUNTIME_CLASS_PATH,

    // Annotation
    ANNOTATION_PROCESSOR,
    KAPT
}