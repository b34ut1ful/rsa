package com.ricedotwho.rsa.mixins;

import com.ricedotwho.rsa.module.impl.dungeon.SecretHitboxes;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_3959;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_3959.class)
public class MixinClipContext {
   @Inject(
      method = "method_17748(Lnet/minecraft/class_2680;Lnet/minecraft/class_1922;Lnet/minecraft/class_2338;)Lnet/minecraft/class_265;",
      at = @At("HEAD"),
      cancellable = true
   )
   private void getBlockShape(class_2680 blockState, class_1922 blockGetter, class_2338 blockPos, CallbackInfoReturnable<class_265> cir) {
      class_265 shape = SecretHitboxes.getShape(blockState, blockPos);
      if (shape != null) {
         cir.setReturnValue(shape);
      }
   }
}
