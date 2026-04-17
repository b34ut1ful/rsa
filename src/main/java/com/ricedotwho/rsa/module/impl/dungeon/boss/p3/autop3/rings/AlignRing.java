package com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.rings;

import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.AutoP3;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.MovementPredictor;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.RingType;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.args.ArgumentManager;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.subactions.SubActionManager;
import com.ricedotwho.rsm.data.Colour;
import com.ricedotwho.rsm.data.MutableInput;
import com.ricedotwho.rsm.data.Pos;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import net.minecraft.class_10185;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_304;
import net.minecraft.class_310;
import oshi.util.tuples.Pair;

public class AlignRing extends Ring {
   private Queue<Pair<Float, Boolean>> yaws;

   public AlignRing(Pos min, Pos max, double renderOffset, ArgumentManager manager, SubActionManager actions) {
      super(min, max, renderOffset, manager, actions);
   }

   public AlignRing(Pos min, Pos max, ArgumentManager manager, SubActionManager actions) {
      super(min, max, RingType.ALIGN.getRenderSizeOffset(), manager, actions);
   }

   public AlignRing(Pos min, Pos max, ArgumentManager manager, SubActionManager actions, Map<String, Object> ignored) {
      super(min, max, RingType.ALIGN.getRenderSizeOffset(), manager, actions);
   }

   @Override
   public RingType getType() {
      return RingType.ALIGN;
   }

   @Override
   public boolean run() {
      this.yaws = null;
      if (class_310.method_1551().field_1724 != null && class_310.method_1551().field_1724.method_24828()) {
         class_243 initialVelocity = class_310.method_1551().field_1724.method_18798();
         class_241 initialDisplacement = MovementPredictor.getDisplacementVector(
            new class_241((float)initialVelocity.field_1352, (float)initialVelocity.field_1350)
         );
         class_243 position = class_310.method_1551().field_1724.method_73189();
         class_243 boxCenter = this.getBox().method_1005();
         class_243 target = new class_243(boxCenter.field_1352, position.field_1351, boxCenter.field_1350);
         class_243 delta = target.method_1020(position.method_1031(initialDisplacement.field_1343, 0.0, initialDisplacement.field_1342));
         double deltaLength = delta.method_1033();
         boolean sneaking = true;
         double displacement = MovementPredictor.getDisplacementFromInput(class_310.method_1551().field_1724.method_6029() * 10.0F, sneaking);
         if (deltaLength < 0.01) {
            this.yaws = new LinkedList<>();
            return false;
         } else {
            if (deltaLength > 2.0 * displacement) {
               sneaking = false;
               displacement = MovementPredictor.getDisplacementFromInput(class_310.method_1551().field_1724.method_6029() * 10.0F, sneaking);
               if (deltaLength > 2.0 * displacement) {
                  AutoP3.modMessage("Too far!");
                  this.reset();
                  return false;
               }
            }

            class_304.method_1437();
            double yaw = (float)Math.atan2(-delta.field_1350, delta.field_1352);
            double theta = Math.acos(deltaLength / (2.0 * displacement));
            this.yaws = new LinkedList<>();
            this.yaws.add(new Pair((float)(-Math.toDegrees(yaw + theta)) - 90.0F, sneaking));
            this.yaws.add(new Pair((float)(-Math.toDegrees(yaw - theta)) - 90.0F, sneaking));
            return false;
         }
      } else {
         this.reset();
         return false;
      }
   }

   @Override
   public Colour getColour() {
      return Colour.GREEN;
   }

   @Override
   public int getPriority() {
      return 100;
   }

   protected double getPrecision() {
      return 1.0E-4;
   }

   @Override
   public boolean tick(MutableInput mutableInput, class_10185 input, AutoP3 autoP3) {
      if (this.yaws != null) {
         if (class_310.method_1551().field_1724 == null) {
            return true;
         } else if (this.yaws.isEmpty()) {
            class_243 vel = class_310.method_1551().field_1724.method_18798();
            if (vel.field_1352 == 0.0 && vel.field_1350 == 0.0) {
               return true;
            } else if (vel.method_1027() > 0.09) {
               return false;
            } else {
               class_243 boxCenter = this.getBox().method_1005();
               class_243 target = new class_243(boxCenter.field_1352, class_310.method_1551().field_1724.method_73189().field_1351, boxCenter.field_1350);
               return class_310.method_1551().field_1724.method_73189().method_1025(target) <= this.getPrecision();
            }
         } else if ((Boolean)this.yaws.peek().getB() && !class_310.method_1551().field_1724.method_71091().comp_3164()) {
            mutableInput.shift(true);
            return false;
         } else {
            autoP3.setDesync(true);
            Pair<Float, Boolean> entry = this.yaws.poll();
            class_310.method_1551().field_1724.method_36456((Float)entry.getA());
            mutableInput.shift((Boolean)entry.getB());
            mutableInput.forward(true);
            return false;
         }
      } else {
         return class_310.method_1551().field_1724 != null && !class_310.method_1551().field_1724.method_24828();
      }
   }

   @Override
   public boolean isStop() {
      return true;
   }

   @Override
   public void feedback() {
      AutoP3.modMessage("Aligning!");
   }
}
