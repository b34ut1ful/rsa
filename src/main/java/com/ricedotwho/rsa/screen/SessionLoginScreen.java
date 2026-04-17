package com.ricedotwho.rsa.screen;

import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.ricedotwho.rsa.utils.api.SessionAPI;
import com.ricedotwho.rsm.utils.Accessor;
import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.class_2561;
import net.minecraft.class_320;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_7842;

public class SessionLoginScreen extends class_437 implements Accessor {
   private static final Pattern TOKEN_REGEX = Pattern.compile("(?:accessToken:\"|token:)?([A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+)");
   private static SessionLoginScreen instance;
   private static class_320 user;
   private final class_437 parent;
   private class_342 sessionField;
   private String feedBackMessage = "";
   private int feedBackColor = -1;
   private int centerX = 0;
   private int centerY = 0;

   public static SessionLoginScreen getInstance() {
      if (instance == null) {
         instance = new SessionLoginScreen(null);
      }

      return instance;
   }

   private SessionLoginScreen(class_437 parent) {
      super(class_2561.method_43470("SessionLogin"));
      this.parent = parent;
   }

   public void method_25419() {
      mc.method_1507(this.parent);
   }

   protected void method_25426() {
      class_7842 ssidText = new class_7842(class_2561.method_43470("SSID"), mc.field_1772);
      this.centerX = this.field_22789 / 2 - 50;
      this.centerY = 60;
      this.sessionField = new class_342(mc.field_1772, 100, 20, user == null ? class_2561.method_43473() : class_2561.method_43470(user.method_1675()));
      ssidText.method_25358(100);
      ssidText.method_48229(this.centerX, this.centerY + 35);
      this.sessionField.method_48229(this.centerX, this.centerY + 45);
      this.sessionField.method_1880(10000);
      this.method_37063(ssidText);
      this.method_37063(this.sessionField);
      this.method_37063(
         class_4185.method_46430(class_2561.method_43470("Login"), button -> this.login())
            .method_46432(100)
            .method_46433(this.centerX, this.centerY + 70)
            .method_46431()
      );
      this.method_37063(
         class_4185.method_46430(class_2561.method_43470("Reset"), button -> reset())
            .method_46432(100)
            .method_46433(this.centerX, this.centerY + 95)
            .method_46431()
      );
      this.method_37063(
         class_4185.method_46430(class_2561.method_43470("Copy SSID"), button -> copySSID())
            .method_46432(100)
            .method_46433(this.centerX, this.centerY + 120)
            .method_46431()
      );
      this.method_37063(
         class_4185.method_46430(class_2561.method_43470("Back"), button -> this.method_25419())
            .method_46432(100)
            .method_46433(this.centerX, this.centerY + 145)
            .method_46431()
      );
   }

   public void method_25394(class_332 gfx, int mouseX, int mouseY, float deltaTicks) {
      gfx.method_51433(
         mc.field_1772,
         this.feedBackMessage,
         this.centerX + 50 - (mc.field_1772.method_1727(this.feedBackMessage) >> 1),
         this.centerY,
         this.feedBackColor,
         true
      );
      String currentUser = "Current Account: " + mc.method_1548().method_1676();
      gfx.method_51433(mc.field_1772, currentUser, this.centerX + 50 - (mc.field_1772.method_1727(currentUser) >> 1), this.centerY + 10, -1, true);
      super.method_25394(gfx, mouseX, mouseY, deltaTicks);
   }

   private void login() {
      if (this.sessionField.method_1882().isBlank()) {
         this.feedBackMessage = "Please enter an SSID!";
         this.feedBackColor = -7405568;
      } else {
         String ssidText = this.parseToken(this.sessionField.method_1882().trim());
         String[] info = null;
         int i = 0;

         while (i < 10) {
            try {
               info = SessionAPI.getProfileInfo(ssidText);
               break;
            } catch (MalformedJsonException | JsonSyntaxException var6) {
               this.feedBackMessage = "Ran out of retries, network error!";
               this.feedBackColor = -7405568;
               System.err.println("Failed to parse json! Retries left: " + i);
               i++;
            } catch (IOException var7) {
               this.feedBackMessage = "Failed to poll API for username and UUID!";
               this.feedBackColor = -7405568;
               return;
            } catch (Exception var8) {
               this.feedBackMessage = "Invalid SSID!";
               var8.printStackTrace();
               this.feedBackColor = -7405568;
               return;
            }
         }

         if (info != null) {
            try {
               user = new class_320(info[0], SessionAPI.undashedToUUID(info[1]), ssidText, Optional.empty(), Optional.empty());
            } catch (Exception var5) {
               this.feedBackMessage = "Failed to parse UUID from string!";
               this.feedBackColor = -7405568;
               return;
            }

            this.feedBackMessage = "Successfully updated session!";
            this.feedBackColor = -16739323;
         }
      }
   }

   private String parseToken(String input) {
      if (input != null && !input.isEmpty()) {
         Matcher matcher = TOKEN_REGEX.matcher(input);
         return matcher.find() ? matcher.group(1) : "";
      } else {
         return "";
      }
   }

   public static void reset() {
      user = null;
   }

   public static void copySSID() {
      mc.field_1774.method_1455(mc.method_1548().method_1674());
   }

   public static class_320 getUser() {
      return user;
   }
}
