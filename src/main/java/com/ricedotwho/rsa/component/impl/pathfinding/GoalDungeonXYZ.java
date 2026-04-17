package com.ricedotwho.rsa.component.impl.pathfinding;

import com.ricedotwho.rsa.RSA;
import com.ricedotwho.rsm.component.impl.map.map.Room;
import com.ricedotwho.rsm.component.impl.map.utils.ScanUtils;
import com.ricedotwho.rsm.utils.EtherUtils;
import java.util.HashMap;
import java.util.List;
import net.minecraft.class_2338;
import net.minecraft.class_310;
import net.minecraft.class_746;

public class GoalDungeonXYZ implements Goal {
   public static final float ROOM_COST = 100000.0F;
   private static final float MAX = 1.0E8F;
   private final class_2338 endPos;
   private final HashMap<String, RoomCandidate> rooms;

   public GoalDungeonXYZ(class_2338 endPos, List<RoomCandidate> rooms) {
      this.endPos = endPos;
      this.rooms = new HashMap<>(rooms.size());

      for (int i = 0; i < rooms.size(); i++) {
         RoomCandidate candidate = rooms.get(i);
         this.rooms.put(candidate.getName(), candidate);
      }
   }

   public static GoalDungeonXYZ create(class_2338 endPos) {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         return null;
      } else {
         Room startRoom = ScanUtils.getRoomFromPos(player.method_31477(), player.method_31479());
         Room endRoom = ScanUtils.getRoomFromPos(endPos.method_10263(), endPos.method_10260());
         if (startRoom != null && endRoom != null) {
            List<RoomCandidate> candidates = DungeonMapPathfinder.solve(startRoom, endRoom);
            if (candidates.isEmpty()) {
               RSA.chat("Failed to find path!");
               return null;
            } else {
               return new GoalDungeonXYZ(endPos, candidates);
            }
         } else {
            RSA.chat("Room is not loaded!");
            return null;
         }
      }
   }

   @Override
   public boolean test(int x, int y, int z) {
      return x == this.endPos.method_10263() && y == this.endPos.method_10264() && z == this.endPos.method_10260();
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
            int endX;
            int endY;
            int endZ;
            if (bl) {
               endX = candidate.getDoorRoom().getX() + candidate.getNextDoorRoom().getX() >> 1;
               endY = y;
               endZ = candidate.getDoorRoom().getZ() + candidate.getNextDoorRoom().getZ() >> 1;
            } else {
               endX = this.endPos.method_10263();
               endY = this.endPos.method_10264();
               endZ = this.endPos.method_10260();
            }

            int xDif = x - endX;
            int yDif = y - endY;
            int zDif = z - endZ;
            return xDif * xDif + yDif * yDif + zDif * zDif + candidate.getCost();
         }
      } else {
         return 1.0E8;
      }
   }

   @Override
   public boolean isPossible() {
      return EtherUtils.isValidEtherwarpPosition(this.endPos);
   }
}
