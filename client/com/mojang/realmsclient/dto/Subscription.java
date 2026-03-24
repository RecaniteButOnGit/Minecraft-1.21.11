package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import java.time.Instant;
import net.minecraft.util.LenientJsonParser;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public record Subscription(Instant startDate, int daysLeft, Subscription.SubscriptionType type) {
   private static final Logger LOGGER = LogUtils.getLogger();

   public Subscription(Instant param1, int param2, Subscription.SubscriptionType param3) {
      super();
      this.startDate = var1;
      this.daysLeft = var2;
      this.type = var3;
   }

   public static Subscription parse(String var0) {
      try {
         JsonObject var1 = LenientJsonParser.parse(var0).getAsJsonObject();
         return new Subscription(JsonUtils.getDateOr("startDate", var1), JsonUtils.getIntOr("daysLeft", var1, 0), typeFrom(JsonUtils.getStringOr("subscriptionType", var1, (String)null)));
      } catch (Exception var2) {
         LOGGER.error("Could not parse Subscription", var2);
         return new Subscription(Instant.EPOCH, 0, Subscription.SubscriptionType.NORMAL);
      }
   }

   private static Subscription.SubscriptionType typeFrom(@Nullable String var0) {
      try {
         if (var0 != null) {
            return Subscription.SubscriptionType.valueOf(var0);
         }
      } catch (Exception var2) {
      }

      return Subscription.SubscriptionType.NORMAL;
   }

   public Instant startDate() {
      return this.startDate;
   }

   public int daysLeft() {
      return this.daysLeft;
   }

   public Subscription.SubscriptionType type() {
      return this.type;
   }

   public static enum SubscriptionType {
      NORMAL,
      RECURRING;

      private SubscriptionType() {
      }

      // $FF: synthetic method
      private static Subscription.SubscriptionType[] $values() {
         return new Subscription.SubscriptionType[]{NORMAL, RECURRING};
      }
   }
}
