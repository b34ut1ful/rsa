package com.ricedotwho.rsa.mixins;

import com.ricedotwho.rsa.component.impl.TickFreeze;
import net.minecraft.class_9779.class_9781;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_9781.class)
public abstract class MixinDeltaTracker {
   @Shadow
   public abstract float method_60637(boolean var1);

   @Inject(method = "method_60637(Z)F", at = @At("HEAD"), cancellable = true)
   private void isEntityFrozen(boolean isPaused, CallbackInfoReturnable<Float> cir) {
      if (TickFreeze.isFrozen()) {
         cir.setReturnValue(TickFreeze.getPartialTick());
      }
   }

   @Inject(method = "method_60640(JZ)I", at = @At("HEAD"))
   public void advanceGameTime(long frameTime, boolean tick, CallbackInfoReturnable<Integer> cir) {
      TickFreeze.setLastTickPartialTicks(this.method_60637(true));
   }
}
