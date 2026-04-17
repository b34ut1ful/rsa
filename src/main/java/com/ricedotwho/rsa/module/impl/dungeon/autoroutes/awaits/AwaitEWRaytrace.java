package com.ricedotwho.rsa.module.impl.dungeon.autoroutes.awaits;

import com.google.gson.JsonObject;
import com.ricedotwho.rsa.module.impl.dungeon.autoroutes.AwaitCondition;
import com.ricedotwho.rsa.module.impl.dungeon.autoroutes.AwaitType;
import com.ricedotwho.rsa.module.impl.dungeon.autoroutes.Node;
import com.ricedotwho.rsa.module.impl.dungeon.autoroutes.nodes.EtherwarpNode;
import com.ricedotwho.rsm.data.Pos;
import com.ricedotwho.rsm.utils.EtherUtils;
import net.minecraft.class_2338;
import net.minecraft.class_243;

public class AwaitEWRaytrace extends AwaitCondition<EtherwarpNode> {
   public AwaitEWRaytrace() {
      super(AwaitType.ETHERWARP_TRACE);
   }

   @Override
   public boolean test(Node node) {
      if (node instanceof EtherwarpNode etherwarpNode) {
         Pos eyePos = etherwarpNode.getRealPos().add(0.0, 1.54F, 0.0);
         Pos viewVector = etherwarpNode.getRealTargetPos().subtract(eyePos).normalize();
         float[] angles = EtherUtils.getYawAndPitch(viewVector.x, viewVector.y, viewVector.z);
         class_243 vec = EtherUtils.rayTraceBlock(61, angles[0], angles[1], eyePos.asVec3());
         viewVector = viewVector.multiply(0.001F).selfAdd(vec.field_1352, vec.field_1351, vec.field_1350);
         class_2338 blockPos = class_2338.method_49637(viewVector.x, viewVector.y, viewVector.z);
         class_2338 etherPos = etherwarpNode.getRealTargetPos()
            .add(etherwarpNode.getRealTargetPos().subtract(eyePos).normalize().multiply(0.001F))
            .asBlockPos();
         return blockPos.equals(etherPos);
      } else {
         return true;
      }
   }

   @Override
   public void onEnter() {
   }

   @Override
   public void reset() {
   }

   protected void consume(EtherwarpNode node) {
   }

   @Override
   public void serialize(JsonObject json) {
      json.addProperty(this.getType().getName(), true);
   }
}
