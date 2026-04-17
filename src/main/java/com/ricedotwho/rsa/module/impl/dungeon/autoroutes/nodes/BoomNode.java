package com.ricedotwho.rsa.module.impl.dungeon.autoroutes.nodes;

import com.google.gson.JsonObject;
import com.ricedotwho.rsa.RSA;
import com.ricedotwho.rsa.component.impl.managers.PacketOrderManager;
import com.ricedotwho.rsa.component.impl.managers.SwapManager;
import com.ricedotwho.rsa.module.impl.dungeon.autoroutes.AutoRoutes;
import com.ricedotwho.rsa.module.impl.dungeon.autoroutes.AwaitManager;
import com.ricedotwho.rsa.module.impl.dungeon.autoroutes.Node;
import com.ricedotwho.rsa.utils.render3d.type.Ring;
import com.ricedotwho.rsm.component.impl.Renderer3D;
import com.ricedotwho.rsm.component.impl.map.map.Room;
import com.ricedotwho.rsm.component.impl.map.map.UniqueRoom;
import com.ricedotwho.rsm.component.impl.map.utils.RoomUtils;
import com.ricedotwho.rsm.data.Colour;
import com.ricedotwho.rsm.data.Pos;
import com.ricedotwho.rsm.utils.FileUtils;
import com.ricedotwho.rsm.utils.RotationUtils;
import com.ricedotwho.rsm.utils.render.render3d.type.FilledOutlineBox;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_746;
import net.minecraft.class_239.class_240;

public class BoomNode extends Node {
   private final Pos target;
   private Pos realTargetPosition;
   private class_238 renderAABB;

   public BoomNode(Pos localPos, Pos target, AwaitManager awaits, boolean start) {
      super(localPos, awaits, start);
      this.target = target;
      this.realTargetPosition = null;
      this.renderAABB = null;
   }

   @Override
   public void calculate(UniqueRoom room) {
      super.calculate(room);
      this.realTargetPosition = RoomUtils.getRealPosition(this.target, room.getMainRoom());
      this.renderAABB = new class_238(
         this.realTargetPosition.x - 0.1F,
         this.realTargetPosition.y - 0.1F,
         this.realTargetPosition.z - 0.1F,
         this.realTargetPosition.x + 0.1F,
         this.realTargetPosition.y + 0.1F,
         this.realTargetPosition.z + 0.1F
      );
   }

   @Override
   public boolean run(Pos playerPos) {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null && class_310.method_1551().field_1687 != null) {
         class_304.method_1437();
         if (!SwapManager.reserveSwap("INFINITE_SUPERBOOM_TNT", "SUPERBOOM_TNT")) {
            return this.cancel();
         } else {
            class_243 eyePos = class_310.method_1551().field_1724.method_73189().method_1031(0.0, 1.54F, 0.0);
            class_243 targetVec = this.realTargetPosition.asVec3();
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
                           RSA.chat("Failed to find block hit result!");
                        } else {
                           SwapManager.sendBlockC08(result, swap, false);
                        }
                     }
                  }
               }
            );
            return false;
         }
      } else {
         return this.cancel();
      }
   }

   @Override
   public void render(boolean depth) {
      Colour c = AutoRoutes.getBoomColour().getValue();
      Renderer3D.addTask(
         new Ring(new class_243(this.getRealPos().x, this.getRealPos().y + 0.2F, this.getRealPos().z), depth, this.getRadius(), this.getColour())
      );
      Renderer3D.addTask(new FilledOutlineBox(this.renderAABB, c.brighter(), c.darker(), true));
   }

   @Override
   public int getPriority() {
      return 20;
   }

   @Override
   public String getName() {
      return "boom";
   }

   @Override
   public Colour getColour() {
      return this.isStart() ? AutoRoutes.getStartColour().getValue() : AutoRoutes.getBoomColour().getValue();
   }

   @Override
   public JsonObject serialize() {
      JsonObject json = super.serialize();
      json.add("target", FileUtils.getGson().toJsonTree(this.target));
      return json;
   }

   public static BoomNode supply(UniqueRoom fullRoom, class_746 player, AwaitManager awaits, boolean start) {
      Room mainRoom = fullRoom.getMainRoom();
      Pos playerRelative = RoomUtils.getRelativePosition(new Pos(player.method_73189()), mainRoom);
      if (class_310.method_1551().field_1765 instanceof class_3965 blockHitResult && blockHitResult.method_17783() != class_240.field_1333) {
         class_243 eyePos = player.method_73189().method_1031(0.0, 1.54F, 0.0);
         class_243 dir = blockHitResult.method_17784().method_1020(eyePos).method_1029().method_1021(0.001F);
         Pos pos = new Pos(blockHitResult.method_17784());
         pos.selfAdd(dir.field_1352, dir.field_1351, dir.field_1350);
         return new BoomNode(playerRelative, RoomUtils.getRelativePosition(pos, mainRoom), awaits, start);
      } else {
         return null;
      }
   }
}
