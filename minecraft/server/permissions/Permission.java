package net.minecraft.server.permissions;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public interface Permission {
   Codec<Permission> FULL_CODEC = BuiltInRegistries.PERMISSION_TYPE.byNameCodec().dispatch(Permission::codec, (var0) -> {
      return var0;
   });
   Codec<Permission> CODEC = Codec.either(FULL_CODEC, Identifier.CODEC).xmap((var0) -> {
      return (Permission)var0.map((var0x) -> {
         return var0x;
      }, Permission.Atom::create);
   }, (var0) -> {
      Either var10000;
      if (var0 instanceof Permission.Atom) {
         Permission.Atom var1 = (Permission.Atom)var0;
         var10000 = Either.right(var1.id());
      } else {
         var10000 = Either.left(var0);
      }

      return var10000;
   });

   MapCodec<? extends Permission> codec();

   public static record Atom(Identifier id) implements Permission {
      public static final MapCodec<Permission.Atom> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(Identifier.CODEC.fieldOf("id").forGetter(Permission.Atom::id)).apply(var0, Permission.Atom::new);
      });

      public Atom(Identifier param1) {
         super();
         this.id = var1;
      }

      public MapCodec<Permission.Atom> codec() {
         return MAP_CODEC;
      }

      public static Permission.Atom create(String var0) {
         return create(Identifier.withDefaultNamespace(var0));
      }

      public static Permission.Atom create(Identifier var0) {
         return new Permission.Atom(var0);
      }

      public Identifier id() {
         return this.id;
      }
   }

   public static record HasCommandLevel(PermissionLevel level) implements Permission {
      public static final MapCodec<Permission.HasCommandLevel> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(PermissionLevel.CODEC.fieldOf("level").forGetter(Permission.HasCommandLevel::level)).apply(var0, Permission.HasCommandLevel::new);
      });

      public HasCommandLevel(PermissionLevel param1) {
         super();
         this.level = var1;
      }

      public MapCodec<Permission.HasCommandLevel> codec() {
         return MAP_CODEC;
      }

      public PermissionLevel level() {
         return this.level;
      }
   }
}
