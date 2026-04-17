package com.ricedotwho.rsa.module.impl.render;

import com.ricedotwho.rsa.RSA;
import com.ricedotwho.rsm.RSM;
import com.ricedotwho.rsm.component.impl.camera.CameraHandler;
import com.ricedotwho.rsm.component.impl.camera.CameraPositionProvider;
import com.ricedotwho.rsm.component.impl.camera.ClientRotationHandler;
import com.ricedotwho.rsm.component.impl.camera.ClientRotationProvider;
import com.ricedotwho.rsm.data.Pos;
import com.ricedotwho.rsm.event.api.SubscribeEvent;
import com.ricedotwho.rsm.event.impl.render.Render3DEvent.Start;
import com.ricedotwho.rsm.event.impl.world.WorldEvent.Load;
import com.ricedotwho.rsm.module.Module;
import com.ricedotwho.rsm.module.api.Category;
import com.ricedotwho.rsm.module.api.ModuleInfo;
import com.ricedotwho.rsm.ui.clickgui.settings.Setting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.NumberSetting;
import com.ricedotwho.rsm.utils.RotationUtils;
import java.math.BigDecimal;
import net.minecraft.class_124;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_315;

@ModuleInfo(aliases = "Freecam", id = "Freecam", category = Category.RENDER, hasKeybind = true)
public class Freecam extends Module implements ClientRotationProvider, CameraPositionProvider {
   private static final String ENABLE_MSG = "Freecam " + class_124.field_1060 + "enabled!";
   private static final String DISABLE_MSG = "Freecam " + class_124.field_1061 + "disabled!";
   private final NumberSetting horizontalSpeed = new NumberSetting("Horizontal Speed", 0.0, 1.0, 0.35, 0.05);
   private final NumberSetting verticalSpeed = new NumberSetting("Vertical Speed", 0.0, 0.5, 0.25, 0.025);
   private static Freecam INSTANCE;
   private Pos freecamPos = new Pos();

   public Freecam() {
      this.registerProperty(new Setting[]{this.horizontalSpeed, this.verticalSpeed});
   }

   public void onEnable() {
      RSA.chat(ENABLE_MSG);
      if (INSTANCE == null) {
         INSTANCE = (Freecam)RSM.getModule(Freecam.class);
      }

      this.freecamPos = new Pos(class_310.method_1551().field_1773.method_19418().method_19326());
      CameraHandler.registerProvider(this);
      ClientRotationHandler.registerProvider(this);
   }

   public void onDisable() {
      RSA.chat(DISABLE_MSG);
   }

   @SubscribeEvent
   public void onWorldLoad(Load event) {
      this.setEnabled(false);
   }

   @SubscribeEvent
   public void onRenderWorld(Start event) {
      if (class_310.method_1551().method_1560() != null) {
         class_315 options = class_310.method_1551().field_1690;
         boolean up = options.field_1894.method_1434();
         boolean down = options.field_1881.method_1434();
         boolean left = options.field_1913.method_1434();
         boolean right = options.field_1849.method_1434();
         float x = RotationUtils.calculateImpulse(up, down);
         float y = RotationUtils.calculateImpulse(left, right);
         class_241 hori = class_241.field_1340;
         if (x != 0.0F || y != 0.0F) {
            hori = RotationUtils.rotateVector(y, x, -ClientRotationHandler.getClientYaw())
               .method_35581()
               .method_35582(((BigDecimal)this.horizontalSpeed.getValue()).floatValue());
         }

         float vertical = RotationUtils.calculateImpulse(options.field_1903.method_1434(), options.field_1832.method_1434())
            * ((BigDecimal)this.verticalSpeed.getValue()).floatValue();
         this.freecamPos.selfAdd(hori.field_1343, vertical, hori.field_1342);
      }
   }

   public static boolean isDetached() {
      return INSTANCE != null && INSTANCE.isEnabled();
   }

   public boolean shouldOverridePosition() {
      return this.isEnabled();
   }

   public boolean shouldOverrideHitPos() {
      return false;
   }

   public boolean shouldOverrideHitRot() {
      return false;
   }

   public boolean shouldBlockKeyboardMovement() {
      return true;
   }

   public class_243 getCameraPosition() {
      return this.freecamPos.asVec3();
   }

   public class_243 getPosForHit() {
      return null;
   }

   public class_243 getRotForHit() {
      return null;
   }

   public boolean isClientRotationActive() {
      return this.isEnabled();
   }

   public boolean allowClientKeyInputs() {
      return false;
   }

   public NumberSetting getHorizontalSpeed() {
      return this.horizontalSpeed;
   }

   public NumberSetting getVerticalSpeed() {
      return this.verticalSpeed;
   }

   public Pos getFreecamPos() {
      return this.freecamPos;
   }
}
