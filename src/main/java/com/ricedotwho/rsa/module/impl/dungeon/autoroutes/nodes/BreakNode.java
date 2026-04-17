package com.ricedotwho.rsa.module.impl.dungeon.autoroutes.nodes;

import com.google.gson.JsonObject;
import com.ricedotwho.rsa.RSA;
import com.ricedotwho.rsa.component.impl.managers.SwapManager;
import com.ricedotwho.rsa.module.impl.dungeon.DungeonBreaker;
import com.ricedotwho.rsa.module.impl.dungeon.autoroutes.AutoRoutes;
import com.ricedotwho.rsa.module.impl.dungeon.autoroutes.AwaitManager;
import com.ricedotwho.rsa.module.impl.dungeon.autoroutes.Node;
import com.ricedotwho.rsa.utils.InteractUtils;
import com.ricedotwho.rsa.utils.render3d.type.Ring;
import com.ricedotwho.rsm.RSM;
import com.ricedotwho.rsm.component.impl.Renderer3D;
import com.ricedotwho.rsm.component.impl.map.Map;
import com.ricedotwho.rsm.component.impl.map.map.Room;
import com.ricedotwho.rsm.component.impl.map.map.UniqueRoom;
import com.ricedotwho.rsm.component.impl.map.utils.RoomUtils;
import com.ricedotwho.rsm.component.impl.task.TaskComponent;
import com.ricedotwho.rsm.data.Colour;
import com.ricedotwho.rsm.data.Pos;
import com.ricedotwho.rsm.utils.Accessor;
import com.ricedotwho.rsm.utils.FileUtils;
import com.ricedotwho.rsm.utils.render.render3d.type.FilledBox;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_124;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_746;
import net.minecraft.class_239.class_240;

public class BreakNode extends Node implements Accessor {
   private final List<Pos> blocks;
   private List<Pos> rotated = null;
   private boolean running = false;

   public BreakNode(Pos localPos, AwaitManager awaits, boolean start) {
      super(localPos, awaits, start);
      this.blocks = new ArrayList<>();
   }

   public BreakNode(Pos localPos, List<Pos> blocks, AwaitManager awaits, boolean start) {
      super(localPos, awaits, start);
      this.blocks = blocks;
   }

   @Override
   public void calculate(UniqueRoom room) {
      super.calculate(room);
      this.rotated = new ArrayList<>();
      this.rotated = this.blocks.stream().map(pos -> RoomUtils.getRealPositionFixed(pos, room.getMainRoom())).toList();
   }

   @Override
   public boolean run(Pos playerPos) {
      if (!SwapManager.reserveSwap("DUNGEONBREAKER")) {
         return this.cancel();
      } else if (this.running) {
         return this.cancel();
      } else {
         List<Pos> f = this.rotated
            .stream()
            .filter(
               p -> {
                  class_2338 bp = p.asBlockPos();
                  class_2680 state = mc.field_1687.method_8320(bp);
                  class_265 shape = state.method_26218(mc.field_1687, bp);
                  return !shape.method_1110()
                     && DungeonBreaker.canInstantMine(state)
                     && InteractUtils.faceDistance(
                           p.asVec3(), mc.field_1724.method_73189().method_1031(0.0, mc.field_1724.method_18381(mc.field_1724.method_18376()), 0.0)
                        )
                        <= 25.0;
               }
            )
            .toList();
         if (f.isEmpty()) {
            return true;
         } else {
            this.running = true;
            if ((Boolean)AutoRoutes.getZeroTickBreak().getValue()) {
               for (Pos pos : f) {
                  InteractUtils.breakBlock(pos, true, SwapManager.isDesynced());
               }

               this.running = false;
            } else {
               for (int i = 0; i < f.size(); i++) {
                  Pos block = f.get(i);
                  TaskComponent.onTick(i, () -> InteractUtils.breakBlock(block, true, SwapManager.isDesynced()));
               }

               TaskComponent.onTick(f.size(), () -> this.running = false);
            }

            return this.cancel();
         }
      }
   }

   @Override
   public boolean cancel() {
      this.reset();
      return false;
   }

   @Override
   public void render(boolean depth) {
      Renderer3D.addTask(new Ring(this.getRealPos().asVec3(), depth, this.getRadius(), this.getColour()));
      if (this.rotated != null && !this.rotated.isEmpty()) {
         Colour colour = AutoRoutes.getBreakColour().getValue().alpha(90.0F);

         for (Pos pos : this.rotated) {
            class_2338 bp = pos.asBlockPos();
            class_2680 state = mc.field_1687.method_8320(bp);
            class_265 shape = state.method_26218(mc.field_1687, bp);
            if (!shape.method_1110()) {
               class_238 aabb = shape.method_1107().method_996(bp);
               Renderer3D.addTask(new FilledBox(aabb, colour, true));
            }
         }
      }
   }

   @Override
   public int getPriority() {
      return 18;
   }

   @Override
   public String getName() {
      return "break";
   }

   @Override
   public Colour getColour() {
      return this.isStart() ? AutoRoutes.getStartColour().getValue() : AutoRoutes.getBreakColour().getValue();
   }

   @Override
   public JsonObject serialize() {
      JsonObject json = super.serialize();
      json.add("blocks", FileUtils.getGson().toJsonTree(this.blocks));
      return json;
   }

   public static BreakNode supply(UniqueRoom fullRoom, class_746 player, AwaitManager awaits, boolean start) {
      Room mainRoom = fullRoom.getMainRoom();
      Pos playerRelative = RoomUtils.getRelativePosition(new Pos(player.method_73189()), mainRoom);
      return new BreakNode(playerRelative, awaits, start);
   }

   public void addOrRemoveBlock() {
      if (Map.getCurrentRoom() == null) {
         RSA.chat(class_124.field_1061 + "Room is null!");
      }

      if (class_310.method_1551().field_1765 instanceof class_3965 blockHitResult && blockHitResult.method_17783() != class_240.field_1333) {
         Pos pos = new Pos(blockHitResult.method_17777());
         Pos relPos = RoomUtils.getRelativePositionFixed(pos, Map.getCurrentRoom().getUniqueRoom().getMainRoom());
         if (this.blocks.contains(relPos)) {
            this.blocks.remove(relPos);
            RSA.chat(class_124.field_1061 + "Removed " + relPos.toChatString() + " from break node");
         } else {
            this.blocks.add(relPos);
            RSA.chat(class_124.field_1060 + "Added " + relPos.toChatString() + " to break node!");
         }

         this.calculate(Map.getCurrentRoom().getUniqueRoom());
         ((AutoRoutes)RSM.getModule(AutoRoutes.class)).save();
      } else {
         RSA.chat(class_124.field_1061 + "Not looking at a block");
      }
   }

   public List<Pos> getBlocks() {
      return this.blocks;
   }
}
