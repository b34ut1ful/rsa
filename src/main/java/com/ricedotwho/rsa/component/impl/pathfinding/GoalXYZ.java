package com.ricedotwho.rsa.component.impl.pathfinding;

import com.ricedotwho.rsm.utils.EtherUtils;
import net.minecraft.class_2338;

public class GoalXYZ implements Goal {
   private final class_2338 endPos;

   public GoalXYZ(class_2338 endPos) {
      this.endPos = endPos;
   }

   @Override
   public boolean test(int x, int y, int z) {
      return x == this.endPos.method_10263() && y == this.endPos.method_10264() && z == this.endPos.method_10260();
   }

   @Override
   public double heuristic(int x, int y, int z) {
      int xDif = x - this.endPos.method_10263();
      int yDif = y - this.endPos.method_10264();
      int zDif = z - this.endPos.method_10260();
      return xDif * xDif + yDif * yDif + zDif * zDif;
   }

   @Override
   public boolean isPossible() {
      return EtherUtils.isValidEtherwarpPosition(this.endPos);
   }
}
