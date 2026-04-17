package com.ricedotwho.rsa.module.impl.dungeon;

import com.ricedotwho.rsm.RSM;
import com.ricedotwho.rsm.component.impl.location.Island;
import com.ricedotwho.rsm.component.impl.location.Location;
import com.ricedotwho.rsm.module.Module;
import com.ricedotwho.rsm.module.api.Category;
import com.ricedotwho.rsm.module.api.ModuleInfo;
import com.ricedotwho.rsm.utils.ItemUtils;
import java.util.Arrays;
import java.util.List;
import net.minecraft.class_10735;
import net.minecraft.class_1799;
import net.minecraft.class_2237;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2281;
import net.minecraft.class_2377;
import net.minecraft.class_2401;
import net.minecraft.class_2459;
import net.minecraft.class_2484;
import net.minecraft.class_2680;
import net.minecraft.class_3481;
import net.minecraft.class_5546;
import net.minecraft.class_6862;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@ModuleInfo(aliases = "ZPDB", id = "DungeonBreaker", category = Category.DUNGEONS)
public class DungeonBreaker extends Module {
   private static final List<class_2248> BLACKLIST = Arrays.asList(
      class_2246.field_10499,
      class_2246.field_10525,
      class_2246.field_10085,
      class_2246.field_9987,
      class_2246.field_10560,
      class_2246.field_10379,
      class_2246.field_10008,
      class_2246.field_10615,
      class_2246.field_10375,
      class_2246.field_10027,
      class_2246.field_10398,
      class_2246.field_10613,
      class_2246.field_10316,
      class_2246.field_10034,
      class_2246.field_10443,
      class_2246.field_10380
   );
   private static final List<class_6862<class_2248>> TAGS = List.of(class_3481.field_15493, class_3481.field_61206);
   private static final List<Class<?>> CLASSES = List.of(
      class_2401.class, class_2459.class, class_10735.class, class_5546.class, class_2484.class, class_2281.class, class_2377.class, class_2237.class
   );
   private static int maxCharges = 20;
   private static int charges = 20;

   public void reset() {
      charges = 20;
   }

   public static void handleDigSpeed(class_2680 state, class_1799 held, CallbackInfoReturnable<Float> cir) {
      if (Location.getArea().is(Island.Dungeon)
         && "DUNGEONBREAKER".equals(ItemUtils.getID(held))
         && ((DungeonBreaker)RSM.getModule(DungeonBreaker.class)).isEnabled()) {
         if (canInstantMine(state)) {
            cir.setReturnValue(1500.0F);
         } else {
            cir.setReturnValue(0.0F);
         }
      }
   }

   public static boolean canInstantMine(class_2680 state) {
      return !BLACKLIST.contains(state.method_26204())
         && TAGS.stream().noneMatch(state::method_26164)
         && CLASSES.stream().noneMatch(c -> c.isInstance(state.method_26204()));
   }
}
