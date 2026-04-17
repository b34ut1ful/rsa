package com.ricedotwho.rsa.mixins;

import com.ricedotwho.rsa.component.impl.TickFreeze;
import com.ricedotwho.rsa.component.impl.managers.PacketOrderManager;
import com.ricedotwho.rsa.component.impl.managers.SwapManager;
import com.ricedotwho.rsa.event.impl.RawTickEvent;
import com.ricedotwho.rsa.screen.SessionLoginScreen;
import net.minecraft.class_10209;
import net.minecraft.class_310;
import net.minecraft.class_320;
import net.minecraft.class_4071;
import net.minecraft.class_437;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = class_310.class, priority = 600)
public abstract class MixinMinecraft {
   @Shadow
   @Nullable
   private class_4071 field_18175;
   @Shadow
   @Nullable
   public class_437 field_1755;
   @Unique
   private boolean bla = false;
   @Unique
   private boolean blu = false;

   @Shadow
   protected abstract void method_1508();

   @Inject(method = "method_1574()V", at = @At("HEAD"), cancellable = true)
   private void onTickStart(CallbackInfo ci) {
      new RawTickEvent().post();
      if (TickFreeze.isFrozen()) {
         ci.cancel();
      } else {
         SwapManager.onPreTickStart();
         PacketOrderManager.onPreTickStart();
      }
   }

   @Redirect(method = "method_1574()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_310;method_1508()V"))
   public void onHandleKeyBinds(class_310 instance) {
   }

   @Inject(method = "method_1574()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_340;method_53536()Z"))
   public void onGetShowDebugScreen(CallbackInfo ci) {
      if (this.field_18175 == null && class_310.method_1551().field_1724 != null) {
         class_10209.method_64146().method_15405("Keybindings");
         this.method_1508();
      }
   }

   @Inject(method = "method_1508()V", at = @At("HEAD"))
   public void onHandleKeybinds(CallbackInfo ci) {
      this.bla = true;
      this.blu = true;
   }

   @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/class_304;method_1436()Z", ordinal = 14), method = "method_1508()V")
   public void onHandleInputEvent(CallbackInfo ci) {
      if (this.bla) {
         PacketOrderManager.execute(PacketOrderManager.STATE.ATTACK);
         this.bla = false;
      }
   }

   @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/class_304;method_1436()Z", ordinal = 15), method = "method_1508()V")
   public void onHandleInputEvent2(CallbackInfo ci) {
      if (this.blu) {
         PacketOrderManager.execute(PacketOrderManager.STATE.ITEM_USE);
         this.blu = false;
      }
   }

   @Inject(at = @At("RETURN"), method = "method_1548()Lnet/minecraft/class_320;", cancellable = true)
   private void onGetSSID(CallbackInfoReturnable<class_320> cir) {
      if (SessionLoginScreen.getUser() != null) {
         cir.setReturnValue(SessionLoginScreen.getUser());
      }
   }
}
