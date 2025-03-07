# DependencyToUml
A program that creates a package relationship using uml by referring to the dependency relationship.

# Build

# Usage
generated uml file :
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

# License
Copyright 2025 chaehwan.li@gmail.com

Licensed under the Apache License, Version 2.0 (the "License");
