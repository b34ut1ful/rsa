package com.ricedotwho.rsa.module.impl.dungeon.autoroutes.nodes;

import com.google.gson.JsonObject;
import com.ricedotwho.rsa.component.impl.managers.PacketOrderManager;
import com.ricedotwho.rsa.component.impl.managers.SwapManager;
import com.ricedotwho.rsa.module.impl.dungeon.autoroutes.AutoRoutes;
import com.ricedotwho.rsa.module.impl.dungeon.autoroutes.AwaitManager;
import com.ricedotwho.rsa.module.impl.dungeon.autoroutes.Node;
import com.ricedotwho.rsa.utils.render3d.type.Ring;
import com.ricedotwho.rsm.component.impl.Renderer3D;
import com.ricedotwho.rsm.component.impl.map.Map;
import com.ricedotwho.rsm.component.impl.map.map.Room;
import com.ricedotwho.rsm.component.impl.map.map.UniqueRoom;
import com.ricedotwho.rsm.component.impl.map.utils.RoomUtils;
import com.ricedotwho.rsm.data.Colour;
import com.ricedotwho.rsm.data.Pos;
import com.ricedotwho.rsm.utils.ItemUtils;
import com.ricedotwho.rsm.utils.Utils;
import net.minecraft.class_1420;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;

public class BatNode extends Node {
   private final float yaw;
   private final float pitch;

   public BatNode(Pos localPos, float yaw, float pitch, AwaitManager awaits, boolean start) {
      super(localPos, awaits, start);
      this.yaw = yaw;
      this.pitch = pitch;
   }

   @Override
   public boolean run(Pos playerPos) {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null && class_310.method_1551().field_1687 != null && Map.getCurrentRoom() != null && Map.getCurrentRoom().getUniqueRoom() != null) {
         class_304.method_1437();
         if (!SwapManager.reserveSwap(BatNode::isWitherBlade) && !SwapManager.reserveSwap(class_1802.field_17500)) {
            return this.cancel();
         } else if (!this.hasBatNear(playerPos, class_310.method_1551().field_1687)) {
            return this.cancel();
         } else {
            boolean swap = SwapManager.isDesynced();
            PacketOrderManager.register(PacketOrderManager.STATE.ITEM_USE, () -> SwapManager.sendAirC08(this.yaw, this.pitch, swap, false));
            return false;
         }
      } else {
         return this.cancel();
      }
   }

   private boolean hasBatNear(Pos player, class_638 level) {
      class_243 playerPos = player.asVec3();
      class_238 aabb = new class_238(playerPos, playerPos).method_1009(10.0, 10.0, 10.0);
      return level.method_18467(class_1420.class, aabb).stream().anyMatch(bat -> bat.method_5707(playerPos) < 100.0);
   }

   private static boolean isWitherBlade(class_1799 itemStack) {
      if (itemStack == null) {
         return false;
      } else {
         String sbId = ItemUtils.getID(itemStack);
         return sbId.isEmpty()
            ? false
            : Utils.equalsOneOf(sbId, new Object[]{"NECRON_BLADE", "SCYLLA", "HYPERION", "VALKYRIE", "ASTRAEA"})
               && ItemUtils.getCustomData(itemStack).method_68569("ability_scroll").size() == 3;
      }
   }

   @Override
   public void render(boolean depth) {
      Renderer3D.addTask(
         new Ring(new class_243(this.getRealPos().x, this.getRealPos().y + 0.3F, this.getRealPos().z), depth, this.getRadius(), this.getColour())
      );
   }

   @Override
   public int getPriority() {
      return 16;
   }

   @Override
   public String getName() {
      return "bat";
   }

   @Override
   public Colour getColour() {
      return this.isStart() ? AutoRoutes.getStartColour().getValue() : AutoRoutes.getBatColour().getValue();
   }

   @Override
   public JsonObject serialize() {
      JsonObject json = super.serialize();
      json.addProperty("yaw", this.yaw);
      json.addProperty("pitch", this.pitch);
      return json;
   }

   public static BatNode supply(UniqueRoom fullRoom, class_746 player, AwaitManager awaits, boolean start) {
      Room mainRoom = fullRoom.getMainRoom();
      Pos playerRelative = RoomUtils.getRelativePosition(new Pos(player.method_73189()), mainRoom);
      return new BatNode(playerRelative, 0.0F, 90.0F, awaits, start);
   }
}
