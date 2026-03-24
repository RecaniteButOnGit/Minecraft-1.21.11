package net.minecraft.world.attribute;

import com.mojang.serialization.Codec;
import java.util.Objects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class EnvironmentAttribute<Value> {
   private final AttributeType<Value> type;
   private final Value defaultValue;
   private final AttributeRange<Value> valueRange;
   private final boolean isSyncable;
   private final boolean isPositional;
   private final boolean isSpatiallyInterpolated;

   EnvironmentAttribute(AttributeType<Value> var1, Value var2, AttributeRange<Value> var3, boolean var4, boolean var5, boolean var6) {
      super();
      this.type = var1;
      this.defaultValue = var2;
      this.valueRange = var3;
      this.isSyncable = var4;
      this.isPositional = var5;
      this.isSpatiallyInterpolated = var6;
   }

   public static <Value> EnvironmentAttribute.Builder<Value> builder(AttributeType<Value> var0) {
      return new EnvironmentAttribute.Builder(var0);
   }

   public AttributeType<Value> type() {
      return this.type;
   }

   public Value defaultValue() {
      return this.defaultValue;
   }

   public Codec<Value> valueCodec() {
      Codec var10000 = this.type.valueCodec();
      AttributeRange var10001 = this.valueRange;
      Objects.requireNonNull(var10001);
      return var10000.validate(var10001::validate);
   }

   public Value sanitizeValue(Value var1) {
      return this.valueRange.sanitize(var1);
   }

   public boolean isSyncable() {
      return this.isSyncable;
   }

   public boolean isPositional() {
      return this.isPositional;
   }

   public boolean isSpatiallyInterpolated() {
      return this.isSpatiallyInterpolated;
   }

   public String toString() {
      return Util.getRegisteredName(BuiltInRegistries.ENVIRONMENT_ATTRIBUTE, this);
   }

   public static class Builder<Value> {
      private final AttributeType<Value> type;
      @Nullable
      private Value defaultValue;
      private AttributeRange<Value> valueRange = AttributeRange.any();
      private boolean isSyncable = false;
      private boolean isPositional = true;
      private boolean isSpatiallyInterpolated = false;

      public Builder(AttributeType<Value> var1) {
         super();
         this.type = var1;
      }

      public EnvironmentAttribute.Builder<Value> defaultValue(Value var1) {
         this.defaultValue = var1;
         return this;
      }

      public EnvironmentAttribute.Builder<Value> valueRange(AttributeRange<Value> var1) {
         this.valueRange = var1;
         return this;
      }

      public EnvironmentAttribute.Builder<Value> syncable() {
         this.isSyncable = true;
         return this;
      }

      public EnvironmentAttribute.Builder<Value> notPositional() {
         this.isPositional = false;
         return this;
      }

      public EnvironmentAttribute.Builder<Value> spatiallyInterpolated() {
         this.isSpatiallyInterpolated = true;
         return this;
      }

      public EnvironmentAttribute<Value> build() {
         return new EnvironmentAttribute(this.type, Objects.requireNonNull(this.defaultValue, "Missing default value"), this.valueRange, this.isSyncable, this.isPositional, this.isSpatiallyInterpolated);
      }
   }
}
