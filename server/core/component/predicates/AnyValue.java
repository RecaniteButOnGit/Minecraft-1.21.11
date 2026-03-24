package net.minecraft.core.component.predicates;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentType;

public record AnyValue(DataComponentType<?> type) implements DataComponentPredicate {
   public AnyValue(DataComponentType<?> param1) {
      super();
      this.type = var1;
   }

   public boolean matches(DataComponentGetter var1) {
      return var1.get(this.type) != null;
   }

   public DataComponentType<?> type() {
      return this.type;
   }
}
