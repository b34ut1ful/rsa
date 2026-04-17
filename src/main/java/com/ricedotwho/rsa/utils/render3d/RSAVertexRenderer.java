package com.ricedotwho.rsa.utils.render3d;

import com.ricedotwho.rsm.data.Colour;
import com.ricedotwho.rsm.utils.render.render3d.VertexRenderer;
import net.minecraft.class_243;
import net.minecraft.class_4588;
import net.minecraft.class_4587.class_4665;

public final class RSAVertexRenderer {
   public static void renderRing(class_4665 pose, class_4588 buffer, class_243 pos, float radius, Colour colour, int slices, int layers) {
      if (slices >= 3) {
         pose.method_67796((float)pos.method_10216(), (float)pos.method_10214(), (float)pos.method_10215());
         double h = radius * 2.0F / 3.0;
         float oneOverLayers = 1.0F / layers;
         float red = colour.getRedFloat();
         float green = colour.getGreenFloat();
         float blue = colour.getBlueFloat();

         for (int i = 0; i < layers; i++) {
            float yOffset = (float)(h * i / layers);
            float t = 1.0F - i * oneOverLayers;
            float alpha = t * t * t;
            if (!(alpha < 0.01F)) {
               VertexRenderer.circle(pose, buffer, radius, yOffset, alpha, red, green, blue, slices);
            }
         }

         pose.method_67796((float)(-pos.method_10216()), (float)(-pos.method_10214()), (float)(-pos.method_10215()));
      }
   }

   private RSAVertexRenderer() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
