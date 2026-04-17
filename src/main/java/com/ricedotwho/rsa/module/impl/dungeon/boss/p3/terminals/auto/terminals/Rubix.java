package com.ricedotwho.rsa.module.impl.dungeon.boss.p3.terminals.auto.terminals;

import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.terminals.auto.AutoTerms;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_3944;

public class Rubix extends Terminal {
   public static final class_1792[] COLOR_ORDER = new class_1792[]{
      class_1802.field_8761, class_1802.field_8703, class_1802.field_8656, class_1802.field_8747, class_1802.field_8879
   };

   protected Rubix(class_3944 packet, class_1703 menu) {
      super(TerminalType.RUBIX, packet, menu);
   }

   @Override
   public TerminalState getNextState() {
      if (this.solution == null) {
         throw new IllegalStateException("Tried to get next state without solving!");
      } else {
         List<Terminal.HashInfo> infos = new ArrayList<>(this.getType().getSlotCount());
         SolutionClick solutionClick = this.solution.getNext();

         for (int i = 0; i < this.getType().getSlotCount(); i++) {
            class_1735 slot = this.terminalContainer.method_7611(i);
            Terminal.HashInfo hashInfo = new Terminal.HashInfo(slot.method_7677());
            if (slot.field_7874 == solutionClick.index()) {
               int colorIndex = ((RubixSolutionClick)solutionClick).colorIndex();
               if (solutionClick.button() == 0) {
                  hashInfo.setItem(COLOR_ORDER[(colorIndex + 1) % COLOR_ORDER.length]);
                  hashInfo.setEnchanted(false);
                  hashInfo.setSize(1);
               } else {
                  hashInfo.setItem(COLOR_ORDER[(colorIndex - 1 + COLOR_ORDER.length) % COLOR_ORDER.length]);
                  hashInfo.setEnchanted(false);
                  hashInfo.setSize(1);
               }
            }

            infos.add(hashInfo);
         }

         return Terminal.getTerminalState(TerminalType.RUBIX, infos);
      }
   }

   @Override
   public TerminalState getCurrentState() {
      List<Terminal.HashInfo> infos = new ArrayList<>(this.getType().getSlotCount());

      for (int i = 0; i < this.getType().getSlotCount(); i++) {
         class_1735 slot = this.terminalContainer.method_7611(i);
         infos.add(new Terminal.HashInfo(slot.method_7677()));
      }

      return Terminal.getTerminalState(TerminalType.RUBIX, infos);
   }

   @Override
   public void solve() {
      super.solve();
      List<Integer> rubixSlots = new ArrayList<>();

      for (class_1735 slot : this.terminalContainer.field_7761) {
         class_1799 stack = slot.method_7677();
         if (!stack.method_7960() && stack.method_7909() != class_1802.field_8157 && this.isRubixPane(stack.method_7909())) {
            rubixSlots.add(slot.field_7874);
         }
      }

      int minIndex = -1;
      int minTotal = Integer.MAX_VALUE;

      for (int targetIndex = 0; targetIndex < COLOR_ORDER.length; targetIndex++) {
         int totalClicks = 0;

         for (Integer slotx : rubixSlots) {
            class_1799 stack = this.terminalContainer.method_7611(slotx).method_7677();
            int currentIndex = this.indexOf(COLOR_ORDER, stack.method_7909());
            int clockwise = (targetIndex - currentIndex + COLOR_ORDER.length) % COLOR_ORDER.length;
            int counterClockwise = (currentIndex - targetIndex + COLOR_ORDER.length) % COLOR_ORDER.length;
            totalClicks += Math.min(clockwise, counterClockwise);
         }

         if (totalClicks < minTotal) {
            minTotal = totalClicks;
            minIndex = targetIndex;
         }
      }

      List<SolutionClick> solutionClicks = new ArrayList<>();

      for (Integer slotx : rubixSlots) {
         class_1799 stack = this.terminalContainer.method_7611(slotx).method_7677();
         int currentIndex = this.indexOf(COLOR_ORDER, stack.method_7909());
         int clockwise = (minIndex - currentIndex + COLOR_ORDER.length) % COLOR_ORDER.length;
         int counterClockwise = (currentIndex - minIndex + COLOR_ORDER.length) % COLOR_ORDER.length;
         if (clockwise <= counterClockwise) {
            for (int j = 0; j < clockwise; j++) {
               solutionClicks.add(new RubixSolutionClick(class_1713.field_7790, slotx, 0, currentIndex));
            }
         } else {
            for (int j = 0; j < counterClockwise; j++) {
               solutionClicks.add(new RubixSolutionClick(class_1713.field_7790, slotx, 1, currentIndex));
            }
         }
      }

      this.solution = new Solution(solutionClicks);
      this.solveState = SolveState.SOLVED;
   }

   private <T> int indexOf(T[] array, T val) {
      for (int i = 0; i < array.length; i++) {
         if (array[i] == val) {
            return i;
         }
      }

      throw new IndexOutOfBoundsException("Could not find color : " + ((class_1792)val).method_63680().getString());
   }

   private boolean isRubixPane(class_1792 item) {
      return item == class_1802.field_8747
         || item == class_1802.field_8879
         || item == class_1802.field_8761
         || item == class_1802.field_8703
         || item == class_1802.field_8656;
   }

   @Override
   public boolean isEnabled() {
      return AutoTerms.getTerminals().get("Rubix");
   }

   protected static Rubix supply(class_3944 packet, class_1703 menu) {
      return new Rubix(packet, menu);
   }
}
