package dev.fastmc.allocfix

import net.fabricmc.api.ModInitializer

class FastMcAllocFixFabricEntryPoint : ModInitializer {
    override fun onInitialize() {
        FastMcAllocFixMod.init()
    }
}