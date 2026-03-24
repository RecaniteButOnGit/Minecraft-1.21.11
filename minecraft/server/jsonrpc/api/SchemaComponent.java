package net.minecraft.server.jsonrpc.api;

import java.net.URI;
import java.util.List;

public record SchemaComponent<T>(String name, URI ref, Schema<T> schema) {
   public SchemaComponent(String param1, URI param2, Schema<T> param3) {
      super();
      this.name = var1;
      this.ref = var2;
      this.schema = var3;
   }

   public Schema<T> asRef() {
      return Schema.ofRef(this.ref, this.schema.codec());
   }

   public Schema<List<T>> asArray() {
      return Schema.arrayOf(this.asRef(), this.schema.codec());
   }

   public String name() {
      return this.name;
   }

   public URI ref() {
      return this.ref;
   }

   public Schema<T> schema() {
      return this.schema;
   }
}
