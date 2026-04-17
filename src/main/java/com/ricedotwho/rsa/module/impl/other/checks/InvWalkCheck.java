package com.ricedotwho.rsa.module.impl.other.checks;

import com.ricedotwho.rsa.RSA;
import com.ricedotwho.rsa.module.impl.other.AntiCheat;
import com.ricedotwho.rsm.event.api.SubscribeEvent;
import com.ricedotwho.rsm.event.impl.game.ChatEvent.Chat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.class_1297;
import net.minecraft.class_1531;
import net.minecraft.class_1657;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_310;
import net.minecraft.class_3544;
import net.minecraft.class_638;
import net.minecraft.class_746;

public class InvWalkCheck {
   public static boolean startChecking;
   private static final Pattern playerName = Pattern.compile("^(\\w+)\\s+activated a terminal");
   public static String username;
   private static final Map<class_2338, class_1297> inactiveTerminals = new HashMap<>();
   private static final List<String> termCompleter = new ArrayList<>();
   private static final List<Double> TermPos = new ArrayList<>();
   private static final List<Double> PlayerPos = new ArrayList<>();

   @SubscribeEvent
   public static void setRunning() {
      if ((Boolean)AntiCheat.termWalking.getValue()) {
         startChecking = true;
      }
   }

   @SubscribeEvent
   public static void Check1() {
      class_310 mc = class_310.method_1551();
      class_638 level = mc.field_1687;
      class_746 player = mc.field_1724;
      if (player != null && level != null) {
         class_238 searchBox = player.method_5829().method_1014(192.0);
         List<class_1297> entities = level.method_8335(null, searchBox);
         Set<class_2338> currentInactive = new HashSet<>();

         for (class_1297 entity : entities) {
            String name = entity.method_5477().getString();
            class_2338 pos = entity.method_24515();
            if (entity instanceof class_1531) {
               if (name.contains("Inactive Terminal")) {
                  currentInactive.add(pos);
                  inactiveTerminals.putIfAbsent(pos, entity);
               } else if (name.contains("Terminal Active") && inactiveTerminals.containsKey(pos)) {
                  double TermX = entity.method_23317();
                  double TermY = entity.method_23318();
                  double TermZ = entity.method_23321();
                  TermPos.add(TermX);
                  TermPos.add(TermY);
                  TermPos.add(TermZ);
                  inactiveTerminals.remove(pos);
               }
            }

            if (entity instanceof class_1657 && !termCompleter.isEmpty() && name.contains(termCompleter.getFirst())) {
               double playerx = entity.method_23317();
               double playery = entity.method_23318();
               double playerz = entity.method_23321();
               PlayerPos.add(playerx);
               PlayerPos.add(playery);
               PlayerPos.add(playerz);
               termCompleter.removeFirst();
            }
         }

         if (!PlayerPos.isEmpty() && !TermPos.isEmpty()) {
            double xOffset = PlayerPos.getFirst() - TermPos.getFirst();
            double yOffset = PlayerPos.get(1) - TermPos.get(1);
            double zOffset = PlayerPos.get(2) - TermPos.get(2);
            PlayerPos.clear();
            TermPos.clear();
            if ((!(xOffset > 7.0) || !(xOffset < 40.0)) && (!(xOffset < -7.0) || !(xOffset > -40.0))) {
               if (yOffset > 13.0 || yOffset < -13.0) {
                  RSA.chat("§b" + username + " §7Failed InvWalk Check §4§lyOffSet§r§7: §8" + yOffset);
                  username = null;
               } else if ((!(zOffset > 7.0) || !(zOffset < 40.0)) && (!(zOffset < -7.0) || !(zOffset > -40.0))) {
                  if (xOffset > 40.0 || xOffset < -40.0) {
                     RSA.chat("§b" + username + " §7Failed AutoLeap Check §4§lzOffSet§r§7: §8" + xOffset);
                     username = null;
                  } else if (zOffset > 40.0 || zOffset < -40.0) {
                     RSA.chat("§b" + username + " §7Failed AutoLeap Check §4§lzOffSet§r§7: §8" + zOffset);
                     username = null;
                  }
               } else {
                  RSA.chat("§b" + username + " §7Failed InvWalk Check §4§lzOffSet§r§7: §8" + zOffset);
                  username = null;
               }
            } else {
               RSA.chat("§b" + username + " §7Failed InvWalk Check §4§lxOffSet§r§7: §8" + xOffset);
               username = null;
            }

            PlayerPos.clear();
            TermPos.clear();
         }

         termCompleter.clear();
         inactiveTerminals.keySet().retainAll(currentInactive);
      }
   }

   @SubscribeEvent
   public static void terminalCompletedMsg(Chat event) {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null) {
         String unformatted = class_3544.method_15440(event.getMessage().getString());
         Matcher matcher = playerName.matcher(unformatted);
         if (matcher.find()) {
            termCompleter.add(matcher.group(1));
            username = matcher.group(1);
         }
      }
   }
}
