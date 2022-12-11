architecturyProject {
    mixinConfig("mixins.fastmc.allocfix.accessor.json", "mixins.fastmc.allocfix.main.json")
    accessWidenerPath.set(file("common/src/main/resources/fastmc-allocfix.accesswidener").absoluteFile)
    forge {
        atPatch {
            patch("net/minecraft/util/math/Matrix3f", "net/minecraft/util/math/vector/Matrix3f")
            patch("net/minecraft/util/math/Matrix4f", "net/minecraft/util/math/vector/Matrix4f")
        }
    }
}