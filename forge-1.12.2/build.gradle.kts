import org.spongepowered.asm.gradle.plugins.MixinExtension

forgeProject {
    accessTransformer = "fastmc-allocfix-at.cfg"
    mixinConfig("mixins.fastmc.allocfix.accessor.json", "mixins.fastmc.allocfix.main.json")
    coreModClass.set("dev.fastmc.allocfix.FastMcAllocFixCoremod")
    devCoreModClass.set("dev.fastmc.allocfix.FastMcAllocFixDevFixCoremod")
}

configure<MixinExtension> {
    add(sourceSets["main"], "mixins.fastmc.allocfix.refmap.json")
}

val dummy by sourceSets.creating {
    sourceSets.main.get().compileClasspath = this.output + sourceSets.main.get().compileClasspath
}