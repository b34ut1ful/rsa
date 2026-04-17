package com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3;

import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_3532;

public class MovementPredictor {
   public static double getTickVelocityFromInput(int tickIndex, double walkSpeed) {
      return Math.pow(0.546000082, tickIndex) * 0.098 * walkSpeed;
   }

   public static double getDisplacementFromInput(double walkSpeed, boolean sneaking) {
      if (sneaking) {
         walkSpeed *= 0.3;
      }

      int movementTicks = getInputMovementTicks(walkSpeed);
      return 0.098 * walkSpeed * (1.0 - Math.pow(0.546000082, movementTicks)) / 0.45399991799999995;
   }

   public static double squaredAfterTick(double fwd, double right, double dFwd, double dRight) {
      double nf = (fwd + dFwd) * 0.546000082;
      double nr = (right + dRight) * 0.546000082;
      return nf * nf + nr * nr;
   }

   public static int getMovementTicks(float dx, float dy) {
      return (int)Math.ceil(Math.log(0.003 / class_3532.method_15355(dx * dx + dy + dy)) / Math.log(0.546000082));
   }

   public static double getDisplacementMagnitude(class_241 velocity) {
      double magnitude = velocity.method_35584();
      int movementTicks = (int)Math.ceil(Math.log(0.003 / magnitude) / Math.log(0.546000082));
      return movementTicks <= 0 ? magnitude : magnitude * (1.0 - Math.pow(0.546000082, movementTicks)) / 0.45399991799999995;
   }

   public static class_241 getDisplacementVector(class_241 velocity) {
      float magnitude = velocity.method_35584();
      if (magnitude < 1.0E-6) {
         return class_241.field_1340;
      } else {
         float displacement = (float)getDisplacementMagnitude(velocity);
         float scale = displacement / magnitude;
         return velocity.method_35582(scale);
      }
   }

   private static int getInputMovementTicks(double velocity) {
      return (int)Math.ceil(Math.log(0.003 / (0.098 * velocity)) / Math.log(0.546000082));
   }

   public static class_241 rotateVec(class_241 vec, float yaw) {
      double yawRad = Math.toRadians(yaw);
      double cos = Math.cos(yawRad);
      double sin = Math.sin(yawRad);
      float newX = (float)(vec.field_1343 * cos - vec.field_1342 * sin);
      float newY = (float)(vec.field_1343 * sin + vec.field_1342 * cos);
      return new class_241(newX, newY);
   }

   public static class_243 rotateVec(class_243 vec, float yaw) {
      double yawRad = Math.toRadians(yaw);
      double cos = Math.cos(yawRad);
      double sin = Math.sin(yawRad);
      double newX = vec.field_1352 * cos - vec.field_1350 * sin;
      double newZ = vec.field_1352 * sin + vec.field_1350 * cos;
      return new class_243(newX, vec.field_1351, newZ);
   }
}
