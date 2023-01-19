package dev.fastmc.allocfix

import net.fabricmc.api.ModInitializer

class FastMcAllocFixEntryPoint : ModInitializer {
    override fun onInitialize() {
        FastMcAllocFixMod.init()
    }
}