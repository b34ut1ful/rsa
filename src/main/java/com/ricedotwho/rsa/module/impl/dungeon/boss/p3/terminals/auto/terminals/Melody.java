package com.ricedotwho.rsa.module.impl.dungeon.boss.p3.terminals.auto.terminals;

import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.terminals.auto.AutoTerms;
import com.ricedotwho.rsm.RSM;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2653;
import net.minecraft.class_3944;

public class Melody extends Terminal {
   private LinkedList<SolutionClick> queue = new LinkedList<>();

   protected Melody(class_3944 packet, class_1703 menu) {
      super(TerminalType.MELODY, packet, menu);
   }

   @Override
   public TerminalState getNextState() {
      return this.getCurrentState();
   }

   @Override
   public TerminalState getCurrentState() {
      List<Terminal.HashInfo> infos = new ArrayList<>(this.getType().getSlotCount());

      for (int i = 0; i < this.getType().getSlotCount(); i++) {
         class_1735 slot = this.terminalContainer.method_7611(i);
         infos.add(new Terminal.HashInfo(slot.method_7677()));
      }

      return Terminal.getTerminalState(TerminalType.MELODY, infos);
   }

   public boolean onTickStart(AutoTerms autoTerms) {
      if (this.queue.isEmpty()) {
         return false;
      } else {
         SolutionClick click = this.queue.removeFirst();
         autoTerms.sendWindowClick(click);
         return true;
      }
   }

   @Override
   public void solve() {
      super.solve();
      this.solution = new Solution(Collections.emptyList());
      this.solveState = SolveState.SOLVED;
   }

   @Override
   public void loadSlot(class_2653 packet) {
      super.loadSlot(packet);
      int slot = packet.method_11450();
      if (packet.method_11452() == this.getWindowID() && slot >= 10 && slot < this.getType().getSlotCount()) {
         class_1799 stack = packet.method_11449();
         if (stack.method_7909() == class_1802.field_8581
            && ((class_1735)this.terminalContainer.field_7761.get(slot % 9)).method_7677().method_7909() == class_1802.field_8119) {
            int buttonIndex = (slot / 9 - 1) * 9 + 16;
            int mod = slot % 9;
            this.queue.clear();
            AutoTerms module = (AutoTerms)RSM.getModule(AutoTerms.class);
            boolean skip = (Boolean)module.getMelodySkip().getValue()
               && (mod == 1 || mod == 5)
               && (!(Boolean)module.getMelodySkipFirst().getValue() || buttonIndex > 18);
            if (!skip) {
               this.queue.add(new SolutionClick(class_1713.field_7796, buttonIndex, 0));
            } else {
               while (buttonIndex <= 43) {
                  this.queue.add(new SolutionClick(class_1713.field_7796, buttonIndex, 0));
                  buttonIndex += 9;
               }
            }
         }
      }
   }

   @Override
   public boolean isEnabled() {
      return AutoTerms.getTerminals().get("Melody");
   }

   protected static Melody supply(class_3944 packet, class_1703 menu) {
      return new Melody(packet, menu);
   }
}
