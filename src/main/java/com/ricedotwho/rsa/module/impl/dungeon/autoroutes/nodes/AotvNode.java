package com.ricedotwho.rsa.module.impl.dungeon.autoroutes.nodes;

import com.google.gson.JsonObject;
import com.ricedotwho.rsa.RSA;
import com.ricedotwho.rsa.component.impl.managers.PacketOrderManager;
import com.ricedotwho.rsa.component.impl.managers.SwapManager;
import com.ricedotwho.rsa.module.impl.dungeon.autoroutes.AutoRoutes;
import com.ricedotwho.rsa.module.impl.dungeon.autoroutes.AwaitManager;
import com.ricedotwho.rsa.module.impl.dungeon.autoroutes.Node;
import com.ricedotwho.rsa.utils.render3d.type.Ring;
import com.ricedotwho.rsm.RSM;
import com.ricedotwho.rsm.component.impl.Renderer3D;
import com.ricedotwho.rsm.component.impl.map.map.Room;
import com.ricedotwho.rsm.component.impl.map.map.UniqueRoom;
import com.ricedotwho.rsm.component.impl.map.utils.RoomUtils;
import com.ricedotwho.rsm.data.Colour;
import com.ricedotwho.rsm.data.Pos;
import com.ricedotwho.rsm.utils.Accessor;
import com.ricedotwho.rsm.utils.EtherUtils;
import com.ricedotwho.rsm.utils.FileUtils;
import com.ricedotwho.rsm.utils.ItemUtils;
import net.minecraft.class_1802;
import net.minecraft.class_243;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class AotvNode extends Node implements Accessor {
   private static final int READY_DELAY_TICKS = 2;
   private final Pos rotationVec;
   private Pos realRotationVector;
   private transient int armedTick;

   public AotvNode(Pos localPos, Pos localRotationVector, AwaitManager awaits, boolean start) {
      super(localPos, awaits, start);
      this.rotationVec = localRotationVector;
      this.realRotationVector = null;
      this.armedTick = -1;
   }

   @Override
   public void calculate(UniqueRoom room) {
      super.calculate(room);
      this.realRotationVector = RoomUtils.rotateRealFixed(this.rotationVec, room.getRotation());
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
         AutoRoutes autoRoutes = (AutoRoutes)RSM.getModule(AutoRoutes.class);
         autoRoutes.setForceSneak(true);
         if (!SwapManager.reserveSwap(class_1802.field_8250)) {
            return this.cancel();
         } else if (class_310.method_1551().field_1724.method_71091().comp_3164()) {
            return this.cancel();
         } else {
            float[] angles = EtherUtils.getYawAndPitch(this.realRotationVector.x, this.realRotationVector.y, this.realRotationVector.z);
            boolean swap = SwapManager.isDesynced();
            PacketOrderManager.register(
               PacketOrderManager.STATE.ITEM_USE,
               () -> {
                  if ((!swap || SwapManager.checkClientItem(class_1802.field_8250)) && (swap || SwapManager.checkServerItem(class_1802.field_8250))) {
                     if (!SwapManager.sendAirC08(angles[0], angles[1], swap, false)) {
                        RSA.chat("Failed to send ether C08!");
                     } else {
                        autoRoutes.setForceSneak(false);
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
            int slot = SwapManager.getItemSlot(class_1802.field_8250);
            if (slot == -1) {
               return false;
            } else {
               Pos prediction = EtherUtils.predictTeleport(
                  8 + ItemUtils.getTunerDistance(mc.field_1724.method_31548().method_5438(slot)), playerPos, angles[0], angles[1]
               );
               if (prediction == null) {
                  return false;
               } else {
                  playerPos.set(prediction);
                  return true;
               }
            }
         }
      }
   }

   @Override
   public void render(boolean depth) {
      class_243 playerRealPos = this.getRealPos().asVec3();
      Renderer3D.addTask(new Ring(playerRealPos.method_1031(0.0, 0.1, 0.0), depth, this.getRadius(), this.getColour()));
   }

   @Override
   public int getPriority() {
      return 8;
   }

   @Override
   public String getName() {
      return "aotv";
   }

   @Override
   public Colour getColour() {
      return this.isStart() ? AutoRoutes.getStartColour().getValue() : AutoRoutes.getAotvColour().getValue();
   }

   @Override
   public JsonObject serialize() {
      JsonObject json = super.serialize();
      json.add("rotationVec", FileUtils.getGson().toJsonTree(this.rotationVec));
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

   public static AotvNode supply(UniqueRoom fullRoom, class_746 player, AwaitManager awaits, boolean start) {
      Room mainRoom = fullRoom.getMainRoom();
      Pos playerRelative = RoomUtils.getRelativePosition(new Pos(player.method_73189()), mainRoom);
      Pos targetRelative = RoomUtils.rotateRelativeFixed(new Pos(player.method_5828(1.0F)), fullRoom.getRotation());
      return new AotvNode(playerRelative, targetRelative, awaits, start);
   }
}
