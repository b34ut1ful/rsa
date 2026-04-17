package com.ricedotwho.rsa.module.impl.dungeon.boss.p3;

import com.ricedotwho.rsa.component.impl.managers.PacketOrderManager;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.terminals.auto.AutoTerms;
import com.ricedotwho.rsa.utils.InteractUtils;
import com.ricedotwho.rsm.RSM;
import com.ricedotwho.rsm.component.impl.location.Floor;
import com.ricedotwho.rsm.component.impl.location.Island;
import com.ricedotwho.rsm.component.impl.location.Location;
import com.ricedotwho.rsm.component.impl.map.handler.Dungeon;
import com.ricedotwho.rsm.data.Phase7;
import com.ricedotwho.rsm.event.api.SubscribeEvent;
import com.ricedotwho.rsm.event.impl.game.ClientTickEvent.Start;
import com.ricedotwho.rsm.module.Module;
import com.ricedotwho.rsm.module.api.Category;
import com.ricedotwho.rsm.module.api.ModuleInfo;
import com.ricedotwho.rsm.ui.clickgui.settings.Setting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.BooleanSetting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.NumberSetting;
import com.ricedotwho.rsm.utils.DungeonUtils;
import com.ricedotwho.rsm.utils.MathUtils;
import java.math.BigDecimal;
import net.minecraft.class_1297;
import net.minecraft.class_1531;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_465;

@ModuleInfo(aliases = "Term Aura", id = "TermAura", category = Category.DUNGEONS)
public class TermAura extends Module {
   private static final double AURA_RANGE = 4.0;
   private static final double AURA_RANGE_SQ = 16.0;
   private final NumberSetting delay = new NumberSetting("Delay", 50.0, 5000.0, 500.0, 50.0);
   private final BooleanSetting showArmorStands = new BooleanSetting("Show Hitboxes", false);
   private final BooleanSetting forceSkyblock = new BooleanSetting("Force Skyblock", false);
   private long lastClick = 0L;

   public TermAura() {
      this.registerProperty(new Setting[]{this.delay, this.showArmorStands, this.forceSkyblock});
   }

   @SubscribeEvent
   public void onTick(Start event) {
      if (mc.field_1755 == null) {
         PacketOrderManager.register(PacketOrderManager.STATE.ITEM_USE, this::rapeArmorstands);
      }
   }

   private void rapeArmorstands() {
      if (class_310.method_1551().field_1724 != null
         && class_310.method_1551().field_1687 != null
         && class_310.method_1551().method_1562() != null
         && System.currentTimeMillis() - this.lastClick >= ((BigDecimal)this.delay.getValue()).longValue()
         && this.locationCheck()
         && !AutoTerms.isInTerminal()
         && !(class_310.method_1551().field_1755 instanceof class_465)) {
         class_243 eyePos = class_310.method_1551().field_1724.method_73189().method_1031(0.0, class_310.method_1551().field_1724.method_5751(), 0.0);
         double bestDistance = 16.0;
         class_1531 bestCandidate = null;
         class_243 retardedPos = class_310.method_1551().field_1724.method_73189().method_1031(0.0, -2.0, 0.0);
         class_238 box = new class_238(retardedPos, retardedPos).method_1009(4.0, 4.0, 4.0);

         for (class_1531 stand : class_310.method_1551().field_1687.method_8390(class_1531.class, box, TermAura::filterEntities)) {
            double distance = stand.method_73189().method_1025(retardedPos);
            if (distance <= bestDistance) {
               bestCandidate = stand;
               bestDistance = distance;
            }
         }

         if (bestCandidate != null) {
            class_243 vec3 = MathUtils.clamp(bestCandidate.method_5829(), eyePos)
               .method_1023(bestCandidate.method_23317(), bestCandidate.method_23318(), bestCandidate.method_23321());
            InteractUtils.interactOnEntity(bestCandidate, vec3);
            this.lastClick = System.currentTimeMillis();
         }
      }
   }

   public static boolean getEntityVisibility(class_1297 entity) {
      if (!entity.method_5767()) {
         return true;
      } else {
         TermAura termAura = (TermAura)RSM.getModule(TermAura.class);
         return termAura == null ? false : termAura.isEnabled() && (Boolean)termAura.showArmorStands.getValue() && termAura.locationCheck();
      }
   }

   private boolean locationCheck() {
      return (Boolean)this.forceSkyblock.getValue()
         || Location.getArea().is(Island.Dungeon)
            && (Location.getFloor() == Floor.F7 || Location.getFloor() == Floor.M7)
            && DungeonUtils.isPhase(Phase7.P3)
            && Dungeon.isInBoss();
   }

   private static boolean filterEntities(class_1531 armorStand) {
      if (armorStand.method_29504()) {
         return false;
      } else {
         class_2561 name = armorStand.method_5797();
         return name == null ? false : name.getString().equals("Inactive Terminal");
      }
   }
}
