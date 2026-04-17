package com.ricedotwho.rsa.module.impl.dungeon.boss.p3.terminals.auto.terminals;

import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.terminals.auto.AutoTerms;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1802;
import net.minecraft.class_3944;

public class Numbers extends Terminal {
   protected Numbers(class_3944 packet, class_1703 menu) {
      super(TerminalType.NUMBERS, packet, menu);
   }

   @Override
   public TerminalState getNextState() {
      if (this.solution == null) {
         throw new IllegalStateException("Tried to get next state without solving!");
      } else {
         List<Terminal.HashInfo> infos = new ArrayList<>(this.getType().getSlotCount());
         int changedIndex = this.solution.getNext().index();

         for (int i = 0; i < this.getType().getSlotCount(); i++) {
            class_1735 slot = this.terminalContainer.method_7611(i);
            Terminal.HashInfo hashInfo = new Terminal.HashInfo(slot.method_7677());
            if (slot.field_7874 == changedIndex) {
               hashInfo.setItem(class_1802.field_8581);
            }

            infos.add(hashInfo);
         }

         return Terminal.getTerminalState(TerminalType.NUMBERS, infos);
      }
   }

   @Override
   public TerminalState getCurrentState() {
      List<Terminal.HashInfo> infos = new ArrayList<>(this.getType().getSlotCount());

      for (int i = 0; i < this.getType().getSlotCount(); i++) {
         class_1735 slot = this.terminalContainer.method_7611(i);
         infos.add(new Terminal.HashInfo(slot.method_7677()));
      }

      return Terminal.getTerminalState(TerminalType.NUMBERS, infos);
   }

   @Override
   public void solve() {
      super.solve();
      List<SolutionClick> sortedSlots = this.terminalContainer
         .field_7761
         .stream()
         .filter(slot -> slot.method_7677().method_7909() == class_1802.field_8879)
         .sorted(Comparator.comparingInt(slot -> slot.method_7677().method_7947()))
         .map(slot -> new SolutionClick(class_1713.field_7796, slot.field_7874, 0))
         .toList();
      this.solution = new Solution(sortedSlots);
      this.solveState = SolveState.SOLVED;
   }

   @Override
   public boolean isEnabled() {
      return AutoTerms.getTerminals().get("Numbers");
   }

   protected static Numbers supply(class_3944 packet, class_1703 menu) {
      return new Numbers(packet, menu);
   }
}
