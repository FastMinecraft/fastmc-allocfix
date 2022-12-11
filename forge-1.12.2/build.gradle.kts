import org.spongepowered.asm.gradle.plugins.MixinExtension

forgeProject {
    accessTransformer = "fastmc-allocfix-at.cfg"
    mixinConfig("mixins.fastmc.allocfix.main.json")
    coreModClass.set("dev.fastmc.allocfix.FastMcAllocFixCoremod")
    coreModClass.set("dev.fastmc.allocfix.FastMcAllocFixDevFixCoremod")
}

configure<MixinExtension> {
    add(sourceSets["main"], "mixins.fastmc.allocfix.refmap.json")
}