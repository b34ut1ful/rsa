package com.ricedotwho.rsa.mixins;

import com.ricedotwho.rsa.IMixin.IConnection;
import com.ricedotwho.rsa.component.impl.managers.PacketOrderManager;
import com.ricedotwho.rsa.module.impl.dungeon.boss.Blink;
import com.ricedotwho.rsa.module.impl.movement.VelocityBuffer;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.class_2535;
import net.minecraft.class_2596;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = class_2535.class, priority = 400)
public abstract class MixinLowPriorityConnection implements IConnection {
   @Shadow
   protected abstract void method_10764(class_2596<?> var1, @Nullable ChannelFutureListener var2, boolean var3);

   @Inject(
      method = "method_10770(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/class_2596;)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/class_2535;method_10759(Lnet/minecraft/class_2596;Lnet/minecraft/class_2547;)V"),
      cancellable = true
   )
   private void channelRead0(ChannelHandlerContext channelHandlerContext, class_2596<?> packet, CallbackInfo ci) {
      PacketOrderManager.onPreReceivePacket(packet);
      if (VelocityBuffer.onReceivePacketPre(packet)) {
         ci.cancel();
      }
   }

   @Inject(method = "method_10764(Lnet/minecraft/class_2596;Lio/netty/channel/ChannelFutureListener;Z)V", at = @At("HEAD"), cancellable = true)
   private void onSendPacket(class_2596<?> packet, @Nullable ChannelFutureListener channelFutureListener, boolean flush, CallbackInfo ci) {
      if (Blink.onSendPacket(packet)) {
         ci.cancel();
      }
   }

   @Override
   public void sendPacketImmediately(class_2596<?> packet) {
      this.method_10764(packet, null, true);
   }
}
