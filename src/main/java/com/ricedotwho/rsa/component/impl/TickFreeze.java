package com.ricedotwho.rsa.component.impl;

import com.ricedotwho.rsm.component.api.ModComponent;
import com.ricedotwho.rsm.event.api.SubscribeEvent;
import com.ricedotwho.rsm.event.impl.client.PacketEvent.Receive;
import com.ricedotwho.rsm.event.impl.world.WorldEvent.Load;
import net.minecraft.class_2724;

public class TickFreeze extends ModComponent {
   private static boolean frozen = false;
   private static long end = 0L;
   private static float partialTick = 1.0F;
   private static float lastTickPartialTicks = 1.0F;

   public TickFreeze() {
      super("TickFreeze");
   }

   public static void freeze(long millis) {
      freeze(millis, false);
   }

   public static void freeze(long millis, boolean lastTick) {
      end = System.currentTimeMillis() + millis;
      if (!frozen) {
         freeze(lastTick);
      }
   }

   public static void freeze() {
      freeze(false);
   }

   public static void freeze(boolean lastTick) {
      partialTick = lastTick ? lastTickPartialTicks : mc.method_61966().method_60637(true);
      frozen = true;
   }

   public static void unFreeze() {
      frozen = false;
      end = 0L;
      partialTick = 1.0F;
   }

   public static boolean isFrozen() {
      if (frozen && end > 0L && System.currentTimeMillis() > end) {
         unFreeze();
      }

      return frozen;
   }

   @SubscribeEvent
   public void onLoad(Load event) {
      unFreeze();
   }

   @SubscribeEvent
   public void onPacket(Receive event) {
      if (event.getPacket() instanceof class_2724) {
         unFreeze();
      }
   }

   public static float getPartialTick() {
      return partialTick;
   }

   public static void setLastTickPartialTicks(float lastTickPartialTicks) {
      TickFreeze.lastTickPartialTicks = lastTickPartialTicks;
   }
}
