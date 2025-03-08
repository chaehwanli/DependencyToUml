# DependencyToUml
A program that creates a package relationship using uml by referring to the dependency relationship.

# Build

## modify `settings.gradle.kts`
```kotlin
pluginManagement {
    repositories {
        gradlePluginPortal() //Import plugins from the Plugin Portal
    }
}
```
## modify `build.gradle.kts`
```kotlin
plugins {
    id("io.github.chaehwanli.dep2uml.dependencyToUMLPlugin") version "${latest_version}"
}
```

# Outputs
## Example Projects
```gradle
dependencies {
...
    implementation(com.example:libraryA:1.0.0)
    implementation(com.example.utils:libraryB:1.2.0)
    implementation(org.example:libraryC:2.0.0)
...
}

```
## generated uml file : 
```uml
@startuml
package com.example {
    class libraryA {
        version : 1.0.0
    }
}
package com.example.utils {
    class libraryB {
        version : 1.2.0
    }
}
package org.example {
    class libraryC {
        version : 2.0.0
    }
}
com.example.libraryA --> com.example.utils.libraryB
com.example.libraryA --> org.example.libraryC
@enduml
```
## verify with [plantUML Online](https://plantuml.online)

# License
Copyright 2025 chaehwan.li@gmail.com

Licensed under the Apache License, Version 2.0 (the "License");
