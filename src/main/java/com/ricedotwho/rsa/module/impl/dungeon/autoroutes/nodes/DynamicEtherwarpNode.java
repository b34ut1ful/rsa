package com.ricedotwho.rsa.module.impl.dungeon.autoroutes.nodes;

import com.ricedotwho.rsa.RSA;
import com.ricedotwho.rsa.component.impl.managers.PacketOrderManager;
import com.ricedotwho.rsa.component.impl.managers.SwapManager;
import com.ricedotwho.rsa.module.impl.dungeon.DynamicRoutes;
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
import com.ricedotwho.rsm.utils.render.render3d.type.Line;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class DynamicEtherwarpNode extends Node {
   private final float yaw;
   private final float pitch;
   private final boolean await;
   private final int priority;
   private class_243 target;
   private transient int armedTick = -1;

   public DynamicEtherwarpNode(Pos localPos, float yaw, float pitch, boolean await, int priority, AwaitManager awaits, boolean start) {
      super(localPos, null, false);
      this.yaw = yaw;
      this.pitch = pitch;
      this.await = await;
      this.priority = priority;
   }

   public DynamicEtherwarpNode(Pos localPos, float yaw, float pitch, boolean await, int priority) {
      this(localPos, yaw, pitch, await, priority, null, false);
   }

   @Override
   public boolean shouldAwait() {
      return this.await;
   }

   @Override
   protected boolean shouldUseCenterOnly() {
      return DynamicRoutes.shouldCenterOnly();
   }

   @Override
   public boolean updateNodeState(Pos playerPos, int tickTime) {
      if (tickTime <= this.getLastTickTime()) {
         return false;
      } else {
         if (this.isInNode(playerPos) && this.armedTick < 0) {
            this.armedTick = tickTime;
         }

         return super.updateNodeState(playerPos, tickTime);
      }
   }

   @Override
   public boolean isReadyToRun(int tickTime) {
      int delay = DynamicRoutes.getEtherwarpTickDelay();
      return delay <= 0 || this.armedTick >= 0 && tickTime - this.armedTick >= delay;
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
            boolean swap = SwapManager.isDesynced();
            PacketOrderManager.register(
               PacketOrderManager.STATE.ITEM_USE,
               () -> {
                  if ((!swap || SwapManager.checkClientItem(class_1802.field_8250)) && (swap || SwapManager.checkServerItem(class_1802.field_8250))) {
                     if (!SwapManager.sendAirC08(this.yaw, this.pitch, swap, false)) {
                        RSA.chat("Failed to send dyn ether C08!");
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
            class_2338 etherPos = EtherUtils.fastGetEtherFromOrigin(playerCopy.asVec3(), this.yaw, this.pitch, 61);
            if (etherPos == null) {
               return false;
            } else {
               playerPos.x = etherPos.method_10263() + 0.5;
               playerPos.y = etherPos.method_10264() + 1.05;
               playerPos.z = etherPos.method_10260() + 0.5;
               return true;
            }
         }
      }
   }

   @Override
   public void calculate(UniqueRoom room) {
      this.realPos = this.localPos;
      this.armedTick = -1;
      class_243 origin = this.localPos.add(0.0, 1.5899999618530274, 0.0).asVec3();
      this.target = EtherUtils.rayTraceBlock(61, this.yaw, this.pitch, origin);
      if (this.target == null) {
         class_2338 pos = EtherUtils.fastGetEtherFromOrigin(origin, this.yaw, this.pitch, 61);
         if (pos == null) {
            this.target = class_243.field_1353;
            return;
         }

         this.target = pos.method_46558();
      }
   }

   @Override
   public int getPriority() {
      return this.priority;
   }

   @Override
   public String getName() {
      return "dynamicEther";
   }

   @Override
   public void render(boolean depth) {
      class_243 position = this.getRealPos().asVec3();
      Colour colour = this.getColour();
      Renderer3D.addTask(new Ring(position, depth, this.getRadius(), colour));
      Renderer3D.addTask(new Line(position, this.target, colour, colour, true));
   }

   @Override
   public Colour getColour() {
      return DynamicRoutes.getNodeColor().getValue();
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

   public static DynamicEtherwarpNode fromBlockPos(class_2338 pos, float yaw, float pitch, boolean await, int priority) {
      Pos nodePos = new Pos(pos.method_61082()).selfAdd(0.0, 1.0, 0.0);
      return new DynamicEtherwarpNode(nodePos, yaw, pitch, await, Integer.MAX_VALUE - priority);
   }

   public static DynamicEtherwarpNode supply(UniqueRoom fullRoom, class_746 player) {
      Room mainRoom = fullRoom.getMainRoom();
      Pos playerRelative = RoomUtils.getRelativePosition(new Pos(player.method_23317(), player.method_23318(), player.method_23321()), mainRoom);
      return new DynamicEtherwarpNode(playerRelative, player.method_36454(), player.method_36455(), false, 0);
   }
}
