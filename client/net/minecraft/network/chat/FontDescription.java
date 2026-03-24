package net.minecraft.network.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.component.ResolvableProfile;

public interface FontDescription {
   Codec<FontDescription> CODEC = Identifier.CODEC.flatComapMap(FontDescription.Resource::new, (var0) -> {
      if (var0 instanceof FontDescription.Resource) {
         FontDescription.Resource var1 = (FontDescription.Resource)var0;
         return DataResult.success(var1.id());
      } else {
         return DataResult.error(() -> {
            return "Unsupported font description type: " + String.valueOf(var0);
         });
      }
   });
   FontDescription.Resource DEFAULT = new FontDescription.Resource(Identifier.withDefaultNamespace("default"));

   public static record Resource(Identifier id) implements FontDescription {
      public Resource(Identifier param1) {
         super();
         this.id = var1;
      }

      public Identifier id() {
         return this.id;
      }
   }

   public static record PlayerSprite(ResolvableProfile profile, boolean hat) implements FontDescription {
      public PlayerSprite(ResolvableProfile param1, boolean param2) {
         super();
         this.profile = var1;
         this.hat = var2;
      }

      public ResolvableProfile profile() {
         return this.profile;
      }

      public boolean hat() {
         return this.hat;
      }
   }

   public static record AtlasSprite(Identifier atlasId, Identifier spriteId) implements FontDescription {
      public AtlasSprite(Identifier param1, Identifier param2) {
         super();
         this.atlasId = var1;
         this.spriteId = var2;
      }

      public Identifier atlasId() {
         return this.atlasId;
      }

      public Identifier spriteId() {
         return this.spriteId;
      }
   }
}
