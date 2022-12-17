dependencies {
    api("dev.fastmc:fastmc-common:1.0-SNAPSHOT") {
        isTransitive = false
    }
}

subprojects {
    dependencies {
        "modCore"("dev.fastmc:fastmc-common-${project.name}:1.0-SNAPSHOT") {
            isTransitive = false
        }
        libraryApi("dev.fastmc:fastmc-common-${project.name}:1.0-SNAPSHOT") {
            isTransitive = false
        }
    }
}