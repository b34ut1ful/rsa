package com.ricedotwho.rsa.component.impl.pathfinding;

import net.minecraft.class_2338;

public interface Goal {
   boolean test(int var1, int var2, int var3);

   default boolean test(class_2338 pos) {
      return this.test(pos.method_10263(), pos.method_10264(), pos.method_10260());
   }

   double heuristic(int var1, int var2, int var3);

   default double heuristic(class_2338 pos) {
      return this.heuristic(pos.method_10263(), pos.method_10264(), pos.method_10260());
   }

   default boolean isPossible() {
      return true;
   }
}
