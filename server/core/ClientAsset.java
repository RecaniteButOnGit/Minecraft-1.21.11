package net.minecraft.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public interface ClientAsset {
   Identifier id();

   public static record DownloadedTexture(Identifier texturePath, String url) implements ClientAsset.Texture {
      public DownloadedTexture(Identifier param1, String param2) {
         super();
         this.texturePath = var1;
         this.url = var2;
      }

      public Identifier id() {
         return this.texturePath;
      }

      public Identifier texturePath() {
         return this.texturePath;
      }

      public String url() {
         return this.url;
      }
   }

   public static record ResourceTexture(Identifier id, Identifier texturePath) implements ClientAsset.Texture {
      public static final Codec<ClientAsset.ResourceTexture> CODEC;
      public static final MapCodec<ClientAsset.ResourceTexture> DEFAULT_FIELD_CODEC;
      public static final StreamCodec<ByteBuf, ClientAsset.ResourceTexture> STREAM_CODEC;

      public ResourceTexture(Identifier var1) {
         this(var1, var1.withPath((var0) -> {
            return "textures/" + var0 + ".png";
         }));
      }

      public ResourceTexture(Identifier param1, Identifier param2) {
         super();
         this.id = var1;
         this.texturePath = var2;
      }

      public Identifier id() {
         return this.id;
      }

      public Identifier texturePath() {
         return this.texturePath;
      }

      static {
         CODEC = Identifier.CODEC.xmap(ClientAsset.ResourceTexture::new, ClientAsset.ResourceTexture::id);
         DEFAULT_FIELD_CODEC = CODEC.fieldOf("asset_id");
         STREAM_CODEC = Identifier.STREAM_CODEC.map(ClientAsset.ResourceTexture::new, ClientAsset.ResourceTexture::id);
      }
   }

   public interface Texture extends ClientAsset {
      Identifier texturePath();
   }
}
