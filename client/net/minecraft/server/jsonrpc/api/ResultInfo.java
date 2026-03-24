package net.minecraft.server.jsonrpc.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ResultInfo<Result>(String name, Schema<Result> schema) {
   public ResultInfo(String param1, Schema<Result> param2) {
      super();
      this.name = var1;
      this.schema = var2;
   }

   public static <Result> Codec<ResultInfo<Result>> typedCodec() {
      return RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.STRING.fieldOf("name").forGetter(ResultInfo::name), Schema.typedCodec().fieldOf("schema").forGetter(ResultInfo::schema)).apply(var0, ResultInfo::new);
      });
   }

   public String name() {
      return this.name;
   }

   public Schema<Result> schema() {
      return this.schema;
   }
}
