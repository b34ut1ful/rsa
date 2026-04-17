package com.ricedotwho.rsa.mixins;

import com.ricedotwho.rsa.screen.SessionLoginScreen;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_442;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_442.class)
public class MixinTitleScreen extends class_437 {
   protected MixinTitleScreen(class_2561 component) {
      super(component);
   }

   @Inject(at = @At("HEAD"), method = "method_25426()V")
   private void onInit(CallbackInfo ci) {
      class_4185 theButton = class_4185.method_46430(
            class_2561.method_43470("Session Login"), button -> class_310.method_1551().method_1507(SessionLoginScreen.getInstance())
         )
         .method_46432(100)
         .method_46433(this.field_22789 - 110, 20)
         .method_46431();
      this.method_37063(theButton);
   }
}
