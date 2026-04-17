package com.ricedotwho.rsa.mixins;

import com.ricedotwho.rsa.module.impl.dungeon.SecretHitboxes;
import net.minecraft.class_2338;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_638;
import net.minecraft.class_761;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(class_761.class)
public class MixinLevelRenderer {
   @Shadow
   private class_638 field_4085;
   @Shadow
   @Final
   private class_310 field_4088;

   @ModifyVariable(method = "method_74923(Lnet/minecraft/class_4184;Lnet/minecraft/class_11658;)V", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
   private class_265 extractBlockOutline(class_265 original) {
      if (this.field_4085 != null && this.field_4088.field_1765 instanceof class_3965 hit) {
         class_2338 var6 = hit.method_17777();
         class_2680 state = this.field_4085.method_8320(var6);
         class_265 shape = SecretHitboxes.getShape(state, var6);
         return shape != null ? shape : original;
      } else {
         return original;
      }
   }
}
