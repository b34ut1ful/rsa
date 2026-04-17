package com.ricedotwho.rsa.module.impl.dungeon.device;

import com.ricedotwho.rsa.component.impl.managers.PacketOrderManager;
import com.ricedotwho.rsa.utils.InteractUtils;
import com.ricedotwho.rsm.RSM;
import com.ricedotwho.rsm.component.impl.location.Island;
import com.ricedotwho.rsm.component.impl.location.Location;
import com.ricedotwho.rsm.component.impl.map.handler.Dungeon;
import com.ricedotwho.rsm.data.Phase7;
import com.ricedotwho.rsm.data.Pos;
import com.ricedotwho.rsm.event.api.SubscribeEvent;
import com.ricedotwho.rsm.event.impl.game.ClientTickEvent.Start;
import com.ricedotwho.rsm.event.impl.world.WorldEvent.Load;
import com.ricedotwho.rsm.module.Module;
import com.ricedotwho.rsm.module.api.Category;
import com.ricedotwho.rsm.module.api.ModuleInfo;
import com.ricedotwho.rsm.module.impl.render.ClickGUI;
import com.ricedotwho.rsm.utils.DungeonUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_1533;
import net.minecraft.class_1744;
import net.minecraft.class_238;
import net.minecraft.class_243;
import org.jetbrains.annotations.NotNull;

@ModuleInfo(aliases = "Align Aura", id = "AlignAura", category = Category.DUNGEONS)
public class AlignAura extends Module {
   private static final int[][] SOLUTIONS = new int[][]{
      {7, 7, 7, 7, -1, 1, -1, -1, -1, -1, 1, 3, 3, 3, 3, -1, -1, -1, -1, 1, -1, 7, 7, 7, 1},
      {-1, -1, -1, -1, -1, 1, -1, 1, -1, 1, 1, -1, 1, -1, 1, 1, -1, 1, -1, 1, -1, -1, -1, -1, -1},
      {5, 3, 3, 3, -1, 5, -1, -1, -1, -1, 7, 7, -1, -1, -1, 1, -1, -1, -1, -1, 1, 3, 3, 3, -1},
      {-1, -1, -1, -1, -1, -1, 1, -1, 1, -1, 7, 1, 7, 1, 3, 1, -1, 1, -1, 1, -1, -1, -1, -1, -1},
      {-1, -1, 7, 7, 5, -1, 7, 1, -1, 5, -1, -1, -1, -1, -1, -1, 7, 5, -1, 1, -1, -1, 7, 7, 1},
      {7, 7, -1, -1, -1, 1, -1, -1, -1, -1, 1, 3, 3, 3, 3, -1, -1, -1, -1, 1, -1, -1, -1, 7, 1},
      {5, 3, 3, 3, 3, 5, -1, -1, -1, 1, 7, 7, -1, -1, 1, -1, -1, -1, -1, 1, -1, 7, 7, 7, 1},
      {7, 7, -1, -1, -1, 1, -1, -1, -1, -1, 1, 3, -1, 7, 5, -1, -1, -1, -1, 5, -1, -1, -1, 3, 3},
      {-1, -1, -1, -1, -1, 1, 3, 3, 3, 3, -1, -1, -1, -1, 1, 7, 7, 7, 7, 1, -1, -1, -1, -1, -1}
   };
   private static final Pos DEVICE_MIDDLE = new Pos(-3.0, 120.0, 77.0);
   private static final Pos DEVICE_CORNER = new Pos(-2.0, 120.0, 75.0);
   private static final class_238 DEVICE_BOX = new class_238(-1.0, 119.0, 74.0, -4.0, 125.0, 80.0);
   private final long[] recentClicks = new long[25];
   private AlignAura.FrameData[] currentFrames = null;

   @SubscribeEvent
   public void onClientTick(Start event) {
      if ((
            Location.getArea().is(Island.Dungeon)
               || (Boolean)((ClickGUI)Objects.requireNonNull((ClickGUI)RSM.getModule(ClickGUI.class))).getForceSkyBlock().getValue()
         )
         && DungeonUtils.isPhase(Phase7.P3)) {
         assert mc.field_1724 != null;

         if (mc.field_1724.method_5649(DEVICE_MIDDLE.x(), DEVICE_MIDDLE.y(), DEVICE_MIDDLE.z()) > 100.0) {
            this.currentFrames = null;
         } else {
            PacketOrderManager.register(PacketOrderManager.STATE.ITEM_USE, this::aura);
         }
      }
   }

   private void aura() {
      this.currentFrames = this.getCurrentFrames();
      if (this.currentFrames.length != 0 && mc.method_1562() != null && mc.field_1724 != null && mc.field_1761 != null && mc.field_1687 != null) {
         int[] rotations = new int[25];

         for (int i = 0; i < 25; i++) {
            rotations[i] = this.currentFrames[i] != null ? this.currentFrames[i].rotation : -1;
         }

         int[] solution = this.findMatchingSolution(rotations);
         if (solution != null) {
            List<Integer> frameIndices = this.getIndices();
            boolean isFirst = true;

            for (int index : frameIndices) {
               AlignAura.FrameData frame = this.currentFrames[index];
               if (frame != null) {
                  double distance = this.getDistanceToFrame(frame.entity);
                  if (!(distance > 25.0)) {
                     int clicksNeeded = (solution[index] - frame.rotation + 8) % 8;
                     if (clicksNeeded > 0) {
                        if (!Dungeon.isInP3() && (this.countFramesToSolve(solution) <= 1 || isFirst)) {
                           clicksNeeded--;
                           isFirst = false;
                        }

                        if (clicksNeeded > 0) {
                           this.recentClicks[index] = System.currentTimeMillis();

                           for (int i = 0; i < clicksNeeded; i++) {
                              frame.rotation = (frame.rotation + 1) % 8;
                              class_243 vec3 = this.clamp(
                                    frame.entity.method_5829(), mc.field_1724.method_73189().method_1031(0.0, mc.field_1724.method_5751(), 0.0)
                                 )
                                 .method_1023(frame.entity.method_23317(), frame.entity.method_23318(), frame.entity.method_23321());
                              InteractUtils.interactOnEntity(frame.entity, vec3);
                           }
                           break;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private class_243 clamp(class_238 aabb, class_243 vec3) {
      return new class_243(
         this.clamp(vec3.field_1352, aabb.field_1323, aabb.field_1320),
         this.clamp(vec3.field_1351, aabb.field_1322, aabb.field_1325),
         this.clamp(vec3.field_1350, aabb.field_1321, aabb.field_1324)
      );
   }

   private double clamp(double d, double min, double max) {
      return Math.min(max, Math.max(d, min));
   }

   @NotNull
   private List<Integer> getIndices() {
      List<Integer> frameIndices = new ArrayList<>();

      for (int i = 0; i < 25; i++) {
         if (this.currentFrames[i] != null) {
            frameIndices.add(i);
         }
      }

      frameIndices.sort((a, b) -> {
         AlignAura.FrameData frameA = this.currentFrames[a];
         AlignAura.FrameData frameB = this.currentFrames[b];
         double distA = this.getDistanceToFrame(frameA.entity);
         double distB = this.getDistanceToFrame(frameB.entity);
         return Double.compare(distA, distB);
      });
      return frameIndices;
   }

   private AlignAura.FrameData[] getCurrentFrames() {
      AlignAura.FrameData[] array = new AlignAura.FrameData[25];

      assert mc.field_1687 != null;

      for (class_1533 itemFrame : mc.field_1687.method_8390(class_1533.class, DEVICE_BOX, e -> e.method_6940().method_7909() instanceof class_1744)) {
         int dy = (int)(itemFrame.method_31478() - DEVICE_CORNER.y());
         int dz = (int)(itemFrame.method_31479() - DEVICE_CORNER.z());
         int index = dy + dz * 5;
         if (this.currentFrames != null && System.currentTimeMillis() - this.recentClicks[index] < 1000L) {
            array[index] = this.currentFrames[index];
         } else {
            int rotation = itemFrame.method_6934();
            array[index] = new AlignAura.FrameData(itemFrame, rotation);
         }
      }

      return array;
   }

   private int[] findMatchingSolution(int[] rotations) {
      for (int[] solution : SOLUTIONS) {
         boolean matches = true;

         for (int i = 0; i < 25; i++) {
            boolean solutionHasFrame = solution[i] != -1;
            boolean currentHasFrame = rotations[i] != -1;
            if (solutionHasFrame != currentHasFrame) {
               matches = false;
               break;
            }
         }

         if (matches) {
            return solution;
         }
      }

      return null;
   }

   private double getDistanceToFrame(class_1533 frame) {
      return mc.field_1724.method_33571().method_1025(frame.method_73189());
   }

   private int countFramesToSolve(int[] solution) {
      int count = 0;

      for (int i = 0; i < 25; i++) {
         if (this.currentFrames[i] != null && solution[i] != -1) {
            int clicksNeeded = (solution[i] - this.currentFrames[i].rotation + 8) % 8;
            if (clicksNeeded > 0) {
               count++;
            }
         }
      }

      return count;
   }

   @SubscribeEvent
   public void onWorldLoad(Load event) {
      this.reset();
   }

   public void reset() {
      this.currentFrames = null;
   }

   public long[] getRecentClicks() {
      return this.recentClicks;
   }

   private static class FrameData {
      public class_1533 entity;
      public int rotation;

      FrameData(class_1533 entity, int rotation) {
         this.entity = entity;
         this.rotation = rotation;
      }
   }
}
