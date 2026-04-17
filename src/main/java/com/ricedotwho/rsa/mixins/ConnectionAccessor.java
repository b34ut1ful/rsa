package com.ricedotwho.rsa.mixins;

import net.minecraft.class_2535;
import net.minecraft.class_8762;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_2535.class)
public interface ConnectionAccessor {
   @Accessor("field_45955")
   @Nullable
   class_8762 getBandwidthDebugMonitor();
}
