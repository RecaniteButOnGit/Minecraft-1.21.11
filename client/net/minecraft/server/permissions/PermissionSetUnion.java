package net.minecraft.server.permissions;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;

public class PermissionSetUnion implements PermissionSet {
   private final ReferenceSet<PermissionSet> permissions = new ReferenceArraySet();

   PermissionSetUnion(PermissionSet var1, PermissionSet var2) {
      super();
      this.permissions.add(var1);
      this.permissions.add(var2);
      this.ensureNoUnionsWithinUnions();
   }

   private PermissionSetUnion(ReferenceSet<PermissionSet> var1, PermissionSet var2) {
      super();
      this.permissions.addAll(var1);
      this.permissions.add(var2);
      this.ensureNoUnionsWithinUnions();
   }

   private PermissionSetUnion(ReferenceSet<PermissionSet> var1, ReferenceSet<PermissionSet> var2) {
      super();
      this.permissions.addAll(var1);
      this.permissions.addAll(var2);
      this.ensureNoUnionsWithinUnions();
   }

   public boolean hasPermission(Permission var1) {
      ObjectIterator var2 = this.permissions.iterator();

      PermissionSet var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (PermissionSet)var2.next();
      } while(!var3.hasPermission(var1));

      return true;
   }

   public PermissionSet union(PermissionSet var1) {
      if (var1 instanceof PermissionSetUnion) {
         PermissionSetUnion var2 = (PermissionSetUnion)var1;
         return new PermissionSetUnion(this.permissions, var2.permissions);
      } else {
         return new PermissionSetUnion(this.permissions, var1);
      }
   }

   @VisibleForTesting
   public ReferenceSet<PermissionSet> getPermissions() {
      return new ReferenceArraySet(this.permissions);
   }

   private void ensureNoUnionsWithinUnions() {
      ObjectIterator var1 = this.permissions.iterator();

      PermissionSet var2;
      do {
         if (!var1.hasNext()) {
            return;
         }

         var2 = (PermissionSet)var1.next();
      } while(!(var2 instanceof PermissionSetUnion));

      throw new IllegalArgumentException("Cannot have PermissionSetUnion within another PermissionSetUnion");
   }
}
