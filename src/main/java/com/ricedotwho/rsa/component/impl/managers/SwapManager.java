package com.ricedotwho.rsa.component.impl.managers;

import com.ricedotwho.rsa.RSA;
import com.ricedotwho.rsa.IMixin.IMultiPlayerGameMode;
import com.ricedotwho.rsm.data.Rotation;
import com.ricedotwho.rsm.utils.ItemUtils;
import com.ricedotwho.rsm.utils.RotationUtils;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.function.Predicate;
import net.minecraft.class_1268;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1934;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2846;
import net.minecraft.class_2868;
import net.minecraft.class_2885;
import net.minecraft.class_2886;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_746;
import net.minecraft.class_239.class_240;
import net.minecraft.class_2846.class_2847;

public class SwapManager {
   private static int serverSlot;
   private static int lastSentServerSlot;
   private static boolean swappedThisTick = false;
   private static int requireSwap = -1;
   private static final Queue<class_2596<?>> pendingC08Packets = new ArrayDeque<>();
   private static int currentTick = 0;
   private static int lastC08Tick = Integer.MIN_VALUE;
   private static int lastSwapTick = Integer.MIN_VALUE;
   private static boolean flushingQueuedC08 = false;

   public static void onPreTickStart() {
      currentTick++;
      swappedThisTick = false;
      requireSwap = -1;
      flushQueuedC08();
   }

   public static boolean onPostSendPacket(class_2596<?> packet) {
      if (packet instanceof class_2868 slotPacket) {
         if (!swappedThisTick && slotPacket.method_12442() != lastSentServerSlot) {
            swappedThisTick = true;
            serverSlot = slotPacket.method_12442();
            lastSentServerSlot = slotPacket.method_12442();
            lastSwapTick = currentTick;
            return true;
         } else {
            RSA.chat("Prevented packet 0 tick swap! This shouldn't happen, tell hyper!");
            return false;
         }
      } else if (isC08Packet(packet)) {
         if (flushingQueuedC08) {
            lastC08Tick = currentTick;
            return true;
         } else if (pendingC08Packets.isEmpty() && lastC08Tick != currentTick && lastSwapTick != currentTick) {
            lastC08Tick = currentTick;
            return true;
         } else {
            pendingC08Packets.add(packet);
            return false;
         }
      } else {
         return true;
      }
   }

   public static void onHandleLogin() {
      serverSlot = 0;
      lastSentServerSlot = 0;
      pendingC08Packets.clear();
      lastC08Tick = Integer.MIN_VALUE;
      lastSwapTick = Integer.MIN_VALUE;
      flushingQueuedC08 = false;
   }

   private static boolean isC08Packet(class_2596<?> packet) {
      return packet instanceof class_2886 || packet instanceof class_2885;
   }

   private static void flushQueuedC08() {
      class_310 client = class_310.method_1551();
      if (!pendingC08Packets.isEmpty() && lastC08Tick != currentTick && lastSwapTick != currentTick && client.method_1562() != null) {
         class_2596<?> packet = pendingC08Packets.poll();
         if (packet != null) {
            flushingQueuedC08 = true;

            try {
               client.method_1562().method_52787(packet);
            } finally {
               flushingQueuedC08 = false;
            }
         }
      }
   }

   public static boolean onEnsureHasSentCarriedItem(int managerServerSlot) {
      if (class_310.method_1551().field_1724 == null) {
         return false;
      } else {
         if (serverSlot != managerServerSlot) {
            RSA.chat("Slot mismatch! Tell Hyper if you see this!");
            RSA.chat("SwapManger : " + serverSlot);
            RSA.chat("GameMode : " + managerServerSlot);
         }

         int i = class_310.method_1551().field_1724.method_31548().method_67532();
         if (!swappedThisTick && requireSwap > -1 && i != requireSwap) {
            if (requireSwap == managerServerSlot) {
               return false;
            }

            class_310.method_1551().field_1724.method_31548().method_61496(requireSwap);
            i = requireSwap;
         }

         if (i != managerServerSlot && !swappedThisTick) {
            serverSlot = i;
            return true;
         } else {
            return false;
         }
      }
   }

   private static boolean reserveSwap0(int index) {
      if (index < 0 || index > 8) {
         return false;
      } else if (!canSwap()) {
         return index == getNextUpdateIndex();
      } else {
         requireSwap = index;
         return true;
      }
   }

   public static boolean reserveSwap(int index) {
      if (!reserveSwap0(index)) {
         return false;
      } else {
         swapSlot(index);
         return true;
      }
   }

   public static int getNextUpdateIndex() {
      if (swappedThisTick) {
         return serverSlot;
      } else if (requireSwap > -1) {
         return requireSwap;
      } else {
         return class_310.method_1551().field_1724 == null ? 0 : class_310.method_1551().field_1724.method_31548().method_67532();
      }
   }

   public static boolean canSwap() {
      return !swappedThisTick && requireSwap < 0;
   }

   public static boolean sendAirC08(float yaw, float pitch, boolean syncSlots, boolean swing) {
      if (class_310.method_1551().field_1724 == null || class_310.method_1551().field_1724.method_68876() == class_1934.field_9219) {
         return false;
      } else if (class_310.method_1551().field_1761 != null && class_310.method_1551().field_1687 != null) {
         IMultiPlayerGameMode manager = (IMultiPlayerGameMode)class_310.method_1551().field_1761;
         int i = class_310.method_1551().field_1724.method_31548().method_67532();
         if (syncSlots) {
            manager.syncSlot();
         }

         if (syncSlots && !checkServerSlot(i)) {
            RSA.chat("Failed to swap to slot : " + i);
            return false;
         } else {
            manager.sendPacketSequenced(class_310.method_1551().field_1687, sequence -> new class_2886(class_1268.field_5808, sequence, yaw, pitch));
            if (swing) {
               class_310.method_1551().field_1724.method_6104(class_1268.field_5808);
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean isDesynced() {
      return getNextUpdateIndex() != serverSlot;
   }

   public static boolean sendAirC08(float yaw, float pitch, boolean syncSlots) {
      return sendAirC08(yaw, pitch, syncSlots, false);
   }

   public static boolean sendAirC08(Rotation rot, boolean syncSlots) {
      return sendAirC08(rot.getYaw(), rot.getPitch(), syncSlots, false);
   }

   public static boolean sendAirC08(Rotation rot, boolean syncSlots, boolean swing) {
      return sendAirC08(rot.getYaw(), rot.getPitch(), syncSlots, swing);
   }

   public static boolean sendBlockC08(class_3965 result, boolean swing, boolean syncSlot) {
      if (class_310.method_1551().field_1724 == null || class_310.method_1551().field_1724.method_68876() == class_1934.field_9219) {
         return false;
      } else if (class_310.method_1551().field_1761 != null && class_310.method_1551().field_1687 != null) {
         if (syncSlot) {
            IMultiPlayerGameMode manager = (IMultiPlayerGameMode)class_310.method_1551().field_1761;
            int i = class_310.method_1551().field_1724.method_31548().method_67532();
            manager.syncSlot();
            if (!checkServerSlot(i)) {
               RSA.chat("Failed to swap to slot : " + i);
               return false;
            }
         }

         ((IMultiPlayerGameMode)class_310.method_1551().field_1761)
            .sendPacketSequenced(class_310.method_1551().field_1687, sequence -> new class_2885(class_1268.field_5808, result, sequence));
         if (swing) {
            class_310.method_1551().field_1724.method_6104(class_1268.field_5808);
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean sendBlockC08(float yaw, float pitch, boolean swing, boolean syncSlot) {
      class_239 result = RotationUtils.getBlockHitResult(
         class_310.method_1551().field_1724.method_72381(), yaw, pitch, class_310.method_1551().field_1724.method_73189().method_1031(0.0, 1.54F, 0.0)
      );
      if (result.method_17783() != class_240.field_1332) {
         RSA.chat("Failed to send block C08!");
      }

      return sendBlockC08((class_3965)result, swing, syncSlot);
   }

   public static boolean sendBlockC08(class_243 pos, class_2350 direction, boolean swing, boolean syncSlot) {
      return sendBlockC08(new class_3965(pos, direction, class_2338.method_49638(pos), false), swing, syncSlot);
   }

   public static boolean sendC07(class_2338 result, class_2847 action, class_2350 face, boolean swing, boolean syncSlot) {
      if (class_310.method_1551().field_1724 == null || class_310.method_1551().field_1724.method_68876() == class_1934.field_9219) {
         return false;
      } else if (class_310.method_1551().field_1761 != null && class_310.method_1551().field_1687 != null) {
         if (syncSlot) {
            IMultiPlayerGameMode manager = (IMultiPlayerGameMode)class_310.method_1551().field_1761;
            int i = class_310.method_1551().field_1724.method_31548().method_67532();
            manager.syncSlot();
            if (!checkServerSlot(i)) {
               RSA.chat("Failed to swap to slot : " + i);
               return false;
            }
         }

         ((IMultiPlayerGameMode)class_310.method_1551().field_1761)
            .sendPacketSequenced(class_310.method_1551().field_1687, sequence -> new class_2846(action, result, face, sequence));
         if (swing) {
            class_310.method_1551().field_1724.method_6104(class_1268.field_5808);
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean reserveSwap(class_1792 item) {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null && item != null) {
         if (!canSwap()) {
            return item == player.method_31548().method_5438(getNextUpdateIndex()).method_7909();
         } else {
            for (int i = 0; i < 9; i++) {
               class_1799 stack = player.method_31548().method_5438(i);
               if (stack.method_7909() == item) {
                  boolean bl = swapSlot(i);
                  if (bl) {
                     reserveSwap0(i);
                  }

                  return bl;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean reserveSwap(Predicate<class_1799> predicate) {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         return false;
      } else if (!canSwap()) {
         return predicate.test(player.method_31548().method_5438(getNextUpdateIndex()));
      } else {
         for (int i = 0; i < 9; i++) {
            if (predicate.test(player.method_31548().method_5438(i))) {
               boolean bl = swapSlot(i);
               if (bl) {
                  reserveSwap0(i);
               }

               return bl;
            }
         }

         return false;
      }
   }

   public static boolean reserveSwap(String... sbId) {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null && sbId != null && sbId.length != 0) {
         if (!canSwap()) {
            String next = ItemUtils.getID(player.method_31548().method_5438(getNextUpdateIndex()));
            return Arrays.stream(sbId).anyMatch(idx -> !idx.isBlank() && next.equals(idx));
         } else {
            for (int i = 0; i < 9; i++) {
               String id = ItemUtils.getID(player.method_31548().method_5438(i));
               if (!Arrays.stream(sbId).noneMatch(id1 -> !id1.isBlank() && id.equals(id1))) {
                  boolean bl = swapSlot(i);
                  if (bl) {
                     reserveSwap0(i);
                  }

                  return bl;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean swapItem(class_1792 item) {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null && item != null) {
         if (item == player.method_31548().method_5438(getNextUpdateIndex()).method_7909()) {
            return true;
         } else if (!canSwap()) {
            return false;
         } else {
            for (int i = 0; i < 9; i++) {
               class_1799 stack = player.method_31548().method_5438(i);
               if (stack.method_7909() == item) {
                  return swapSlot(i);
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean swapItem(String... sbId) {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null && sbId != null && sbId.length != 0) {
         String heldId = ItemUtils.getID(player.method_31548().method_5438(getNextUpdateIndex()));
         if (Arrays.stream(sbId).anyMatch(idx -> !idx.isBlank() && heldId.equals(idx))) {
            return true;
         } else if (!canSwap()) {
            return false;
         } else {
            for (int i = 0; i < 9; i++) {
               String id = ItemUtils.getID(player.method_31548().method_5438(i));
               if (!Arrays.stream(sbId).noneMatch(id1 -> !id1.isBlank() && id.equals(id1))) {
                  return swapSlot(i);
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean swapItem(Predicate<class_1799> predicate) {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         return false;
      } else if (predicate.test(player.method_31548().method_5438(getNextUpdateIndex()))) {
         return true;
      } else if (!canSwap()) {
         return false;
      } else {
         for (int i = 0; i < 9; i++) {
            if (predicate.test(player.method_31548().method_5438(i))) {
               return swapSlot(i);
            }
         }

         return false;
      }
   }

   public static boolean swapSlot(int slot) {
      class_746 player = class_310.method_1551().field_1724;
      if (slot == getNextUpdateIndex()) {
         return true;
      } else if (player == null || swappedThisTick) {
         return false;
      } else if (slot >= 0 && slot <= 8) {
         player.method_31548().method_61496(slot);
         return true;
      } else {
         RSA.getLogger().error("Invalid swap slot! : {}", slot);
         return false;
      }
   }

   public static boolean checkServerSlot(int slot) {
      return serverSlot == slot;
   }

   public static boolean checkServerItem(class_1792 item) {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null && serverSlot >= 0 && serverSlot <= 8) {
         class_1799 stack = player.method_31548().method_5438(serverSlot);
         return stack.method_7909() == item;
      } else {
         return false;
      }
   }

   public static boolean checkServerItem(String... sbId) {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null && serverSlot >= 0 && serverSlot <= 8 && sbId.length != 0) {
         String heldId = ItemUtils.getID(player.method_31548().method_5438(serverSlot));
         return Arrays.stream(sbId).anyMatch(id -> !id.isBlank() && heldId.equals(id));
      } else {
         return false;
      }
   }

   public static boolean checkClientItem(class_1792 item) {
      class_746 player = class_310.method_1551().field_1724;
      if (player == null) {
         return false;
      } else {
         class_1799 stack = player.method_31548().method_5438(player.method_31548().method_67532());
         return stack.method_7909() == item;
      }
   }

   public static boolean checkClientItem(String... sbId) {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null && sbId.length != 0) {
         String heldId = ItemUtils.getID(player.method_31548().method_5438(player.method_31548().method_67532()));
         return Arrays.stream(sbId).anyMatch(id -> !id.isBlank() && heldId.equals(id));
      } else {
         return false;
      }
   }

   public static int getItemSlot(class_1792 item) {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null && item != null) {
         for (int i = 0; i < 9; i++) {
            class_1799 stack = player.method_31548().method_5438(i);
            if (stack.method_7909() == item) {
               return i;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   public static int getItemSlot(String... id) {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null && id != null && id.length != 0) {
         for (int i = 0; i < 9; i++) {
            class_1799 stack = player.method_31548().method_5438(i);
            if (Arrays.stream(id).anyMatch(s -> s.equals(ItemUtils.getID(stack)))) {
               return i;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   public static int getServerSlot() {
      return serverSlot;
   }
}
