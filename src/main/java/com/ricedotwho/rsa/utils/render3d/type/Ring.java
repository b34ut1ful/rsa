package com.ricedotwho.rsa.utils.render3d.type;

import com.ricedotwho.rsa.utils.render3d.RSAVertexRenderer;
import com.ricedotwho.rsm.data.Colour;
import com.ricedotwho.rsm.utils.render.render3d.type.RenderTask;
import com.ricedotwho.rsm.utils.render.render3d.type.RenderType;
import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_4588;

public class Ring extends RenderTask {
   private final class_243 pos;
   private final float radius;
   private final Colour colour;
   private final int slices;
   private final int layers;

   public Ring(class_243 pos, boolean depth, float radius, Colour colour) {
      this(pos, depth, radius, colour, 64, 16);
   }

   public Ring(class_243 pos, boolean depth, float radius, Colour colour, int slices, int layers) {
      super(RenderType.LINE, depth);
      this.pos = pos;
      this.radius = radius;
      this.colour = colour;
      this.slices = slices;
      this.layers = layers;
   }

   private int getFactor() {
      class_1297 camera = class_310.method_1551().method_1560();
      double dist = camera.method_5707(this.pos);
      if (dist > 4096.0) {
         return 0;
      } else if (dist > 2304.0) {
         return 8;
      } else if (dist > 1024.0) {
         return 4;
      } else {
         return dist > 256.0 ? 2 : 1;
      }
   }

   public void render(class_4587 stack, class_4588 buffer, RenderType source) {
      int factor = this.getFactor();
      if (factor != 0) {
         int slices = this.slices / factor;
         int layers = this.layers / factor;
         RSAVertexRenderer.renderRing(stack.method_23760(), buffer, this.pos, this.radius, this.colour, slices, layers);
      }
   }
}
