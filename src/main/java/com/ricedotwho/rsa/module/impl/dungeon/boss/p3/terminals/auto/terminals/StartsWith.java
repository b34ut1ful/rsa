package com.ricedotwho.rsa.module.impl.dungeon.boss.p3.terminals.auto.terminals;

import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.terminals.auto.AutoTerms;
import com.ricedotwho.rsm.RSM;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.class_124;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_3944;

public class StartsWith extends Terminal {
   protected StartsWith(class_3944 packet, class_1703 menu) {
      super(TerminalType.STARTSWITH, packet, menu);
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
               hashInfo.setEnchanted(true);
            }

            infos.add(hashInfo);
         }

         return Terminal.getTerminalState(TerminalType.STARTSWITH, infos);
      }
   }

   @Override
   public TerminalState getCurrentState() {
      List<Terminal.HashInfo> infos = new ArrayList<>(this.getType().getSlotCount());

      for (int i = 0; i < this.getType().getSlotCount(); i++) {
         class_1735 slot = this.terminalContainer.method_7611(i);
         infos.add(new Terminal.HashInfo(slot.method_7677()));
      }

      return Terminal.getTerminalState(TerminalType.STARTSWITH, infos);
   }

   @Override
   public void solve() {
      super.solve();
      Pattern pattern = Pattern.compile("What starts with: '(\\w+)'?");
      Matcher matcher = pattern.matcher(this.getTitle());
      if (matcher.find()) {
         String matchLetter = matcher.group(1).toLowerCase();
         List<SolutionClick> solutionClicks = new ArrayList<>();

         for (class_1735 slot : this.terminalContainer.field_7761) {
            class_1799 stack = slot.method_7677();
            if (!stack.method_7960() && !((AutoTerms)RSM.getModule(AutoTerms.class)).getClickedSlotsTracker().contains(slot)) {
               String name = class_124.method_539(stack.method_7964().getString()).toLowerCase();
               if (name.startsWith(matchLetter)) {
                  solutionClicks.add(new SolutionClick(class_1713.field_7796, slot.field_7874, 0));
               }
            }
         }

         this.solution = new Solution(solutionClicks);
         this.solveState = SolveState.SOLVED;
      }
   }

   @Override
   public boolean isEnabled() {
      return AutoTerms.getTerminals().get("Starts With");
   }

   protected static StartsWith supply(class_3944 packet, class_1703 menu) {
      return new StartsWith(packet, menu);
   }
}
