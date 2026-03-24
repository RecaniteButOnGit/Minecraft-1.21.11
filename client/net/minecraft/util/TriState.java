package net.minecraft.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.function.Function;

public enum TriState implements StringRepresentable {
   TRUE("true"),
   FALSE("false"),
   DEFAULT("default");

   public static final Codec<TriState> CODEC = Codec.either(Codec.BOOL, StringRepresentable.fromEnum(TriState::values)).xmap((var0) -> {
      return (TriState)var0.map(TriState::from, Function.identity());
   }, (var0) -> {
      Either var10000;
      switch(var0.ordinal()) {
      case 0:
         var10000 = Either.left(true);
         break;
      case 1:
         var10000 = Either.left(false);
         break;
      case 2:
         var10000 = Either.right(var0);
         break;
      default:
         throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   });
   private final String name;

   private TriState(final String param3) {
      this.name = var3;
   }

   public static TriState from(boolean var0) {
      return var0 ? TRUE : FALSE;
   }

   public boolean toBoolean(boolean var1) {
      boolean var10000;
      switch(this.ordinal()) {
      case 0:
         var10000 = true;
         break;
      case 1:
         var10000 = false;
         break;
      default:
         var10000 = var1;
      }

      return var10000;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static TriState[] $values() {
      return new TriState[]{TRUE, FALSE, DEFAULT};
   }
}
