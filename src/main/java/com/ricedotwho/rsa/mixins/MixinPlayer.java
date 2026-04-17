package com.ricedotwho.rsa.mixins;

import com.ricedotwho.rsa.module.impl.dungeon.DungeonBreaker;
import net.minecraft.class_1657;
import net.minecraft.class_1661;
import net.minecraft.class_2680;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_1657.class)
public abstract class MixinPlayer {
   @Shadow
   public abstract class_1661 method_31548();

   @Inject(method = "method_7351(Lnet/minecraft/class_2680;)F", at = @At("RETURN"), cancellable = true)
   private void modifyBreakSpeed(class_2680 state, CallbackInfoReturnable<Float> cir) {
      DungeonBreaker.handleDigSpeed(state, this.method_31548().method_7391(), cir);
   }
}
