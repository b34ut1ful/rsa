package com.ricedotwho.rsa.mixins;

import com.ricedotwho.rsa.module.impl.render.Freecam;
import net.minecraft.class_5498;
import net.minecraft.class_757;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(class_757.class)
public class MixinGameRenderer {
   @Redirect(
      method = "method_3188(Lnet/minecraft/class_9779;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/class_5498;method_31034()Z", ordinal = 0)
   )
   private boolean onRenderLevel(class_5498 instance) {
      return instance.method_31034() && !Freecam.isDetached();
   }
}
