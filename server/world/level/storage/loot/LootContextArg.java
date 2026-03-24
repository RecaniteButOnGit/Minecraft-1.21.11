package net.minecraft.world.level.storage.loot;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

public interface LootContextArg<R> {
   Codec<LootContextArg<Object>> ENTITY_OR_BLOCK = createArgCodec((var0) -> {
      return var0.anyOf(LootContext.EntityTarget.values()).anyOf(LootContext.BlockEntityTarget.values());
   });

   @Nullable
   R get(LootContext var1);

   ContextKey<?> contextParam();

   static <U> LootContextArg<U> cast(LootContextArg<? extends U> var0) {
      return var0;
   }

   static <R> Codec<LootContextArg<R>> createArgCodec(UnaryOperator<LootContextArg.ArgCodecBuilder<R>> var0) {
      return ((LootContextArg.ArgCodecBuilder)var0.apply(new LootContextArg.ArgCodecBuilder())).build();
   }

   public static final class ArgCodecBuilder<R> {
      private final ExtraCodecs.LateBoundIdMapper<String, LootContextArg<R>> sources = new ExtraCodecs.LateBoundIdMapper();

      ArgCodecBuilder() {
         super();
      }

      public <T> LootContextArg.ArgCodecBuilder<R> anyOf(T[] var1, Function<T, String> var2, Function<T, ? extends LootContextArg<R>> var3) {
         Object[] var4 = var1;
         int var5 = var1.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Object var7 = var4[var6];
            this.sources.put((String)var2.apply(var7), (LootContextArg)var3.apply(var7));
         }

         return this;
      }

      public <T extends StringRepresentable> LootContextArg.ArgCodecBuilder<R> anyOf(T[] var1, Function<T, ? extends LootContextArg<R>> var2) {
         return this.anyOf(var1, StringRepresentable::getSerializedName, var2);
      }

      public <T extends StringRepresentable & LootContextArg<? extends R>> LootContextArg.ArgCodecBuilder<R> anyOf(T[] var1) {
         return this.anyOf(var1, (var0) -> {
            return LootContextArg.cast((LootContextArg)var0);
         });
      }

      public LootContextArg.ArgCodecBuilder<R> anyEntity(Function<? super ContextKey<? extends Entity>, ? extends LootContextArg<R>> var1) {
         return this.anyOf(LootContext.EntityTarget.values(), (var1x) -> {
            return (LootContextArg)var1.apply(var1x.contextParam());
         });
      }

      public LootContextArg.ArgCodecBuilder<R> anyBlockEntity(Function<? super ContextKey<? extends BlockEntity>, ? extends LootContextArg<R>> var1) {
         return this.anyOf(LootContext.BlockEntityTarget.values(), (var1x) -> {
            return (LootContextArg)var1.apply(var1x.contextParam());
         });
      }

      public LootContextArg.ArgCodecBuilder<R> anyItemStack(Function<? super ContextKey<? extends ItemStack>, ? extends LootContextArg<R>> var1) {
         return this.anyOf(LootContext.ItemStackTarget.values(), (var1x) -> {
            return (LootContextArg)var1.apply(var1x.contextParam());
         });
      }

      Codec<LootContextArg<R>> build() {
         return this.sources.codec(Codec.STRING);
      }
   }

   public interface SimpleGetter<T> extends LootContextArg<T> {
      ContextKey<? extends T> contextParam();

      @Nullable
      default T get(LootContext var1) {
         return var1.getOptionalParameter(this.contextParam());
      }
   }

   public interface Getter<T, R> extends LootContextArg<R> {
      @Nullable
      R get(T var1);

      ContextKey<? extends T> contextParam();

      @Nullable
      default R get(LootContext var1) {
         Object var2 = var1.getOptionalParameter(this.contextParam());
         return var2 != null ? this.get(var2) : null;
      }
   }
}
