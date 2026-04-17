package com.ricedotwho.rsa.mixins;

import com.ricedotwho.rsa.IMixin.IMultiPlayerGameMode;
import com.ricedotwho.rsa.component.impl.managers.SwapManager;
import com.ricedotwho.rsa.module.impl.player.CancelInteract;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_3965;
import net.minecraft.class_636;
import net.minecraft.class_638;
import net.minecraft.class_7204;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_636.class)
public abstract class MixinMultiPlayerGameMode implements IMultiPlayerGameMode {
   @Shadow
   private int field_3721;

   @Shadow
   protected abstract void method_2911();

   @Shadow
   protected abstract void method_41931(class_638 var1, class_7204 var2);

   @Override
   public void sendPacketSequenced(class_638 world, class_7204 packetCreator) {
      this.method_41931(world, packetCreator);
   }

   @Override
   public void syncSlot() {
      this.method_2911();
   }

   @Inject(method = "method_2911()V", at = @At("HEAD"), cancellable = true)
   public void onSyncSlot(CallbackInfo ci) {
      if (!SwapManager.onEnsureHasSentCarriedItem(this.field_3721)) {
         ci.cancel();
      }
   }

   @Inject(
      method = "method_2896(Lnet/minecraft/class_746;Lnet/minecraft/class_1268;Lnet/minecraft/class_3965;)Lnet/minecraft/class_1269;",
      at = @At("HEAD"),
      cancellable = true
   )
   private void onInteractBlock(class_746 player, class_1268 hand, class_3965 hit, CallbackInfoReturnable<class_1269> cir) {
      if (CancelInteract.shouldCancelInteract(hit, player, player.method_6118(hand.method_73186()))) {
         cir.setReturnValue(class_1269.field_5811);
      }
   }
}
