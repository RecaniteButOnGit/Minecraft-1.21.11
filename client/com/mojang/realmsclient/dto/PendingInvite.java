package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import java.time.Instant;
import java.util.UUID;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public record PendingInvite(String invitationId, String realmName, String realmOwnerName, UUID realmOwnerUuid, Instant date) {
   private static final Logger LOGGER = LogUtils.getLogger();

   public PendingInvite(String param1, String param2, String param3, UUID param4, Instant param5) {
      super();
      this.invitationId = var1;
      this.realmName = var2;
      this.realmOwnerName = var3;
      this.realmOwnerUuid = var4;
      this.date = var5;
   }

   @Nullable
   public static PendingInvite parse(JsonObject var0) {
      try {
         return new PendingInvite(JsonUtils.getStringOr("invitationId", var0, ""), JsonUtils.getStringOr("worldName", var0, ""), JsonUtils.getStringOr("worldOwnerName", var0, ""), JsonUtils.getUuidOr("worldOwnerUuid", var0, Util.NIL_UUID), JsonUtils.getDateOr("date", var0));
      } catch (Exception var2) {
         LOGGER.error("Could not parse PendingInvite", var2);
         return null;
      }
   }

   public String invitationId() {
      return this.invitationId;
   }

   public String realmName() {
      return this.realmName;
   }

   public String realmOwnerName() {
      return this.realmOwnerName;
   }

   public UUID realmOwnerUuid() {
      return this.realmOwnerUuid;
   }

   public Instant date() {
      return this.date;
   }
}
