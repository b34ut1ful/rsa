package com.ricedotwho.rsa.module.impl.render;

import com.ricedotwho.rsm.component.impl.Renderer3D;
import com.ricedotwho.rsm.component.impl.location.Island;
import com.ricedotwho.rsm.component.impl.location.Location;
import com.ricedotwho.rsm.component.impl.map.handler.Dungeon;
import com.ricedotwho.rsm.data.Colour;
import com.ricedotwho.rsm.event.api.SubscribeEvent;
import com.ricedotwho.rsm.event.impl.game.ClientTickEvent.Start;
import com.ricedotwho.rsm.event.impl.render.Render3DEvent.Extract;
import com.ricedotwho.rsm.module.Module;
import com.ricedotwho.rsm.module.api.Category;
import com.ricedotwho.rsm.module.api.ModuleInfo;
import com.ricedotwho.rsm.ui.clickgui.settings.Setting;
import com.ricedotwho.rsm.ui.clickgui.settings.group.DefaultGroupSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.BooleanSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.ColourSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.ModeSetting;
import com.ricedotwho.rsm.utils.render.render3d.type.FilledBox;
import com.ricedotwho.rsm.utils.render.render3d.type.FilledOutlineBox;
import com.ricedotwho.rsm.utils.render.render3d.type.OutlineBox;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1420;
import net.minecraft.class_1528;
import net.minecraft.class_1531;
import net.minecraft.class_1560;
import net.minecraft.class_1570;
import net.minecraft.class_1657;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3544;
import net.minecraft.class_638;
import net.minecraft.class_746;

@ModuleInfo(aliases = "Esp", id = "Esp", category = Category.RENDER)
public class Esp extends Module {
   private final ModeSetting renderMode = new ModeSetting("Mode", "Filled Outline", List.of("Filled Outline", "Filled", "Outline"));
   private final BooleanSetting showStarredMobs = new BooleanSetting("Starred Mobs", true);
   private final BooleanSetting onlyShowInCurrentRoom = new BooleanSetting("Current Room Only", true);
   private final BooleanSetting drawBloodMobs = new BooleanSetting("Blood Mobs", false);
   private final BooleanSetting withers = new BooleanSetting("Withers", true);
   private final BooleanSetting bats = new BooleanSetting("Bats", false);
   private final BooleanSetting depth = new BooleanSetting("Depth", false);
   private final DefaultGroupSetting colours = new DefaultGroupSetting("Colours", this);
   private final ColourSetting starredFill = new ColourSetting("Star Fill", new Colour(444137617));
   private final ColourSetting starredOutline = new ColourSetting("Star Outline", new Colour(-2752257));
   private final ColourSetting bloodFill = new ColourSetting("Blood Fill", new Colour(443678720));
   private final ColourSetting bloodOutline = new ColourSetting("Blood Outline", new Colour(-65536));
   private final ColourSetting witherFill = new ColourSetting("Wither Fill", new Colour(436221576));
   private final ColourSetting witherOutline = new ColourSetting("Wither Outline", new Colour(-16750849));
   private final ColourSetting batFill = new ColourSetting("Bat Fill", new Colour(173, 92, 173, 90));
   private final ColourSetting batOutline = new ColourSetting("Bat Outline", new Colour(173, 92, 173));
   private final Set<Integer> starredMobs = new HashSet<>();
   private final Set<Integer> bloodMobs = new HashSet<>();
   private final Set<Integer> batMobs = new HashSet<>();
   private final Set<Integer> bloodNames = new HashSet<>();
   private int wither = -1;
   private double witherDistance = Double.MAX_VALUE;
   public float updateInterval = 10.0F;

   public Esp() {
      this.addName("Revoker");
      this.addName("Psycho");
      this.addName("Reaper");
      this.addName("Cannibal");
      this.addName("Mute");
      this.addName("Ooze");
      this.addName("Putrid");
      this.addName("Freak");
      this.addName("Leech");
      this.addName("Tear");
      this.addName("Parasite");
      this.addName("Flamer");
      this.addName("Skull");
      this.addName("Mr. Dead");
      this.addName("Vader");
      this.addName("Frost");
      this.addName("Walker");
      this.addName("Wandering Soul");
      this.addName("Bonzo");
      this.addName("Scarf");
      this.addName("Livid");
      this.addName("Spirit Bear");
      this.registerProperty(
         new Setting[]{this.renderMode, this.showStarredMobs, this.onlyShowInCurrentRoom, this.drawBloodMobs, this.bats, this.withers, this.colours}
      );
      this.colours
         .add(
            new Setting[]{
               this.starredFill, this.starredOutline, this.bloodFill, this.bloodOutline, this.witherFill, this.witherOutline, this.batFill, this.batOutline
            }
         );
   }

   private void addName(String name) {
      this.bloodNames.add(name.hashCode());
   }

   public void onEnable() {
      this.reset();
   }

   public void reset() {
      this.starredMobs.clear();
      this.bloodMobs.clear();
      this.batMobs.clear();
      this.wither = -1;
      this.witherDistance = Double.MAX_VALUE;
   }

   @SubscribeEvent
   public void onRender3dEvent(Extract event) {
      if (mc.field_1724 != null && mc.field_1687 != null && Location.getArea() == Island.Dungeon) {
         float partialTicks = event.getContext().tickCounter().method_60637(false);
         if ((Boolean)this.showStarredMobs.getValue() && !this.starredMobs.isEmpty()) {
            this.handleRender(this.starredMobs, this.getStarredOutline().getValue(), this.getStarredFill().getValue(), partialTicks);
         }

         if ((Boolean)this.drawBloodMobs.getValue() && !this.bloodMobs.isEmpty()) {
            this.handleRender(this.bloodMobs, this.getBloodOutline().getValue(), this.getBloodFill().getValue(), partialTicks);
         }

         if ((Boolean)this.bats.getValue() && !this.batMobs.isEmpty()) {
            this.handleRender(this.batMobs, this.getBatOutline().getValue(), this.getBatFill().getValue(), partialTicks);
         }

         if ((Boolean)this.withers.getValue() && this.wither != -1) {
            class_1297 entity = mc.field_1687.method_8469(this.wither);
            if (entity != null) {
               this.renderEntityBox(entity, this.getWitherOutline().getValue(), this.getWitherFill().getValue(), partialTicks);
            } else {
               this.wither = -1;
            }
         }
      }
   }

   @SubscribeEvent
   public void onTick(Start event) {
      if (mc.field_1687 != null && mc.field_1724 != null && Location.getArea().is(Island.Dungeon) && (float)event.getTime() % this.updateInterval == 0.0F) {
         this.updateTrackedEntities(mc.field_1687);
      }
   }

   private void updateTrackedEntities(class_638 level) {
      this.starredMobs.clear();
      this.bloodMobs.clear();
      this.wither = -1;
      this.witherDistance = Double.MAX_VALUE;

      for (class_1297 entity : level.method_18112()) {
         if ((Boolean)this.showStarredMobs.getValue() && entity instanceof class_1531 stand) {
            if (this.isValidStarredEntity(stand)) {
               class_1297 mob = this.getMobEntity(stand, level);
               if (mob != null) {
                  this.starredMobs.add(mob.method_5628());
                  stand.method_5880(true);
                  mob.method_5648(false);
               }
            }
         } else if ((Boolean)this.showStarredMobs.getValue() && entity instanceof class_1657 && !(entity instanceof class_746)) {
            String name = entity.method_5477().getString().trim();
            if (name.hashCode() == -662331259) {
               this.starredMobs.add(entity.method_5628());
               entity.method_5648(false);
            }
         } else if ((Boolean)this.showStarredMobs.getValue() && entity instanceof class_1560) {
            if (entity.method_5477().getString().hashCode() == -1005553066) {
               entity.method_5648(false);
            }
         } else if ((Boolean)this.drawBloodMobs.getValue() && entity instanceof class_1657 && !(entity instanceof class_746)) {
            String name = entity.method_5477().getString().trim();
            if (this.bloodNames.contains(name.hashCode())) {
               this.bloodMobs.add(entity.method_5628());
               entity.method_5648(false);
            }
         } else if ((Boolean)this.drawBloodMobs.getValue() && entity instanceof class_1570 && !Dungeon.isInBoss()) {
            this.bloodMobs.add(entity.method_5628());
            entity.method_5648(false);
         } else if ((Boolean)this.bats.getValue() && entity instanceof class_1420 && !entity.method_5767()) {
            this.batMobs.add(entity.method_5628());
         } else if ((Boolean)this.withers.getValue() && entity instanceof class_1528 e && !entity.method_5767()) {
            class_746 Player = class_310.method_1551().field_1724;
            if (e.method_6063() != 300.0F) {
               if (this.wither == -1) {
                  this.wither = entity.method_5628();
               } else {
                  double dist = entity.method_5858(Player);
                  if (dist < this.witherDistance) {
                     this.witherDistance = dist;
                     this.wither = entity.method_5628();
                  }
               }
            }
         }
      }
   }

   private void handleRender(Set<Integer> entityIds, Colour outlineColor, Colour fillColor, float partialTicks) {
      class_638 level = class_310.method_1551().field_1687;
      if (level != null) {
         List<Integer> toRemove = new ArrayList<>();

         for (int entityId : entityIds) {
            class_1297 entity = level.method_8469(entityId);
            if (entity == null || entity instanceof class_1309 living && living.method_29504()) {
               toRemove.add(entityId);
            } else {
               this.renderEntityBox(entity, outlineColor, fillColor, partialTicks);
            }
         }

         toRemove.forEach(entityIds::remove);
      }
   }

   private void renderEntityBox(class_1297 entity, Colour outline, Colour fill, float partialTicks) {
      class_243 interpolatedPos = entity.method_30950(partialTicks);
      float width = entity.method_17681();
      float height = entity.method_17682();
      class_238 aabb = new class_238(
         interpolatedPos.field_1352 - width / 2.0F,
         interpolatedPos.field_1351,
         interpolatedPos.field_1350 - width / 2.0F,
         interpolatedPos.field_1352 + width / 2.0F,
         interpolatedPos.field_1351 + height,
         interpolatedPos.field_1350 + width / 2.0F
      );
      switch (this.renderMode.getIndex()) {
         case 0:
            Renderer3D.addTask(new FilledOutlineBox(aabb, fill, outline, (Boolean)this.getDepth().getValue()));
            break;
         case 1:
            Renderer3D.addTask(new FilledBox(aabb, fill, (Boolean)this.getDepth().getValue()));
            break;
         default:
            Renderer3D.addTask(new OutlineBox(aabb, outline, (Boolean)this.getDepth().getValue()));
      }
   }

   private boolean isValidStarredEntity(class_1531 entity) {
      if (!entity.method_16914()) {
         return false;
      } else {
         String name = class_3544.method_15440(Objects.requireNonNull(entity.method_5797()).getString());
         return name.contains("✯ ") && name.endsWith("❤");
      }
   }

   private class_1297 getMobEntity(class_1531 stand, class_638 level) {
      class_238 searchBox = stand.method_5829().method_989(0.0, -1.0, 0.0);
      return level.method_8335(stand, searchBox)
         .stream()
         .filter(e -> e instanceof class_1309 && !(e instanceof class_1531) && !(e instanceof class_746) && (!(e instanceof class_1528) || !e.method_5767()))
         .min(Comparator.comparingDouble(e -> e.method_5858(stand)))
         .orElse(null);
   }

   public ModeSetting getRenderMode() {
      return this.renderMode;
   }

   public BooleanSetting getShowStarredMobs() {
      return this.showStarredMobs;
   }

   public BooleanSetting getOnlyShowInCurrentRoom() {
      return this.onlyShowInCurrentRoom;
   }

   public BooleanSetting getDrawBloodMobs() {
      return this.drawBloodMobs;
   }

   public BooleanSetting getWithers() {
      return this.withers;
   }

   public BooleanSetting getBats() {
      return this.bats;
   }

   public BooleanSetting getDepth() {
      return this.depth;
   }

   public DefaultGroupSetting getColours() {
      return this.colours;
   }

   public ColourSetting getStarredFill() {
      return this.starredFill;
   }

   public ColourSetting getStarredOutline() {
      return this.starredOutline;
   }

   public ColourSetting getBloodFill() {
      return this.bloodFill;
   }

   public ColourSetting getBloodOutline() {
      return this.bloodOutline;
   }

   public ColourSetting getWitherFill() {
      return this.witherFill;
   }

   public ColourSetting getWitherOutline() {
      return this.witherOutline;
   }

   public ColourSetting getBatFill() {
      return this.batFill;
   }

   public ColourSetting getBatOutline() {
      return this.batOutline;
   }

   public Set<Integer> getStarredMobs() {
      return this.starredMobs;
   }

   public Set<Integer> getBloodMobs() {
      return this.bloodMobs;
   }

   public Set<Integer> getBatMobs() {
      return this.batMobs;
   }

   public Set<Integer> getBloodNames() {
      return this.bloodNames;
   }

   public int getWither() {
      return this.wither;
   }

   public double getWitherDistance() {
      return this.witherDistance;
   }

   public float getUpdateInterval() {
      return this.updateInterval;
   }
}
