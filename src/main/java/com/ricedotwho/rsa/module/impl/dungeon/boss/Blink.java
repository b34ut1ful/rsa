package com.ricedotwho.rsa.module.impl.dungeon.boss;

import com.ricedotwho.rsa.IMixin.IConnection;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.rings.BlinkRing;
import com.ricedotwho.rsm.RSM;
import com.ricedotwho.rsm.event.api.SubscribeEvent;
import com.ricedotwho.rsm.event.impl.client.PacketEvent.Send;
import com.ricedotwho.rsm.event.impl.render.Render2DEvent;
import com.ricedotwho.rsm.event.impl.world.WorldEvent.Load;
import com.ricedotwho.rsm.module.Module;
import com.ricedotwho.rsm.module.api.Category;
import com.ricedotwho.rsm.module.api.ModuleInfo;
import com.ricedotwho.rsm.ui.clickgui.settings.Setting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.DragSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.NumberSetting;
import com.ricedotwho.rsm.utils.ChatUtils;
import java.math.BigDecimal;
import java.util.LinkedList;
import net.minecraft.class_10185;
import net.minecraft.class_2596;
import net.minecraft.class_2793;
import net.minecraft.class_2828;
import net.minecraft.class_2851;
import net.minecraft.class_310;
import net.minecraft.class_9836;
import org.joml.Vector2d;

@ModuleInfo(aliases = "Blink", id = "Blink", category = Category.MOVEMENT, hasKeybind = true)
public class Blink extends Module {
   private static Blink INSTANCE;
   private class_10185 lastInput;
   private final DragSetting gui = new DragSetting("Blink Hud", new Vector2d(100.0, 100.0), new Vector2d(144.0, 80.0));
   private final NumberSetting maxBlinkPacket = new NumberSetting("Max Blink Ticks", 1.0, 30.0, 17.0, 1.0);
   private BlinkRing currentRing;
   private final LinkedList<class_2596<?>> queue = new LinkedList<>();
   private boolean flushing = false;
   private int packetCount = 0;

   public Blink() {
      this.registerProperty(new Setting[]{this.maxBlinkPacket, this.gui});
   }

   @SubscribeEvent
   public void onRenderGui(Render2DEvent event) {
      if (!this.queue.isEmpty()) {
         this.gui
            .renderScaled(
               event.getGfx(),
               () -> event.getGfx()
                  .method_25300(class_310.method_1551().field_1772, "Blinking", (int)this.gui.getPosition().x, (int)this.gui.getPosition().y, -1),
               10.0F,
               10.0F
            );
      }
   }

   @SubscribeEvent
   public void onSendPacket(Send event) {
      if (event.getPacket() instanceof class_2851 inputPacket) {
         if (this.lastInput != null && this.inputEquals(inputPacket.comp_3139(), this.lastInput)) {
            event.setCancelled(true);
         }

         this.lastInput = inputPacket.comp_3139();
      }
   }

   private boolean inputEquals(class_10185 input1, class_10185 input2) {
      return input1.comp_3164() == input2.comp_3164()
         && input1.comp_3159() == input2.comp_3159()
         && input1.comp_3160() == input2.comp_3160()
         && input1.comp_3161() == input2.comp_3161()
         && input1.comp_3162() == input2.comp_3162()
         && input1.comp_3163() == input2.comp_3163()
         && input1.comp_3165() == input2.comp_3165();
   }

   @SubscribeEvent
   public void onWorldLoad(Load event) {
      synchronized (this.queue) {
         this.queue.clear();
         if (this.isEnabled()) {
            this.setEnabled(false);
         }
      }
   }

   public static boolean onSendPacket(class_2596<?> packet) {
      if (INSTANCE == null) {
         INSTANCE = (Blink)RSM.getModule(Blink.class);
      }

      return INSTANCE.onPreSendPacket(packet);
   }

   private boolean onPreSendPacket(class_2596<?> packet) {
      if (class_310.method_1551().field_1724 != null && this.isEnabled()) {
         synchronized (this.queue) {
            if (this.flushing) {
               return false;
            } else {
               boolean bl = true;
               if (this.currentRing != null
                  && (this.packetCount >= ((BigDecimal)this.maxBlinkPacket.getValue()).intValue() || this.currentRing.isDonePlaying())) {
                  if (packet instanceof class_2828 || packet instanceof class_2851) {
                     return true;
                  }

                  if (packet instanceof class_2793) {
                     if (this.isEnabled()) {
                        this.onKeyToggle();
                     }

                     return false;
                  }
               }

               if (packet instanceof class_9836) {
                  this.packetCount++;
                  if (this.currentRing != null) {
                     if (this.packetCount >= ((BigDecimal)this.maxBlinkPacket.getValue()).intValue()) {
                        bl = false;
                        this.packetCount--;
                     }
                  } else if (this.packetCount >= ((BigDecimal)this.maxBlinkPacket.getValue()).intValue()) {
                     this.onKeyToggle();
                     return false;
                  }
               }

               if (bl) {
                  this.queue.add(packet);
                  return true;
               } else {
                  return false;
               }
            }
         }
      } else {
         return false;
      }
   }

   public int getChargedCount() {
      return this.packetCount;
   }

   public void clearMovements() {
      this.queue.removeIf(p -> p instanceof class_2851 || p instanceof class_2828);
   }

   public void actuallySendImmediately(class_2596<?> packet) {
      if (class_310.method_1551().method_1562() != null) {
         synchronized (this.queue) {
            this.flushing = true;
            ((IConnection)class_310.method_1551().method_1562().method_48296()).sendPacketImmediately(packet);
            this.flushing = true;
         }
      }
   }

   public void actuallySend(class_2596<?> packet) {
      if (class_310.method_1551().method_1562() != null) {
         synchronized (this.queue) {
            this.flushing = true;
            class_310.method_1551().method_1562().method_52787(packet);
            this.flushing = true;
         }
      }
   }

   public void onEnable() {
      super.onEnable();
      this.flush();
      this.currentRing = null;
      this.lastInput = null;
      this.packetCount = 0;
   }

   public void onDisable() {
      super.onDisable();
      ChatUtils.chat("Packets : " + this.queue.stream().filter(p -> p instanceof class_2828).count(), new Object[0]);
      this.flush();
      this.currentRing = null;
      this.lastInput = null;
      this.packetCount = 0;
   }

   private void flushTick() {
      if (class_310.method_1551().method_1562() != null) {
         synchronized (this.queue) {
            this.flushing = true;
            if (this.queue.isEmpty()) {
               this.flushing = false;
               this.setEnabled(false);
            } else {
               while (!this.queue.isEmpty()) {
                  class_2596<?> packet = this.queue.poll();
                  ((IConnection)class_310.method_1551().method_1562().method_48296()).sendPacketImmediately(packet);
                  if (packet instanceof class_9836) {
                     this.flushing = false;
                     return;
                  }
               }

               this.flushing = false;
            }
         }
      }
   }

   private void flush() {
      if (class_310.method_1551().method_1562() != null) {
         synchronized (this.queue) {
            this.flushing = true;
            if (this.queue.isEmpty()) {
               this.flushing = false;
            } else {
               this.queue.forEach(packet -> ((IConnection)class_310.method_1551().method_1562().method_48296()).sendPacketImmediately((class_2596<?>)packet));
               this.queue.clear();
               this.flushing = false;
            }
         }
      }
   }

   public void setCurrentRing(BlinkRing currentRing) {
      this.currentRing = currentRing;
   }
}
