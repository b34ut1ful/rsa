package com.ricedotwho.rsa.module.impl.dungeon.boss.p3.terminals.auto.terminals;

import net.minecraft.class_1713;

public class RubixSolutionClick extends SolutionClick {
   private final int colorIndex;

   public RubixSolutionClick(class_1713 type, int index, int button, int colorIndex) {
      super(type, index, button);
      this.colorIndex = colorIndex;
   }

   public int colorIndex() {
      return this.colorIndex;
   }
}
