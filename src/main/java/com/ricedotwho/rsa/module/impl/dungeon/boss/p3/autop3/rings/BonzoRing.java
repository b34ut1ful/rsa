package com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.rings;

import com.google.gson.JsonObject;
import com.ricedotwho.rsa.component.impl.managers.PacketOrderManager;
import com.ricedotwho.rsa.component.impl.managers.SwapManager;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.AutoP3;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.RingType;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.args.ArgumentManager;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.subactions.SubActionManager;
import com.ricedotwho.rsa.module.impl.movement.VelocityBuffer;
import com.ricedotwho.rsm.RSM;
import com.ricedotwho.rsm.data.Colour;
import com.ricedotwho.rsm.data.MutableInput;
import com.ricedotwho.rsm.data.Pos;
import java.util.Map;
import net.minecraft.class_10185;
import net.minecraft.class_243;
import net.minecraft.class_2743;
import net.minecraft.class_310;

public class BonzoRing extends Ring {
   private final float yaw;
   private final float pitch;
   protected byte state;
   protected static final byte END_STATE = 5;

   public BonzoRing(class_243 pos) {
      super(pos, 0.5, (double)RingType.BONZO.getRenderSizeOffset());
      this.yaw = class_310.method_1551().field_1773.method_19418().method_71155();
      this.pitch = class_310.method_1551().field_1773.method_19418().method_19329();
      this.state = 0;
   }

   public BonzoRing(Pos min, Pos max, ArgumentManager manager, SubActionManager actions, Map<String, Object> extra) {
      this(
         min,
         max,
         (Float)extra.getOrDefault("yaw", class_310.method_1551().field_1773.method_19418().method_71155()),
         (Float)extra.getOrDefault("pitch", class_310.method_1551().field_1773.method_19418().method_19329()),
         manager,
         actions
      );
   }

   public BonzoRing(Pos min, Pos max, float yaw, float pitch, ArgumentManager manager, SubActionManager actions) {
      super(min, max, RingType.BONZO.getRenderSizeOffset(), manager, actions);
      this.yaw = yaw;
      this.pitch = pitch;
   }

   @Override
   public RingType getType() {
      return RingType.BONZO;
   }

   @Override
   public void reset() {
      super.reset();
      this.state = 0;
   }

   protected void registerWaitCondition() {
      PacketOrderManager.registerReceiveListener(p -> {
         if (class_310.method_1551().field_1724 != null && this.state == 1) {
            if (p instanceof class_2743 motionPacket && motionPacket.method_11818() == class_310.method_1551().field_1724.method_5628()) {
               this.state = 5;
               return true;
            } else {
               return false;
            }
         } else {
            return true;
         }
      });
   }

   @Override
   public boolean run() {
      if (class_310.method_1551().field_1724 == null) {
         return false;
      } else {
         switch (this.state) {
            case 0:
               super.reset();
               if (!SwapManager.swapItem("BONZO_STAFF")) {
                  return false;
               }

               VelocityBuffer velocityBuffer = (VelocityBuffer)RSM.getModule(VelocityBuffer.class);
               if (!velocityBuffer.isEnabled()) {
                  velocityBuffer.onKeyToggle();
               }

               PacketOrderManager.register(PacketOrderManager.STATE.ITEM_USE, () -> SwapManager.sendAirC08(this.yaw, this.pitch, true));
               this.state = 1;
               this.registerWaitCondition();
               return false;
            case 5:
               return false;
            default:
               super.reset();
               return false;
         }
      }
   }

   @Override
   public JsonObject serialize() {
      JsonObject obj = super.serialize();
      obj.addProperty("yaw", this.yaw);
      obj.addProperty("pitch", this.pitch);
      return obj;
   }

   @Override
   public Colour getColour() {
      return Colour.MAGENTA;
   }

   @Override
   public int getPriority() {
      return 75;
   }

   @Override
   public boolean tick(MutableInput mutableInput, class_10185 input, AutoP3 autoP3) {
      return true;
   }

   @Override
   public void feedback() {
   }
}
