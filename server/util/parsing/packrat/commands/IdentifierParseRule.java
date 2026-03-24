package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.resources.Identifier;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Rule;
import org.jspecify.annotations.Nullable;

public class IdentifierParseRule implements Rule<StringReader, Identifier> {
   public static final Rule<StringReader, Identifier> INSTANCE = new IdentifierParseRule();

   private IdentifierParseRule() {
      super();
   }

   @Nullable
   public Identifier parse(ParseState<StringReader> var1) {
      ((StringReader)var1.input()).skipWhitespace();

      try {
         return Identifier.readNonEmpty((StringReader)var1.input());
      } catch (CommandSyntaxException var3) {
         return null;
      }
   }

   // $FF: synthetic method
   @Nullable
   public Object parse(final ParseState param1) {
      return this.parse(var1);
   }
}
