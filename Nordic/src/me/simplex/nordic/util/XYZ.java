/*    */ package me.simplex.nordic.util;
/*    */ 
/*    */ public class XYZ
/*    */ {
/*    */   public int x;
/*    */   public int y;
/*    */   public int z;
/*    */ 
/*    */   public XYZ(int x, int y, int z)
/*    */   {
/* 15 */     this.x = x;
/* 16 */     this.y = y;
/* 17 */     this.z = z;
/*    */   }
/*    */ 
/*    */   public XYZ()
/*    */   {
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 28 */     int prime = 31;
/* 29 */     int result = 1;
/* 30 */     result = 31 * result + this.x;
/* 31 */     result = 31 * result + this.y;
/* 32 */     result = 31 * result + this.z;
/* 33 */     return result;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object obj)
/*    */   {
/* 41 */     if (this == obj) {
/* 42 */       return true;
/*    */     }
/* 44 */     if (obj == null) {
/* 45 */       return false;
/*    */     }
/* 47 */     if (!(obj instanceof XYZ)) {
/* 48 */       return false;
/*    */     }
/* 50 */     XYZ other = (XYZ)obj;
/* 51 */     if (this.x != other.x) {
/* 52 */       return false;
/*    */     }
/* 54 */     if (this.y != other.y) {
/* 55 */       return false;
/*    */     }
/* 57 */     if (this.z != other.z) {
/* 58 */       return false;
/*    */     }
/* 60 */     return true;
/*    */   }
/*    */ }

/* Location:           C:\Users\Baran\Downloads\Nordic.jar
 * Qualified Name:     me.simplex.nordic.util.XYZ
 * JD-Core Version:    0.6.2
 */