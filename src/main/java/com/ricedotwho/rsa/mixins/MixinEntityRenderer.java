package com.ricedotwho.rsa.mixins;

import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.TermAura;
import net.minecraft.class_1297;
import net.minecraft.class_897;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(class_897.class)
public abstract class MixinEntityRenderer<T extends class_1297> {
   @Redirect(
      method = "method_62354(Lnet/minecraft/class_1297;Lnet/minecraft/class_10017;F)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/class_1297;method_5767()Z")
   )
   public boolean onGetInvisibility(class_1297 instance) {
      return !TermAura.getEntityVisibility(instance);
   }
}
