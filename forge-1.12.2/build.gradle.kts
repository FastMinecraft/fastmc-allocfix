import org.spongepowered.asm.gradle.plugins.MixinExtension

forgeProject {
    coreModClass.set("dev.fastmc.allocfix.FastMcAllocationFixCoremod")
}

configure<MixinExtension> {
    add(sourceSets["main"], "mixins.fastmc.allocfix.refmap.json")
}