package com.ricedotwho.rsa;

import com.ricedotwho.rsa.command.impl.AutoCroesusCommand;
import com.ricedotwho.rsa.command.impl.BBGCommand;
import com.ricedotwho.rsa.command.impl.BloodBlinkCommand;
import com.ricedotwho.rsa.command.impl.DynamicRouteCommand;
import com.ricedotwho.rsa.command.impl.RSADevCommand;
import com.ricedotwho.rsa.command.impl.RouteCommand;
import com.ricedotwho.rsa.command.impl.SecretAuraCommand;
import com.ricedotwho.rsa.command.impl.VelocityBufferCommand;
import com.ricedotwho.rsa.component.impl.Edge;
import com.ricedotwho.rsa.component.impl.Jump;
import com.ricedotwho.rsa.module.impl.dungeon.AutoUlt;
import com.ricedotwho.rsa.module.impl.dungeon.BloodBlink;
import com.ricedotwho.rsa.module.impl.dungeon.DungeonBreaker;
import com.ricedotwho.rsa.module.impl.dungeon.DynamicRoutes;
import com.ricedotwho.rsa.module.impl.dungeon.FastLeap;
import com.ricedotwho.rsa.module.impl.dungeon.SecretAura;
import com.ricedotwho.rsa.module.impl.dungeon.SecretHitboxes;
import com.ricedotwho.rsa.module.impl.dungeon.autoroutes.AutoRoutes;
import com.ricedotwho.rsa.module.impl.dungeon.boss.Blink;
import com.ricedotwho.rsa.module.impl.dungeon.boss.BreakerAura;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p2.PadTimer;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.TermAura;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.autop3.AutoP3;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.terminals.auto.AutoTerms;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p3.terminals.solver.TerminalSolver;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p4.InstaMid;
import com.ricedotwho.rsa.module.impl.dungeon.boss.p5.Relics;
import com.ricedotwho.rsa.module.impl.dungeon.croesus.AutoCroesus;
import com.ricedotwho.rsa.module.impl.dungeon.device.AlignAura;
import com.ricedotwho.rsa.module.impl.dungeon.device.Auto4;
import com.ricedotwho.rsa.module.impl.dungeon.device.AutoSS;
import com.ricedotwho.rsa.module.impl.dungeon.puzzle.Puzzles;
import com.ricedotwho.rsa.module.impl.movement.VelocityBuffer;
import com.ricedotwho.rsa.module.impl.other.AntiCheat;
import com.ricedotwho.rsa.module.impl.other.AutoGfs;
import com.ricedotwho.rsa.module.impl.other.AutoJax;
import com.ricedotwho.rsa.module.impl.other.CustomKeybinds;
import com.ricedotwho.rsa.module.impl.other.DevUtils;
import com.ricedotwho.rsa.module.impl.player.BonzoHelper;
import com.ricedotwho.rsa.module.impl.player.CancelInteract;
import com.ricedotwho.rsa.module.impl.render.EffectsAndRender;
import com.ricedotwho.rsa.module.impl.render.Esp;
import com.ricedotwho.rsa.module.impl.render.Freecam;
import com.ricedotwho.rsa.module.impl.render.HidePlayers;
import com.ricedotwho.rsa.module.impl.render.PresetWaypoints;
import com.ricedotwho.rsa.packet.sb.BloodClipHelperStartPacket;
import com.ricedotwho.rsa.packet.sb.BloodClipHelperStopPacket;
import com.ricedotwho.rsa.utils.render3d.type.Ring;
import com.ricedotwho.rsm.addon.Addon;
import com.ricedotwho.rsm.command.Command;
import com.ricedotwho.rsm.component.api.ModComponent;
import com.ricedotwho.rsm.component.impl.Renderer3D;
import com.ricedotwho.rsm.module.Module;
import com.ricedotwho.rsm.utils.ChatUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_5250;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RSA implements Addon {
   private static final Logger logger = LogManager.getLogger("rsa");
   public static Path SOUNDS_FOLDER;
   private static final class_5250 prefix = class_2561.method_43473()
      .method_10852(class_2561.method_43470("[").method_27692(class_124.field_1063))
      .method_10852(class_2561.method_43470("R").method_54663(11690975))
      .method_10852(class_2561.method_43470("S").method_54663(12942314))
      .method_10852(class_2561.method_43470("A").method_54663(14128116))
      .method_10852(class_2561.method_43470("] ").method_27692(class_124.field_1063));

   public void onInitialize() {
      PayloadTypeRegistry.playC2S().register(BloodClipHelperStartPacket.TYPE, BloodClipHelperStartPacket.CODEC);
      PayloadTypeRegistry.playC2S().register(BloodClipHelperStopPacket.TYPE, BloodClipHelperStopPacket.CODEC);
      EffectsAndRender.init();
      Renderer3D.registerLine(Ring.class);
      SOUNDS_FOLDER = FabricLoader.getInstance().getConfigDir().resolve("rsm").resolve("sounds");

      try {
         Files.createDirectories(SOUNDS_FOLDER);
      } catch (Exception var2) {
         var2.printStackTrace();
      }
   }

   public void onUnload() {
   }

   public List<Class<? extends Module>> getModules() {
      return List.of(
         DungeonBreaker.class,
         AutoRoutes.class,
         DynamicRoutes.class,
         AutoJax.class,
         PadTimer.class,
         BloodBlink.class,
         AutoSS.class,
         SecretAura.class,
         EffectsAndRender.class,
         PresetWaypoints.class,
         CustomKeybinds.class,
         AutoGfs.class,
         AutoTerms.class,
         InstaMid.class,
         SecretHitboxes.class,
         CancelInteract.class,
         DevUtils.class,
         CancelInteract.class,
         TermAura.class,
         Esp.class,
         AlignAura.class,
         FastLeap.class,
         AntiCheat.class,
         Puzzles.class,
         HidePlayers.class,
         Auto4.class,
         BonzoHelper.class,
         AutoCroesus.class,
         AutoP3.class,
         Freecam.class,
         AutoUlt.class,
         TerminalSolver.class,
         Relics.class,
         VelocityBuffer.class,
         BreakerAura.class,
         Blink.class
      );
   }

   public List<Class<? extends ModComponent>> getComponents() {
      return List.of(Edge.class, Jump.class);
   }

   public List<Class<? extends Command>> getCommands() {
      return List.of(
         RouteCommand.class,
         DynamicRouteCommand.class,
         BloodBlinkCommand.class,
         BBGCommand.class,
         RSADevCommand.class,
         AutoCroesusCommand.class,
         SecretAuraCommand.class,
         VelocityBufferCommand.class
      );
   }

   public static void chat(Object message, Object... objects) {
      ChatUtils.chatClean(getPrefix().method_27661().method_27693(String.format(message.toString(), objects)));
   }

   public static void chat(String text) {
      ChatUtils.chatClean(getPrefix().method_27661().method_27693(text));
   }

   public static void chat(class_2561 component) {
      ChatUtils.chatClean(getPrefix().method_27661().method_10852(component));
   }

   public static Logger getLogger() {
      return logger;
   }

   public static class_5250 getPrefix() {
      return prefix;
   }
}
