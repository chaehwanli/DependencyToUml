package com.meteroid.dep2uml.model

data class DependencyInfo(
    val group: String,
    val name: String,
    val version: String,
    val type: DependencyType
)