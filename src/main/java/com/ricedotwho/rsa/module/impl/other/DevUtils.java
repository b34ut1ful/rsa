package com.ricedotwho.rsa.module.impl.other;

import com.ricedotwho.rsa.RSA;
import com.ricedotwho.rsm.module.Module;
import com.ricedotwho.rsm.module.api.Category;
import com.ricedotwho.rsm.module.api.ModuleInfo;
import com.ricedotwho.rsm.ui.clickgui.settings.Setting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.ButtonSetting;
import com.ricedotwho.rsm.utils.ItemUtils;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_309;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_746;
import net.minecraft.class_239.class_240;

@ModuleInfo(aliases = "Dev Utils", id = "DevUtils", category = Category.OTHER)
public class DevUtils extends Module {
   private final ButtonSetting pos = new ButtonSetting("Your XYZ", "List Pos", () -> {
      class_746 player = class_310.method_1551().field_1724;
      class_310 mc = class_310.method_1551();
      class_309 keyboard = mc.field_1774;
      if (player != null) {
         double x = player.method_23317();
         double y = player.method_23318();
         double z = player.method_23321();
         String xyz = x + ", " + y + ", " + z;
         RSA.chat(xyz);
         keyboard.method_1455(xyz);
         RSA.chat("Copied to clipboard!");
      }
   });
   private final ButtonSetting yawPitch = new ButtonSetting("Yaw and Pitch", "Yaw/Pitch", () -> {
      class_746 player = class_310.method_1551().field_1724;
      class_310 mc = class_310.method_1551();
      class_309 keyboard = mc.field_1774;
      if (player != null) {
         float yaw = player.method_36454();
         float pitch = player.method_36455();
         String yp = yaw + ", " + pitch;
         RSA.chat(yp);
         keyboard.method_1455(yp);
      }
   });
   private final ButtonSetting blockinfo = new ButtonSetting("Block info that you're lookin at", "Block Info", () -> {
      class_746 player = class_310.method_1551().field_1724;
      class_310 mc = class_310.method_1551();
      class_309 keyboard = mc.field_1774;
      class_239 hitResult = class_310.method_1551().field_1765;
      if (player != null && hitResult.method_17783() == class_240.field_1332) {
         class_310 client = class_310.method_1551();
         class_3965 blockHit = (class_3965)client.field_1765;
         class_2338 pos = blockHit.method_17777();
         double x = pos.method_10263() + 0.5;
         int y = pos.method_10264();
         double z = pos.method_10260() + 0.5;
         String BlockInfo = x + ", " + y + ", " + z;
         RSA.chat("XYZ: " + BlockInfo);
      }
   });
   private final ButtonSetting entityinfo = new ButtonSetting(
      "Entity info that you're lookin at",
      "Entity Info",
      () -> {
         class_746 player = class_310.method_1551().field_1724;
         class_239 hitResult = class_310.method_1551().field_1765;
         if (player != null) {
            class_3966 entityHR = (class_3966)hitResult;
            String entityInfo = entityHR.method_17782().method_5477().getString();
            String entityId = String.valueOf(entityHR.method_17782().method_5628());
            String simplePos = entityHR.method_17782().method_24515().method_10263()
               + ", "
               + entityHR.method_17782().method_24515().method_10264()
               + ", "
               + entityHR.method_17782().method_24515().method_10260();
            RSA.chat("Name: " + entityInfo);
            RSA.chat("ID: " + entityId);
            RSA.chat("Pos: " + simplePos);
         }
      }
   );
   private final ButtonSetting getSbID = new ButtonSetting("Gets the SBID of the item you're holding", "Get SBID", () -> {
      class_746 player = class_310.method_1551().field_1724;
      class_310 mc = class_310.method_1551();
      if (player != null) {
         class_1799 stack = player.method_6047();
         String sbid = ItemUtils.getID(stack);
         RSA.chat("SBID: " + sbid);
      }
   });

   public DevUtils() {
      this.registerProperty(new Setting[]{this.pos, this.yawPitch, this.blockinfo, this.entityinfo, this.getSbID});
   }

   public void onEnable() {
   }

   public void onDisable() {
   }

   public void reset() {
   }

   public ButtonSetting getPos() {
      return this.pos;
   }

   public ButtonSetting getYawPitch() {
      return this.yawPitch;
   }

   public ButtonSetting getBlockinfo() {
      return this.blockinfo;
   }

   public ButtonSetting getEntityinfo() {
      return this.entityinfo;
   }

   public ButtonSetting getGetSbID() {
      return this.getSbID;
   }
}
