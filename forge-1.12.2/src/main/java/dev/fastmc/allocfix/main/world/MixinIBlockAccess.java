package dev.fastmc.allocfix.main.world;

import dev.fastmc.allocfix.IPatchedIBlockAccess;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IBlockAccess.class)
public interface MixinIBlockAccess extends IPatchedIBlockAccess {}
