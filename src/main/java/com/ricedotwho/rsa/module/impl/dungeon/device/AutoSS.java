package com.ricedotwho.rsa.module.impl.dungeon.device;

import com.ricedotwho.rsa.RSA;
import com.ricedotwho.rsa.component.impl.managers.PacketOrderManager;
import com.ricedotwho.rsa.component.impl.managers.SwapManager;
import com.ricedotwho.rsm.component.impl.Renderer3D;
import com.ricedotwho.rsm.component.impl.location.Floor;
import com.ricedotwho.rsm.component.impl.location.Island;
import com.ricedotwho.rsm.component.impl.location.Location;
import com.ricedotwho.rsm.data.Colour;
import com.ricedotwho.rsm.data.Keybind;
import com.ricedotwho.rsm.data.Phase7;
import com.ricedotwho.rsm.event.api.SubscribeEvent;
import com.ricedotwho.rsm.event.impl.game.ChatEvent.Chat;
import com.ricedotwho.rsm.event.impl.render.Render3DEvent.Extract;
import com.ricedotwho.rsm.event.impl.render.Render3DEvent.Last;
import com.ricedotwho.rsm.event.impl.world.BlockChangeEvent;
import com.ricedotwho.rsm.event.impl.world.WorldEvent.Load;
import com.ricedotwho.rsm.module.Module;
import com.ricedotwho.rsm.module.api.Category;
import com.ricedotwho.rsm.module.api.ModuleInfo;
import com.ricedotwho.rsm.ui.clickgui.settings.Setting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.BooleanSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.ColourSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.KeybindSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.NumberSetting;
import com.ricedotwho.rsm.utils.DungeonUtils;
import com.ricedotwho.rsm.utils.render.render3d.type.FilledOutlineBox;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;

@ModuleInfo(aliases = "AutoSS", id = "AutoSS", category = Category.DUNGEONS)
public class AutoSS extends Module {
   private static final class_243 START_BUTTON = new class_243(110.875, 121.5, 91.5);
   private static final class_2338 DETECT = new class_2338(110, 123, 92);
   KeybindSetting resetKey = new KeybindSetting("Reset SS Key", new Keybind(-1, false, null), this::SSR);
   BooleanSetting sendChat = new BooleanSetting("Send SSR Chat Message", true);
   BooleanSetting autoStart = new BooleanSetting("Autostart", true);
   BooleanSetting forceSkyblock = new BooleanSetting("Force Skyblock (Don't keep enabled)", false);
   private final NumberSetting clickDelay = new NumberSetting("Click Delay (MS)", 10.0, 500.0, 200.0, 10.0);
   private final NumberSetting autoStartDelay = new NumberSetting("Autostart Delay (MS)", 10.0, 500.0, 120.0, 10.0);
   private final ColourSetting fillColor = new ColourSetting("Button Fill Color", Colour.GREEN.brighter());
   private final ColourSetting outlineColor = new ColourSetting("Button Outline Color", Colour.GREEN.darker());
   private long lastClickTime = System.currentTimeMillis();
   private boolean next = false;
   private int state = 0;
   private boolean doneFirst = false;
   private boolean doingSS = false;
   private final List<class_2338> clicks = new ArrayList<>();
   private final List<class_243> allButtons = new ArrayList<>();
   private class_243 clickedButton;

   public AutoSS() {
      this.registerProperty(new Setting[]{this.resetKey, this.sendChat, this.autoStart, this.forceSkyblock, this.clickDelay, this.autoStartDelay});
   }

   private void start() {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null && !(player.method_5707(START_BUTTON) > 25.0)) {
         this.allButtons.clear();
         RSA.chat("Starting SS!");
         this.resetState();
         this.doingSS = true;
         new Thread(() -> {
            try {
               for (int i = 0; i < 2; i++) {
                  this.reset();
                  this.clickButton(START_BUTTON);
                  Thread.sleep(((BigDecimal)this.autoStartDelay.getValue()).longValue());
               }

               this.doingSS = true;
               this.clickButton(START_BUTTON);
            } catch (Exception var21) {
               RSA.chat("Error Occurred");
            }
         }).start();
      }
   }

   @SubscribeEvent
   public void onRender(Last event) {
      if (this.areaCheck()
         && System.currentTimeMillis() - this.lastClickTime + 1L >= ((BigDecimal)this.clickDelay.getValue()).longValue()
         && class_310.method_1551().field_1687 != null
         && class_310.method_1551().field_1724 != null) {
         class_746 player = class_310.method_1551().field_1724;
         if (!(player.method_5707(START_BUTTON) > 25.0)
            && class_310.method_1551().field_1687.method_8320(DETECT).method_26204() == class_2246.field_10494
            && this.doingSS) {
            if (!this.doneFirst && this.clicks.size() == 3) {
               this.clicks.removeFirst();
               this.allButtons.removeFirst();
            }

            this.doneFirst = true;
            if (this.state < this.clicks.size()) {
               class_2338 next = this.clicks.get(this.state);
               if (class_310.method_1551().field_1687.method_8320(next).method_26204() == class_2246.field_10494) {
                  this.clickButton(class_243.method_24954(next));
                  this.state++;
               }
            }
         }
      }
   }

   private boolean areaCheck() {
      return this.forceSkyblock.getValue()
         ? true
         : Location.getArea().is(Island.Dungeon) && (Location.getFloor() == Floor.F7 || Location.getFloor() == Floor.M7) && DungeonUtils.isPhase(Phase7.P3);
   }

   @SubscribeEvent
   public void onRenderButtons(Extract event) {
      if (this.areaCheck() && class_310.method_1551().field_1724 != null && class_310.method_1551().field_1687 != null) {
         class_638 level = class_310.method_1551().field_1687;
         if (System.currentTimeMillis() - this.lastClickTime > ((BigDecimal)this.clickDelay.getValue()).longValue()) {
            this.clickedButton = null;
         }

         if (!(class_310.method_1551().field_1724.method_5707(START_BUTTON) >= 1600.0) && this.clickedButton != null) {
            this.renderButton(level, class_2338.method_49638(this.clickedButton), this.fillColor.getValue(), this.outlineColor.getValue());
         }
      }
   }

   private void renderButton(class_638 level, class_2338 pos, Colour colorFill, Colour colorOutline) {
      class_2680 state = level.method_8320(pos);
      class_265 shape = state.method_26218(level, pos);
      if (!shape.method_1110()) {
         Renderer3D.addTask(new FilledOutlineBox(shape.method_1107().method_996(pos), colorFill, colorOutline, false));
      }
   }

   private void clickButton(class_243 vec3) {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null) {
         if (player.method_5707(vec3) > 36.0) {
            RSA.chat("Button too far!");
         } else {
            this.lastClickTime = System.currentTimeMillis();
            PacketOrderManager.register(PacketOrderManager.STATE.ITEM_USE, () -> this.clickButton0(vec3));
         }
      }
   }

   private void clickButton0(class_243 vec3) {
      class_746 player = class_310.method_1551().field_1724;
      if (player != null) {
         if (player.method_5707(vec3) > 36.0) {
            RSA.chat("Button too far!");
         } else {
            this.clickedButton = vec3;
            SwapManager.sendBlockC08(vec3, class_2350.field_11039, true, false);
         }
      }
   }

   @SubscribeEvent
   public void onWorldLoad(Load event) {
      this.resetState();
   }

   @SubscribeEvent
   public void onChatMessage(Chat event) {
      if (this.areaCheck() && (Boolean)this.autoStart.getValue() && class_310.method_1551().field_1724 != null) {
         String msg = event.getMessage().getString();
         if (msg.equals("[BOSS] Goldor: Who dares trespass into my domain?")) {
            this.start();
         }
      }
   }

   @SubscribeEvent
   public void onBlockChange(BlockChangeEvent event) {
      class_2338 pos = event.getBlockPos();
      if (event.getNewState().method_26204() == class_2246.field_10174
         && this.areaCheck()
         && pos.method_10263() == 111
         && pos.method_10264() >= 120
         && pos.method_10264() <= 123
         && pos.method_10260() >= 92
         && pos.method_10260() <= 95) {
         class_2338 button = new class_2338(110, event.getBlockPos().method_10264(), event.getBlockPos().method_10260());
         if (this.clicks.size() == 2 && this.clicks.getFirst().equals(button) && !this.doneFirst) {
            this.doneFirst = true;
            this.clicks.removeFirst();
            this.allButtons.removeFirst();
         }

         if (!this.clicks.contains(button)) {
            this.state = 0;
            this.clicks.add(button);
            this.allButtons.add(class_243.method_24954(button));
         }
      }
   }

   public void SSR() {
      if (this.areaCheck()) {
         if ((Boolean)this.sendChat.getValue() && class_310.method_1551().method_1562() != null) {
            class_310.method_1551().method_1562().method_45730("pc SSRS! blame tps");
         }

         this.start();
      }
   }

   public void resetState() {
      this.allButtons.clear();
      this.clicks.clear();
      this.next = false;
      this.state = 0;
      this.doneFirst = false;
      this.doingSS = false;
   }

   public void onEnable() {
      this.resetState();
   }

   public KeybindSetting getResetKey() {
      return this.resetKey;
   }

   public BooleanSetting getSendChat() {
      return this.sendChat;
   }

   public BooleanSetting getAutoStart() {
      return this.autoStart;
   }

   public BooleanSetting getForceSkyblock() {
      return this.forceSkyblock;
   }

   public NumberSetting getClickDelay() {
      return this.clickDelay;
   }

   public NumberSetting getAutoStartDelay() {
      return this.autoStartDelay;
   }

   public ColourSetting getFillColor() {
      return this.fillColor;
   }

   public ColourSetting getOutlineColor() {
      return this.outlineColor;
   }

   public long getLastClickTime() {
      return this.lastClickTime;
   }

   public boolean isNext() {
      return this.next;
   }

   public int getState() {
      return this.state;
   }

   public boolean isDoneFirst() {
      return this.doneFirst;
   }

   public boolean isDoingSS() {
      return this.doingSS;
   }

   public List<class_2338> getClicks() {
      return this.clicks;
   }

   public List<class_243> getAllButtons() {
      return this.allButtons;
   }

   public class_243 getClickedButton() {
      return this.clickedButton;
   }
}
