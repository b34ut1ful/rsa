package com.ricedotwho.rsa.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.ricedotwho.rsa.RSA;
import com.ricedotwho.rsa.component.impl.pathfinding.GoalDungeonRoom;
import com.ricedotwho.rsa.component.impl.pathfinding.GoalDungeonXYZ;
import com.ricedotwho.rsa.component.impl.pathfinding.GoalXYZ;
import com.ricedotwho.rsa.module.impl.dungeon.DynamicRoutes;
import com.ricedotwho.rsa.module.impl.dungeon.autoroutes.NodeType;
import com.ricedotwho.rsm.RSM;
import com.ricedotwho.rsm.command.Command;
import com.ricedotwho.rsm.command.api.CommandInfo;
import com.ricedotwho.rsm.component.impl.map.handler.DungeonInfo;
import com.ricedotwho.rsm.component.impl.map.map.UniqueRoom;
import com.ricedotwho.rsm.utils.EtherUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2262;
import net.minecraft.class_2280;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_637;

@CommandInfo(name = "dynamicroute", aliases = "dr", description = "Handles creating dynamic routes.")
public class DynamicRouteCommand extends Command {
   public LiteralArgumentBuilder<class_637> build() {
      return (LiteralArgumentBuilder<class_637>)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)literal(
                                    this.name()
                                 )
                                 .then(literal("add").executes(DynamicRouteCommand::addNode)))
                              .then(literal("clear").executes(DynamicRouteCommand::clearNodes)))
                           .then(literal("stop").executes(DynamicRouteCommand::stopPathing)))
                        .then(
                           literal("path")
                              .then(argument("pos", class_2262.method_9698()).executes(ctx -> path(ctx, (class_2280)ctx.getArgument("pos", class_2280.class))))
                        ))
                     .then(
                        literal("roompath")
                           .then(
                              argument("room", StringArgumentType.greedyString())
                                 .executes(ctx -> dungeonRoomPath(ctx, (String)ctx.getArgument("room", String.class)))
                           )
                     ))
                  .then(
                     literal("insta")
                        .then(
                           argument("room1", StringArgumentType.string())
                              .then(
                                 argument("room2", StringArgumentType.string())
                                    .then(
                                       argument("room3", StringArgumentType.string())
                                          .executes(
                                             ctx -> insta(
                                                ctx,
                                                (String)ctx.getArgument("room1", String.class),
                                                (String)ctx.getArgument("room2", String.class),
                                                (String)ctx.getArgument("room3", String.class)
                                             )
                                          )
                                    )
                              )
                        )
                  ))
               .then(
                  literal("roomfind")
                     .then(argument("pos", class_2262.method_9698()).executes(ctx -> dungeonPath(ctx, (class_2280)ctx.getArgument("pos", class_2280.class))))
               ))
            .then(literal("cp").executes(DynamicRouteCommand::copyBlockPosLook)))
         .then(literal("remove").executes(DynamicRouteCommand::removeNode));
   }

   private static int stopPathing(CommandContext<class_637> ctx) {
      boolean cancelled = ((DynamicRoutes)RSM.getModule(DynamicRoutes.class)).cancelPathing();
      if (cancelled) {
         RSA.chat("Cancelled pathing!");
         return 1;
      } else {
         RSA.chat("No pathing active!");
         return 0;
      }
   }

   private static int copyBlockPosLook(CommandContext<class_637> ctx) {
      class_310 client = class_310.method_1551();
      class_243 cameraPos = client.field_1773.method_19418().method_19326();
      float yaw = client.field_1773.method_19418().method_19330();
      float pitch = client.field_1773.method_19418().method_19329();
      class_243 hitPos = EtherUtils.rayTraceBlock(61, yaw, pitch, cameraPos);
      class_243 viewVector = hitPos.method_1020(cameraPos).method_1029();
      class_243 nudgedHitPos = viewVector.method_1021(0.001F).method_1019(hitPos);
      class_2338 blockPos = class_2338.method_49638(nudgedHitPos);
      String clipboardText = blockPos.method_10263() + " " + blockPos.method_10264() + " " + blockPos.method_10260();
      client.field_1774.method_1455(clipboardText);
      RSA.chat("Copied " + clipboardText);
      return 1;
   }

   private static class_2338 getPlayerStartPos(class_310 client) {
      return class_2338.method_49638(client.field_1724.method_73189().method_1023(0.0, 0.001F, 0.0));
   }

   private static int path(CommandContext<class_637> ctx, class_2280 pos) {
      class_310 client = class_310.method_1551();
      if (client.field_1724 == null) {
         return 0;
      } else {
         class_2338 blockPos = class_2338.method_49637(pos.comp_4926().comp_4925(), pos.comp_4927().comp_4925(), pos.comp_4928().comp_4925());
         class_2338 startPos = getPlayerStartPos(client);
         ((DynamicRoutes)RSM.getModule(DynamicRoutes.class)).executePath(startPos, new GoalXYZ(blockPos));
         return 1;
      }
   }

   private static int insta(CommandContext<class_637> ctx, String... roomNames) {
      class_310 client = class_310.method_1551();
      if (client.field_1724 == null) {
         return 0;
      } else {
         class_2338 startPos = getPlayerStartPos(client);
         List<GoalDungeonRoom> goals = new ArrayList<>();

         for (String roomName : roomNames) {
            UniqueRoom uniqueRoom = DungeonInfo.getRoomByName(roomName);
            if (uniqueRoom == null || uniqueRoom.getTiles().isEmpty()) {
               RSA.chat("Room not loaded!");
            }

            GoalDungeonRoom goal = GoalDungeonRoom.create(uniqueRoom);
            if (goal == null) {
               RSA.chat("Failed to create goal!");
               return 0;
            }

            goals.add(goal);
         }

         ((DynamicRoutes)RSM.getModule(DynamicRoutes.class)).pathGoals(startPos, goals);
         return 1;
      }
   }

   private static int dungeonRoomPath(CommandContext<class_637> ctx, String uniqueRoomName) {
      class_310 client = class_310.method_1551();
      if (client.field_1724 == null) {
         return 0;
      } else {
         UniqueRoom uniqueRoom = DungeonInfo.getRoomByName(uniqueRoomName);
         if (uniqueRoom == null || uniqueRoom.getTiles().isEmpty()) {
            RSA.chat("Room not loaded!");
         }

         class_2338 startPos = getPlayerStartPos(client);
         GoalDungeonRoom goal = GoalDungeonRoom.create(uniqueRoom);
         if (goal == null) {
            RSA.chat("Failed to create goal!");
            return 0;
         } else {
            ((DynamicRoutes)RSM.getModule(DynamicRoutes.class)).executePath(startPos, goal);
            return 1;
         }
      }
   }

   private static int dungeonPath(CommandContext<class_637> ctx, class_2280 pos) {
      class_310 client = class_310.method_1551();
      if (client.field_1724 == null) {
         return 0;
      } else {
         class_2338 blockPos = class_2338.method_49637(pos.comp_4926().comp_4925(), pos.comp_4927().comp_4925(), pos.comp_4928().comp_4925());
         class_2338 startPos = getPlayerStartPos(client);
         GoalDungeonXYZ goal = GoalDungeonXYZ.create(blockPos);
         if (goal == null) {
            RSA.chat("Failed to create goal!");
            return 0;
         } else {
            ((DynamicRoutes)RSM.getModule(DynamicRoutes.class)).executePath(startPos, goal);
            return 1;
         }
      }
   }

   private static int clearNodes(CommandContext<class_637> ctx) {
      if (!((DynamicRoutes)RSM.getModule(DynamicRoutes.class)).clearNodes()) {
         RSA.chat("No nodes found!");
         return 0;
      } else {
         RSA.chat("Cleared all nodes!");
         return 1;
      }
   }

   private static int removeNode(CommandContext<class_637> ctx) {
      if (!((DynamicRoutes)RSM.getModule(DynamicRoutes.class)).removeNearest()) {
         RSA.chat("No nodes found in this room!");
         return 0;
      } else {
         RSA.chat("Removed node!");
         return 1;
      }
   }

   private static int addNode(CommandContext<class_637> ctx) {
      class_310 client = class_310.method_1551();
      if (client.field_1724 == null) {
         return 0;
      } else {
         boolean added = ((DynamicRoutes)RSM.getModule(DynamicRoutes.class)).addNode(client.field_1724);
         if (!added) {
            RSA.chat("Failed to raytrace etherwarp!");
            return 0;
         } else {
            RSA.chat("Added " + NodeType.ETHERWARP + " node!");
            return 1;
         }
      }
   }
}
