package com.ricedotwho.rsa.component.impl;

import com.ricedotwho.rsm.component.api.ModComponent;
import com.ricedotwho.rsm.event.api.SubscribeEvent;
import com.ricedotwho.rsm.event.impl.client.InputPollEvent;

public class Jump extends ModComponent {
   private static boolean jump = false;

   public Jump() {
      super("Jump");
   }

   public static void jump() {
      jump = true;
   }

   @SubscribeEvent
   public void onInput(InputPollEvent event) {
      if (jump && mc.field_1724 != null && mc.field_1724.method_24828() && !mc.field_1690.field_1903.method_1434()) {
         jump = false;
         event.getInput().jump(true);
      }
   }

   public static boolean isJump() {
      return jump;
   }
}
