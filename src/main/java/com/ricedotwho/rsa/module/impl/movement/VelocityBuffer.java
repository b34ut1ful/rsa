package com.ricedotwho.rsa.module.impl.movement;

import com.ricedotwho.rsm.RSM;
import com.ricedotwho.rsm.data.Keybind;
import com.ricedotwho.rsm.event.api.SubscribeEvent;
import com.ricedotwho.rsm.event.impl.render.Render2DEvent;
import com.ricedotwho.rsm.event.impl.world.WorldEvent.Load;
import com.ricedotwho.rsm.module.Module;
import com.ricedotwho.rsm.module.api.Category;
import com.ricedotwho.rsm.module.api.ModuleInfo;
import com.ricedotwho.rsm.ui.clickgui.settings.Setting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.DragSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.KeybindSetting;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.class_1109;
import net.minecraft.class_2596;
import net.minecraft.class_2708;
import net.minecraft.class_2743;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_6373;
import net.minecraft.class_746;
import net.minecraft.class_8042;
import org.joml.Vector2d;

@ModuleInfo(aliases = "VelocityBuffer", id = "VelocityBuffer", category = Category.MOVEMENT, hasKeybind = true)
public class VelocityBuffer extends Module {
   private static VelocityBuffer INSTANCE;
   private final KeybindSetting popKey = new KeybindSetting("Queue Pop Key", new Keybind(-1, false, this::popQueue));
   private final DragSetting gui = new DragSetting("Velocity Buffer Hud", new Vector2d(100.0, 100.0), new Vector2d(144.0, 80.0));
   private int bufferedCount = 0;
   private static final Set<Class<? extends class_2596<?>>> PACKET_SET = Set.of(class_6373.class, class_8042.class);
   private final ConcurrentLinkedQueue<class_2596<?>> queue = new ConcurrentLinkedQueue<>();

   public VelocityBuffer() {
      this.registerProperty(new Setting[]{this.popKey, this.gui});
   }

   @SubscribeEvent
   public void onRenderGui(Render2DEvent event) {
      if (!this.queue.isEmpty()) {
         this.gui
            .renderScaled(
               event.getGfx(),
               () -> event.getGfx().method_25300(class_310.method_1551().field_1772, "Buffered Packets : " + this.bufferedCount, 0, 0, -1),
               10.0F,
               10.0F
            );
      }
   }

   @SubscribeEvent
   public void onWorldLoad(Load event) {
      synchronized (this.queue) {
         this.queue.clear();
         this.bufferedCount = 0;
         if (this.isEnabled()) {
            this.setEnabled(false);
         }
      }
   }

   public static boolean onReceivePacketPre(class_2596<?> packet) {
      if (INSTANCE == null) {
         INSTANCE = (VelocityBuffer)RSM.getModule(VelocityBuffer.class);
      }

      return INSTANCE.onReceivePacket(packet);
   }

   private boolean onReceivePacket(class_2596<?> packet) {
      synchronized (this.queue) {
         if (class_310.method_1551().field_1724 == null || !this.isEnabled()) {
            return false;
         } else if (packet instanceof class_2708) {
            this.onKeyToggle();
            return false;
         } else if (this.isMotionPacket(packet, class_310.method_1551().field_1724)) {
            this.queue.add(packet);
            this.bufferedCount++;
            class_310.method_1551().method_1483().method_4873(class_1109.method_4757((class_3414)class_3417.field_14622.comp_349(), 0.5F, 0.5F));
            return true;
         } else {
            if (packet instanceof class_8042 bundlePacket) {
               bundlePacket.method_48324().forEach(p -> System.out.println(p.getClass()));
            }

            if (!PACKET_SET.contains(packet.getClass())) {
               return false;
            } else {
               synchronized (this.queue) {
                  if (this.queue.isEmpty()) {
                     return false;
                  }

                  this.queue.add(packet);
               }

               return true;
            }
         }
      }
   }

   public void onEnable() {
      super.onEnable();
      this.flush();
   }

   public void onDisable() {
      this.flush();
      super.onDisable();
   }

   public void popQueue() {
      if (class_310.method_1551().field_1724 != null) {
         synchronized (this.queue) {
            if (this.queue.isEmpty()) {
               return;
            }

            while (!this.queue.isEmpty()) {
               class_2596<?> packet = this.queue.poll();
               this.receivePacket(packet);
               if (this.isMotionPacket(packet, class_310.method_1551().field_1724)) {
                  this.bufferedCount--;
                  if (!this.queue.stream().anyMatch(p -> this.isMotionPacket((class_2596<?>)p, class_310.method_1551().field_1724))) {
                     this.flush();
                     if (this.isEnabled()) {
                        this.onKeyToggle();
                     }
                  }
                  break;
               }
            }
         }

         class_310.method_1551().method_1483().method_4873(class_1109.method_4757((class_3414)class_3417.field_14622.comp_349(), 2.0F, 2.0F));
      }
   }

   private void receivePacket(class_2596<?> packet) {
      if (class_310.method_1551().method_1562() != null) {
         ((class_2596) packet).method_65081(class_310.method_1551().method_1562());
      }
   }

   private boolean isMotionPacket(class_2596<?> packet, class_746 player) {
      return packet instanceof class_2743 motionPacket && motionPacket.method_11818() == player.method_5628();
   }

   private void flush() {
      synchronized (this.queue) {
         if (!this.queue.isEmpty()) {
            this.queue.forEach(this::receivePacket);
         }

         this.queue.clear();
      }

      this.bufferedCount = 0;
   }
}
