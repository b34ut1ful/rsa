package com.ricedotwho.rsa.packet.sb;

import net.minecraft.class_2540;
import net.minecraft.class_2960;
import net.minecraft.class_8710;
import net.minecraft.class_9139;
import net.minecraft.class_8710.class_9154;
import org.jetbrains.annotations.NotNull;

public record BloodClipHelperStopPacket() implements class_8710 {
   public static final class_9139<class_2540, BloodClipHelperStopPacket> CODEC = class_8710.method_56484(
      BloodClipHelperStopPacket::write, BloodClipHelperStopPacket::new
   );
   public static final class_9154<BloodClipHelperStopPacket> TYPE = new class_9154(class_2960.method_60655("zero", "bloodcliphelper/stop"));

   public BloodClipHelperStopPacket(class_2540 buf) {
      this();
   }

   public void write(class_2540 buf) {
   }

   @NotNull
   public class_9154<BloodClipHelperStopPacket> method_56479() {
      return TYPE;
   }
}
