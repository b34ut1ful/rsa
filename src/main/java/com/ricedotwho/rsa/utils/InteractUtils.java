package com.ricedotwho.rsa.utils;

import com.ricedotwho.rsa.component.impl.managers.PacketOrderManager;
import com.ricedotwho.rsa.component.impl.managers.SwapManager;
import com.ricedotwho.rsm.data.Pos;
import com.ricedotwho.rsm.utils.Accessor;
import com.ricedotwho.rsm.utils.MathUtils;
import com.ricedotwho.rsm.utils.RotationUtils;
import net.minecraft.class_1268;
import net.minecraft.class_1269;
import net.minecraft.class_1297;
import net.minecraft.class_1799;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_1269.class_9860;
import net.minecraft.class_1269.class_9861;
import net.minecraft.class_2846.class_2847;

public final class InteractUtils implements Accessor {
   public static final double BLOCK_RANGE = 25.0;
   public static final double ENTITY_RANGE = 4.0;

   public static boolean interactOnEntity(class_1297 entity) {
      if (mc.field_1724 == null) {
         return false;
      } else {
         class_243 eyePos = mc.field_1724.method_73189().method_1031(0.0, mc.field_1724.method_5751(), 0.0);
         class_243 location = MathUtils.clamp(entity.method_5829(), eyePos).method_1023(entity.method_23317(), entity.method_23318(), entity.method_23321());
         return interactOnEntity(entity, location);
      }
   }

   public static boolean interactOnEntity(class_1297 entity, class_243 location) {
      if (mc.field_1724 != null && mc.field_1687 != null && mc.field_1761 != null) {
         for (class_1268 interactionHand : class_1268.values()) {
            class_1799 itemStack = mc.field_1724.method_5998(interactionHand);
            if (!itemStack.method_45435(mc.field_1687.method_45162())) {
               return false;
            }

            class_1269 interactionResult = mc.field_1761.method_2917(mc.field_1724, entity, new class_3966(entity, location), interactionHand);
            if (!interactionResult.method_23665()) {
               interactionResult = mc.field_1761.method_2905(mc.field_1724, entity, interactionHand);
            }

            if (interactionResult instanceof class_9860 success) {
               if (success.comp_2909() == class_9861.field_52427) {
                  mc.field_1724.method_6104(interactionHand);
               }

               return true;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean interactOnBlock(class_2338 pos, boolean swing) {
      if (mc.field_1724 != null && mc.field_1687 != null) {
         class_243 eyePos = mc.field_1724.method_73189().method_1031(0.0, mc.field_1724.method_5751(), 0.0);
         return interactOnBlock(pos, eyePos, swing);
      } else {
         return false;
      }
   }

   public static boolean interactOnBlock(class_2338 pos, class_243 eyePos, boolean swing) {
      if (mc.field_1687 == null) {
         return false;
      } else {
         class_2680 blockState = mc.field_1687.method_8320(pos);
         class_238 blockAABB = blockState.method_26218(mc.field_1687, pos).method_1107();
         class_243 center = new class_243(
            (blockAABB.field_1323 + blockAABB.field_1320) * 0.5 + pos.method_10263(),
            (blockAABB.field_1322 + blockAABB.field_1325) * 0.5 + pos.method_10264(),
            (blockAABB.field_1321 + blockAABB.field_1324) * 0.5 + pos.method_10260()
         );
         class_3965 result = RotationUtils.collisionRayTrace(pos, blockAABB, eyePos, center);
         if (result == null) {
            return false;
         } else {
            SwapManager.sendBlockC08(result.method_17784(), result.method_17780(), swing, true);
            return true;
         }
      }
   }

   public static boolean interactOnBlock(class_2338 pos, class_243 eyePos, class_243 hit, boolean swing) {
      if (mc.field_1687 == null) {
         return false;
      } else {
         class_2680 blockState = mc.field_1687.method_8320(pos);
         class_238 blockAABB = blockState.method_26218(mc.field_1687, pos).method_1107();
         class_3965 result = RotationUtils.collisionRayTrace(pos, blockAABB, eyePos, hit);
         if (result == null) {
            return false;
         } else {
            SwapManager.sendBlockC08(result.method_17784(), result.method_17780(), swing, true);
            return true;
         }
      }
   }

   public static boolean attackEntity(class_1297 entity) {
      if (mc.field_1724 != null && mc.field_1687 != null && mc.field_1761 != null) {
         class_1799 itemStack = mc.field_1724.method_5998(class_1268.field_5808);
         if (!itemStack.method_45435(mc.field_1687.method_45162())) {
            return false;
         } else {
            mc.field_1761.method_2918(mc.field_1724, entity);
            mc.field_1724.method_6104(class_1268.field_5808);
            return true;
         }
      } else {
         return false;
      }
   }

   public static void breakBlock(Pos pos, boolean remove, boolean sync) {
      if (!(faceDistance(pos.asVec3(), mc.field_1724.method_73189().method_1031(0.0, mc.field_1724.method_18381(mc.field_1724.method_18376()), 0.0)) > 25.0)) {
         class_2350 dir = closestFace(pos.asVec3(), mc.field_1724.method_33571());
         PacketOrderManager.register(PacketOrderManager.STATE.ATTACK, () -> {
            class_2338 bp = pos.asBlockPos();
            SwapManager.sendC07(bp, class_2847.field_12968, dir, true, sync);
            if (remove) {
               mc.field_1687.method_8652(bp, class_2246.field_10124.method_9564(), 0);
            }
         });
      }
   }

   public static double faceDistance(class_243 pos, class_243 player) {
      double minDist = Double.MAX_VALUE;

      for (class_2350 face : class_2350.values()) {
         double offsetX = 0.0;
         double offsetY = 0.0;
         double offsetZ = 0.0;
         switch (face) {
            case field_11033:
               offsetY = -0.5;
               break;
            case field_11036:
               offsetY = 0.5;
               break;
            case field_11043:
               offsetZ = -0.5;
               break;
            case field_11035:
               offsetZ = 0.5;
               break;
            case field_11039:
               offsetX = -0.5;
               break;
            case field_11034:
               offsetX = 0.5;
         }

         class_243 faceVec = pos.method_1031(0.5 + offsetX, 0.5 + offsetY, 0.5 + offsetZ);
         double dist = player.method_1025(faceVec);
         if (dist < minDist) {
            minDist = dist;
         }
      }

      return minDist;
   }

   public static class_243 getFaceVec(class_2350 direction, class_243 pos) {
      double offsetX = 0.0;
      double offsetY = 0.0;
      double offsetZ = 0.0;
      switch (direction) {
         case field_11033:
            offsetY = -0.5;
            break;
         case field_11036:
            offsetY = 0.5;
            break;
         case field_11043:
            offsetZ = -0.5;
            break;
         case field_11035:
            offsetZ = 0.5;
            break;
         case field_11039:
            offsetX = -0.5;
            break;
         case field_11034:
            offsetX = 0.5;
      }

      return pos.method_1031(0.5 + offsetX, 0.5 + offsetY, 0.5 + offsetZ);
   }

   public static class_2350 closestFace(class_243 pos, class_243 player) {
      double minDist = Double.MAX_VALUE;
      class_2350 closest = class_2350.field_11036;

      for (class_2350 face : class_2350.values()) {
         double offsetX = 0.0;
         double offsetY = 0.0;
         double offsetZ = 0.0;
         switch (face) {
            case field_11033:
               offsetY = -0.5;
               break;
            case field_11036:
               offsetY = 0.5;
               break;
            case field_11043:
               offsetZ = -0.5;
               break;
            case field_11035:
               offsetZ = 0.5;
               break;
            case field_11039:
               offsetX = -0.5;
               break;
            case field_11034:
               offsetX = 0.5;
         }

         class_243 faceVec = pos.method_1031(0.5 + offsetX, 0.5 + offsetY, 0.5 + offsetZ);
         double dist = player.method_1025(faceVec);
         if (dist < minDist) {
            minDist = dist;
            closest = face;
         }
      }

      return closest;
   }

   private InteractUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
