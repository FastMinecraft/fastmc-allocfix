package dev.fastmc.allocfix

import net.minecraftforge.fml.common.Mod

@Mod("fastmc-allocfix")
class FastMcEntryPoint {
    init {
        FastMcAllocFixMod.init()
    }
}