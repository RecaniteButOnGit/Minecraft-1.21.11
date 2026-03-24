package net.minecraft.client.gui.font.providers;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

public record ProviderReferenceDefinition(Identifier id) implements GlyphProviderDefinition {
   public static final MapCodec<ProviderReferenceDefinition> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Identifier.CODEC.fieldOf("id").forGetter(ProviderReferenceDefinition::id)).apply(var0, ProviderReferenceDefinition::new);
   });

   public ProviderReferenceDefinition(Identifier param1) {
      super();
      this.id = var1;
   }

   public GlyphProviderType type() {
      return GlyphProviderType.REFERENCE;
   }

   public Either<GlyphProviderDefinition.Loader, GlyphProviderDefinition.Reference> unpack() {
      return Either.right(new GlyphProviderDefinition.Reference(this.id));
   }

   public Identifier id() {
      return this.id;
   }
}
