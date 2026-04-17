package com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.args.type;

import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.args.Argument;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.args.RingArgType;

public class GroundArg extends Argument<Object> {
   public GroundArg() {
      super(RingArgType.GROUND);
   }

   @Override
   public boolean check() {
      return mc.field_1724 != null && mc.field_1724.method_24828();
   }

   @Override
   public void consume(Object event) {
   }

   @Override
   public void reset() {
   }

   @Override
   public String stringValue() {
      return "ground";
   }

   public static GroundArg create(String arg) {
      return new GroundArg();
   }
}
