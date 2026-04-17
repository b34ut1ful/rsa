package com.ricedotwho.rsa.module.impl.dungeon.boss.p3.terminals.auto.terminals;

import com.ricedotwho.rsa.RSA;
import java.util.List;
import net.minecraft.class_1703;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2653;
import net.minecraft.class_3917;
import net.minecraft.class_3944;
import net.minecraft.class_9334;

public abstract class Terminal {
   private final TerminalType type;
   protected SolveState solveState;
   private final String title;
   private final int windowID;
   protected final class_1703 terminalContainer;
   protected Solution solution;

   protected Terminal(TerminalType type, class_3944 packet, class_1703 terminalContainer) {
      this.type = type;
      this.windowID = packet.method_17592();
      this.title = packet.method_17594().getString();
      this.solveState = SolveState.NOT_LOADED;
      this.terminalContainer = terminalContainer;
   }

   public void loadSlot(class_2653 packet) {
      if (packet.method_11452() != this.getWindowID()) {
         RSA.chat("Window ID slot load mismatch! -> term : " + this.getWindowID() + " packet : " + packet.method_11452());
      } else if (packet.method_11450() > this.type.getSlotCount() && this.solveState == SolveState.NOT_LOADED) {
         this.solveState = SolveState.LOADED;
      }
   }

   public abstract TerminalState getNextState();

   public abstract TerminalState getCurrentState();

   protected static TerminalState getTerminalState(TerminalType type, List<Terminal.HashInfo> stacks) {
      int hash = 1;

      for (int i = 0; i < stacks.size(); i++) {
         Terminal.HashInfo stack = stacks.get(i);
         hash = 31 * hash + stack.getItem();
         hash = 31 * hash + stack.getSize();
         hash = 31 * hash + (stack.isEnchanted() ? 1 : 0);
      }

      return new TerminalState(type, hash);
   }

   public boolean shouldSolve() {
      return this.solveState != SolveState.NOT_LOADED;
   }

   public boolean isSolved() {
      return this.solution != null && this.solveState != SolveState.NOT_LOADED;
   }

   public void solve() {
      if (this.solveState == SolveState.NOT_LOADED) {
         throw new IllegalStateException("Tried to solve incomplete terminal!");
      }
   }

   public static Terminal fromPacket(class_3944 packet, class_1703 menu) {
      class_3917<?> menuType = packet.method_17593();
      return menuType != class_3917.field_18666 && menuType != class_3917.field_18667 && menuType != class_3917.field_17327
         ? null
         : findTerminalClass(packet, menu);
   }

   private static Terminal findTerminalClass(class_3944 packet, class_1703 menu) {
      TerminalType terminalType = TerminalType.getType(packet.method_17594().getString());
      return terminalType == null ? null : terminalType.supply(packet, menu);
   }

   public abstract boolean isEnabled();

   public TerminalType getType() {
      return this.type;
   }

   public SolveState getSolveState() {
      return this.solveState;
   }

   public String getTitle() {
      return this.title;
   }

   public int getWindowID() {
      return this.windowID;
   }

   public Solution getSolution() {
      return this.solution;
   }

   protected static class HashInfo {
      private boolean enchanted;
      private int item;
      private int size;

      protected HashInfo(class_1799 stack) {
         this.enchanted = stack.method_7923() || Boolean.TRUE.equals(stack.method_58694(class_9334.field_49641));
         this.item = stack.method_7909().hashCode();
         this.size = stack.method_7947();
      }

      protected void setEnchanted(boolean bl) {
         this.enchanted = bl;
      }

      protected void setItem(class_1792 item) {
         this.item = item.hashCode();
      }

      protected void setSize(int size) {
         this.size = size;
      }

      public boolean isEnchanted() {
         return this.enchanted;
      }

      public int getItem() {
         return this.item;
      }

      public int getSize() {
         return this.size;
      }
   }
}
