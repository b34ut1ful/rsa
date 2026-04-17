package com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.rings;

import com.google.gson.JsonObject;
import com.ricedotwho.rsa.component.impl.managers.PacketOrderManager;
import com.ricedotwho.rsa.component.impl.managers.SwapManager;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.AutoP3;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.RingType;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.args.ArgumentManager;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.subactions.SubActionManager;
import com.ricedotwho.rsm.data.Colour;
import com.ricedotwho.rsm.data.MutableInput;
import com.ricedotwho.rsm.data.Pos;
import com.ricedotwho.rsm.utils.FileUtils;
import com.ricedotwho.rsm.utils.RotationUtils;
import java.util.Map;
import net.minecraft.class_10185;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;

public class BoomRing extends Ring {
   private final Pos target;

   public BoomRing(Pos min, Pos max, Pos target, ArgumentManager manager, SubActionManager actions) {
      super(min, max, RingType.BOOM.getRenderSizeOffset(), manager, actions);
      this.target = target;
   }

   public BoomRing(Pos min, Pos max, ArgumentManager manager, SubActionManager actions, Map<String, Object> ignored) {
      super(min, max, RingType.BOOM.getRenderSizeOffset(), manager, actions);
      if (class_310.method_1551().field_1765 instanceof class_3965 blockHitResult && blockHitResult.method_17783() != class_240.field_1333) {
         class_243 eyePos = mc.field_1724.method_73189().method_1031(0.0, mc.field_1724.method_18381(mc.field_1724.method_18376()), 0.0);
         class_243 dir = blockHitResult.method_17784().method_1020(eyePos).method_1029().method_1021(0.001F);
         this.target = new Pos(blockHitResult.method_17784());
         this.target.selfAdd(dir.field_1352, dir.field_1351, dir.field_1350);
      } else {
         this.target = null;
      }
   }

   @Override
   public RingType getType() {
      return RingType.BOOM;
   }

   @Override
   public boolean run() {
      if (!SwapManager.reserveSwap("INFINITE_SUPERBOOM_TNT", "SUPERBOOM_TNT")) {
         return false;
      } else {
         class_243 eyePos = mc.field_1724.method_73189().method_1031(0.0, mc.field_1724.method_18381(mc.field_1724.method_18376()), 0.0);
         class_243 targetVec = this.target.asVec3();
         boolean swap = SwapManager.isDesynced();
         PacketOrderManager.register(
            PacketOrderManager.STATE.ITEM_USE,
            () -> {
               class_2338 blockPos = class_2338.method_49638(targetVec);
               class_2680 blockState = class_310.method_1551().field_1687.method_8320(blockPos);
               if (blockState.method_26204() != class_2246.field_10124) {
                  class_265 voxelShape = blockState.method_26218(class_310.method_1551().field_1687, blockPos);
                  if (!voxelShape.method_1110()) {
                     class_238 blockAABB = voxelShape.method_1107();
                     class_243 center = new class_243(
                        (blockAABB.field_1323 + blockAABB.field_1320) * 0.5 + blockPos.method_10263(),
                        (blockAABB.field_1322 + blockAABB.field_1325) * 0.5 + blockPos.method_10264(),
                        (blockAABB.field_1321 + blockAABB.field_1324) * 0.5 + blockPos.method_10260()
                     );
                     class_3965 result = RotationUtils.collisionRayTrace(blockPos, blockAABB, eyePos, center);
                     if (result == null) {
                        AutoP3.modMessage("Failed to find block hit result!");
                     } else {
                        SwapManager.sendBlockC08(result, swap, false);
                     }
                  }
               }
            }
         );
         return true;
      }
   }

   @Override
   public Colour getColour() {
      return Colour.RED;
   }

   @Override
   public int getPriority() {
      return 60;
   }

   @Override
   public boolean tick(MutableInput mutableInput, class_10185 input, AutoP3 autoP3) {
      return true;
   }

   @Override
   public JsonObject serialize() {
      JsonObject obj = super.serialize();
      obj.add("target", FileUtils.getGson().toJsonTree(this.target));
      return obj;
   }

   @Override
   public void feedback() {
      AutoP3.modMessage("Booming");
   }
}
