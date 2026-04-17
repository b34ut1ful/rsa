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
import com.ricedotwho.rsm.utils.EtherUtils;
import com.ricedotwho.rsm.utils.FileUtils;
import com.ricedotwho.rsm.utils.ItemUtils;
import net.minecraft.class_243;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_4050;
import net.minecraft.class_746;

public class UseNode extends Node {
   private final Pos rotationVec;
   private final String itemID;
   private boolean sneak;
   private Pos realRotationVector;

   public UseNode(Pos localPos, Pos localRotationVector, String itemID, boolean sneak, AwaitManager awaits, boolean start) {
      super(localPos, awaits, start);
      this.rotationVec = localRotationVector;
      this.itemID = itemID;
      this.sneak = sneak;
      this.realRotationVector = null;
   }

   @Override
   public void calculate(UniqueRoom room) {
      super.calculate(room);
      this.realRotationVector = RoomUtils.rotateRealFixed(this.rotationVec, room.getRotation());
   }

   @Override
   public boolean run(Pos playerPos) {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         return this.cancel();
      } else {
         class_304.method_1437();
         AutoRoutes autoRoutes = (AutoRoutes)RSM.getModule(AutoRoutes.class);
         autoRoutes.setForceSneak(!this.sneak);
         if (!SwapManager.reserveSwap(this.itemID)) {
            return this.cancel();
         } else if (class_310.method_1551().field_1724.method_71091().comp_3164() != this.sneak) {
            return this.cancel();
         } else {
            boolean swap = SwapManager.isDesynced();
            PacketOrderManager.register(
               PacketOrderManager.STATE.ITEM_USE,
               () -> {
                  if ((!swap || SwapManager.checkClientItem(this.itemID)) && (swap || SwapManager.checkServerItem(this.itemID))) {
                     float[] angles = EtherUtils.getYawAndPitch(this.realRotationVector.x, this.realRotationVector.y, this.realRotationVector.z);
                     if (!SwapManager.sendAirC08(angles[0], angles[1], swap, false)) {
                        RSA.chat("Failed to send use C08!");
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
            playerPos.selfAdd(0.0, player.method_18381(class_4050.field_18076), 0.0).selfAdd(this.realRotationVector.multiply(12.0));
            autoRoutes.setForceSneak(!this.sneak);
            return true;
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
      return "use";
   }

   @Override
   public Colour getColour() {
      return this.isStart() ? AutoRoutes.getStartColour().getValue() : AutoRoutes.getUseColour().getValue();
   }

   @Override
   public JsonObject serialize() {
      JsonObject json = super.serialize();
      json.add("rotationVec", FileUtils.getGson().toJsonTree(this.rotationVec));
      json.addProperty("itemID", this.itemID);
      json.addProperty("sneak", this.sneak);
      return json;
   }

   public static UseNode supply(UniqueRoom fullRoom, class_746 player, AwaitManager awaits, boolean start) {
      Room mainRoom = fullRoom.getMainRoom();
      Pos playerRelative = RoomUtils.getRelativePosition(new Pos(player.method_73189()), mainRoom);
      Pos targetRelative = RoomUtils.rotateRelativeFixed(new Pos(player.method_5828(1.0F)), fullRoom.getRotation());
      String itemID = ItemUtils.getID(class_310.method_1551().field_1724.method_31548().method_7391());
      return itemID.isBlank() ? null : new UseNode(playerRelative, targetRelative, itemID, false, awaits, start);
   }

   public void setSneak(boolean sneak) {
      this.sneak = sneak;
   }
}
