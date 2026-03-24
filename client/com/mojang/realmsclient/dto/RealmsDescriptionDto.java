package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import org.jspecify.annotations.Nullable;

public record RealmsDescriptionDto(@Nullable String name, String description) implements ReflectionBasedSerialization {
   public RealmsDescriptionDto(@Nullable String param1, String param2) {
      super();
      this.name = var1;
      this.description = var2;
   }

   @SerializedName("name")
   @Nullable
   public String name() {
      return this.name;
   }

   @SerializedName("description")
   public String description() {
      return this.description;
   }
}
