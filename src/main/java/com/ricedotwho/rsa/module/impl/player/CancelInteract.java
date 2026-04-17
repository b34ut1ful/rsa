package com.ricedotwho.rsa.module.impl.player;

import com.ricedotwho.rsm.RSM;
import com.ricedotwho.rsm.component.impl.location.Location;
import com.ricedotwho.rsm.module.Module;
import com.ricedotwho.rsm.module.api.Category;
import com.ricedotwho.rsm.module.api.ModuleInfo;
import com.ricedotwho.rsm.ui.clickgui.settings.Setting;
import com.ricedotwho.rsm.ui.clickgui.settings.impl.BooleanSetting;
import com.ricedotwho.rsm.utils.ItemUtils;
import java.util.List;
import net.minecraft.class_1799;
import net.minecraft.class_2248;
import net.minecraft.class_2275;
import net.minecraft.class_2281;
import net.minecraft.class_2304;
import net.minecraft.class_2377;
import net.minecraft.class_2401;
import net.minecraft.class_2484;
import net.minecraft.class_2680;
import net.minecraft.class_3481;
import net.minecraft.class_3965;
import net.minecraft.class_6862;
import net.minecraft.class_746;

@ModuleInfo(aliases = "Cancel Interact", id = "CancelInteract", category = Category.PLAYER)
public class CancelInteract extends Module {
   private final BooleanSetting abilityOnly = new BooleanSetting("Ability Only", false);
   private static final List<Class<?>> WHITELIST = List.of(class_2401.class, class_2484.class, class_2275.class, class_2281.class);
   private static final List<class_6862<class_2248>> WHITELIST_TAGS = List.of(class_3481.field_15493, class_3481.field_61206);
   private static final List<Class<?>> BLACKLIST = List.of(class_2377.class, class_2304.class);
   private static final List<class_6862<class_2248>> BLACKLIST_TAGS = List.of(class_3481.field_15504, class_3481.field_16584, class_3481.field_29822);

   public CancelInteract() {
      this.registerProperty(new Setting[]{this.abilityOnly});
   }

   public static boolean shouldCancelInteract(class_3965 hit, class_746 player, class_1799 item) {
      CancelInteract module = (CancelInteract)RSM.getModule(CancelInteract.class);
      if (module.isEnabled() && Location.isInSkyblock()) {
         class_2680 state = player.method_73183().method_8320(hit.method_17777());
         if (!WHITELIST.stream().anyMatch(c -> c.isInstance(state.method_26204())) && !WHITELIST_TAGS.stream().anyMatch(state::method_26164)) {
            return "ENDER_PEARL".equals(ItemUtils.getID(item))
               ? true
               : (!(Boolean)module.getAbilityOnly().getValue() || ItemUtils.isAbilityItem(mc.field_1724.method_31548().method_7391()))
                  && (BLACKLIST_TAGS.stream().anyMatch(state::method_26164) || BLACKLIST.stream().anyMatch(c -> c.isInstance(state.method_26204())));
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public BooleanSetting getAbilityOnly() {
      return this.abilityOnly;
   }
}
