package com.ricedotwho.rsa.module.impl.dungeon;

import com.mojang.datafixers.util.Pair;
import com.ricedotwho.rsa.RSA;
import com.ricedotwho.rsa.component.impl.managers.PacketOrderManager;
import com.ricedotwho.rsa.component.impl.managers.SwapManager;
import com.ricedotwho.rsm.component.impl.location.Floor;
import com.ricedotwho.rsm.component.impl.location.Island;
import com.ricedotwho.rsm.component.impl.location.Location;
import com.ricedotwho.rsm.component.impl.map.Map;
import com.ricedotwho.rsm.component.impl.map.handler.Dungeon;
import com.ricedotwho.rsm.data.Phase7;
import com.ricedotwho.rsm.event.api.SubscribeEvent;
import com.ricedotwho.rsm.event.impl.client.PacketEvent.Receive;
import com.ricedotwho.rsm.event.impl.client.PacketEvent.Send;
import com.ricedotwho.rsm.event.impl.game.ChatEvent.Chat;
import com.ricedotwho.rsm.event.impl.game.ClientTickEvent.Start;
import com.ricedotwho.rsm.event.impl.world.BlockChangeEvent;
import com.ricedotwho.rsm.event.impl.world.WorldEvent.Load;
import com.ricedotwho.rsm.module.Module;
import com.ricedotwho.rsm.module.api.Category;
import com.ricedotwho.rsm.module.api.ModuleInfo;
import com.ricedotwho.rsm.ui.clickgui.settings.Setting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.BooleanSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.ModeSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.NumberSetting;
import com.ricedotwho.rsm.utils.DungeonUtils;
import com.ricedotwho.rsm.utils.RotationUtils;
import com.ricedotwho.rsm.utils.Utils;
import it.unimi.dsi.fastutil.ints.Int2LongOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import net.minecraft.class_124;
import net.minecraft.class_1297;
import net.minecraft.class_1531;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2401;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2623;
import net.minecraft.class_2631;
import net.minecraft.class_2680;
import net.minecraft.class_2744;
import net.minecraft.class_2815;
import net.minecraft.class_2885;
import net.minecraft.class_310;
import net.minecraft.class_3944;
import net.minecraft.class_3965;
import net.minecraft.class_4050;
import net.minecraft.class_465;
import net.minecraft.class_638;
import net.minecraft.class_9296;
import net.minecraft.class_9334;

@ModuleInfo(aliases = "Secrets", id = "Secrets", category = Category.DUNGEONS, hasKeybind = true)
public class SecretAura extends Module {
   private static final double CHEST_RANGE = 5.745;
   private static final double SKULL_RANGE = 4.5;
   private static final double CHEST_RANGE_SQ = 33.005025;
   private static final double SKULL_RANGE_SQ = 20.25;
   private static final String REDSTONE_KEY_ID = "fed95410-aba1-39df-9b95-1d4f361eb66e";
   private static final String WITHER_ESSENCE_ID = "e0f3e929-869e-3dca-9504-54c666ee6f23";
   private static final class_2561 CHEST_KEY = class_2561.method_43471("container.chest");
   private static final class_2561 LARGE_CHEST_KEY = class_2561.method_43471("container.chestDouble");
   private final HashSet<Integer> BOSS_LEVERS = new HashSet<>();
   private final HashSet<Integer> LIGHTS_DEV = new HashSet<>();
   private final int jewLeverHash = new class_2338(61, 134, 142).hashCode();
   private final ModeSetting type = new ModeSetting("Type", "Aura", List.of("Aura", "Triggerbot", "None"));
   private final NumberSetting delay = new NumberSetting("Click Delay", 100.0, 4000.0, 150.0, 50.0);
   private final NumberSetting reclick = new NumberSetting("Re-Click Delay", 200.0, 10000.0, 500.0, 50.0);
   private final NumberSetting swapSlot = new NumberSetting("Swap Slot Index", 0.0, 7.0, 0.0, 1.0);
   private final BooleanSetting invWalk = new BooleanSetting("In inventory", true);
   private final BooleanSetting allowReclick = new BooleanSetting("Allow Re-click", true);
   private final BooleanSetting allowBossReclick = new BooleanSetting("Allow Boss Re-click", true);
   private final BooleanSetting inBoss = new BooleanSetting("In Boss", true);
   private final BooleanSetting autoClose = new BooleanSetting("Auto Close GUI", false);
   private final BooleanSetting forceSkyblock = new BooleanSetting("Force Skyblock", false);
   private boolean hasRedstoneKey = false;
   private final Int2LongOpenHashMap clickedBlocks = new Int2LongOpenHashMap(5);
   private final IntOpenHashSet blocksDone = new IntOpenHashSet();
   private int clickBlockCooldown = 20;
   private int lastSlot = -1;

   public SecretAura() {
      this.registerProperty(
         new Setting[]{
            this.type,
            this.delay,
            this.reclick,
            this.swapSlot,
            this.invWalk,
            this.allowReclick,
            this.allowBossReclick,
            this.inBoss,
            this.autoClose,
            this.forceSkyblock
         }
      );
      this.BOSS_LEVERS.add(new class_2338(106, 124, 113).hashCode());
      this.BOSS_LEVERS.add(new class_2338(94, 124, 113).hashCode());
      this.BOSS_LEVERS.add(new class_2338(23, 132, 138).hashCode());
      this.BOSS_LEVERS.add(new class_2338(27, 124, 127).hashCode());
      this.BOSS_LEVERS.add(new class_2338(2, 122, 55).hashCode());
      this.BOSS_LEVERS.add(new class_2338(14, 122, 55).hashCode());
      this.BOSS_LEVERS.add(new class_2338(84, 121, 34).hashCode());
      this.BOSS_LEVERS.add(new class_2338(86, 128, 46).hashCode());
      this.LIGHTS_DEV.add(new class_2338(58, 133, 142).hashCode());
      this.LIGHTS_DEV.add(new class_2338(58, 136, 142).hashCode());
      this.LIGHTS_DEV.add(new class_2338(62, 136, 142).hashCode());
      this.LIGHTS_DEV.add(new class_2338(62, 133, 142).hashCode());
      this.LIGHTS_DEV.add(new class_2338(60, 135, 142).hashCode());
      this.LIGHTS_DEV.add(new class_2338(60, 134, 142).hashCode());
   }

   @SubscribeEvent
   public void onWorldLoad(Load event) {
      this.clear();
      this.clickBlockCooldown = 20;
   }

   @SubscribeEvent
   public void onTickEnd(Start event) {
      this.clickBlockCooldown--;
   }

   @SubscribeEvent
   public void onSendPacket(Send event) {
      if (event.getPacket() instanceof class_2885) {
         this.clickBlockCooldown = 1;
      }
   }

   @SubscribeEvent
   public void onReceivePacket(Receive event) {
      if ((Boolean)this.autoClose.getValue()
         && Location.getArea().is(Island.Dungeon)
         && event.getPacket() instanceof class_3944 openScreenPacket
         && class_310.method_1551().method_1562() != null) {
         RSA.getLogger().info("Container title: {}", openScreenPacket.method_17594());
         String content = class_124.method_539(openScreenPacket.method_17594().getString());
         if (Utils.equalsOneOf(openScreenPacket.method_17594(), new Object[]{CHEST_KEY, LARGE_CHEST_KEY})
            || Utils.equalsOneOf(content, new Object[]{"Chest", "Large Chest"})) {
            int windowId = openScreenPacket.method_17592();
            class_310.method_1551().method_1562().method_52787(new class_2815(windowId));
            event.setCancelled(true);
         }
      }
   }

   @SubscribeEvent
   public void onTickStart(Start event) {
      class_310 client = class_310.method_1551();
      if (client.field_1724 != null && client.field_1687 != null && !this.type.is("None")) {
         boolean forcedSkyblock = (Boolean)this.forceSkyblock.getValue();
         if (forcedSkyblock || Location.getArea().is(Island.Dungeon) && !this.isRoomDisabled()) {
            if (forcedSkyblock || !Dungeon.isInBoss() || (Boolean)this.inBoss.getValue()) {
               if ((Boolean)this.invWalk.getValue() || !(client.field_1755 instanceof class_465)) {
                  class_638 world = client.field_1687;
                  boolean sneaking = client.field_1724.method_71091().comp_3164();
                  class_243 eyePos = client.field_1724
                     .method_73189()
                     .method_1031(0.0, sneaking ? 1.54F : client.field_1724.method_18381(class_4050.field_18076), 0.0);
                  class_243 flooredEyePos = eyePos.method_1023(0.5, 0.0, 0.5);
                  Iterable<class_2338> positions;
                  if (this.type.is("Aura")) {
                     class_238 box = new class_238(eyePos, eyePos).method_1009(5.745, 5.745, 5.745);
                     positions = class_2338.method_62671(box);
                  } else {
                     if (!(client.field_1765 instanceof class_3965 blockHitResult)) {
                        return;
                     }

                     positions = Collections.singleton(blockHitResult.method_17777());
                  }

                  boolean isFloor7Phase3 = Location.getArea().is(Island.Dungeon)
                     && (Location.getFloor() == Floor.F7 || Location.getFloor() == Floor.M7)
                     && DungeonUtils.isPhase(Phase7.P3);
                  boolean isAllowedArea = forcedSkyblock || isFloor7Phase3;
                  double bestDistanceSq = Double.MAX_VALUE;
                  class_2338 bestCandidate = null;
                  boolean requireUnclickedBlock = !(Boolean)this.allowReclick.getValue();

                  for (class_2338 blockPos : positions) {
                     int hash = getBlockPosHash(blockPos);
                     class_2680 blockState = world.method_8320(blockPos);
                     class_2248 block = blockState.method_26204();
                     long clickDelay = ((BigDecimal)this.delay.getValue()).longValue();
                     if (isAllowedArea) {
                        if (Dungeon.isInBoss() && block != class_2246.field_10363) {
                           continue;
                        }

                        if (block == class_2246.field_10363) {
                           if (this.checkF7BossBlock(blockPos, blockState)) {
                              if (!(Boolean)this.inBoss.getValue()) {
                                 continue;
                              }

                              if ((Boolean)this.allowBossReclick.getValue()) {
                                 requireUnclickedBlock = false;
                              }

                              clickDelay = 0L;
                           } else if (this.checkLightsDev(blockPos)) {
                              continue;
                           }
                        }
                     }

                     boolean isSkullCandidate = block == class_2246.field_10432 && isValidSkull(blockPos, world);
                     if ((!requireUnclickedBlock || !this.blocksDone.contains(hash))
                        && (this.isValidBlock(block) || isSkullCandidate)
                        && (!Dungeon.isInBoss() || block != class_2246.field_10432)) {
                        if (getSkullType(blockPos, world).equals(SecretAura.SkullType.KEY)) {
                           this.hasRedstoneKey = false;
                        }

                        if (!this.clickedBlocks.containsKey(hash)) {
                           if (clickDelay > 0L) {
                              this.clickedBlocks.put(hash, System.currentTimeMillis() + clickDelay);
                              continue;
                           }

                           this.clickedBlocks.put(hash, System.currentTimeMillis());
                        }

                        long nextClickTime = this.clickedBlocks.get(hash);
                        if (nextClickTime <= System.currentTimeMillis()) {
                           double distanceSq = flooredEyePos.method_1028(blockPos.method_10263(), blockPos.method_10264(), blockPos.method_10260());
                           boolean inSkullRange = block != class_2246.field_10432 || distanceSq <= 20.25;
                           boolean inChestRange = distanceSq <= 33.005025;
                           if (inSkullRange && inChestRange && distanceSq < bestDistanceSq) {
                              bestDistanceSq = distanceSq;
                              bestCandidate = new class_2338(blockPos);
                           }
                        }
                     }
                  }

                  if (bestCandidate != null) {
                     class_2680 candidateState = world.method_8320(bestCandidate);
                     class_2248 candidateBlock = candidateState.method_26204();
                     boolean alreadyOnSkullSlot = candidateBlock != class_2246.field_10432 && client.field_1724.method_31548().method_67532() == 8;
                     if (!alreadyOnSkullSlot || SwapManager.swapSlot(((BigDecimal)this.swapSlot.getValue()).intValue())) {
                        this.clickedBlocks.put(getBlockPosHash(bestCandidate), System.currentTimeMillis() + ((BigDecimal)this.reclick.getValue()).longValue());
                        class_238 blockAABB = candidateState.method_26218(world, bestCandidate).method_1107();
                        class_243 center = new class_243(
                           (blockAABB.field_1323 + blockAABB.field_1320) * 0.5 + bestCandidate.method_10263(),
                           (blockAABB.field_1322 + blockAABB.field_1325) * 0.5 + bestCandidate.method_10264(),
                           (blockAABB.field_1321 + blockAABB.field_1324) * 0.5 + bestCandidate.method_10260()
                        );
                        class_3965 result = RotationUtils.collisionRayTrace(bestCandidate, blockAABB, eyePos, center);
                        if (result != null) {
                           PacketOrderManager.register(
                              PacketOrderManager.STATE.ITEM_USE,
                              () -> SwapManager.sendBlockC08(result.method_17784(), result.method_17780(), candidateBlock != class_2246.field_10432, true)
                           );
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean checkLightsDev(class_2338 pos) {
      return pos.method_10260() == 142 && pos.method_10264() <= 136 && pos.method_10264() >= 133 && pos.method_10263() >= 58 && pos.method_10263() <= 62;
   }

   private boolean checkF7BossBlock(class_2338 pos, class_2680 blockState) {
      int hash = pos.hashCode();
      boolean isKnownBossLever = this.BOSS_LEVERS.contains(hash);
      boolean isUnpoweredLightsLever = this.checkLightsDev(pos) && this.LIGHTS_DEV.contains(hash) && !(Boolean)blockState.method_11654(class_2401.field_11265);
      boolean isJewLever = hash == this.jewLeverHash;
      return isKnownBossLever || isUnpoweredLightsLever || isJewLever;
   }

   private boolean isValidBlock(class_2248 block) {
      return block != class_2246.field_10124
         && (
            block == class_2246.field_10363
               || block == class_2246.field_10034
               || block == class_2246.field_10380
               || block == class_2246.field_10002 && this.hasRedstoneKey
         );
   }

   public static boolean isValidSkull(class_2338 blockPos, class_638 level) {
      return isValidSkull(blockPos, level, false);
   }

   public static boolean isValidSkull(class_2338 blockPos, class_638 level, boolean keyOnly) {
      return level.method_8321(blockPos) instanceof class_2631 skullBlockEntity ? isValidProfile(skullBlockEntity.method_11334(), keyOnly) : false;
   }

   public static boolean isValidProfile(class_9296 gameProfile, boolean keyOnly) {
      if (gameProfile == null) {
         return false;
      } else {
         String uuid = gameProfile.method_73313().id().toString();
         if (keyOnly) {
            return uuid.equals("fed95410-aba1-39df-9b95-1d4f361eb66e");
         } else {
            return switch (uuid) {
               case "e0f3e929-869e-3dca-9504-54c666ee6f23", "fed95410-aba1-39df-9b95-1d4f361eb66e" -> true;
               default -> false;
            };
         }
      }
   }

   public static SecretAura.SkullType getSkullType(class_2338 blockPos, class_638 level) {
      return level.method_8321(blockPos) instanceof class_2631 skullBlockEntity ? getSkullType(skullBlockEntity.method_11334()) : SecretAura.SkullType.NONE;
   }

   public static SecretAura.SkullType getSkullType(class_9296 gameProfile) {
      if (gameProfile == null) {
         return SecretAura.SkullType.NONE;
      } else {
         String uuid = gameProfile.method_73313().id().toString();

         return switch (uuid) {
            case "e0f3e929-869e-3dca-9504-54c666ee6f23" -> SecretAura.SkullType.ESSENCE;
            case "fed95410-aba1-39df-9b95-1d4f361eb66e" -> SecretAura.SkullType.KEY;
            default -> SecretAura.SkullType.NONE;
         };
      }
   }

   private boolean isRoomDisabled() {
      if (!Location.getArea().is(Island.Dungeon) || Dungeon.isInBoss()) {
         return false;
      } else if (Map.getCurrentRoom() == null) {
         return true;
      } else {
         String roomName = Map.getCurrentRoom().getData().name();

         return switch (roomName) {
            case "Water Board", "Three Weirdos" -> true;
            default -> false;
         };
      }
   }

   private static int getBlockPosHash(class_2338 blockPos) {
      return blockPos.method_10264() & 0xFF | (blockPos.method_10263() + 2048 & 4095) << 8 | (blockPos.method_10260() + 2048 & 4095) << 20;
   }

   @SubscribeEvent
   public void onPacket(Receive event) {
      if (mc.field_1724 != null && mc.field_1687 != null && !this.type.is("None")) {
         if (event.getPacket() instanceof class_2623 packet) {
            if (packet.method_11295().equals(class_2246.field_42729)) {
               this.blocksDone.add(getBlockPosHash(packet.method_11298()));
            }
         } else if (event.getPacket() instanceof class_2744 equipmentPacket) {
            class_1297 entity = mc.field_1687.method_8469(equipmentPacket.method_11820());
            if (!(entity instanceof class_1531) || equipmentPacket.method_30145().size() < 4) {
               return;
            }

            class_1799 stack = (class_1799)((Pair)equipmentPacket.method_30145().get(4)).getSecond();
            if (!stack.method_31574(class_1802.field_8575)) {
               return;
            }

            Optional<? extends class_9296> profile = stack.method_57380().method_57845(class_9334.field_49617);
            if (profile == null || profile.isEmpty() || !isValidProfile(profile.get(), true)) {
               return;
            }

            this.blocksDone.add(getBlockPosHash(new class_2338(entity.method_31477(), entity.method_31478() + 2, entity.method_31479())));
         }
      }
   }

   @SubscribeEvent
   public void onChat(Chat event) {
      if (mc.field_1724 != null && mc.field_1687 != null && !this.type.is("None") && (Boolean)this.inBoss.getValue()) {
         String content = class_124.method_539(event.getMessage().getString());
         if ("[BOSS] Goldor: Who dares trespass into my domain?".equals(content)) {
            this.clear();
            RSA.chat("Blocks cleared!");
         }
      }
   }

   @SubscribeEvent
   public void onBlockChange(BlockChangeEvent event) {
      if (mc.field_1724 != null && mc.field_1687 != null && !this.type.is("None") && mc.field_1724.method_5707(event.getPos().asVec3()) <= 40.0) {
         if (event.getOldState().method_27852(class_2246.field_10363)) {
            this.blocksDone.add(getBlockPosHash(event.getBlockPos()));
         } else if (event.getOldState().method_27852(class_2246.field_10432)) {
            if (!event.getNewState().method_27852(class_2246.field_10124)) {
               return;
            }

            if (isValidSkull(event.getBlockPos(), mc.field_1687, true)) {
               this.hasRedstoneKey = true;
            }
         } else if (event.getOldState().method_27852(class_2246.field_10002)) {
            this.blocksDone.add(getBlockPosHash(event.getBlockPos()));
         }
      }
   }

   public void clear() {
      this.blocksDone.clear();
      this.hasRedstoneKey = false;
   }

   public void onEnable() {
      this.clear();
   }

   public void onDisable() {
   }

   public HashSet<Integer> getBOSS_LEVERS() {
      return this.BOSS_LEVERS;
   }

   public HashSet<Integer> getLIGHTS_DEV() {
      return this.LIGHTS_DEV;
   }

   public int getJewLeverHash() {
      return this.jewLeverHash;
   }

   public ModeSetting getType() {
      return this.type;
   }

   public NumberSetting getDelay() {
      return this.delay;
   }

   public NumberSetting getReclick() {
      return this.reclick;
   }

   public NumberSetting getSwapSlot() {
      return this.swapSlot;
   }

   public BooleanSetting getInvWalk() {
      return this.invWalk;
   }

   public BooleanSetting getAllowReclick() {
      return this.allowReclick;
   }

   public BooleanSetting getAllowBossReclick() {
      return this.allowBossReclick;
   }

   public BooleanSetting getInBoss() {
      return this.inBoss;
   }

   public BooleanSetting getAutoClose() {
      return this.autoClose;
   }

   public BooleanSetting getForceSkyblock() {
      return this.forceSkyblock;
   }

   public boolean isHasRedstoneKey() {
      return this.hasRedstoneKey;
   }

   public Int2LongOpenHashMap getClickedBlocks() {
      return this.clickedBlocks;
   }

   public IntOpenHashSet getBlocksDone() {
      return this.blocksDone;
   }

   public int getClickBlockCooldown() {
      return this.clickBlockCooldown;
   }

   public int getLastSlot() {
      return this.lastSlot;
   }

   public static enum SkullType {
      ESSENCE,
      KEY,
      NONE;
   }
}
