package com.ricedotwho.rsa.mixins;

import com.ricedotwho.rsa.module.impl.render.Freecam;
import net.minecraft.class_2338;
import net.minecraft.class_852;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_852.class)
public class MixinVisGraph {
   @Inject(at = @At("HEAD"), method = "method_3682(Lnet/minecraft/class_2338;)V", cancellable = true)
   private void onMarkClosed(class_2338 blockPos, CallbackInfo ci) {
      if (Freecam.isDetached()) {
         ci.cancel();
      }
   }
}
