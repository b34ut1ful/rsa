package com.ricedotwho.rsa.module.impl.dungeon;

import com.google.common.collect.Lists;
import com.google.common.primitives.Shorts;
import com.google.common.primitives.SignedBytes;
import com.ricedotwho.rsa.RSA;
import com.ricedotwho.rsa.component.impl.managers.PacketOrderManager;
import com.ricedotwho.rsa.component.impl.managers.SwapManager;
import com.ricedotwho.rsm.RSM;
import com.ricedotwho.rsm.component.impl.Terminals;
import com.ricedotwho.rsm.component.impl.location.Floor;
import com.ricedotwho.rsm.component.impl.location.Island;
import com.ricedotwho.rsm.component.impl.location.Location;
import com.ricedotwho.rsm.component.impl.map.handler.Dungeon;
import com.ricedotwho.rsm.component.impl.task.TaskComponent;
import com.ricedotwho.rsm.data.DungeonClass;
import com.ricedotwho.rsm.data.DungeonPlayer;
import com.ricedotwho.rsm.data.Keybind;
import com.ricedotwho.rsm.data.Phase7;
import com.ricedotwho.rsm.event.api.SubscribeEvent;
import com.ricedotwho.rsm.event.impl.client.PacketEvent.Receive;
import com.ricedotwho.rsm.event.impl.game.TerminalEvent.Close;
import com.ricedotwho.rsm.event.impl.world.WorldEvent.Load;
import com.ricedotwho.rsm.module.Module;
import com.ricedotwho.rsm.module.api.Category;
import com.ricedotwho.rsm.module.api.ModuleInfo;
import com.ricedotwho.rsm.ui.clickgui.settings.Setting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.BooleanSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.KeybindSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.ModeSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.NumberSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.StringSetting;
import com.ricedotwho.rsm.utils.DungeonUtils;
import com.ricedotwho.rsm.utils.ItemUtils;
import com.ricedotwho.rsm.utils.Utils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import net.minecraft.class_10938;
import net.minecraft.class_124;
import net.minecraft.class_1657;
import net.minecraft.class_1703;
import net.minecraft.class_1707;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2371;
import net.minecraft.class_2653;
import net.minecraft.class_2813;
import net.minecraft.class_2815;
import net.minecraft.class_310;
import net.minecraft.class_3944;
import net.minecraft.class_634;

@ModuleInfo(aliases = "Fast Leap", id = "FastLeap", category = Category.DUNGEONS)
public class FastLeap extends Module {
   private final KeybindSetting key = new KeybindSetting("Key", new Keybind(0, false, true, true, null) {
      public boolean run() {
         return FastLeap.doAutoLeap();
      }
   });
   private final NumberSetting cooldown = new NumberSetting("Cooldown", 0.0, 5000.0, 2000.0, 50.0);
   private final BooleanSetting flMessage = new BooleanSetting("Chat Message", false);
   private final BooleanSetting flP3 = new BooleanSetting("P3 Only", true);
   private final ModeSetting flS1 = new ModeSetting("S1", "Archer", Arrays.asList("Archer", "Mage", "Berserk", "Healer", "Tank", "Custom"));
   private final StringSetting flS1Custom = new StringSetting("S1 Custom", "", true, false, () -> this.flS1.is("Custom"));
   private final ModeSetting flS2 = new ModeSetting("S2", "Healer", Arrays.asList("Archer", "Mage", "Berserk", "Healer", "Tank", "Custom"));
   private final StringSetting flS2Custom = new StringSetting("S2 Custom", "", true, false, () -> this.flS2.is("Custom"));
   private final ModeSetting flS3 = new ModeSetting("S3", "Mage", Arrays.asList("Archer", "Mage", "Berserk", "Healer", "Tank", "Custom"));
   private final StringSetting flS3Custom = new StringSetting("S3 Custom", "", true, false, () -> this.flS3.is("Custom"));
   private final ModeSetting flS4 = new ModeSetting("S4", "Mage", Arrays.asList("Archer", "Mage", "Berserk", "Healer", "Tank", "Custom"));
   private final StringSetting flS4Custom = new StringSetting("S4 Custom", "", true, false, () -> this.flS4.is("Custom"));
   private final ModeSetting flP1 = new ModeSetting("P1", "Berserk", Arrays.asList("Archer", "Mage", "Berserk", "Healer", "Tank", "Custom"));
   private final StringSetting flP1Custom = new StringSetting("P1 Custom", "", true, false, () -> this.flP1.is("Custom"));
   private final ModeSetting flP2 = new ModeSetting("P2", "Auto", Arrays.asList("Archer", "Mage", "Berserk", "Healer", "Tank", "Custom", "Auto"));
   private final StringSetting flP2Custom = new StringSetting("P2 Custom", "", true, false, () -> this.flP2.is("Custom"));
   private final ModeSetting flP4 = new ModeSetting("P4", "Berserk", Arrays.asList("Archer", "Mage", "Berserk", "Healer", "Tank", "Custom"));
   private final StringSetting flP4Custom = new StringSetting("P4 Custom", "", true, false, () -> this.flP4.is("Custom"));
   private final ModeSetting flP5Orange = new ModeSetting("P5 Orange", "Berserk", Arrays.asList("Archer", "Mage", "Berserk", "Healer", "Tank", "Custom"));
   private final StringSetting flP5OrangeCustom = new StringSetting("Orange Custom", "", true, false, () -> this.flP5Orange.is("Custom"));
   private final ModeSetting flP5Red = new ModeSetting("P5 Red", "Archer", Arrays.asList("Archer", "Mage", "Berserk", "Healer", "Tank", "Custom"));
   private final StringSetting flP5RedCustom = new StringSetting("Red Custom", "", true, false, () -> this.flP5Red.is("Custom"));
   private static String toLeap = null;
   private static boolean openingGui = false;
   private static long lastUsed = 0L;
   private boolean windowOpen = false;
   private static boolean queuedLeap = false;
   private class_1703 container;

   public FastLeap() {
      this.registerProperty(
         new Setting[]{
            this.key,
            this.cooldown,
            this.flMessage,
            this.flP3,
            this.flS1,
            this.flS1Custom,
            this.flS2,
            this.flS2Custom,
            this.flS3,
            this.flS3Custom,
            this.flS4,
            this.flS4Custom,
            this.flP1,
            this.flP1Custom,
            this.flP2,
            this.flP2Custom,
            this.flP4,
            this.flP4Custom,
            this.flP5Orange,
            this.flP5OrangeCustom,
            this.flP5Red,
            this.flP5RedCustom
         }
      );
   }

   public void reset() {
      toLeap = null;
      openingGui = false;
      this.windowOpen = false;
      this.container = null;
      queuedLeap = false;
   }

   @SubscribeEvent
   public void onLoad(Load event) {
      this.reset();
   }

   public static boolean doAutoLeap() {
      FastLeap module = (FastLeap)RSM.getModule(FastLeap.class);
      if ((!(Boolean)module.flP3.getValue() || DungeonUtils.isPhase(Phase7.P3))
         && Location.getArea().is(Island.Dungeon)
         && mc.field_1724 != null
         && mc.field_1687 != null
         && Utils.equalsOneOf(ItemUtils.getID(mc.field_1724.method_31548().method_7391()), new Object[]{"SPIRIT_LEAP", "INFINITE_SPIRIT_LEAP"})
         && System.currentTimeMillis() - lastUsed >= ((BigDecimal)module.cooldown.getValue()).longValue()
         && !module.windowOpen
         && !openingGui
         && (Terminals.isInTerminal() || mc.field_1755 == null)
         && module.container == null
         && Dungeon.isInBoss()) {
         if (Terminals.isInTerminal()) {
            queuedLeap = true;
            module.modMessage("Queued leap");
            return true;
         } else {
            String leap = getLeap();
            if (leap != null && !"NONE".equals(leap) && !mc.field_1724.method_5477().getString().equalsIgnoreCase(leap)) {
               doLeap(leap);
               return true;
            } else {
               module.modMessage(class_124.field_1061 + "Couldn't find who to leap to! (" + leap + ")");
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public static void doLeap(String name) {
      toLeap = name;
      openingGui = true;
      lastUsed = System.currentTimeMillis();
      PacketOrderManager.register(PacketOrderManager.STATE.ITEM_USE, () -> SwapManager.sendAirC08(mc.field_1724.field_5982, mc.field_1724.field_6004, false));
   }

   public static void doLeap(DungeonPlayer player) {
      doLeap(player.getName());
   }

   public static boolean doLeapFromOpenMenu(DungeonPlayer player) {
      return doLeapFromOpenMenu(player.getName());
   }

   public static boolean doLeapFromOpenMenu(String leap) {
      if (mc.field_1724 != null
         && mc.field_1724.field_7512 instanceof class_1707 menu
         && mc.field_1755 != null
         && mc.field_1755.method_25440().getString().equals("Spirit Leap")) {
         for (class_1735 slot : menu.field_7761) {
            class_1799 item = slot.method_7677();
            if (item.method_7909().equals(class_1802.field_8575)) {
               String name = class_124.method_539(item.method_7964().getString());
               if (name.equals(leap)) {
                  sendWindowClick(slot.field_7874, mc.field_1724, menu);
                  FastLeap fl = (FastLeap)RSM.getModule(FastLeap.class);
                  if (fl == null) {
                     return true;
                  }

                  if ((Boolean)fl.getFlMessage().getValue()) {
                     mc.method_1562().method_45730("pc Leaping to " + toLeap);
                  } else {
                     fl.modMessage("Leaping to " + toLeap);
                  }

                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @SubscribeEvent
   public void onTerminalClose(Close event) {
      if (event.isServer() && queuedLeap) {
         TaskComponent.onTick(0L, FastLeap::doAutoLeap);
      }

      queuedLeap = false;
   }

   @SubscribeEvent
   public void onOpenWindow(Receive event) {
      if (event.getPacket() instanceof class_3944 packet) {
         if (packet.method_17592() < 1 || packet.method_17592() > 100 || mc.field_1724 == null || !Location.getArea().is(Island.Dungeon)) {
            return;
         }

         if (openingGui && "Spirit Leap".equals(packet.method_17594().getString())) {
            openingGui = false;
            this.windowOpen = true;
            this.container = packet.method_17593().method_17434(packet.method_17592(), mc.field_1724.method_31548());
            event.setCancelled(true);
         } else {
            this.reset();
         }
      } else if (event.getPacket() instanceof class_2653 packet) {
         if (packet.method_11452() < 1
            || packet.method_11452() > 100
            || mc.field_1724 == null
            || !this.windowOpen
            || openingGui
            || this.container.field_7763 != packet.method_11452()
            || packet.method_11450() < 11
            || toLeap == null) {
            return;
         }

         this.container.method_7619(packet.method_11450(), packet.method_37439(), packet.method_11449());
         if (packet.method_11450() > 16) {
            this.modMessage(class_124.field_1061 + "Failed to find player!");
            this.close();
            return;
         }

         class_1799 item = packet.method_11449();
         if (!item.method_7909().equals(class_1802.field_8575)) {
            return;
         }

         String name = class_124.method_539(item.method_7964().getString());
         if (!name.equals(toLeap)) {
            return;
         }

         sendWindowClick(packet.method_11450(), mc.field_1724, this.container);
         if ((Boolean)this.getFlMessage().getValue()) {
            mc.method_1562().method_45730("pc Leaping to " + toLeap);
         } else {
            this.modMessage("Leaping to " + toLeap);
         }

         this.reset();
      }
   }

   private void close() {
      if (this.container != null && mc.method_1562() != null) {
         mc.method_1562().method_52787(new class_2815(this.container.field_7763));
         this.reset();
      }
   }

   private static void sendWindowClick(int slotNumber, class_1657 player, class_1703 abstractContainerMenu) {
      class_634 connection = class_310.method_1551().method_1562();
      if (connection != null) {
         class_2371<class_1735> nonNullList = abstractContainerMenu.field_7761;
         int l = nonNullList.size();
         List<class_1799> list = Lists.newArrayListWithCapacity(l);

         for (class_1735 slot : nonNullList) {
            list.add(slot.method_7677().method_7972());
         }

         abstractContainerMenu.method_7593(slotNumber, 0, class_1713.field_7796, player);
         Int2ObjectMap<class_10938> int2ObjectMap = new Int2ObjectOpenHashMap();

         for (int m = 0; m < l; m++) {
            class_1799 itemStack = list.get(m);
            class_1799 itemStack2 = ((class_1735)nonNullList.get(m)).method_7677();
            if (!class_1799.method_7973(itemStack, itemStack2)) {
               int2ObjectMap.put(m, class_10938.method_68853(itemStack2, connection.method_68823()));
            }
         }

         class_10938 hashedStack = class_10938.method_68853(abstractContainerMenu.method_34255(), connection.method_68823());
         connection.method_52787(
            new class_2813(
               abstractContainerMenu.field_7763,
               abstractContainerMenu.method_37421(),
               Shorts.checkedCast(slotNumber),
               SignedBytes.checkedCast(0L),
               class_1713.field_7796,
               int2ObjectMap,
               hashedStack
            )
         );
      }
   }

   private static String getLeap() {
      DungeonPlayer player = getClassPlayer();
      return player == null ? null : player.getName();
   }

   private static Object getStageClass() {
      if (Utils.equalsOneOf(Location.getFloor(), new Object[]{Floor.F7, Floor.M7}) && Dungeon.isInBoss()) {
         FastLeap module = (FastLeap)RSM.getModule(FastLeap.class);
         DungeonPlayer me = Dungeon.getMyPlayer();
         switch (DungeonUtils.getF7Phase()) {
            case P1:
               return module.getFlP1().is("Custom") ? module.getFlP1Custom().getValue() : module.getFlP1().getIndex();
            case P2:
               if (module.getFlP2().is("Auto")) {
                  if (me == null) {
                     return -1;
                  }

                  DungeonPlayer healer = Dungeon.getClazz(DungeonClass.HEALER);
                  DungeonPlayer mage = Dungeon.getClazz(DungeonClass.MAGE);
                  DungeonPlayer bers = Dungeon.getClazz(DungeonClass.BERSERKER);
                  if (me.getDClass().equals(DungeonClass.TANK) && mage != null && Location.getFloor().equals(Floor.F7)) {
                     return mage;
                  }

                  if (healer != null && !me.equals(healer)) {
                     return healer;
                  }

                  if (bers != null && me.equals(healer)) {
                     return bers;
                  }

                  return -1;
               }

               return module.getFlP2().is("Custom") ? module.getFlP2Custom().getValue() : module.getFlP2().getIndex();
            case P3:
               return switch (DungeonUtils.getP3Section()) {
                  case S1 -> module.getFlS1().is("Custom") ? module.getFlS1Custom().getValue() : module.getFlS1().getIndex();
                  case S2 -> module.getFlS2().is("Custom") ? module.getFlS2Custom().getValue() : module.getFlS2().getIndex();
                  case S3 -> module.getFlS3().is("Custom") ? module.getFlS3Custom().getValue() : module.getFlS3().getIndex();
                  case S4 -> module.getFlS4().is("Custom") ? module.getFlS4Custom().getValue() : module.getFlS4().getIndex();
                  default -> -1;
               };
            case P4:
               return module.getFlS4().is("Custom") ? module.getFlS4Custom().getValue() : module.getFlS4().getIndex();
            case P5:
               if (me == null) {
                  return -1;
               } else if (DungeonClass.HEALER.equals(me.getDClass())) {
                  return module.getFlP5Orange().is("Custom") ? module.getFlP5OrangeCustom().getValue() : module.getFlP5Orange().getIndex();
               } else if (DungeonClass.MAGE.equals(me.getDClass())) {
                  return module.getFlP5Orange().is("Custom") ? module.getFlP5OrangeCustom().getValue() : module.getFlP5Orange().getIndex();
               } else {
                  if (DungeonClass.TANK.equals(me.getDClass())) {
                     return module.getFlP5Red().is("Custom") ? module.getFlP5RedCustom().getValue() : module.getFlP5Red().getIndex();
                  }

                  return -1;
               }
            default:
               return -1;
         }
      } else {
         return -1;
      }
   }

   private static DungeonPlayer getClassPlayer() {
      Object yuh = getStageClass();
      if (yuh instanceof DungeonPlayer dp) {
         return dp;
      } else {
         return yuh instanceof String s ? Dungeon.getPlayer(s) : Dungeon.getClazz((Integer)yuh);
      }
   }

   private void modMessage(String message) {
      RSA.chat(class_124.field_1078 + "Fast Leap » " + class_124.field_1070 + message);
   }

   public KeybindSetting getKey() {
      return this.key;
   }

   public NumberSetting getCooldown() {
      return this.cooldown;
   }

   public BooleanSetting getFlMessage() {
      return this.flMessage;
   }

   public BooleanSetting getFlP3() {
      return this.flP3;
   }

   public ModeSetting getFlS1() {
      return this.flS1;
   }

   public StringSetting getFlS1Custom() {
      return this.flS1Custom;
   }

   public ModeSetting getFlS2() {
      return this.flS2;
   }

   public StringSetting getFlS2Custom() {
      return this.flS2Custom;
   }

   public ModeSetting getFlS3() {
      return this.flS3;
   }

   public StringSetting getFlS3Custom() {
      return this.flS3Custom;
   }

   public ModeSetting getFlS4() {
      return this.flS4;
   }

   public StringSetting getFlS4Custom() {
      return this.flS4Custom;
   }

   public ModeSetting getFlP1() {
      return this.flP1;
   }

   public StringSetting getFlP1Custom() {
      return this.flP1Custom;
   }

   public ModeSetting getFlP2() {
      return this.flP2;
   }

   public StringSetting getFlP2Custom() {
      return this.flP2Custom;
   }

   public ModeSetting getFlP4() {
      return this.flP4;
   }

   public StringSetting getFlP4Custom() {
      return this.flP4Custom;
   }

   public ModeSetting getFlP5Orange() {
      return this.flP5Orange;
   }

   public StringSetting getFlP5OrangeCustom() {
      return this.flP5OrangeCustom;
   }

   public ModeSetting getFlP5Red() {
      return this.flP5Red;
   }

   public StringSetting getFlP5RedCustom() {
      return this.flP5RedCustom;
   }

   public boolean isWindowOpen() {
      return this.windowOpen;
   }

   public class_1703 getContainer() {
      return this.container;
   }
}
