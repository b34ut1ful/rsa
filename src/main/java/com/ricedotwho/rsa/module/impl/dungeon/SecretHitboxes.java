package com.ricedotwho.rsa.module.impl.dungeon;

import com.ricedotwho.rsm.RSM;
import com.ricedotwho.rsm.component.impl.location.Island;
import com.ricedotwho.rsm.component.impl.location.Location;
import com.ricedotwho.rsm.module.Module;
import com.ricedotwho.rsm.module.api.Category;
import com.ricedotwho.rsm.module.api.ModuleInfo;
import com.ricedotwho.rsm.ui.clickgui.settings.Setting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.BooleanSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.ModeSetting;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.class_2248;
import net.minecraft.class_2269;
import net.minecraft.class_2338;
import net.minecraft.class_2341;
import net.minecraft.class_2350;
import net.minecraft.class_2401;
import net.minecraft.class_2484;
import net.minecraft.class_259;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2738;

@ModuleInfo(aliases = "Hitboxes", id = "SecretHitboxes", category = Category.DUNGEONS)
public class SecretHitboxes extends Module {
   private final BooleanSetting essence = new BooleanSetting("Essence", false);
   private final ModeSetting buttons = new ModeSetting("Buttons", "Off", List.of("Full", "Flat", "Off"));
   private final BooleanSetting ssButtonsOnly = new BooleanSetting("SS Buttons Only", false);
   private final ModeSetting levers = new ModeSetting("Levers", "Off", List.of("Full", "Half", "1.8", "Off"));
   private final ModeSetting preDevLevers = new ModeSetting("Predev Levers", "Off", List.of("Full", "Half", "1.8", "Off"));
   private static final SecretHitboxes.ShapeData V47_LEVERS = new SecretHitboxes.ShapeData(class_2248.method_9541(4.0, 0.0, 4.0, 12.0, 10.0, 12.0));
   private static final SecretHitboxes.ShapeData HALF_LEVERS = new SecretHitboxes.ShapeData(class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 10.0, 16.0));
   private static final SecretHitboxes.ShapeData BUTTON;
   private static final SecretHitboxes.ShapeData BUTTON_POWERED;

   public SecretHitboxes() {
      this.registerProperty(new Setting[]{this.essence, this.buttons, this.ssButtonsOnly, this.levers, this.preDevLevers});
   }

   public static class_265 getShape(class_2680 state, class_2338 pos) {
      SecretHitboxes module = (SecretHitboxes)RSM.getModule(SecretHitboxes.class);
      if (Location.getArea().is(Island.Dungeon) && module != null && module.isEnabled() && mc.field_1687 != null) {
         class_2248 block = state.method_26204();
         Objects.requireNonNull(block);
         Objects.requireNonNull(block);

         return switch (block) {
            case class_2484 ignored when SecretAura.isValidSkull(pos, mc.field_1687) -> module.essence.getValue() ? class_259.method_1077() : null;
            case class_2401 ignoredx -> {
               switch (isLamps(pos) ? module.preDevLevers.getValue() : module.levers.getValue()) {
                  case "Full":
                     yield class_259.method_1077();
                  case "Half":
                     yield HALF_LEVERS.getShape((class_2738)state.method_11654(class_2341.field_11007), (class_2350)state.method_11654(class_2341.field_11177));
                  case "1.8":
                     yield V47_LEVERS.getShape((class_2738)state.method_11654(class_2341.field_11007), (class_2350)state.method_11654(class_2341.field_11177));
                  case null:
                  default:
                     yield null;
               }
            }
            case class_2269 ignoredxx -> {
               if (module.buttons.is("Off")) {
                  yield null;
               } else if ((Boolean)module.ssButtonsOnly.getValue() && !isSS(pos)) {
                  yield null;
               } else if (module.buttons.is("Full")) {
                  yield class_259.method_1077();
               } else {
                  SecretHitboxes.ShapeData data = state.method_11654(class_2269.field_10729) ? BUTTON_POWERED : BUTTON;
                  yield data.getShape((class_2738)state.method_11654(class_2341.field_11007), (class_2350)state.method_11654(class_2341.field_11177));
               }
            }
            default -> null;
         };
      } else {
         return null;
      }
   }

   private static boolean isSS(class_2338 pos) {
      return pos.method_10263() == 110 && pos.method_10264() >= 120 && pos.method_10264() <= 123 && pos.method_10260() >= 91 && pos.method_10260() <= 95;
   }

   private static boolean isLamps(class_2338 pos) {
      return pos.method_10263() >= 58 && pos.method_10263() <= 62 && pos.method_10264() >= 133 && pos.method_10264() <= 136 && pos.method_10260() == 142;
   }

   public BooleanSetting getEssence() {
      return this.essence;
   }

   public ModeSetting getButtons() {
      return this.buttons;
   }

   public BooleanSetting getSsButtonsOnly() {
      return this.ssButtonsOnly;
   }

   public ModeSetting getLevers() {
      return this.levers;
   }

   public ModeSetting getPreDevLevers() {
      return this.preDevLevers;
   }

   static {
      V47_LEVERS.add(class_2350.field_11033, class_2248.method_9541(4.0, 0.0, 4.0, 12.0, 10.0, 12.0));
      V47_LEVERS.add(class_2350.field_11043, class_2248.method_9541(5.0, 3.0, 10.0, 11.0, 13.0, 16.0));
      V47_LEVERS.add(class_2350.field_11035, class_2248.method_9541(5.0, 3.0, 0.0, 11.0, 13.0, 6.0));
      V47_LEVERS.add(class_2350.field_11034, class_2248.method_9541(0.0, 3.0, 5.0, 6.0, 13.0, 11.0));
      V47_LEVERS.add(class_2350.field_11039, class_2248.method_9541(10.0, 3.0, 5.0, 16.0, 13.0, 11.0));
      HALF_LEVERS.add(class_2350.field_11033, class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 10.0, 16.0));
      HALF_LEVERS.add(class_2350.field_11043, class_2248.method_9541(0.0, 0.0, 10.0, 16.0, 16.0, 16.0));
      HALF_LEVERS.add(class_2350.field_11035, class_2248.method_9541(0.0, 0.0, 0.0, 16.0, 16.0, 6.0));
      HALF_LEVERS.add(class_2350.field_11034, class_2248.method_9541(0.0, 0.0, 0.0, 6.0, 16.0, 16.0));
      HALF_LEVERS.add(class_2350.field_11039, class_2248.method_9541(10.0, 0.0, 0.0, 16.0, 16.0, 16.0));
      double pow = 0.0625;
      BUTTON_POWERED = new SecretHitboxes.ShapeData(
         class_259.method_1081(0.0, 1.0 - pow, 0.0, 1.0, 1.0, 1.0), class_259.method_1081(0.0, 0.0, 0.0, 1.0, 0.0 + pow, 1.0)
      );
      BUTTON_POWERED.add(class_2350.field_11034, class_259.method_1081(0.0, 0.0, 0.0, pow, 1.0, 1.0));
      BUTTON_POWERED.add(class_2350.field_11039, class_259.method_1081(1.0 - pow, 0.0, 0.0, 1.0, 1.0, 1.0));
      BUTTON_POWERED.add(class_2350.field_11035, class_259.method_1081(0.0, 0.0, 0.0, 1.0, 1.0, pow));
      BUTTON_POWERED.add(class_2350.field_11043, class_259.method_1081(0.0, 0.0, 1.0 - pow, 1.0, 1.0, 1.0));
      BUTTON_POWERED.add(class_2350.field_11036, class_259.method_1081(0.0, 0.0, 0.0, 1.0, 0.0 + pow, 1.0));
      BUTTON_POWERED.add(class_2350.field_11033, class_259.method_1081(0.0, 1.0 - pow, 0.0, 1.0, 1.0, 1.0));
      double unpow = 0.125;
      BUTTON = new SecretHitboxes.ShapeData(
         class_259.method_1081(0.0, 1.0 - unpow, 0.0, 1.0, 1.0, 1.0), class_259.method_1081(0.0, 0.0, 0.0, 1.0, 0.0 + unpow, 1.0)
      );
      BUTTON.add(class_2350.field_11034, class_259.method_1081(0.0, 0.0, 0.0, unpow, 1.0, 1.0));
      BUTTON.add(class_2350.field_11039, class_259.method_1081(1.0 - unpow, 0.0, 0.0, 1.0, 1.0, 1.0));
      BUTTON.add(class_2350.field_11035, class_259.method_1081(0.0, 0.0, 0.0, 1.0, 1.0, unpow));
      BUTTON.add(class_2350.field_11043, class_259.method_1081(0.0, 0.0, 1.0 - unpow, 1.0, 1.0, 1.0));
      BUTTON.add(class_2350.field_11036, class_259.method_1081(0.0, 0.0, 0.0, 1.0, 0.0 + unpow, 1.0));
      BUTTON.add(class_2350.field_11033, class_259.method_1081(0.0, 1.0 - unpow, 0.0, 1.0, 1.0, 1.0));
   }

   private static class ShapeData {
      private final class_265 ceil;
      private final class_265 floor;
      private final Map<class_2350, class_265> directions = new HashMap<>();

      public ShapeData(class_265 ceil, class_265 floor) {
         this.ceil = ceil;
         this.floor = floor;
      }

      public ShapeData(class_265 ceil) {
         this.ceil = ceil;
         this.floor = ceil;
      }

      public void add(class_2350 dir, class_265 shape) {
         this.directions.put(dir, shape);
      }

      public class_265 getShape(class_2738 face, class_2350 direction) {
         return switch (face) {
            case field_12475 -> this.floor;
            case field_12473 -> this.ceil;
            default -> (class_265)this.directions.getOrDefault(direction, null);
         };
      }
   }
}
