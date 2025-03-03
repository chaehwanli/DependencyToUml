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

import org.gradle.api.Plugin
import org.gradle.api.Project

class DependencyToUMLPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // 플러그인 설정
        //target.extensions.create("DependencyToUML", DependencyToUMLExtension::class.java)

        // 태스크 등록
        target.tasks.register("GenerateDependencyToUML", GenerateDependencyToUMLTask::class.java) {
            // 태스크의 기본 설정을 여기에 추가할 수 있습니다.
        }
    }
}