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
/*    */ public class Populator_Gravel extends BlockPopulator
/*    */ {
/*    */   public void populate(World world, Random random, Chunk source)
/*    */   {
/* 16 */     int chance = random.nextInt(100);
/* 17 */     if (chance < 40)
/* 18 */       for (int gravel_x = 0; gravel_x < 16; gravel_x++)
/* 19 */         for (int gravel_z = 0; gravel_z < 16; gravel_z++) {
/* 20 */           Block handle = world.getBlockAt(gravel_x + source.getX() * 16, 48, gravel_z + source.getZ() * 16);
/* 21 */           if ((isRelativeTo(handle, Material.WATER)) || (isRelativeTo(handle, Material.STATIONARY_WATER))) {
/* 22 */             changeBlockToGravel(handle, 0, random.nextInt(35) + 10, random);
/* 23 */             return;
/*    */           }
/*    */         }
/*    */   }
/*    */ 
/*    */   private boolean isRelativeTo(Block block, Material material)
/*    */   {
/* 31 */     for (BlockFace blockFace : BlockFace.values()) {
/* 32 */       if (block.getRelative(blockFace).getType().equals(material)) {
/* 33 */         return true;
/*    */       }
/*    */     }
/* 36 */     return false;
/*    */   }
/*    */ 
/*    */   private void changeBlockToGravel(Block block, int distance, int max, Random random) {
/* 40 */     if (block.getTypeId() == 2) {
/* 41 */       block.setType(Material.GRAVEL);
/* 42 */       if ((distance <= max) && (random.nextInt(100) < 75)) {
/* 43 */         changeBlockToGravel(block.getRelative(BlockFace.NORTH), distance + 1, max, random);
/* 44 */         changeBlockToGravel(block.getRelative(BlockFace.EAST), distance + 1, max, random);
/* 45 */         changeBlockToGravel(block.getRelative(BlockFace.SOUTH), distance + 1, max, random);
/* 46 */         changeBlockToGravel(block.getRelative(BlockFace.WEST), distance + 1, max, random);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Baran\Downloads\Nordic.jar
 * Qualified Name:     me.simplex.nordic.populators.Populator_Gravel
 * JD-Core Version:    0.6.2
 */