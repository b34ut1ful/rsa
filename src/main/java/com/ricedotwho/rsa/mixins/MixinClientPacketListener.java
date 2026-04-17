package com.ricedotwho.rsa.mixins;

import com.ricedotwho.rsa.component.impl.managers.SwapManager;
import net.minecraft.class_634;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_634.class)
public abstract class MixinClientPacketListener {
   @Inject(
      method = "method_11120(Lnet/minecraft/class_2678;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/class_636;<init>(Lnet/minecraft/class_310;Lnet/minecraft/class_634;)V")
   )
   public void onHandleLogin(CallbackInfo ci) {
      SwapManager.onHandleLogin();
   }
}
