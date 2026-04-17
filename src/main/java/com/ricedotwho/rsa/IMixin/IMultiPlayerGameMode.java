package com.ricedotwho.rsa.IMixin;

import net.minecraft.class_638;
import net.minecraft.class_7204;

public interface IMultiPlayerGameMode {
   void sendPacketSequenced(class_638 var1, class_7204 var2);

   void syncSlot();
}
