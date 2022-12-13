package dev.fastmc.allocfix.mixins.main.world;

import dev.fastmc.allocfix.mixins.IPatchedIBlockAccess;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IBlockAccess.class)
public interface MixinIBlockAccess extends IPatchedIBlockAccess {}
