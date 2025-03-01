package com.meteroid.dep2uml.model

import org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.Test

class DependencyConfigurationTest {

    @Test
    fun valueOf() {
        val dependencyConfiguration = DependencyConfiguration.fromConfigurationName("api")

        assertEquals(DependencyType.API, dependencyConfiguration)
    }
}