package com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.rings;

import com.google.gson.JsonObject;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.AutoP3;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.RingType;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.args.ArgumentManager;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.subactions.SubActionManager;
import com.ricedotwho.rsm.data.Colour;
import com.ricedotwho.rsm.data.MutableInput;
import com.ricedotwho.rsm.data.Pos;
import java.util.Map;
import net.minecraft.class_10185;
import net.minecraft.class_310;

public class WalkRing extends Ring {
   private final float yaw;

   @Override
   public RingType getType() {
      return RingType.WALK;
   }

   public WalkRing(Pos min, Pos max, ArgumentManager manage, SubActionManager actions, Map<String, Object> extra) {
      this(min, max, (Float)extra.getOrDefault("yaw", class_310.method_1551().field_1773.method_19418().method_71155()), manage, actions);
   }

   public WalkRing(Pos min, Pos max, float yaw, ArgumentManager manage, SubActionManager actions) {
      super(min, max, RingType.WALK.getRenderSizeOffset(), manage, actions);
      this.yaw = yaw;
   }

   @Override
   public boolean run() {
      return true;
   }

   @Override
   public Colour getColour() {
      return Colour.CYAN;
   }

   @Override
   public int getPriority() {
      return 50;
   }

   @Override
   public boolean tick(MutableInput mutableInput, class_10185 input, AutoP3 autoP3) {
      if (this.hasInputPressed(input)) {
         return true;
      } else {
         autoP3.setDesync(true);
         if ((Boolean)autoP3.getStrafe().getValue() && !mc.field_1724.method_24828()) {
            mc.field_1724.method_36456(this.yaw - 45.0F);
            mutableInput.right(true);
         } else {
            mc.field_1724.method_36456(this.yaw);
         }

         mutableInput.forward(true);
         mutableInput.sprint(true);
         return false;
      }
   }

   private boolean hasInputPressed(class_10185 input) {
      return input.comp_3159() || input.comp_3160() || input.comp_3161() || input.comp_3162() || input.comp_3163();
   }

   @Override
   public JsonObject serialize() {
      JsonObject obj = super.serialize();
      obj.addProperty("yaw", this.yaw);
      return obj;
   }

   @Override
   public boolean shouldStop() {
      return true;
   }

   @Override
   public void feedback() {
      AutoP3.modMessage("Walking");
   }

   public float getYaw() {
      return this.yaw;
   }
}
