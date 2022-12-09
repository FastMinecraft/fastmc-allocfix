import org.spongepowered.asm.gradle.plugins.MixinExtension

forgeProject {
    accessTransformer = "fastmc-alloc-fix-at.cfg"
    coreModClass.set("dev.fastmc.allocfix.FastMcAllocationFixCoremod")
    coreModClass.set("dev.fastmc.allocfix.FastMcAllocationFixDevFixCoremod")
}

configure<MixinExtension> {
    add(sourceSets["main"], "mixins.fastmc.allocfix.refmap.json")
}