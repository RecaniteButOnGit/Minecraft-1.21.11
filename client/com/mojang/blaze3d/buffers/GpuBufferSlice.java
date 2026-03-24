package com.mojang.blaze3d.buffers;

import com.mojang.blaze3d.DontObfuscate;

@DontObfuscate
public record GpuBufferSlice(GpuBuffer buffer, long offset, long length) {
   public GpuBufferSlice(GpuBuffer param1, long param2, long param4) {
      super();
      this.buffer = var1;
      this.offset = var2;
      this.length = var4;
   }

   public GpuBufferSlice slice(long var1, long var3) {
      if (var1 >= 0L && var3 >= 0L && var1 + var3 <= this.length) {
         return new GpuBufferSlice(this.buffer, this.offset + var1, var3);
      } else {
         throw new IllegalArgumentException("Offset of " + var1 + " and length " + var3 + " would put new slice outside existing slice's range (of " + this.offset + "," + this.length + ")");
      }
   }

   public GpuBuffer buffer() {
      return this.buffer;
   }

   public long offset() {
      return this.offset;
   }

   public long length() {
      return this.length;
   }
}
