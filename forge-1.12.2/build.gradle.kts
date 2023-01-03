forgeProject {
    modPackage.set("dev.fastmc.allocfix")
    accessTransformer = "fastmc-allocfix-at.cfg"
    mixinConfig("mixins.fastmc.allocfix.accessor.json", "mixins.fastmc.allocfix.main.json")
    coreModClass.set("dev.fastmc.allocfix.FastMcAllocFixCoremod")
    devCoreModClass.set("dev.fastmc.allocfix.FastMcAllocFixDevFixCoremod")
}

val dummy by sourceSets.creating {
    sourceSets.main.get().compileClasspath = this.output + sourceSets.main.get().compileClasspath
}