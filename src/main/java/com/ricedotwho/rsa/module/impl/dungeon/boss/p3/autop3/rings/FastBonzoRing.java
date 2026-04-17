package com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.rings;

import com.ricedotwho.rsa.component.impl.managers.PacketOrderManager;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.RingType;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.args.ArgumentManager;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.subactions.SubActionManager;
import com.ricedotwho.rsm.data.Colour;
import com.ricedotwho.rsm.data.Pos;
import java.util.Map;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_6373;

public class FastBonzoRing extends BonzoRing {
   public FastBonzoRing(class_243 pos) {
      super(pos);
   }

   public FastBonzoRing(Pos min, Pos max, float yaw, float pitch, ArgumentManager manager, SubActionManager actions) {
      super(min, max, yaw, pitch, manager, actions);
   }

   public FastBonzoRing(Pos min, Pos max, ArgumentManager manager, SubActionManager actions, Map<String, Object> extra) {
      this(
         min,
         max,
         (Float)extra.getOrDefault("yaw", class_310.method_1551().field_1773.method_19418().method_71155()),
         (Float)extra.getOrDefault("pitch", class_310.method_1551().field_1773.method_19418().method_19329()),
         manager,
         actions
      );
   }

   @Override
   protected void registerWaitCondition() {
      PacketOrderManager.registerReceiveListener(p -> {
         if (class_310.method_1551().field_1724 == null || this.state < 1) {
            return true;
         } else if (!(p instanceof class_6373)) {
            return false;
         } else {
            this.state++;
            return this.state >= 5;
         }
      });
   }

   @Override
   public Colour getColour() {
      return Colour.PINK;
   }

   @Override
   public RingType getType() {
      return RingType.FAST_BONZO;
   }
}
