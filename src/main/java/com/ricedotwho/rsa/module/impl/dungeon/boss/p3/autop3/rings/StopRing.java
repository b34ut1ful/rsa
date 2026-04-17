package com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.rings;

import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.AutoP3;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.MovementPredictor;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.RingType;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.args.ArgumentManager;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.subactions.SubActionManager;
import com.ricedotwho.rsm.data.Colour;
import com.ricedotwho.rsm.data.MutableInput;
import com.ricedotwho.rsm.data.Pos;
import java.util.Map;
import net.minecraft.class_10185;
import net.minecraft.class_243;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3532;

public class StopRing extends Ring {
   public StopRing(Pos min, Pos max, ArgumentManager manage, SubActionManager actions) {
      super(min, max, RingType.STOP.getRenderSizeOffset(), manage, actions);
   }

   public StopRing(Pos min, Pos max, ArgumentManager manage, SubActionManager actions, Map<String, Object> ignored) {
      super(min, max, RingType.STOP.getRenderSizeOffset(), manage, actions);
   }

   @Override
   public RingType getType() {
      return RingType.STOP;
   }

   @Override
   public boolean run() {
      class_304.method_1437();
      return false;
   }

   @Override
   public Colour getColour() {
      return Colour.RED;
   }

   @Override
   public int getPriority() {
      return 110;
   }

   @Override
   public void reset() {
      super.reset();
   }

   @Override
   public boolean tick(MutableInput mutableInput, class_10185 input, AutoP3 autoP3) {
      if (class_310.method_1551().field_1724 == null) {
         return true;
      } else {
         class_243 velocity = class_310.method_1551().field_1724.method_18798();
         double speedSq = velocity.method_37268();
         if (speedSq < 1.0E-4) {
            return true;
         } else {
            float yaw = (float)Math.toRadians(class_310.method_1551().field_1724.method_36454());
            float fwdX = -class_3532.method_15374(yaw);
            float fwdZ = class_3532.method_15362(yaw);
            float rightX = class_3532.method_15362(yaw);
            float rightZ = class_3532.method_15374(yaw);
            double fwdDot = velocity.field_1352 * fwdX + velocity.field_1350 * fwdZ;
            double rightDot = velocity.field_1352 * rightX + velocity.field_1350 * rightZ;
            double accel = class_310.method_1551().field_1724.method_6029() * 0.98;
            double baseNextSq = MovementPredictor.squaredAfterTick(fwdDot, rightDot, 0.0, 0.0);
            boolean pressFwd = fwdDot < -0.01 && MovementPredictor.squaredAfterTick(fwdDot, rightDot, accel, 0.0) < baseNextSq;
            boolean pressBack = fwdDot > 0.01 && MovementPredictor.squaredAfterTick(fwdDot, rightDot, -accel, 0.0) < baseNextSq;
            boolean pressLeft = rightDot > 0.01 && MovementPredictor.squaredAfterTick(fwdDot, rightDot, 0.0, -accel) < baseNextSq;
            boolean pressRight = rightDot < -0.01 && MovementPredictor.squaredAfterTick(fwdDot, rightDot, 0.0, accel) < baseNextSq;
            mutableInput.forward(pressFwd);
            mutableInput.backward(pressBack);
            mutableInput.left(pressLeft);
            mutableInput.right(pressRight);
            return true;
         }
      }
   }

   @Override
   public boolean isStop() {
      return true;
   }

   @Override
   public void feedback() {
      AutoP3.modMessage("Stopping");
   }
}
