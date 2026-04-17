package com.ricedotwho.rsa.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ricedotwho.rsa.RSA;
import com.ricedotwho.rsa.module.impl.dungeon.SecretAura;
import com.ricedotwho.rsm.RSM;
import com.ricedotwho.rsm.command.Command;
import com.ricedotwho.rsm.command.api.CommandInfo;
import net.minecraft.class_637;

@CommandInfo(name = "sa", aliases = "secretaura", description = "Developer")
public class SecretAuraCommand extends Command {
   public LiteralArgumentBuilder<class_637> build() {
      return (LiteralArgumentBuilder<class_637>)((LiteralArgumentBuilder)literal(this.name()).then(literal("c").executes(ctx -> {
         this.clear();
         return 1;
      }))).then(literal("clear").executes(ctx -> {
         this.clear();
         return 1;
      }));
   }

   private void clear() {
      SecretAura s = (SecretAura)RSM.getModule(SecretAura.class);
      if (s != null) {
         s.clear();
         RSA.chat("Blocks cleared!");
      }
   }
}
