package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.DontObfuscate;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntUnaryOperator;
import net.minecraft.util.ARGB;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@DontObfuscate
public class TextureUtil {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int MIN_MIPMAP_LEVEL = 0;
   private static final int DEFAULT_IMAGE_BUFFER_SIZE = 8192;
   private static final int[][] DIRECTIONS = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

   public TextureUtil() {
      super();
   }

   public static ByteBuffer readResource(InputStream var0) throws IOException {
      ReadableByteChannel var1 = Channels.newChannel(var0);
      if (var1 instanceof SeekableByteChannel) {
         SeekableByteChannel var2 = (SeekableByteChannel)var1;
         return readResource(var1, (int)var2.size() + 1);
      } else {
         return readResource(var1, 8192);
      }
   }

   private static ByteBuffer readResource(ReadableByteChannel var0, int var1) throws IOException {
      ByteBuffer var2 = MemoryUtil.memAlloc(var1);

      try {
         while(var0.read(var2) != -1) {
            if (!var2.hasRemaining()) {
               var2 = MemoryUtil.memRealloc(var2, var2.capacity() * 2);
            }
         }

         var2.flip();
         return var2;
      } catch (IOException var4) {
         MemoryUtil.memFree(var2);
         throw var4;
      }
   }

   public static void writeAsPNG(Path var0, String var1, GpuTexture var2, int var3, IntUnaryOperator var4) {
      RenderSystem.assertOnRenderThread();
      long var5 = 0L;

      for(int var7 = 0; var7 <= var3; ++var7) {
         var5 += (long)var2.getFormat().pixelSize() * (long)var2.getWidth(var7) * (long)var2.getHeight(var7);
      }

      if (var5 > 2147483647L) {
         throw new IllegalArgumentException("Exporting textures larger than 2GB is not supported");
      } else {
         GpuBuffer var13 = RenderSystem.getDevice().createBuffer(() -> {
            return "Texture output buffer";
         }, 9, var5);
         CommandEncoder var8 = RenderSystem.getDevice().createCommandEncoder();
         Runnable var9 = () -> {
            GpuBuffer.MappedView var7 = var8.mapBuffer(var13, true, false);

            try {
               int var8x = 0;

               for(int var9 = 0; var9 <= var3; ++var9) {
                  int var10 = var2.getWidth(var9);
                  int var11 = var2.getHeight(var9);

                  try {
                     NativeImage var12 = new NativeImage(var10, var11, false);

                     try {
                        int var13x = 0;

                        while(true) {
                           if (var13x >= var11) {
                              Path var21 = var0.resolve(var1 + "_" + var9 + ".png");
                              var12.writeToFile(var21);
                              LOGGER.debug("Exported png to: {}", var21.toAbsolutePath());
                              break;
                           }

                           for(int var14 = 0; var14 < var10; ++var14) {
                              int var15 = var7.data().getInt(var8x + (var14 + var13x * var10) * var2.getFormat().pixelSize());
                              var12.setPixelABGR(var14, var13x, var4.applyAsInt(var15));
                           }

                           ++var13x;
                        }
                     } catch (Throwable var18) {
                        try {
                           var12.close();
                        } catch (Throwable var17) {
                           var18.addSuppressed(var17);
                        }

                        throw var18;
                     }

                     var12.close();
                  } catch (IOException var19) {
                     LOGGER.debug("Unable to write: ", var19);
                  }

                  var8x += var2.getFormat().pixelSize() * var10 * var11;
               }
            } catch (Throwable var20) {
               if (var7 != null) {
                  try {
                     var7.close();
                  } catch (Throwable var16) {
                     var20.addSuppressed(var16);
                  }
               }

               throw var20;
            }

            if (var7 != null) {
               var7.close();
            }

            var13.close();
         };
         AtomicInteger var10 = new AtomicInteger();
         int var11 = 0;

         for(int var12 = 0; var12 <= var3; ++var12) {
            var8.copyTextureToBuffer(var2, var13, (long)var11, () -> {
               if (var10.getAndIncrement() == var3) {
                  var9.run();
               }

            }, var12);
            var11 += var2.getFormat().pixelSize() * var2.getWidth(var12) * var2.getHeight(var12);
         }

      }
   }

   public static Path getDebugTexturePath(Path var0) {
      return var0.resolve("screenshots").resolve("debug");
   }

   public static Path getDebugTexturePath() {
      return getDebugTexturePath(Path.of("."));
   }

   public static void solidify(NativeImage var0) {
      int var1 = var0.getWidth();
      int var2 = var0.getHeight();
      int[] var3 = new int[var1 * var2];
      int[] var4 = new int[var1 * var2];
      Arrays.fill(var4, 2147483647);
      IntArrayFIFOQueue var5 = new IntArrayFIFOQueue();

      int var6;
      int var7;
      int var8;
      for(var6 = 0; var6 < var1; ++var6) {
         for(var7 = 0; var7 < var2; ++var7) {
            var8 = var0.getPixel(var6, var7);
            if (ARGB.alpha(var8) != 0) {
               int var9 = pack(var6, var7, var1);
               var4[var9] = 0;
               var3[var9] = var8;
               var5.enqueue(var9);
            }
         }
      }

      while(!var5.isEmpty()) {
         var6 = var5.dequeueInt();
         var7 = x(var6, var1);
         var8 = y(var6, var1);
         int[][] var16 = DIRECTIONS;
         int var10 = var16.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            int[] var12 = var16[var11];
            int var13 = var7 + var12[0];
            int var14 = var8 + var12[1];
            int var15 = pack(var13, var14, var1);
            if (var13 >= 0 && var14 >= 0 && var13 < var1 && var14 < var2 && var4[var15] > var4[var6] + 1) {
               var4[var15] = var4[var6] + 1;
               var3[var15] = var3[var6];
               var5.enqueue(var15);
            }
         }
      }

      for(var6 = 0; var6 < var1; ++var6) {
         for(var7 = 0; var7 < var2; ++var7) {
            var8 = var0.getPixel(var6, var7);
            if (ARGB.alpha(var8) == 0) {
               var0.setPixel(var6, var7, ARGB.color(0, var3[pack(var6, var7, var1)]));
            } else {
               var0.setPixel(var6, var7, var8);
            }
         }
      }

   }

   public static void fillEmptyAreasWithDarkColor(NativeImage var0) {
      int var1 = var0.getWidth();
      int var2 = var0.getHeight();
      int var3 = -1;
      int var4 = 2147483647;

      int var5;
      int var6;
      int var7;
      int var8;
      int var9;
      int var10;
      int var11;
      for(var5 = 0; var5 < var1; ++var5) {
         for(var6 = 0; var6 < var2; ++var6) {
            var7 = var0.getPixel(var5, var6);
            var8 = ARGB.alpha(var7);
            if (var8 != 0) {
               var9 = ARGB.red(var7);
               var10 = ARGB.green(var7);
               var11 = ARGB.blue(var7);
               int var12 = var9 + var10 + var11;
               if (var12 < var4) {
                  var4 = var12;
                  var3 = var7;
               }
            }
         }
      }

      var5 = 3 * ARGB.red(var3) / 4;
      var6 = 3 * ARGB.green(var3) / 4;
      var7 = 3 * ARGB.blue(var3) / 4;
      var8 = ARGB.color(0, var5, var6, var7);

      for(var9 = 0; var9 < var1; ++var9) {
         for(var10 = 0; var10 < var2; ++var10) {
            var11 = var0.getPixel(var9, var10);
            if (ARGB.alpha(var11) == 0) {
               var0.setPixel(var9, var10, var8);
            }
         }
      }

   }

   private static int pack(int var0, int var1, int var2) {
      return var0 + var1 * var2;
   }

   private static int x(int var0, int var1) {
      return var0 % var1;
   }

   private static int y(int var0, int var1) {
      return var0 / var1;
   }
}
