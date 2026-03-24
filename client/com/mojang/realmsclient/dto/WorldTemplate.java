package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.util.JsonUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public record WorldTemplate(String id, String name, String version, String author, String link, @Nullable String image, String trailer, String recommendedPlayers, WorldTemplate.WorldTemplateType type) {
   private static final Logger LOGGER = LogUtils.getLogger();

   public WorldTemplate(String param1, String param2, String param3, String param4, String param5, @Nullable String param6, String param7, String param8, WorldTemplate.WorldTemplateType param9) {
      super();
      this.id = var1;
      this.name = var2;
      this.version = var3;
      this.author = var4;
      this.link = var5;
      this.image = var6;
      this.trailer = var7;
      this.recommendedPlayers = var8;
      this.type = var9;
   }

   @Nullable
   public static WorldTemplate parse(JsonObject var0) {
      try {
         String var1 = JsonUtils.getStringOr("type", var0, (String)null);
         return new WorldTemplate(JsonUtils.getStringOr("id", var0, ""), JsonUtils.getStringOr("name", var0, ""), JsonUtils.getStringOr("version", var0, ""), JsonUtils.getStringOr("author", var0, ""), JsonUtils.getStringOr("link", var0, ""), JsonUtils.getStringOr("image", var0, (String)null), JsonUtils.getStringOr("trailer", var0, ""), JsonUtils.getStringOr("recommendedPlayers", var0, ""), var1 == null ? WorldTemplate.WorldTemplateType.WORLD_TEMPLATE : WorldTemplate.WorldTemplateType.valueOf(var1));
      } catch (Exception var2) {
         LOGGER.error("Could not parse WorldTemplate", var2);
         return null;
      }
   }

   public String id() {
      return this.id;
   }

   public String name() {
      return this.name;
   }

   public String version() {
      return this.version;
   }

   public String author() {
      return this.author;
   }

   public String link() {
      return this.link;
   }

   @Nullable
   public String image() {
      return this.image;
   }

   public String trailer() {
      return this.trailer;
   }

   public String recommendedPlayers() {
      return this.recommendedPlayers;
   }

   public WorldTemplate.WorldTemplateType type() {
      return this.type;
   }

   public static enum WorldTemplateType {
      WORLD_TEMPLATE,
      MINIGAME,
      ADVENTUREMAP,
      EXPERIENCE,
      INSPIRATION;

      private WorldTemplateType() {
      }

      // $FF: synthetic method
      private static WorldTemplate.WorldTemplateType[] $values() {
         return new WorldTemplate.WorldTemplateType[]{WORLD_TEMPLATE, MINIGAME, ADVENTUREMAP, EXPERIENCE, INSPIRATION};
      }
   }
}
