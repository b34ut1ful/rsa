package com.ricedotwho.rsa.module.impl.dungeon.boss.p3.terminals.auto.terminals;

import net.minecraft.class_1713;

public class SolutionClick {
   private final class_1713 type;
   private final int index;
   private final int button;

   public SolutionClick(class_1713 type, int index, int button) {
      this.type = type;
      this.index = index;
      this.button = button;
   }

   public class_1713 type() {
      return this.type;
   }

   public int index() {
      return this.index;
   }

   public int button() {
      return this.button;
   }
}
