package com.ricedotwho.rsa.component.impl.pathfinding;

import com.ricedotwho.rsa.RSA;
import com.ricedotwho.rsm.component.impl.map.map.Room;
import com.ricedotwho.rsm.component.impl.map.map.UniqueRoom;
import com.ricedotwho.rsm.component.impl.map.utils.ScanUtils;
import java.util.HashMap;
import java.util.List;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_746;

public class GoalDungeonRoom implements Goal {
   private static final float MAX = 1.0E8F;
   private final UniqueRoom endRoom;
   private final HashMap<String, RoomCandidate> rooms;

   public GoalDungeonRoom(UniqueRoom endRoom, List<RoomCandidate> rooms) {
      this.endRoom = endRoom;
      this.rooms = new HashMap<>(rooms.size());

      for (int i = 0; i < rooms.size(); i++) {
         RoomCandidate candidate = rooms.get(i);
         this.rooms.put(candidate.getName(), candidate);
      }
   }

   public static GoalDungeonRoom create(UniqueRoom endRoom) {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         return null;
      } else {
         Room startRoom = ScanUtils.getRoomFromPos(player.method_31477(), player.method_31479());
         if (startRoom != null && endRoom != null) {
            List<RoomCandidate> candidates = DungeonMapPathfinder.solve(startRoom, (Room)endRoom.getTiles().getFirst());
            if (candidates.isEmpty()) {
               RSA.chat("Failed to find path!");
               return null;
            } else {
               return new GoalDungeonRoom(endRoom, candidates);
            }
         } else {
            RSA.chat("Room is not loaded!");
            return null;
         }
      }
   }

   @Override
   public boolean test(int x, int y, int z) {
      Room current = ScanUtils.getRoomFromPos(x, z);
      if (current != null && current.getUniqueRoom() != null) {
         return current.getUniqueRoom() != this.endRoom
            ? false
            : current.getRoofHeight() > y && class_3532.method_15382(current.getX() - x) <= 14.0F && class_3532.method_15382(current.getZ() - z) <= 14.0F;
      } else {
         return false;
      }
   }

   @Override
   public double heuristic(int x, int y, int z) {
      Room room = ScanUtils.getRoomFromPos(x, z);
      if (room != null && room.getUniqueRoom() != null && room.getUniqueRoom().getMainRoom() != null) {
         RoomCandidate candidate = this.rooms.get(room.getData().name());
         if (candidate == null) {
            return 1.0E8;
         } else {
            boolean bl = candidate.getNextDoorRoom() != null;
            if (bl) {
               int endX = candidate.getDoorRoom().getX() + candidate.getNextDoorRoom().getX() >> 1;
               int endZ = candidate.getDoorRoom().getZ() + candidate.getNextDoorRoom().getZ() >> 1;
               int xDif = x - endX;
               int zDif = z - endZ;
               return xDif * xDif + zDif * zDif + candidate.getCost();
            } else {
               return 0.0;
            }
         }
      } else {
         return 1.0E8;
      }
   }

   @Override
   public boolean isPossible() {
      return this.endRoom != null;
   }
}
