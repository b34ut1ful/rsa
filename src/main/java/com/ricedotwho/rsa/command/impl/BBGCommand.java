package com.ricedotwho.rsa.command.impl;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.AutoP3;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.CenterType;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.RingType;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.args.ArgumentManager;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.args.RingArgType;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.recorder.MovementRecorder;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.rings.Ring;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.subactions.SubActionManager;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.subactions.SubActionType;
import com.ricedotwho.rsm.RSM;
import com.ricedotwho.rsm.command.Command;
import com.ricedotwho.rsm.command.api.CommandInfo;
import com.ricedotwho.rsm.data.Pos;
import com.ricedotwho.rsm.utils.ChatUtils;
import com.ricedotwho.rsm.utils.NumberUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.class_2172;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_637;
import net.minecraft.class_239.class_240;
import org.apache.commons.lang3.EnumUtils;

@CommandInfo(name = "bbg", aliases = "p3", description = "Auto P3 command")
public class BBGCommand extends Command {
   private final Pattern argPattern = Pattern.compile("^(\\w+?)(?:(\\d*\\.?\\d*)|\"([^\"]*)\")$");
   private final Pattern splitter = Pattern.compile("\\w+(?:\\d+(?:\\.\\d+)?|\"[^\"]*\")?");

   public LiteralArgumentBuilder<class_637> build() {
      return (LiteralArgumentBuilder<class_637>)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)literal(
                                 this.name()
                              )
                              .then(
                                 ((LiteralArgumentBuilder)literal("center")
                                       .then(argument("centerType", BBGCommand.CenterArgumentType.centerArgument()).executes(this::center)))
                                    .executes(r -> this.center(CenterType.ALL))
                              ))
                           .then(literal("undo").executes(this::undo)))
                        .then(literal("redo").executes(this::redo)))
                     .then(
                        ((LiteralArgumentBuilder)literal("remove")
                              .then(
                                 argument("index", IntegerArgumentType.integer())
                                    .executes(ctx -> this.removeRing(ctx, IntegerArgumentType.getInteger(ctx, "index")))
                              ))
                           .executes(this::removeRing)
                     ))
                  .then(
                     literal("add")
                        .then(
                           ((RequiredArgumentBuilder)argument("ring", BBGCommand.RingArgumentType.ringArgument()).executes(ctx -> this.addRing(ctx, "")))
                              .then(argument("args", StringArgumentType.greedyString()).executes(ctx -> {
                                 String args = StringArgumentType.getString(ctx, "args");
                                 return this.addRing(ctx, args);
                              }))
                        )
                  ))
               .then(literal("play").then(argument("route", StringArgumentType.greedyString()).executes(ctx -> {
                  String route = StringArgumentType.getString(ctx, "route");
                  MovementRecorder.playRecording(route);
                  AutoP3.modMessage("Playing %s!", route);
                  return 1;
               }))))
            .then(literal("load").then(argument("config", StringArgumentType.greedyString()).executes(ctx -> {
               String config = StringArgumentType.getString(ctx, "config");
               AutoP3.load(config);
               AutoP3.modMessage("Loaded %s", config);
               return 1;
            }))))
         .then(
            literal("help")
               .executes(
                  ctx -> {
                     StringBuilder sb = new StringBuilder();
                     sb.append("Help:").append("\nRing Types:\n");

                     for (RingType type : RingType.values()) {
                        sb.append(type.getName()).append(" ");
                     }

                     sb.append("\nGeneral Arguments:\n");
                     sb.append(
                        "w<number>, h<number>, l<number>, r<number>, exact, blink<number>, yaw<number>, pitch<number> (movement/blink: route\"route\", command: command\"command\", chat: message\"message\")"
                     );
                     sb.append("\n\nArguments: ");

                     for (RingArgType type : RingArgType.values()) {
                        sb.append(type.getAliases().getFirst()).append(" ");
                     }

                     sb.append("\n\nSub Actions: ");

                     for (SubActionType type : SubActionType.values()) {
                        sb.append(type.name().toLowerCase()).append(" ");
                     }

                     AutoP3.modMessage(sb.toString());
                     return 1;
                  }
               )
         );
   }

   private int addRing(CommandContext<class_637> ctx, String args) {
      if (class_310.method_1551().field_1724 == null) {
         return 0;
      } else {
         RingType type = BBGCommand.RingArgumentType.getRing(ctx, "ring");
         Ring ring = this.createRing(type, args);
         if (ring == null) {
            return 0;
         } else {
            ((AutoP3)RSM.getModule(AutoP3.class)).addRing(ring);
            return 1;
         }
      }
   }

   private Ring createRing(RingType type, String full) {
      Pos whl = new Pos(0.5, 1.0, 0.5);
      boolean exact = false;
      ArgumentManager manager = new ArgumentManager();
      SubActionManager subActions = new SubActionManager();
      Map<String, Object> dataMap = new HashMap<>();
      Matcher split = this.splitter.matcher(full);

      while (split.find()) {
         String arg = split.group();
         Matcher matcher = this.argPattern.matcher(arg);
         if (matcher.find()) {
            String key = matcher.group(1);
            Double value = NumberUtils.isDouble(matcher.group(2)) ? Double.parseDouble(matcher.group(2)) : null;
            String stringValue = matcher.group(2) == null ? matcher.group(3) : matcher.group(2);
            String normalizedKey = key.toLowerCase();
            switch (normalizedKey) {
               case "r":
               case "radius":
                  if (value != null) {
                     whl.set(value, value, value);
                  }
                  continue;
               case "exact":
                  exact = true;
                  continue;
               case "w":
               case "width":
                  if (value != null) {
                     whl.x(value);
                  }
                  continue;
               case "h":
               case "height":
                  if (value != null) {
                     whl.y(value);
                  }
                  continue;
               case "l":
               case "length":
                  if (value != null) {
                     whl.z(value);
                  }
                  continue;
               case "y":
               case "yaw":
                  if (value != null) {
                     dataMap.put("yaw", value);
                  }
                  break;
               case "p":
               case "pitch":
                  if (value != null) {
                     dataMap.put("pitch", value);
                  }
                  break;
               case "route":
                  if (stringValue != null) {
                     dataMap.put("route", stringValue);
                  }
                  break;
               case "m":
               case "message":
                  if (stringValue != null) {
                     dataMap.put("message", stringValue);
                  }
                  break;
               case "c":
               case "command":
                  if (stringValue != null) {
                     dataMap.put("command", stringValue);
                  }
                  break;
               case "b":
               case "blink":
                  if (stringValue != null && value != null) {
                     dataMap.put("blink", value.intValue());
                  }
            }

            RingArgType ringArg = RingArgType.fromAliases(key.toLowerCase());
            if (ringArg != null) {
               manager.addArg(ringArg.create(stringValue));
            } else {
               SubActionType s = (SubActionType)EnumUtils.getEnum(SubActionType.class, key.toUpperCase());
               if (s != null) {
                  subActions.addAction(s.create());
               }
            }
         }
      }

      for (String s : type.getRequired()) {
         if (!dataMap.containsKey(s)) {
            AutoP3.modMessage("Failed to place ring! %s required the argument %s!", type.getName(), s);
            return null;
         }
      }

      if (type.getHitResult() != null && mc.field_1765 != null && mc.field_1765.method_17783() != type.getHitResult()) {
         AutoP3.modMessage(
            "Failed to place ring! %s requires you to look at %s!", type.getName(), type.getHitResult() == class_240.field_1332 ? "block" : "entity"
         );
         return null;
      } else {
         Pos playerPos = this.getPlayerPos(exact);
         return type.supply(playerPos.subtract(whl.x(), 0.0, whl.z()), playerPos.add(whl), manager, subActions, dataMap);
      }
   }

   private Pos getPlayerPos(boolean exact) {
      class_243 pos = mc.field_1724.method_73189();
      return exact
         ? new Pos(pos)
         : new Pos(Math.round(pos.method_10216() * 2.0) / 2.0, Math.round(pos.method_10214() * 2.0) / 2.0, Math.round(pos.method_10215() * 2.0) / 2.0);
   }

   private int undo(CommandContext<class_637> ctx) {
      ((AutoP3)RSM.getModule(AutoP3.class)).undo();
      return 1;
   }

   private int redo(CommandContext<class_637> ctx) {
      ((AutoP3)RSM.getModule(AutoP3.class)).redo();
      return 1;
   }

   private int removeRing(CommandContext<class_637> ctx, int index) {
      if (class_310.method_1551().field_1724 == null) {
         return 0;
      } else if (((AutoP3)RSM.getModule(AutoP3.class)).removeIndexed(index)) {
         ChatUtils.chat("Removed ring at index " + index, new Object[0]);
         return 1;
      } else {
         ChatUtils.chat("Could not find ring at index " + index, new Object[0]);
         return 0;
      }
   }

   private int removeRing(CommandContext<class_637> ctx) {
      if (class_310.method_1551().field_1724 == null) {
         return 0;
      } else {
         class_243 position = class_310.method_1551().field_1724.method_73189();
         ((AutoP3)RSM.getModule(AutoP3.class)).removeNearest(position);
         return 1;
      }
   }

   private int center(CenterType centerType) {
      if (centerType == null) {
         return 0;
      } else {
         switch (centerType) {
            case ALL:
               this.centerYaw();
               this.centerPitch();
               this.centerPos();
               break;
            case POS:
               this.centerPos();
               break;
            case ANGLES:
               this.centerYaw();
               this.centerPitch();
               break;
            case YAW:
               this.centerYaw();
               break;
            case PITCH:
               this.centerPitch();
         }

         return 1;
      }
   }

   private int center(CommandContext<class_637> ctx) {
      CenterType centerType = BBGCommand.CenterArgumentType.getType(ctx, "centerType");
      return this.center(centerType);
   }

   private void centerYaw() {
      if (class_310.method_1551().field_1724 != null) {
         class_310.method_1551().field_1724.method_36456(Math.round(mc.field_1724.method_36454() / 45.0F) * 45.0F);
      }
   }

   private void centerPitch() {
      if (class_310.method_1551().field_1724 != null) {
         class_310.method_1551().field_1724.method_36457(0.0F);
      }
   }

   private void centerPos() {
      if (class_310.method_1551().field_1724 != null && class_310.method_1551().method_47392()) {
         class_243 position = class_310.method_1551().field_1724.method_73189();
         class_243 target = new class_243(
            class_3532.method_15357(position.field_1352) + 0.5, position.field_1351, class_3532.method_15357(position.field_1350) + 0.5
         );
         class_310.method_1551().field_1724.method_33574(target);
      }
   }

   private static class CenterArgumentType implements ArgumentType<CenterType> {
      private static final Collection<String> EXAMPLES = Stream.of(CenterType.POS, CenterType.ANGLES).map(CenterType::getName).collect(Collectors.toList());
      private static final CenterType[] VALUES = CenterType.values();
      private static final DynamicCommandExceptionType INVALID_CENTER_EXCEPTION = new DynamicCommandExceptionType(
         ring -> class_2561.method_43470("Invalid center type : " + ring)
      );

      public CenterType parse(StringReader stringReader) throws CommandSyntaxException {
         String string = stringReader.readUnquotedString();
         CenterType ring = CenterType.fromName(string);
         if (ring == null) {
            throw INVALID_CENTER_EXCEPTION.createWithContext(stringReader, string);
         } else {
            return ring;
         }
      }

      public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
         return context.getSource() instanceof class_2172
            ? class_2172.method_9264(Arrays.stream(VALUES).map(CenterType::getName), builder)
            : Suggestions.empty();
      }

      public Collection<String> getExamples() {
         return EXAMPLES;
      }

      public static BBGCommand.CenterArgumentType centerArgument() {
         return new BBGCommand.CenterArgumentType();
      }

      public static CenterType getType(CommandContext<class_637> context, String name) {
         return (CenterType)context.getArgument(name, CenterType.class);
      }
   }

   private static class RingArgumentType implements ArgumentType<RingType> {
      private static final Collection<String> EXAMPLES = Stream.of(RingType.ALIGN, RingType.WALK).map(RingType::getName).collect(Collectors.toList());
      private static final RingType[] VALUES = RingType.values();
      private static final DynamicCommandExceptionType INVALID_RING_EXCEPTION = new DynamicCommandExceptionType(
         ring -> class_2561.method_43470("Invalid ring type : " + ring)
      );

      public RingType parse(StringReader stringReader) throws CommandSyntaxException {
         String string = stringReader.readUnquotedString();
         RingType ring = RingType.byName(string);
         if (ring == null) {
            throw INVALID_RING_EXCEPTION.createWithContext(stringReader, string);
         } else {
            return ring;
         }
      }

      public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
         return context.getSource() instanceof class_2172 ? class_2172.method_9264(Arrays.stream(VALUES).map(RingType::getName), builder) : Suggestions.empty();
      }

      public Collection<String> getExamples() {
         return EXAMPLES;
      }

      public static BBGCommand.RingArgumentType ringArgument() {
         return new BBGCommand.RingArgumentType();
      }

      public static RingType getRing(CommandContext<class_637> context, String name) {
         return (RingType)context.getArgument(name, RingType.class);
      }
   }
}
