package com.ricedotwho.rsa.module.impl.render;

import com.ricedotwho.rsm.RSM;
import com.ricedotwho.rsm.data.Colour;
import com.ricedotwho.rsm.event.api.SubscribeEvent;
import com.ricedotwho.rsm.event.impl.game.ClientTickEvent;
import com.ricedotwho.rsm.event.impl.render.Render2DEvent;
import com.ricedotwho.rsm.module.Module;
import com.ricedotwho.rsm.module.api.Category;
import com.ricedotwho.rsm.module.api.ModuleInfo;
import com.ricedotwho.rsm.ui.clickgui.settings.Setting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.BooleanSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.DragSetting;
import com.ricedotwho.rsm.utils.render.render2d.NVGUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.class_1294;
import net.minecraft.class_2398;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_3937.class_3938;
import net.minecraft.class_3937.class_3995;
import net.minecraft.class_666.class_667;
import net.minecraft.class_687.class_688;
import net.minecraft.class_689.class_690;
import net.minecraft.class_691.class_692;
import net.minecraft.class_696.class_697;
import net.minecraft.class_709.class_710;
import net.minecraft.class_711.class_716;
import net.minecraft.class_717.class_718;
import net.minecraft.class_8899.class_8900;
import org.joml.Vector2d;

@ModuleInfo(aliases = "Effects", id = "EffectsAndRender", category = Category.RENDER)
@Environment(EnvType.CLIENT)
public class EffectsAndRender extends Module {
   private final BooleanSetting Explosions = new BooleanSetting("Explosions", false, () -> true);
   private final BooleanSetting Fires = new BooleanSetting("Fires", false, () -> true);
   private final BooleanSetting EtherWarp = new BooleanSetting("EtherWarp", false, () -> true);
   private final BooleanSetting SMOKE = new BooleanSetting("SMOKE", false, () -> true);
   private final BooleanSetting Nausea = new BooleanSetting("Nausea", false, () -> true);
   private final BooleanSetting Blindness = new BooleanSetting("Blindness", false, () -> true);
   private final BooleanSetting Slowness = new BooleanSetting("Slowness", false, () -> true);
   private final BooleanSetting Haste = new BooleanSetting("Haste", false, () -> true);
   private final BooleanSetting Darkness = new BooleanSetting("Darkness", false, () -> true);
   private final BooleanSetting Mining_Fatigue = new BooleanSetting("Mining Fatigue", false, () -> true);
   private final BooleanSetting Speedness = new BooleanSetting("Speedness", false, () -> true);
   private final BooleanSetting FpsToggled = new BooleanSetting("Fps display", false, () -> true);
   private final DragSetting Fps = new DragSetting("Fps display", new Vector2d(50.0, 50.0), new Vector2d(50.0, 50.0));

   public EffectsAndRender() {
      this.registerProperty(
         new Setting[]{
            this.Explosions,
            this.Fires,
            this.EtherWarp,
            this.SMOKE,
            this.FpsToggled,
            this.Fps,
            this.Nausea,
            this.Blindness,
            this.Slowness,
            this.Haste,
            this.Speedness,
            this.Darkness,
            this.Mining_Fatigue
         }
      );
   }

   @SubscribeEvent
   public void onClientTick(ClientTickEvent event) {
      class_310 mc = class_310.method_1551();
      if (mc.field_1724 != null) {
         if ((Boolean)this.Nausea.getValue()) {
            mc.field_1724.method_6016(class_1294.field_5916);
         }

         if ((Boolean)this.Blindness.getValue()) {
            mc.field_1724.method_6016(class_1294.field_5919);
         }

         if ((Boolean)this.Slowness.getValue()) {
            mc.field_1724.method_6016(class_1294.field_5909);
         }

         if ((Boolean)this.Haste.getValue()) {
            mc.field_1724.method_6016(class_1294.field_5917);
         }

         if ((Boolean)this.Speedness.getValue()) {
            mc.field_1724.method_6016(class_1294.field_5904);
         }

         if ((Boolean)this.Darkness.getValue()) {
            mc.field_1724.method_6016(class_1294.field_38092);
         }

         if ((Boolean)this.Mining_Fatigue.getValue()) {
            mc.field_1724.method_6016(class_1294.field_5901);
         }
      }
   }

   public static void init() {
      class_310 mc = class_310.method_1551();
      class_638 level = mc.field_1687;
      ParticleFactoryRegistry.getInstance()
         .register(
            class_2398.field_11236,
            spriteSet -> {
               class_692 originalFactory = new class_692(spriteSet);
               return (simpleParticleType, clientLevel, d, e, f, g, h, i, randomSource) -> ((EffectsAndRender)RSM.getModule(EffectsAndRender.class))
                     .Explosions
                     .getValue()
                  ? null
                  : originalFactory.method_3038(simpleParticleType, clientLevel, d, e, f, g, h, i, randomSource);
            }
         );
      ParticleFactoryRegistry.getInstance()
         .register(
            class_2398.field_11221,
            spriteSet -> {
               class_690 originalFactory = new class_690();
               return (simpleParticleType, clientLevel, d, e, f, g, h, i, randomSource) -> ((EffectsAndRender)RSM.getModule(EffectsAndRender.class))
                     .Explosions
                     .getValue()
                  ? null
                  : originalFactory.method_3037(simpleParticleType, clientLevel, d, e, f, g, h, i, randomSource);
            }
         );
      ParticleFactoryRegistry.getInstance()
         .register(
            class_2398.field_11216,
            spriteSet -> {
               class_667 originalFactory = new class_667(spriteSet);
               return (simpleParticleType, clientLevel, d, e, f, g, h, i, randomSource) -> ((EffectsAndRender)RSM.getModule(EffectsAndRender.class))
                     .Fires
                     .getValue()
                  ? null
                  : originalFactory.method_3019(simpleParticleType, clientLevel, d, e, f, g, h, i, randomSource);
            }
         );
      ParticleFactoryRegistry.getInstance()
         .register(
            class_2398.field_11240,
            spriteSet -> {
               class_688 originalFactory = new class_688(spriteSet);
               return (simpleParticleType, clientLevel, d, e, f, g, h, i, randomSource) -> ((EffectsAndRender)RSM.getModule(EffectsAndRender.class))
                     .Fires
                     .getValue()
                  ? null
                  : originalFactory.method_3036(simpleParticleType, clientLevel, d, e, f, g, h, i, randomSource);
            }
         );
      ParticleFactoryRegistry.getInstance()
         .register(
            class_2398.field_11214,
            spriteSet -> {
               class_710 originalFactory = new class_710(spriteSet);
               return (simpleParticleType, clientLevel, d, e, f, g, h, i, randomSource) -> ((EffectsAndRender)RSM.getModule(EffectsAndRender.class))
                     .EtherWarp
                     .getValue()
                  ? null
                  : originalFactory.method_3094(simpleParticleType, clientLevel, d, e, f, g, h, i, randomSource);
            }
         );
      ParticleFactoryRegistry.getInstance()
         .register(
            class_2398.field_11249,
            spriteSet -> {
               class_716 originalFactory = new class_716(spriteSet);
               return (simpleParticleType, clientLevel, d, e, f, g, h, i, randomSource) -> ((EffectsAndRender)RSM.getModule(EffectsAndRender.class))
                     .EtherWarp
                     .getValue()
                  ? null
                  : originalFactory.method_3100(simpleParticleType, clientLevel, d, e, f, g, h, i, randomSource);
            }
         );
      ParticleFactoryRegistry.getInstance()
         .register(
            class_2398.field_11237,
            spriteSet -> {
               class_697 originalFactory = new class_697(spriteSet);
               return (simpleParticleType, clientLevel, d, e, f, g, h, i, randomSource) -> ((EffectsAndRender)RSM.getModule(EffectsAndRender.class))
                     .SMOKE
                     .getValue()
                  ? null
                  : originalFactory.method_3040(simpleParticleType, clientLevel, d, e, f, g, h, i, randomSource);
            }
         );
      ParticleFactoryRegistry.getInstance()
         .register(
            class_2398.field_11251,
            spriteSet -> {
               class_718 originalFactory = new class_718(spriteSet);
               return (simpleParticleType, clientLevel, d, e, f, g, h, i, randomSource) -> ((EffectsAndRender)RSM.getModule(EffectsAndRender.class))
                     .SMOKE
                     .getValue()
                  ? null
                  : originalFactory.method_3101(simpleParticleType, clientLevel, d, e, f, g, h, i, randomSource);
            }
         );
      ParticleFactoryRegistry.getInstance()
         .register(
            class_2398.field_17430,
            spriteSet -> {
               class_3938 originalFactory = new class_3938(spriteSet);
               return (simpleParticleType, clientLevel, d, e, f, g, h, i, randomSource) -> ((EffectsAndRender)RSM.getModule(EffectsAndRender.class))
                     .SMOKE
                     .getValue()
                  ? null
                  : originalFactory.method_17579(simpleParticleType, clientLevel, d, e, f, g, h, i, randomSource);
            }
         );
      ParticleFactoryRegistry.getInstance()
         .register(
            class_2398.field_17431,
            spriteSet -> {
               class_3995 originalFactory = new class_3995(spriteSet);
               return (simpleParticleType, clientLevel, d, e, f, g, h, i, randomSource) -> ((EffectsAndRender)RSM.getModule(EffectsAndRender.class))
                     .SMOKE
                     .getValue()
                  ? null
                  : originalFactory.method_18820(simpleParticleType, clientLevel, d, e, f, g, h, i, randomSource);
            }
         );
      ParticleFactoryRegistry.getInstance()
         .register(
            class_2398.field_46911,
            spriteSet -> {
               class_8900 originalFactory = new class_8900(spriteSet);
               return (simpleParticleType, clientLevel, d, e, f, g, h, i, randomSource) -> ((EffectsAndRender)RSM.getModule(EffectsAndRender.class))
                     .SMOKE
                     .getValue()
                  ? null
                  : originalFactory.method_54635(simpleParticleType, clientLevel, d, e, f, g, h, i, randomSource);
            }
         );
   }

   public void onEnable() {
   }

   public void onDisable() {
   }

   public void reset() {
   }

   @SubscribeEvent
   public void onRender2D(Render2DEvent event) {
      class_746 player = class_310.method_1551().field_1724;
      class_638 level = class_310.method_1551().field_1687;
      if (player != null && level != null) {
         int fps = class_310.method_1551().method_47599();
         String fpsString = "Fps: " + fps;
         if ((Boolean)this.FpsToggled.getValue()) {
            this.Fps.renderScaled(event.getGfx(), () -> NVGUtils.drawText(fpsString, 0.0F, 0.0F, 50.0F, Colour.blue, NVGUtils.NUNITO), 60.0F, 30.0F);
         }
      }
   }

   public BooleanSetting getExplosions() {
      return this.Explosions;
   }

   public BooleanSetting getFires() {
      return this.Fires;
   }

   public BooleanSetting getEtherWarp() {
      return this.EtherWarp;
   }

   public BooleanSetting getSMOKE() {
      return this.SMOKE;
   }

   public BooleanSetting getNausea() {
      return this.Nausea;
   }

   public BooleanSetting getBlindness() {
      return this.Blindness;
   }

   public BooleanSetting getSlowness() {
      return this.Slowness;
   }

   public BooleanSetting getHaste() {
      return this.Haste;
   }

   public BooleanSetting getDarkness() {
      return this.Darkness;
   }

   public BooleanSetting getMining_Fatigue() {
      return this.Mining_Fatigue;
   }

   public BooleanSetting getSpeedness() {
      return this.Speedness;
   }

   public BooleanSetting getFpsToggled() {
      return this.FpsToggled;
   }

   public DragSetting getFps() {
      return this.Fps;
   }
}
