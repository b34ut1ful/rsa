package com.ricedotwho.rsa.component.impl.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import net.minecraft.class_2596;

public class PacketOrderManager {
   private static final ConcurrentHashMap<PacketOrderManager.STATE, List<Runnable>> packets = new ConcurrentHashMap<>();
   private static final List<Predicate<class_2596<?>>> receiveListeners = new ArrayList<>(4);

   private PacketOrderManager() {
   }

   public static void onPreTickStart() {
      execute(PacketOrderManager.STATE.START);
   }

   public static void register(PacketOrderManager.STATE state, Runnable runnable) {
      synchronized (packets) {
         if (!packets.containsKey(state)) {
            packets.put(state, new ArrayList<>());
         }
      }

      List<Runnable> list = packets.get(state);
      synchronized (list) {
         list.add(runnable);
      }
   }

   public static void registerReceiveListener(Predicate<class_2596<?>> listener) {
      synchronized (receiveListeners) {
         receiveListeners.add(listener);
      }
   }

   public static void onPreReceivePacket(class_2596<?> packet) {
      synchronized (receiveListeners) {
         if (!receiveListeners.isEmpty()) {
            receiveListeners.removeIf(predicate -> predicate.test(packet));
         }
      }
   }

   public static void execute(PacketOrderManager.STATE state) {
      if (packets.containsKey(state)) {
         List<Runnable> runnables = packets.get(state);
         synchronized (runnables) {
            if (!runnables.isEmpty()) {
               runnables.forEach(Runnable::run);
               runnables.clear();
            }
         }
      }
   }

   public static enum STATE {
      START,
      ITEM_USE,
      ATTACK;
   }
}
