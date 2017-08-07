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
/*    */ public class Populator_Flowers extends BlockPopulator
/*    */ {
/*    */   public void populate(World world, Random random, Chunk source)
/*    */   {
/* 16 */     int chance = random.nextInt(100);
/* 17 */     if (chance < 10) {
/* 18 */       int flowercount = random.nextInt(3) + 2;
/* 19 */       int type = random.nextInt(100);
/* 20 */       for (int t = 0; t <= flowercount; t++) {
/* 21 */         int flower_x = random.nextInt(15);
/* 22 */         int flower_z = random.nextInt(15);
/*    */ 
/* 24 */         Block handle = world.getBlockAt(flower_x + source.getX() * 16, world.getHighestBlockYAt(flower_x + source.getX() * 16, flower_z + source.getZ() * 16), flower_z + source.getZ() * 16);
/* 25 */         if (handle.getRelative(BlockFace.DOWN).getType().equals(Material.GRASS))
/* 26 */           if (type < 33) {
/* 27 */             handle.setType(Material.RED_ROSE);
/*    */           }
/*    */           else
/* 30 */             handle.setType(Material.YELLOW_FLOWER);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Baran\Downloads\Nordic.jar
 * Qualified Name:     me.simplex.nordic.populators.Populator_Flowers
 * JD-Core Version:    0.6.2
 */