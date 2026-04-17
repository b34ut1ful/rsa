package com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.rings;

import com.google.gson.JsonObject;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.AutoP3;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.RingType;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.args.Argument;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.args.ArgumentManager;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.subactions.SubActionManager;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.subactions.SubActionType;
import com.ricedotwho.rsm.component.impl.Renderer3D;
import com.ricedotwho.rsm.data.Colour;
import com.ricedotwho.rsm.data.MutableInput;
import com.ricedotwho.rsm.data.Pos;
import com.ricedotwho.rsm.utils.Accessor;
import com.ricedotwho.rsm.utils.FileUtils;
import com.ricedotwho.rsm.utils.render.render3d.type.FilledBox;
import com.ricedotwho.rsm.utils.render.render3d.type.OutlineBox;
import net.minecraft.class_10185;
import net.minecraft.class_238;
import net.minecraft.class_243;

public abstract class Ring implements Accessor {
   private final class_238 box;
   private final class_238 renderBox;
   private final class_238 fillBox;
   private final class_238 inlineBox;
   private boolean triggered;
   private boolean active = false;
   private final SubActionManager subManager;
   private final ArgumentManager argManager;

   protected Ring(Pos pos, double radius, double renderOffset) {
      this(pos.subtract(radius, 0.0, radius), pos.add(radius, radius * 2.0, radius), renderOffset, null, null);
   }

   protected Ring(class_243 pos, double radius, double renderOffset) {
      this(pos.method_1023(radius, 0.0, radius), pos.method_1031(radius, radius * 2.0, radius), renderOffset);
   }

   protected Ring(class_243 min, class_243 max, double renderOffset) {
      this.box = new class_238(min, max);
      this.renderBox = this.box.method_1002(renderOffset, renderOffset, renderOffset);
      this.triggered = false;
      this.subManager = null;
      this.argManager = null;
      this.fillBox = new class_238(
         min.method_10216(), min.method_10214(), min.method_10215(), max.method_10216(), min.method_10214() + 0.05, max.method_10215()
      );
      class_243 diff = max.method_1020(min).method_18805(0.15, 0.0, 0.15);
      this.inlineBox = new class_238(
         min.method_10216() + diff.method_10216(),
         min.method_10214(),
         min.method_10215() + diff.method_10215(),
         max.method_10216() - diff.method_10216(),
         min.method_10214() + 0.05,
         max.method_10215() - diff.method_10215()
      );
   }

   protected Ring(Pos min, Pos max, double renderOffset, ArgumentManager manager, SubActionManager subManager) {
      this.box = new class_238(min.x(), min.y(), min.z(), max.x(), max.y(), max.z());
      this.renderBox = this.box.method_1002(renderOffset, renderOffset, renderOffset);
      this.triggered = false;
      this.subManager = subManager;
      this.argManager = manager;
      this.fillBox = new class_238(min.x(), min.y(), min.z(), max.x(), min.y() + 0.05, max.z());
      Pos diff = max.subtract(min).multiply(0.15, 0.0, 0.15);
      this.inlineBox = new class_238(min.x() + diff.x(), min.y(), min.z() + diff.z(), max.x() - diff.x(), min.y() + 0.05, max.z() - diff.z());
   }

   public boolean isInNode(class_243 curr, class_243 prev) {
      class_238 feet = new class_238(
         curr.field_1352 - 0.2, curr.field_1351, curr.field_1350 - 0.2, curr.field_1352 + 0.3, curr.field_1351 + 0.5, curr.field_1350
      );
      boolean intercept = this.box.method_993(curr, prev);
      boolean intersects = this.box.method_994(feet);
      return intercept || intersects;
   }

   public void setTriggered() {
      this.triggered = true;
   }

   public void setActive() {
      this.active = true;
   }

   public void setInactive() {
      this.active = false;
   }

   public boolean updateState(class_243 playerPos, class_243 oldPos) {
      boolean inNode = this.isInNode(playerPos, oldPos);
      if (inNode && !this.triggered) {
         return true;
      } else {
         if (!inNode && this.triggered) {
            this.reset();
         }

         return false;
      }
   }

   public float getDistanceSq(class_243 vec3) {
      float dx = (float)((this.box.field_1320 + this.box.field_1323) / 2.0 - vec3.field_1352);
      float dy = (float)((this.box.field_1325 + this.box.field_1322) / 2.0 - vec3.field_1351);
      float dz = (float)((this.box.field_1324 + this.box.field_1321) / 2.0 - vec3.field_1350);
      return dx * dx + dy * dy + dz * dz;
   }

   public abstract RingType getType();

   public void reset() {
      this.triggered = false;
      if (this.argManager != null) {
         this.argManager.reset();
      }
   }

   public void render(boolean depth) {
      Renderer3D.addTask(new FilledBox(this.fillBox, this.getColour().alpha(50.0F), depth));
      Renderer3D.addTask(new OutlineBox(this.inlineBox, this.getColour(), depth));
   }

   public abstract boolean run();

   public boolean execute() {
      if (this.subManager != null) {
         this.subManager.run();
      }

      return this.run();
   }

   public boolean checkArg() {
      return this.argManager != null && this.argManager.check();
   }

   public abstract Colour getColour();

   public abstract int getPriority();

   public abstract boolean tick(MutableInput var1, class_10185 var2, AutoP3 var3);

   public abstract void feedback();

   public boolean isStop() {
      return false;
   }

   public boolean shouldStop() {
      return this.subManager != null && this.subManager.has(SubActionType.STOP);
   }

   public <T> void consumeArg(Class<? extends Argument<T>> clazz, T value) {
      if (this.argManager != null) {
         this.argManager.consume(clazz, value);
      }
   }

   public JsonObject serialize() {
      JsonObject obj = new JsonObject();
      obj.addProperty("type", this.getType().name());
      obj.add("min", FileUtils.getGson().toJsonTree(new Pos(this.box.field_1323, this.box.field_1322, this.box.field_1321)));
      obj.add("max", FileUtils.getGson().toJsonTree(new Pos(this.box.field_1320, this.box.field_1325, this.box.field_1324)));
      if (this.argManager != null && !this.argManager.getArgs().isEmpty()) {
         obj.add("args", this.argManager.serialize());
      }

      if (this.subManager != null && !this.subManager.getActions().isEmpty()) {
         obj.add("sub", this.subManager.serialize());
      }

      return obj;
   }

   public class_238 getBox() {
      return this.box;
   }

   public class_238 getRenderBox() {
      return this.renderBox;
   }

   public boolean isTriggered() {
      return this.triggered;
   }

   public boolean isActive() {
      return this.active;
   }

   public SubActionManager getSubManager() {
      return this.subManager;
   }

   public ArgumentManager getArgManager() {
      return this.argManager;
   }
}
