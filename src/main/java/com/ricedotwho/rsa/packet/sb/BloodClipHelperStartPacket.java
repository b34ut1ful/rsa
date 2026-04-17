package com.ricedotwho.rsa.packet.sb;

import net.minecraft.class_2540;
import net.minecraft.class_2960;
import net.minecraft.class_8710;
import net.minecraft.class_9139;
import net.minecraft.class_8710.class_9154;
import org.jetbrains.annotations.NotNull;

public record BloodClipHelperStartPacket(int roofHeight) implements class_8710 {
   public static final class_9139<class_2540, BloodClipHelperStartPacket> CODEC = class_8710.method_56484(
      BloodClipHelperStartPacket::write, BloodClipHelperStartPacket::new
   );
   public static final class_9154<BloodClipHelperStartPacket> TYPE = new class_9154(class_2960.method_60655("zero", "bloodcliphelper/start"));

   public BloodClipHelperStartPacket(class_2540 buf) {
      this(buf.method_10816());
   }

   public void write(class_2540 buf) {
      buf.method_10804(this.roofHeight);
   }

   @NotNull
   public class_9154<BloodClipHelperStartPacket> method_56479() {
      return TYPE;
   }
}
