package com.ricedotwho.rsa.module.impl.dungeon.puzzle;

import com.ricedotwho.rsa.component.impl.managers.PacketOrderManager;
import com.ricedotwho.rsa.component.impl.managers.SwapManager;
import com.ricedotwho.rsm.module.api.SubModuleInfo;
import com.ricedotwho.rsm.module.impl.dungeon.puzzle.TicTacToe;
import com.ricedotwho.rsm.ui.clickgui.settings.Setting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.BooleanSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.NumberSetting;
import com.ricedotwho.rsm.utils.RotationUtils;
import java.math.BigDecimal;
import net.minecraft.class_2269;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_4050;

@SubModuleInfo(name = "Tic Tac Toe", alwaysDisabled = false)
public class AutoTTT extends TicTacToe {
   private final BooleanSetting auto = new BooleanSetting("Auto", false);
   private final NumberSetting range = new NumberSetting("Range", 1.0, 6.0, 4.5, 0.1);
   private final NumberSetting cooldown = new NumberSetting("Cooldown", 100.0, 1000.0, 500.0, 25.0);
   private long nextClick = 0L;

   public AutoTTT(Puzzles puzzles) {
      super(puzzles);
      this.registerProperty(new Setting[]{this.auto, this.range, this.cooldown});
   }

   public void reset() {
      super.reset();
      this.nextClick = 0L;
   }

   protected void postSolve() {
      if (this.getBestMove() != null && (Boolean)this.auto.getValue() && System.currentTimeMillis() >= this.nextClick) {
         class_243 eyePos = mc.field_1724
            .method_73189()
            .method_1031(0.0, mc.field_1724.method_71091().comp_3164() ? 1.54F : class_310.method_1551().field_1724.method_18381(class_4050.field_18076), 0.0);
         class_2338 best = this.getBestMove();
         double dist = eyePos.method_1028(best.method_10263(), best.method_10264(), best.method_10260());
         double range = ((BigDecimal)this.getRange().getValue()).doubleValue();
         if (!(dist > range * range)) {
            this.clickButton(best, eyePos);
         }
      }
   }

   private void clickButton(class_2338 pos, class_243 eyePos) {
      class_2680 blockState = mc.field_1687.method_8320(pos);
      if (blockState.method_26204() instanceof class_2269) {
         class_238 blockAABB = blockState.method_26218(mc.field_1687, pos).method_1107();
         class_243 center = new class_243(
            (blockAABB.field_1323 + blockAABB.field_1320) * 0.5 + pos.method_10263(),
            (blockAABB.field_1322 + blockAABB.field_1325) * 0.5 + pos.method_10264(),
            (blockAABB.field_1321 + blockAABB.field_1324) * 0.5 + pos.method_10260()
         );
         class_3965 result = RotationUtils.collisionRayTrace(pos, blockAABB, eyePos, center);
         if (result != null) {
            PacketOrderManager.register(
               PacketOrderManager.STATE.ITEM_USE,
               () -> SwapManager.sendBlockC08(result.method_17784(), result.method_17780(), !mc.field_1724.method_71091().comp_3164(), true)
            );
            this.nextClick = System.currentTimeMillis() + ((BigDecimal)this.cooldown.getValue()).longValue();
         }
      }
   }

   public BooleanSetting getAuto() {
      return this.auto;
   }

   public NumberSetting getRange() {
      return this.range;
   }

   public NumberSetting getCooldown() {
      return this.cooldown;
   }

   public long getNextClick() {
      return this.nextClick;
   }
}
