package net.minecraft.util;

import com.mojang.serialization.Codec;
import java.util.HexFormat;

public record ColorRGBA(int rgba) {
   public static final Codec<ColorRGBA> CODEC;

   public ColorRGBA(int param1) {
      super();
      this.rgba = var1;
   }

   public String toString() {
      return HexFormat.of().toHexDigits((long)this.rgba, 8);
   }

   public int rgba() {
      return this.rgba;
   }

   static {
      CODEC = ExtraCodecs.STRING_ARGB_COLOR.xmap(ColorRGBA::new, ColorRGBA::rgba);
   }
}
