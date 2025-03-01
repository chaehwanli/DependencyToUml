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