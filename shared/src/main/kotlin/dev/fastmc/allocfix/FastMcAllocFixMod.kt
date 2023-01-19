package dev.fastmc.allocfix

object FastMcAllocFixMod {
    fun preInit() {
        System.setProperty("joml.fastmath", "true")
        System.setProperty("joml.useMathFma", "true")
        System.setProperty("joml.sinLookup", "true")
    }

    fun init() {}
}