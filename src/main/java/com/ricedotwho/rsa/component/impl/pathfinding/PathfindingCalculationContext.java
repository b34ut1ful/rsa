package com.ricedotwho.rsa.component.impl.pathfinding;

import net.minecraft.class_2338;
import net.minecraft.class_2338.class_2339;

public record PathfindingCalculationContext(class_2339 startBlock, int threadCount, float yawStep, float pitchStep, float newNodeCost, float heuristicThreshold) {
   public static PathfindingCalculationContext simple(class_2338 startBlock, int threadCount) {
      return new PathfindingCalculationContext(startBlock.method_25503(), threadCount, 2.0F, 2.0F, 100.0F, 0.5F);
   }

   public class_2339 getMutableStart() {
      return this.startBlock;
   }
}
