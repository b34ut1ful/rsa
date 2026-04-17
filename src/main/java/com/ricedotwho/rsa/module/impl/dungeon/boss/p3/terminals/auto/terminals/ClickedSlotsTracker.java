package com.ricedotwho.rsa.module.impl.dungeon.boss.p3.terminals.auto.terminals;

import java.util.HashMap;
import net.minecraft.class_1735;
import net.minecraft.class_1792;

public class ClickedSlotsTracker {
   public HashMap<Integer, class_1792> clickedSlots = new HashMap<>();

   public void clickSlot(class_1735 slot) {
      this.clickedSlots.put(slot.field_7874, slot.method_7677().method_7909());
   }

   public boolean contains(class_1735 slot) {
      return this.clickedSlots.containsKey(slot.field_7874) && this.clickedSlots.get(slot.field_7874) == slot.method_7677().method_7909();
   }

   public void clear() {
      this.clickedSlots.clear();
   }
}
