val dummy by sourceSets.creating {
    sourceSets.main.get().compileClasspath = this.output + sourceSets.main.get().compileClasspath
}