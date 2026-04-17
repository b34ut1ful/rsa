package com.ricedotwho.rsa.module.impl.dungeon.boss;

import com.google.common.reflect.TypeToken;
import com.ricedotwho.rsa.RSA;
import com.ricedotwho.rsa.component.impl.managers.SwapManager;
import com.ricedotwho.rsa.module.impl.dungeon.DungeonBreaker;
import com.ricedotwho.rsa.utils.InteractUtils;
import com.ricedotwho.rsm.component.impl.Renderer3D;
import com.ricedotwho.rsm.component.impl.location.Floor;
import com.ricedotwho.rsm.component.impl.location.Island;
import com.ricedotwho.rsm.component.impl.location.Location;
import com.ricedotwho.rsm.component.impl.map.handler.Dungeon;
import com.ricedotwho.rsm.data.Colour;
import com.ricedotwho.rsm.data.Keybind;
import com.ricedotwho.rsm.data.Pos;
import com.ricedotwho.rsm.event.api.SubscribeEvent;
import com.ricedotwho.rsm.event.impl.client.PacketEvent.PostReceive;
import com.ricedotwho.rsm.event.impl.game.ClientTickEvent.Start;
import com.ricedotwho.rsm.event.impl.render.Render3DEvent.Extract;
import com.ricedotwho.rsm.event.impl.world.WorldEvent.Load;
import com.ricedotwho.rsm.module.Module;
import com.ricedotwho.rsm.module.api.Category;
import com.ricedotwho.rsm.module.api.ModuleInfo;
import com.ricedotwho.rsm.ui.clickgui.settings.Setting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.BooleanSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.ColourSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.KeybindSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.NumberSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.SaveSetting;
import com.ricedotwho.rsm.utils.FileUtils;
import com.ricedotwho.rsm.utils.ItemUtils;
import com.ricedotwho.rsm.utils.Utils;
import com.ricedotwho.rsm.utils.render.render3d.type.FilledBox;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.class_124;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2653;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_642;
import net.minecraft.class_239.class_240;

@ModuleInfo(aliases = "Breaker Aura", id = "BreakerAura", category = Category.DUNGEONS, hasKeybind = true)
public class BreakerAura extends Module {
   private final BooleanSetting edit = new BooleanSetting("Edit Mode", false);
   private final KeybindSetting addBlockBind = new KeybindSetting("Add Block Bind", new Keybind(59, true, this::addOrRemoveBlock));
   private final BooleanSetting swap = new BooleanSetting("Auto Swap", true);
   private final BooleanSetting renderBlocks = new BooleanSetting("Render Blocks", true);
   private final ColourSetting colour = new ColourSetting("Colour", Colour.YELLOW.copy());
   private final BooleanSetting zeroTick = new BooleanSetting("Zero Tick", false);
   private final NumberSetting timeout = new NumberSetting("Timeout", 0.0, 1000.0, 500.0, 10.0);
   private final SaveSetting<Set<Pos>> data = new SaveSetting(
      "Aura Blocks", "dungeon/breaker", "breaker_aura.json", HashSet::new, (new TypeToken<Set<Pos>>() {}).getType(), FileUtils.getPgson(), true, null, null
   );
   private int charges = 20;
   private final Map<Pos, Long> nextMineAttempt = new HashMap<>();

   public BreakerAura() {
      this.registerProperty(new Setting[]{this.edit, this.addBlockBind, this.swap, this.renderBlocks, this.colour, this.zeroTick, this.timeout, this.data});
   }

   public boolean inP3Sim(boolean isP3Sim) {
      class_642 server = class_310.method_1551().method_1558();
      return server != null && server.field_3761.equals("hypixelp3sim.zapto.org") ? true : isP3Sim;
   }

   @SubscribeEvent
   public void onTick(Start event) {
      if (Location.getArea().is(Island.Dungeon)
         && Dungeon.isInBoss()
         && Utils.equalsOneOf(Location.getFloor(), new Object[]{Floor.M7, Floor.F7})
         && !((Set)this.data.getValue()).isEmpty()
         && !(Boolean)this.edit.getValue()
         && mc.field_1687 != null
         && mc.field_1724 != null
         && this.charges > 0) {
         long now = System.currentTimeMillis();
         if ((Boolean)this.zeroTick.getValue()) {
            List<Pos> f = ((Set)this.data.getValue())
               .stream()
               .filter(
                  p -> {
                     class_2338 bp = p.asBlockPos();
                     class_2680 state = mc.field_1687.method_8320(bp);
                     class_265 shape = state.method_26218(mc.field_1687, bp);
                     return !shape.method_1110()
                        && DungeonBreaker.canInstantMine(state)
                        && this.canRetryMine(p, now)
                        && InteractUtils.faceDistance(
                              p.asVec3(), mc.field_1724.method_73189().method_1031(0.0, mc.field_1724.method_18381(mc.field_1724.method_18376()), 0.0)
                           )
                           <= 25.0;
                  }
               )
               .toList();
            if (f.isEmpty()
               || (Boolean)this.swap.getValue() && !SwapManager.reserveSwap("DUNGEONBREAKER")
               || !"DUNGEONBREAKER".equals(ItemUtils.getID(mc.field_1724.method_31548().method_7391()))) {
               return;
            }

            for (Pos pos : f) {
               this.markMineAttempt(pos, now);
               InteractUtils.breakBlock(pos, false, SwapManager.isDesynced());
               if (--this.charges <= 0) {
                  return;
               }
            }
         } else {
            Optional<Pos> closest = this.getClosest((Set<Pos>)this.data.getValue());
            closest.ifPresent(
               posx -> {
                  if ((Boolean)this.swap.getValue() && SwapManager.reserveSwap("DUNGEONBREAKER")
                     || "DUNGEONBREAKER".equals(ItemUtils.getID(mc.field_1724.method_31548().method_7391()))) {
                     this.markMineAttempt(posx, now);
                     InteractUtils.breakBlock(posx, false, SwapManager.isDesynced());
                     this.charges--;
                  }
               }
            );
         }
      }
   }

   @SubscribeEvent
   public void onRender3D(Extract event) {
      if (Location.getArea().is(Island.Dungeon)
         && (Boolean)this.renderBlocks.getValue()
         && Dungeon.isInBoss()
         && Utils.equalsOneOf(Location.getFloor(), new Object[]{Floor.M7, Floor.F7})
         && !((Set)this.data.getValue()).isEmpty()
         && mc.field_1687 != null
         && mc.field_1724 != null) {
         for (Pos pos : (Set)this.data.getValue()) {
            class_2338 bp = pos.asBlockPos();
            class_2680 state = mc.field_1687.method_8320(bp);
            class_265 shape = state.method_26218(mc.field_1687, bp);
            if (!shape.method_1110()) {
               class_238 aabb = shape.method_1107().method_996(bp);
               Renderer3D.addTask(new FilledBox(aabb, this.colour.getValue(), true));
            }
         }
      }
   }

   @SubscribeEvent
   public void onReset(Load event) {
      this.charges = 20;
      this.nextMineAttempt.clear();
   }

   @SubscribeEvent
   public void onItemUpdate(PostReceive event) {
      if (event.getPacket() instanceof class_2653 packet
         && Location.getArea().is(Island.Dungeon)
         && "DUNGEONBREAKER".equals(ItemUtils.getID(packet.method_11449()))) {
         this.charges = (Integer)ItemUtils.getDbCharges(packet.method_11449()).getFirst();
      }
   }

   private Optional<Pos> getClosest(Set<Pos> positions) {
      Pos closest = null;
      double dist = 2.147483647E9;
      long now = System.currentTimeMillis();

      assert mc.field_1687 != null;

      assert mc.field_1724 != null;

      for (Pos pos : positions) {
         class_2338 bp = pos.asBlockPos();
         class_2680 state = mc.field_1687.method_8320(bp);
         class_265 shape = state.method_26218(mc.field_1687, bp);
         class_243 vec3 = pos.asVec3();
         if (!shape.method_1110()
            && DungeonBreaker.canInstantMine(state)
            && this.canRetryMine(pos, now)
            && !(
               InteractUtils.faceDistance(vec3, mc.field_1724.method_73189().method_1031(0.0, mc.field_1724.method_18381(mc.field_1724.method_18376()), 0.0))
                  > 25.0
            )) {
            double d = vec3.method_1022(mc.field_1724.method_73189().method_1031(0.0, mc.field_1724.method_18381(mc.field_1724.method_18376()), 0.0));
            if (d < dist) {
               closest = pos;
               dist = d;
            }
         }
      }

      return Optional.ofNullable(closest);
   }

   private boolean canRetryMine(Pos pos, long now) {
      return this.nextMineAttempt.getOrDefault(pos, 0L) <= now;
   }

   private void markMineAttempt(Pos pos, long now) {
      this.nextMineAttempt.put(pos, now + ((BigDecimal)this.timeout.getValue()).longValue());
   }

   public void addOrRemoveBlock() {
      if (Location.getArea().is(Island.Dungeon) && Dungeon.isInBoss() && mc.field_1724 != null) {
         if (class_310.method_1551().field_1765 instanceof class_3965 blockHitResult && blockHitResult.method_17783() != class_240.field_1333) {
            Pos pos = new Pos(blockHitResult.method_17777());
            if (((Set)this.data.getValue()).contains(pos)) {
               ((Set)this.data.getValue()).remove(pos);
               this.nextMineAttempt.remove(pos);
               RSA.chat(class_124.field_1061 + "Removed " + pos.toChatString());
            } else {
               ((Set)this.data.getValue()).add(pos);
               RSA.chat(class_124.field_1060 + "Added " + pos.toChatString());
            }

            this.data.save();
         } else {
            RSA.chat(class_124.field_1061 + "Not looking at a block");
         }
      }
   }

   public BooleanSetting getEdit() {
      return this.edit;
   }

   public KeybindSetting getAddBlockBind() {
      return this.addBlockBind;
   }

   public BooleanSetting getSwap() {
      return this.swap;
   }

   public BooleanSetting getRenderBlocks() {
      return this.renderBlocks;
   }

   public ColourSetting getColour() {
      return this.colour;
   }

   public BooleanSetting getZeroTick() {
      return this.zeroTick;
   }

   public NumberSetting getTimeout() {
      return this.timeout;
   }

   public SaveSetting<Set<Pos>> getData() {
      return this.data;
   }

   public int getCharges() {
      return this.charges;
   }
}
