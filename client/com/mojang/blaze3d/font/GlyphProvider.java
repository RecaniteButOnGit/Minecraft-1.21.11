package com.mojang.blaze3d.font;

import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.gui.font.FontOption;
import org.jspecify.annotations.Nullable;

public interface GlyphProvider extends AutoCloseable {
   float BASELINE = 7.0F;

   default void close() {
   }

   @Nullable
   default UnbakedGlyph getGlyph(int var1) {
      return null;
   }

   IntSet getSupportedGlyphs();

   public static record Conditional(GlyphProvider provider, FontOption.Filter filter) implements AutoCloseable {
      public Conditional(GlyphProvider param1, FontOption.Filter param2) {
         super();
         this.provider = var1;
         this.filter = var2;
      }

      public void close() {
         this.provider.close();
      }

      public GlyphProvider provider() {
         return this.provider;
      }

      public FontOption.Filter filter() {
         return this.filter;
      }
   }
}
