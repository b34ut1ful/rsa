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
import com.ricedotwho.rsm.utils.EtherUtils;
import com.ricedotwho.rsm.utils.FileUtils;
import com.ricedotwho.rsm.utils.render.render3d.type.Line;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_4050;
import net.minecraft.class_746;

public class EtherwarpNode extends Node {
   private static final int READY_DELAY_TICKS = 2;
   protected final Pos localTarget;
   protected Pos realTargetPos;
   private transient int armedTick;

   public EtherwarpNode(Pos localPos, Pos localTargetPos, AwaitManager awaits, boolean start) {
      super(localPos, awaits, start);
      this.localTarget = localTargetPos;
      this.realTargetPos = null;
      this.armedTick = -1;
   }

   @Override
   public void calculate(UniqueRoom room) {
      super.calculate(room);
      this.realTargetPos = RoomUtils.getRealPosition(this.localTarget, room.getMainRoom());
      this.armedTick = -1;
   }

   @Override
   public boolean isReadyToRun(int tickTime) {
      if (this.armedTick < 0) {
         this.armedTick = tickTime;
         return false;
      } else {
         return tickTime - this.armedTick >= 2;
      }
   }

   @Override
   public boolean run(Pos playerPos) {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         return this.cancel();
      } else {
         class_304.method_1437();
         if (!SwapManager.reserveSwap(class_1802.field_8250)) {
            return this.cancel();
         } else if (!class_310.method_1551().field_1724.method_71091().comp_3164()) {
            return this.cancel();
         } else {
            Pos playerCopy = playerPos.add(0.0, 1.54F, 0.0);
            Pos targetDirection = this.realTargetPos.subtract(playerCopy);
            Pos targetDeltaCopy = targetDirection.copy();
            boolean swap = SwapManager.isDesynced();
            PacketOrderManager.register(
               PacketOrderManager.STATE.ITEM_USE,
               () -> {
                  if ((!swap || SwapManager.checkClientItem(class_1802.field_8250)) && (swap || SwapManager.checkServerItem(class_1802.field_8250))) {
                     float[] angles = EtherUtils.getYawAndPitch(targetDeltaCopy.x, targetDeltaCopy.y, targetDeltaCopy.z);
                     if (!SwapManager.sendAirC08(angles[0], angles[1], swap, false)) {
                        RSA.chat("Failed to send ether C08!");
                     }
                  } else {
                     RSA.chat(
                        "Big fuck up! : "
                           + swap
                           + ", "
                           + class_310.method_1551().field_1724.method_31548().method_5438(SwapManager.getServerSlot()).method_7909()
                     );
                  }
               }
            );
            targetDirection.normalize();
            class_2338 etherPos = this.realTargetPos.add(targetDirection.multiply(0.001F)).asBlockPos();
            playerPos.x = etherPos.method_10263() + 0.5;
            playerPos.y = etherPos.method_10264() + 1.05;
            playerPos.z = etherPos.method_10260() + 0.5;
            return true;
         }
      }
   }

   @Override
   public void render(boolean depth) {
      class_243 playerRealPos = this.getRealPos().asVec3();
      Colour colour = this.getColour();
      Renderer3D.addTask(new Ring(playerRealPos, depth, this.getRadius(), colour));
      Renderer3D.addTask(new Line(playerRealPos, this.realTargetPos.asVec3(), colour, colour, true));
   }

   @Override
   public int getPriority() {
      return 5;
   }

   @Override
   public String getName() {
      return "etherwarp";
   }

   @Override
   public Colour getColour() {
      return this.isStart() ? AutoRoutes.getStartColour().getValue() : AutoRoutes.getEtherwarpColour().getValue();
   }

   @Override
   public JsonObject serialize() {
      JsonObject json = super.serialize();
      json.add("localTarget", FileUtils.getGson().toJsonTree(this.localTarget));
      return json;
   }

   @Override
   protected void onNodeInactive() {
      this.armedTick = -1;
   }

   @Override
   public void reset() {
      super.reset();
      this.armedTick = -1;
   }

   public static EtherwarpNode supply(UniqueRoom fullRoom, class_746 player, AwaitManager awaits, boolean start) {
      class_243 target = EtherUtils.rayTraceBlock(
         61,
         player.method_36454(),
         player.method_36455(),
         player.method_73189().method_1031(0.0, AutoRoutes.getUse1_8Height().getValue() ? 1.54F : player.method_18381(class_4050.field_18081), 0.0)
      );
      if (target == null) {
         return null;
      } else {
         Room mainRoom = fullRoom.getMainRoom();
         Pos playerRelative = RoomUtils.getRelativePosition(new Pos(player.method_73189()), mainRoom);
         Pos targetRelative = RoomUtils.getRelativePosition(new Pos(target), mainRoom);
         return new EtherwarpNode(playerRelative, targetRelative, awaits, start);
      }
   }

   public Pos getRealTargetPos() {
      return this.realTargetPos;
   }
}
