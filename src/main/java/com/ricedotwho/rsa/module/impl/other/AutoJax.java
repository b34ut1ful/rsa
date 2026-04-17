package com.ricedotwho.rsa.module.impl.other;

import com.ricedotwho.rsa.RSA;
import com.ricedotwho.rsm.event.api.SubscribeEvent;
import com.ricedotwho.rsm.event.impl.game.ServerTickEvent;
import com.ricedotwho.rsm.event.impl.game.ChatEvent.Chat;
import com.ricedotwho.rsm.module.Module;
import com.ricedotwho.rsm.module.api.Category;
import com.ricedotwho.rsm.module.api.ModuleInfo;
import com.ricedotwho.rsm.ui.clickgui.settings.Setting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.NumberSetting;
import java.math.BigDecimal;
import java.util.List;
import net.minecraft.class_1268;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_243;
import net.minecraft.class_2886;
import net.minecraft.class_310;
import net.minecraft.class_3544;
import net.minecraft.class_746;
import org.joml.Vector2f;

@ModuleInfo(aliases = "Auto Jax", id = "AutoJax", category = Category.OTHER)
public class AutoJax extends Module {
   private final NumberSetting tickdelay = new NumberSetting("Tick Delay", 1.0, 60.0, 10.0, 2.0);
   private final NumberSetting startDelay = new NumberSetting("Start Delay", 1.0, 260.0, 60.0, 2.0);
   private final NumberSetting shootAfterDelay = new NumberSetting("Rotate -> Shoot delay", 1.0, 60.0, 10.0, 2.0);
   private boolean atstart = false;
   private final List<Vector2f> positions = List.of(
      new Vector2f(0.0F, -1.8F),
      new Vector2f(30.3F, -1.8F),
      new Vector2f(59.4F, 11.4F),
      new Vector2f(90.0F, 5.1F),
      new Vector2f(120.2F, -2.9F),
      new Vector2f(-135.0F, -26.0F),
      new Vector2f(-120.3F, -6.2F),
      new Vector2f(-113.2F, 2.1F),
      new Vector2f(-90.0F, -2.0F),
      new Vector2f(-80.5F, 3.0F),
      new Vector2f(-63.4F, -5.9F),
      new Vector2f(-56.9F, 11.3F),
      new Vector2f(-25.4F, -1.7F),
      new Vector2f(-6.1F, 8.5F)
   );
   private int currentIndex = 0;
   private boolean isRunning = false;
   private int tickDelay;
   private final class_243 startPos = new class_243(-55.5, 62.0, -81.5);
   private static final int ROTATE_TO_CLICK_DELAY = 2;
   private int rotateToClickTicks = 0;
   private boolean pendingClick = false;

   public AutoJax() {
      this.registerProperty(new Setting[]{this.tickdelay, this.startDelay, this.shootAfterDelay});
   }

   public void onEnable() {
      this.currentIndex = 0;
      this.isRunning = false;
      this.tickDelay = 0;
      this.pendingClick = false;
      this.rotateToClickTicks = 0;
   }

   public void onDisable() {
      this.isRunning = false;
      this.pendingClick = false;
      this.rotateToClickTicks = 0;
   }

   public void reset() {
      this.currentIndex = 0;
      this.isRunning = false;
      this.tickDelay = 0;
      this.pendingClick = false;
      this.rotateToClickTicks = 0;
   }

   @SubscribeEvent
   public void onChat(Chat event) {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null) {
         class_243 pos = player.method_73189();
         String unformatted = class_3544.method_15440(event.getMessage().getString());
         if (pos.method_1022(this.startPos) < 0.3) {
            RSA.chat("at start area.");
            this.atstart = true;
         }

         class_1799 itemStack = player.method_5998(class_1268.field_5808);
         class_1792 item = itemStack.method_7909();
         if (unformatted.contains("Goal:") && this.atstart && item == class_1802.field_8102) {
            RSA.chat("Shooting All Targets in 3s.");
            this.isRunning = true;
            this.currentIndex = 0;
            this.tickDelay = ((BigDecimal)this.startDelay.getValue()).intValue();
            this.pendingClick = false;
            this.rotateToClickTicks = 0;
         } else if (unformatted.contains("Goal:") && this.atstart && item != class_1802.field_8102) {
            this.isRunning = false;
            RSA.chat("Hold a bow, and go back onto the pad.");
         }

         if (unformatted.contains("Sending packets too fast!") || unformatted.contains("Cancelled!")) {
            RSA.chat("GET BACK ON DA PAD! AutoJax Canceled.");
            this.isRunning = false;
            this.currentIndex = 0;
            this.tickDelay = ((BigDecimal)this.startDelay.getValue()).intValue();
            this.pendingClick = false;
            this.rotateToClickTicks = 0;
         }
      }
   }

   @SubscribeEvent
   public void onTick(ServerTickEvent event) {
      class_746 player = class_310.method_1551().field_1724;
      if (this.isRunning) {
         if (this.pendingClick) {
            if (this.rotateToClickTicks > 0) {
               this.rotateToClickTicks--;
            } else {
               this.rightClick();
               this.pendingClick = false;
               this.currentIndex++;
               this.tickDelay = ((BigDecimal)this.tickdelay.getValue()).intValue();
            }
         } else if (this.currentIndex >= this.positions.size()) {
            RSA.chat("Finished.");
            this.isRunning = false;
         } else {
            Vector2f targetPos = this.positions.get(this.currentIndex);
            if (this.tickDelay > 0) {
               this.tickDelay--;
            } else {
               player.method_36457(targetPos.y);
               player.method_36456(targetPos.x);
               this.tickDelay = ((BigDecimal)this.shootAfterDelay.getValue()).intValue();
               this.pendingClick = true;
               this.rotateToClickTicks = 2;
            }
         }
      }
   }

   private void rightClick() {
      class_746 player = mc.field_1724;
      if (player != null) {
         player.field_3944.method_52787(new class_2886(class_1268.field_5808, 0, player.method_36454(), player.method_36455()));
         player.method_6104(class_1268.field_5808);
      }
   }

   public NumberSetting getTickdelay() {
      return this.tickdelay;
   }

   public NumberSetting getStartDelay() {
      return this.startDelay;
   }

   public NumberSetting getShootAfterDelay() {
      return this.shootAfterDelay;
   }

   public boolean isAtstart() {
      return this.atstart;
   }

   public List<Vector2f> getPositions() {
      return this.positions;
   }

   public int getCurrentIndex() {
      return this.currentIndex;
   }

   public boolean isRunning() {
      return this.isRunning;
   }

   public class_243 getStartPos() {
      return this.startPos;
   }

   public int getRotateToClickTicks() {
      return this.rotateToClickTicks;
   }

   public boolean isPendingClick() {
      return this.pendingClick;
   }
}
