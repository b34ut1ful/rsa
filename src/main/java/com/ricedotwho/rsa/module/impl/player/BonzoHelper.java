package com.ricedotwho.rsa.module.impl.player;

import com.ricedotwho.rsa.RSA;
import com.ricedotwho.rsa.component.impl.TickFreeze;
import com.ricedotwho.rsm.component.impl.task.TaskComponent;
import com.ricedotwho.rsm.event.api.SubscribeEvent;
import com.ricedotwho.rsm.event.impl.client.PacketEvent.Receive;
import com.ricedotwho.rsm.event.impl.client.PacketEvent.Send;
import com.ricedotwho.rsm.module.Module;
import com.ricedotwho.rsm.module.api.Category;
import com.ricedotwho.rsm.module.api.ModuleInfo;
import com.ricedotwho.rsm.ui.clickgui.settings.Setting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.BooleanSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.NumberSetting;
import com.ricedotwho.rsm.utils.ItemUtils;
import java.math.BigDecimal;
import java.util.Objects;
import net.minecraft.class_1268;
import net.minecraft.class_2743;
import net.minecraft.class_2886;
import net.minecraft.class_3965;

@ModuleInfo(aliases = "Bonzo Helper", id = "BonzoHelper", category = Category.PLAYER)
public class BonzoHelper extends Module {
   private final BooleanSetting velo = new BooleanSetting("Await Velocity", false);
   private final NumberSetting timeout = new NumberSetting("Timeout", 0.0, 2000.0, 500.0, 10.0);
   private final NumberSetting time = new NumberSetting("Time", 0.0, 500.0, 100.0, 1.0);
   private boolean awaitingVelo = false;
   private long sentAt = 0L;

   public BonzoHelper() {
      this.registerProperty(new Setting[]{this.velo, this.timeout, this.time});
   }

   @SubscribeEvent
   public void onPacketSend(Send event) {
      if (event.getPacket() instanceof class_2886 packet
         && mc.field_1724 != null
         && packet.method_12551() == class_1268.field_5808
         && Objects.equals(ItemUtils.getID(mc.field_1724.method_6047()), "BONZO_STAFF")) {
         if ((Boolean)this.velo.getValue() && mc.field_1765 instanceof class_3965 && mc.field_1724.method_36455() >= 70.0F) {
            this.awaitingVelo = true;
            this.sentAt = System.currentTimeMillis();
            long a = this.sentAt;
            TaskComponent.onMilli(((BigDecimal)this.timeout.getValue()).longValue(), () -> {
               if (a == this.sentAt && this.awaitingVelo) {
                  TickFreeze.unFreeze();
                  RSA.chat("Reached bonzo timeout!");
               }
            });
            TickFreeze.freeze();
         } else {
            TickFreeze.freeze(((BigDecimal)this.time.getValue()).longValue());
         }
      }
   }

   @SubscribeEvent
   public void onPacket(Receive event) {
      if (event.getPacket() instanceof class_2743 motionPacket
         && mc.field_1724 != null
         && motionPacket.method_11818() == mc.field_1724.method_5628()
         && this.awaitingVelo) {
         TickFreeze.unFreeze();
         this.awaitingVelo = false;
      }
   }

   public BooleanSetting getVelo() {
      return this.velo;
   }

   public NumberSetting getTimeout() {
      return this.timeout;
   }

   public NumberSetting getTime() {
      return this.time;
   }

   public boolean isAwaitingVelo() {
      return this.awaitingVelo;
   }

   public long getSentAt() {
      return this.sentAt;
   }
}
