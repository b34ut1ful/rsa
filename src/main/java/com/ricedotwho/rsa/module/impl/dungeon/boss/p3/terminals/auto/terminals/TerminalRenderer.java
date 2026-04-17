package com.ricedotwho.rsa.module.impl.dungeon.boss.p3.terminals.auto.terminals;

import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.terminals.auto.InvWalk;
import com.ricedotwho.rsm.component.impl.Terminals;
import com.ricedotwho.rsm.utils.Utils;
import java.util.HashMap;
import java.util.List;
import net.minecraft.class_1703;
import net.minecraft.class_1735;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;

public class TerminalRenderer {
   private class_1703 terminalContainer = null;
   private final HashMap<Integer, class_1799> overrides = new HashMap<>();
   private boolean overridesUpdated = false;

   public void renderItems(class_332 guiGraphics, Terminal terminal) {
      if (this.terminalContainer != null && !this.terminalContainer.field_7761.isEmpty()) {
         int slotCount = Utils.getGuiSlotCount(this.terminalContainer.method_17358());
         boolean bl = (Boolean)InvWalk.getUseOverrides().getValue() && (terminal instanceof StartsWith || terminal instanceof Colors);
         if (bl && terminal.isSolved()) {
            this.tryUpdateOverrides(terminal);
         }

         for (int i = 0; i < slotCount && i < this.terminalContainer.field_7761.size(); i++) {
            class_1735 slot = (class_1735)this.terminalContainer.field_7761.get(i);
            class_1799 stack = bl && this.overrides.containsKey(slot.field_7874) ? this.overrides.get(slot.field_7874) : slot.method_7677();
            int x = i % 9 * 16;
            int y = (int)(Math.floor(i / 9.0F) * 16.0);
            renderSlot(guiGraphics, x, y, stack);
         }
      }
   }

   private void tryUpdateOverrides(Terminal terminal) {
      if (!this.overridesUpdated) {
         this.overrides.clear();
         List<class_1735> slots = this.terminalContainer.field_7761;
         int slotCount = Utils.getGuiSlotCount(this.terminalContainer.method_17358());

         for (int i = 0; i < slotCount && i < slots.size(); i++) {
            class_1735 slot = slots.get(i);
            class_1799 stack = slot.method_7677();
            if (!stack.method_7960() && stack.method_7909() != class_1802.field_8157) {
               class_1792 item = terminal.isSolved() && terminal.getSolution().containsIndex(i) ? class_1802.field_8879 : class_1802.field_8581;
               this.overrides.put(slot.field_7874, item.method_7854().method_46651(stack.method_7947()));
            }
         }

         this.overridesUpdated = true;
      }
   }

   private static void renderSlot(class_332 guiGraphics, int x, int y, class_1799 stack) {
      if (!stack.method_7960()) {
         int k = x + y * 176;
         guiGraphics.method_51428(stack, x, y, k);
         renderItemCount(guiGraphics, class_310.method_1551().field_1772, stack, x, y);
      }
   }

   private static void renderItemCount(class_332 guiGraphics, class_327 font, class_1799 itemStack, int i, int j) {
      if (itemStack.method_7947() != 1) {
         String string2 = String.valueOf(itemStack.method_7947());
         guiGraphics.method_51433(font, string2, i + 19 - 2 - font.method_1727(string2), j + 6 + 3, -1, true);
      }
   }

   public void newWindow(class_1703 menu) {
      this.overridesUpdated = false;
      this.terminalContainer = menu;
   }

   public void close() {
      this.terminalContainer = null;
   }

   public void renderSolver(float gap) {
      if (Terminals.isInTerminal()) {
         Terminals.getCurrent().render(0.0F, 0.0F, gap, true);
      }
   }
}
