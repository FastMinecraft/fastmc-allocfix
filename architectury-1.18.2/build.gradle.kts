architecturyProject {
    modPackage.set("dev.fastmc.allocfix")
    mixinConfig(
        "mixins.fastmc.allocfix.accessor.json",
        "mixins.fastmc.allocfix.main.json"
    )
    accessWidenerPath.set(file("common/src/main/resources/fastmc-allocfix.accesswidener").absoluteFile)
}

modLoader {
    forgeModClass.set("dev.fastmc.allocfix.FastMcAllocFixForgeEntryPoint")
}