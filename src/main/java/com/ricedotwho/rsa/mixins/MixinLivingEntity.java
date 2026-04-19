package com.ricedotwho.rsa.mixins;

import com.ricedotwho.rsa.module.impl.render.HidePlayers;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_1309.class)
public class MixinLivingEntity {
   @Inject(method = "method_5863()Z", at = @At("HEAD"), cancellable = true)
   public void isPickable(CallbackInfoReturnable<Boolean> cir) {
      if (HidePlayers.shouldHitThrough((class_1297)(Object)this)) {
         cir.setReturnValue(false);
      }
   }
}
