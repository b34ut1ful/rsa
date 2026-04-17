package com.ricedotwho.rsa.module.impl.dungeon.boss.p3.terminals.auto.terminals;

import java.util.function.BiFunction;
import net.minecraft.class_1703;
import net.minecraft.class_3944;

public enum TerminalType {
   NUMBERS(0, "Click in order!", 35, Numbers::supply),
   COLORS(1, "Select all the", 53, Colors::supply),
   STARTSWITH(2, "What starts with:", 44, StartsWith::supply),
   RUBIX(3, "Change all to same color!", 44, Rubix::supply),
   REDGREEN(4, "Correct all the panes!", 44, RedGreen::supply),
   MELODY(5, "Click the button on time!", 44, Melody::supply);

   private final int id;
   private final int slotCount;
   private final String title;
   private final BiFunction<class_3944, class_1703, Terminal> supplier;

   private TerminalType(int id, String title, int slotCount, BiFunction<class_3944, class_1703, Terminal> supplier) {
      this.id = id;
      this.title = title;
      this.slotCount = slotCount;
      this.supplier = supplier;
   }

   public static TerminalType getType(String s) {
      for (int i = 0; i < values().length; i++) {
         if (s.startsWith(values()[i].getTitle())) {
            return values()[i];
         }
      }

      return null;
   }

   public Terminal supply(class_3944 packet, class_1703 menu) {
      return this.getSupplier() == null ? null : this.getSupplier().apply(packet, menu);
   }

   public int getId() {
      return this.id;
   }

   public int getSlotCount() {
      return this.slotCount;
   }

   public String getTitle() {
      return this.title;
   }

   public BiFunction<class_3944, class_1703, Terminal> getSupplier() {
      return this.supplier;
   }
}
