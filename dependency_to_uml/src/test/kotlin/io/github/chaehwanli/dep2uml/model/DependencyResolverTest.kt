package io.github.chaehwanli.dep2uml.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class DependencyResolverTest {

    @ParameterizedTest
    @CsvSource(
        "api, API",
        "implementation, IMPLEMENTATION",
        "compileOnly, COMPILE_ONLY",
        "compileOnlyApi, COMPILE_ONLY_API",
        "runtimeOnly, RUNTIME_ONLY",
        "runtimeClassPath, RUNTIME_CLASS_PATH",
        "testImplementation, TEST_IMPLEMENTATION",
        "testRuntimeOnly, TEST_RUNTIME_ONLY",
        "testRuntimeClassPath, TEST_RUNTIME_CLASS_PATH",
        "annotationProcessor, ANNOTATION_PROCESSOR",
        "kapt, KAPT"
    )
    fun `resolve should return correct DependencyType`(
        configurationName: String,
        expectedDependencyType: DependencyType,
    ) {
        // when
        val actualDependencyType = DependencyResolver.resolve(configurationName)

        // then
        assertEquals(expectedDependencyType, actualDependencyType)
    }

    @ParameterizedTest
    @CsvSource(
        "API, API",
        "IMPLEMENTATION, IMPLEMENTATION",
        "COMPILEONLY, COMPILE_ONLY",
        "COMPILEONLYAPI, COMPILE_ONLY_API",
        "RUNTIMEONLY, RUNTIME_ONLY",
        "RUNTIMECLASSPATH, RUNTIME_CLASS_PATH",
        "TESTIMPLEMENTATION, TEST_IMPLEMENTATION",
        "TESTRUNTIMEONLY, TEST_RUNTIME_ONLY",
        "TESTRUNTIMECLASSPATH, TEST_RUNTIME_CLASS_PATH",
        "ANNOTATIONPROCESSOR, ANNOTATION_PROCESSOR",
        "KAPT, KAPT"
    )
    fun `resolve should return correct DependencyType when input is uppercase`(
        configurationName: String,
        expectedDependencyType: DependencyType,
    ) {
        // when
        val actualDependencyType = DependencyResolver.resolve(configurationName)

        // then
        assertEquals(expectedDependencyType, actualDependencyType)
    }

    @ParameterizedTest
    @ValueSource(strings = ["unknown", "other", "random"])
    fun `resolve should throw IllegalArgumentException for unknown configuration`(
        configurationName: String,
    ) {
        // when
        val exception = assertThrows(IllegalArgumentException::class.java) {
            DependencyResolver.resolve(configurationName)
        }

        // then
        assertEquals("Unknown configuration: $configurationName", exception.message)
    }
}