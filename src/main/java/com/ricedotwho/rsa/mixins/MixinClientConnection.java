package com.ricedotwho.rsa.mixins;

import com.ricedotwho.rsa.component.impl.managers.SwapManager;
import net.minecraft.class_2535;
import net.minecraft.class_2596;
import net.minecraft.class_8762;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = class_2535.class, priority = 600)
public abstract class MixinClientConnection {
   @Shadow
   @Nullable
   class_8762 field_45955;

   @Inject(method = "method_10743(Lnet/minecraft/class_2596;)V", at = @At("HEAD"), cancellable = true)
   private void onSend(class_2596<?> packet, CallbackInfo ci) {
      if (!SwapManager.onPostSendPacket(packet)) {
         ci.cancel();
      }
   }
}
