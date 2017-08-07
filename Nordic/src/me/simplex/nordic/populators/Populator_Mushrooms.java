/*    */ package me.simplex.nordic.populators;
/*    */ 
/*    */ import java.util.Random;
/*    */ import org.bukkit.Chunk;
/*    */ import org.bukkit.Material;
/*    */ import org.bukkit.World;
/*    */ import org.bukkit.block.Block;
/*    */ import org.bukkit.block.BlockFace;
/*    */ import org.bukkit.generator.BlockPopulator;
/*    */ 
/*    */ public class Populator_Mushrooms extends BlockPopulator
/*    */ {
/*    */   public void populate(World world, Random random, Chunk source)
/*    */   {
/* 16 */     int chance = random.nextInt(100);
/* 17 */     if (chance < 7) {
/* 18 */       int type = random.nextInt(100);
/*    */       Material mushroom;
/*    */       Material mushroom;
/* 20 */       if (type < 33) {
/* 21 */         mushroom = Material.RED_MUSHROOM;
/*    */       }
/*    */       else {
/* 24 */         mushroom = Material.BROWN_MUSHROOM;
/*    */       }
/* 26 */       int mushroomcount = random.nextInt(3) + 2;
/* 27 */       int placed = 0;
/* 28 */       for (int t = 0; t <= mushroomcount; t++)
/* 29 */         for (int flower_x = 0; flower_x < 16; flower_x++)
/* 30 */           for (int flower_z = 0; flower_z < 16; flower_z++) {
/* 31 */             Block handle = world.getBlockAt(flower_x + source.getX() * 16, getHighestEmptyBlockYAtIgnoreTreesAndFoliage(world, flower_x + source.getX() * 16, flower_z + source.getZ() * 16), flower_z + source.getZ() * 16);
/* 32 */             if ((handle.getRelative(BlockFace.DOWN).getType().equals(Material.GRASS)) && (isRelativeTo(handle, Material.LOG)) && (handle.isEmpty())) {
/* 33 */               handle.setType(mushroom);
/* 34 */               placed++;
/* 35 */               if (placed >= mushroomcount)
/* 36 */                 return;
/*    */             }
/*    */           }
/*    */     }
/*    */   }
/*    */ 
/*    */   private boolean isRelativeTo(Block block, Material material)
/*    */   {
/* 46 */     for (BlockFace blockFace : BlockFace.values()) {
/* 47 */       if (block.getRelative(blockFace).getType().equals(material)) {
/* 48 */         return true;
/*    */       }
/*    */     }
/* 51 */     return false;
/*    */   }
/*    */ 
/*    */   private int getHighestEmptyBlockYAtIgnoreTreesAndFoliage(World w, int x, int z) {
/* 55 */     for (int y = 127; y >= 1; y--) {
/* 56 */       Block handle = w.getBlockAt(x, y - 1, z);
/* 57 */       int id = handle.getTypeId();
/* 58 */       if ((id != 0) && (id != 17) && (id != 18) && (id != 37) && (id != 38)) {
/* 59 */         return y;
/*    */       }
/*    */     }
/* 62 */     return 0;
/*    */   }
/*    */ }

/* Location:           C:\Users\Baran\Downloads\Nordic.jar
 * Qualified Name:     me.simplex.nordic.populators.Populator_Mushrooms
 * JD-Core Version:    0.6.2
 */