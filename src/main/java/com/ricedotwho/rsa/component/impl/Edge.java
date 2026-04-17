package com.ricedotwho.rsa.component.impl;

import com.google.common.collect.Streams;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.AutoP3;
import com.ricedotwho.rsm.component.api.ModComponent;
import com.ricedotwho.rsm.event.api.SubscribeEvent;
import com.ricedotwho.rsm.event.impl.client.InputPollEvent;
import java.math.BigDecimal;
import java.util.stream.Stream;
import net.minecraft.class_238;
import net.minecraft.class_265;

public class Edge extends ModComponent {
   private static boolean edge = false;

   public Edge() {
      super("Edge");
   }

   public static void edge() {
      edge = true;
   }

   @SubscribeEvent
   public void onInput(InputPollEvent event) {
      if (edge
         && mc.field_1724 != null
         && mc.field_1724.method_24828()
         && !mc.field_1690.field_1903.method_1434()
         && !mc.field_1724.method_5715()
         && !mc.field_1690.field_1832.method_1434()) {
         double dist = ((BigDecimal)AutoP3.getEdgeDist().getDefaultValue()).doubleValue();
         class_238 box = mc.field_1724.method_5829();
         class_238 adjustedBox = box.method_989(0.0, -0.5, 0.0).method_1009(-dist, 0.0, -dist);
         Stream<class_265> blockCollisions = Streams.stream(mc.field_1687.method_20812(mc.field_1724, adjustedBox));
         if (!blockCollisions.findAny().isPresent()) {
            edge = false;
            event.getInput().jump(true);
         }
      }
   }

   public static boolean isEdge() {
      return edge;
   }
}
