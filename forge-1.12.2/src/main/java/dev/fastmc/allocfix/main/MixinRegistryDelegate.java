package dev.fastmc.allocfix.main;

import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net/minecraftforge/registries/RegistryDelegate", remap = false)
public class MixinRegistryDelegate {
    @Shadow private ResourceLocation name;

    /**
     * @author Luna
     * @reason Memory allocation optimization
     */
    @Override
    @Overwrite
    public int hashCode()
    {
        return name.hashCode();
    }
}
