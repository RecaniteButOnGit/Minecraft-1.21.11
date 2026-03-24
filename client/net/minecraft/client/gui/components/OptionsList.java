package net.minecraft.client.gui.components;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class OptionsList extends ContainerObjectSelectionList<OptionsList.AbstractEntry> {
   private static final int BIG_BUTTON_WIDTH = 310;
   private static final int DEFAULT_ITEM_HEIGHT = 25;
   private final OptionsSubScreen screen;

   public OptionsList(Minecraft var1, int var2, OptionsSubScreen var3) {
      super(var1, var2, var3.layout.getContentHeight(), var3.layout.getHeaderHeight(), 25);
      this.centerListVertically = false;
      this.screen = var3;
   }

   public void addBig(OptionInstance<?> var1) {
      this.addEntry(OptionsList.Entry.big(this.minecraft.options, var1, this.screen));
   }

   public void addSmall(OptionInstance<?>... var1) {
      for(int var2 = 0; var2 < var1.length; var2 += 2) {
         OptionInstance var3 = var2 < var1.length - 1 ? var1[var2 + 1] : null;
         this.addEntry(OptionsList.Entry.small(this.minecraft.options, var1[var2], var3, this.screen));
      }

   }

   public void addSmall(List<AbstractWidget> var1) {
      for(int var2 = 0; var2 < var1.size(); var2 += 2) {
         this.addSmall((AbstractWidget)var1.get(var2), var2 < var1.size() - 1 ? (AbstractWidget)var1.get(var2 + 1) : null);
      }

   }

   public void addSmall(AbstractWidget var1, @Nullable AbstractWidget var2) {
      this.addEntry(OptionsList.Entry.small(var1, var2, this.screen));
   }

   public void addSmall(AbstractWidget var1, OptionInstance<?> var2, @Nullable AbstractWidget var3) {
      this.addEntry(OptionsList.Entry.small((AbstractWidget)var1, var2, (AbstractWidget)var3, (Screen)this.screen));
   }

   public void addHeader(Component var1) {
      Objects.requireNonNull(this.minecraft.font);
      byte var2 = 9;
      int var3 = this.children().isEmpty() ? 0 : var2 * 2;
      this.addEntry(new OptionsList.HeaderEntry(this.screen, var1, var3), var3 + var2 + 4);
   }

   public int getRowWidth() {
      return 310;
   }

   @Nullable
   public AbstractWidget findOption(OptionInstance<?> var1) {
      Iterator var2 = this.children().iterator();

      while(var2.hasNext()) {
         OptionsList.AbstractEntry var3 = (OptionsList.AbstractEntry)var2.next();
         if (var3 instanceof OptionsList.Entry) {
            OptionsList.Entry var4 = (OptionsList.Entry)var3;
            AbstractWidget var5 = var4.findOption(var1);
            if (var5 != null) {
               return var5;
            }
         }
      }

      return null;
   }

   public void applyUnsavedChanges() {
      Iterator var1 = this.children().iterator();

      while(true) {
         OptionsList.AbstractEntry var2;
         do {
            if (!var1.hasNext()) {
               return;
            }

            var2 = (OptionsList.AbstractEntry)var1.next();
         } while(!(var2 instanceof OptionsList.Entry));

         OptionsList.Entry var3 = (OptionsList.Entry)var2;
         Iterator var4 = var3.children.iterator();

         while(var4.hasNext()) {
            OptionsList.OptionInstanceWidget var5 = (OptionsList.OptionInstanceWidget)var4.next();
            if (var5.optionInstance() != null) {
               AbstractWidget var7 = var5.widget();
               if (var7 instanceof OptionInstance.OptionInstanceSliderButton) {
                  OptionInstance.OptionInstanceSliderButton var6 = (OptionInstance.OptionInstanceSliderButton)var7;
                  var6.applyUnsavedValue();
               }
            }
         }
      }
   }

   public void resetOption(OptionInstance<?> var1) {
      Iterator var2 = this.children().iterator();

      while(true) {
         OptionsList.AbstractEntry var3;
         do {
            if (!var2.hasNext()) {
               return;
            }

            var3 = (OptionsList.AbstractEntry)var2.next();
         } while(!(var3 instanceof OptionsList.Entry));

         OptionsList.Entry var4 = (OptionsList.Entry)var3;
         Iterator var5 = var4.children.iterator();

         while(var5.hasNext()) {
            OptionsList.OptionInstanceWidget var6 = (OptionsList.OptionInstanceWidget)var5.next();
            if (var6.optionInstance() == var1) {
               AbstractWidget var8 = var6.widget();
               if (var8 instanceof ResettableOptionWidget) {
                  ResettableOptionWidget var7 = (ResettableOptionWidget)var8;
                  var7.resetValue();
                  return;
               }
            }
         }
      }
   }

   protected static class Entry extends OptionsList.AbstractEntry {
      final List<OptionsList.OptionInstanceWidget> children;
      private final Screen screen;
      private static final int X_OFFSET = 160;

      private Entry(List<OptionsList.OptionInstanceWidget> var1, Screen var2) {
         super();
         this.children = var1;
         this.screen = var2;
      }

      public static OptionsList.Entry big(Options var0, OptionInstance<?> var1, Screen var2) {
         return new OptionsList.Entry(List.of(new OptionsList.OptionInstanceWidget(var1.createButton(var0, 0, 0, 310), var1)), var2);
      }

      public static OptionsList.Entry small(AbstractWidget var0, @Nullable AbstractWidget var1, Screen var2) {
         return var1 == null ? new OptionsList.Entry(List.of(new OptionsList.OptionInstanceWidget(var0)), var2) : new OptionsList.Entry(List.of(new OptionsList.OptionInstanceWidget(var0), new OptionsList.OptionInstanceWidget(var1)), var2);
      }

      public static OptionsList.Entry small(AbstractWidget var0, OptionInstance<?> var1, @Nullable AbstractWidget var2, Screen var3) {
         return var2 == null ? new OptionsList.Entry(List.of(new OptionsList.OptionInstanceWidget(var0, var1)), var3) : new OptionsList.Entry(List.of(new OptionsList.OptionInstanceWidget(var0, var1), new OptionsList.OptionInstanceWidget(var2)), var3);
      }

      public static OptionsList.Entry small(Options var0, OptionInstance<?> var1, @Nullable OptionInstance<?> var2, OptionsSubScreen var3) {
         AbstractWidget var4 = var1.createButton(var0);
         return var2 == null ? new OptionsList.Entry(List.of(new OptionsList.OptionInstanceWidget(var4, var1)), var3) : new OptionsList.Entry(List.of(new OptionsList.OptionInstanceWidget(var4, var1), new OptionsList.OptionInstanceWidget(var2.createButton(var0), var2)), var3);
      }

      public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
         int var6 = 0;
         int var7 = this.screen.width / 2 - 155;

         for(Iterator var8 = this.children.iterator(); var8.hasNext(); var6 += 160) {
            OptionsList.OptionInstanceWidget var9 = (OptionsList.OptionInstanceWidget)var8.next();
            var9.widget().setPosition(var7 + var6, this.getContentY());
            var9.widget().render(var1, var2, var3, var5);
         }

      }

      public List<? extends GuiEventListener> children() {
         return Lists.transform(this.children, OptionsList.OptionInstanceWidget::widget);
      }

      public List<? extends NarratableEntry> narratables() {
         return Lists.transform(this.children, OptionsList.OptionInstanceWidget::widget);
      }

      @Nullable
      public AbstractWidget findOption(OptionInstance<?> var1) {
         Iterator var2 = this.children.iterator();

         OptionsList.OptionInstanceWidget var3;
         do {
            if (!var2.hasNext()) {
               return null;
            }

            var3 = (OptionsList.OptionInstanceWidget)var2.next();
         } while(var3.optionInstance != var1);

         return var3.widget();
      }
   }

   protected static class HeaderEntry extends OptionsList.AbstractEntry {
      private final Screen screen;
      private final int paddingTop;
      private final StringWidget widget;

      protected HeaderEntry(Screen var1, Component var2, int var3) {
         super();
         this.screen = var1;
         this.paddingTop = var3;
         this.widget = new StringWidget(var2, var1.getFont());
      }

      public List<? extends NarratableEntry> narratables() {
         return List.of(this.widget);
      }

      public void renderContent(GuiGraphics var1, int var2, int var3, boolean var4, float var5) {
         this.widget.setPosition(this.screen.width / 2 - 155, this.getContentY() + this.paddingTop);
         this.widget.render(var1, var2, var3, var5);
      }

      public List<? extends GuiEventListener> children() {
         return List.of(this.widget);
      }
   }

   protected abstract static class AbstractEntry extends ContainerObjectSelectionList.Entry<OptionsList.AbstractEntry> {
      protected AbstractEntry() {
         super();
      }
   }

   public static record OptionInstanceWidget(AbstractWidget widget, @Nullable OptionInstance<?> optionInstance) {
      @Nullable
      final OptionInstance<?> optionInstance;

      public OptionInstanceWidget(AbstractWidget var1) {
         this(var1, (OptionInstance)null);
      }

      public OptionInstanceWidget(AbstractWidget param1, @Nullable OptionInstance<?> param2) {
         super();
         this.widget = var1;
         this.optionInstance = var2;
      }

      public AbstractWidget widget() {
         return this.widget;
      }

      @Nullable
      public OptionInstance<?> optionInstance() {
         return this.optionInstance;
      }
   }
}
