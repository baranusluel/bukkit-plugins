/*    */ package me.simplex.nordic.populators;
/*    */ 
/*    */ import java.util.Random;
/*    */ import org.bukkit.Chunk;
/*    */ import org.bukkit.Location;
/*    */ import org.bukkit.Material;
/*    */ import org.bukkit.TreeType;
/*    */ import org.bukkit.World;
/*    */ import org.bukkit.block.Block;
/*    */ import org.bukkit.block.BlockFace;
/*    */ import org.bukkit.generator.BlockPopulator;
/*    */ 
/*    */ public class Populator_Trees extends BlockPopulator
/*    */ {
/*    */   public void populate(World world, Random random, Chunk source)
/*    */   {
/* 18 */     int treecount = random.nextInt(3);
/*    */ 
/* 20 */     for (int t = 0; t <= treecount; t++) {
/* 21 */       int tree_x = random.nextInt(15);
/* 22 */       int tree_z = random.nextInt(15);
/*    */ 
/* 24 */       Block block = world.getHighestBlockAt(tree_x + source.getX() * 16, tree_z + source.getZ() * 16);
/* 25 */       Location high = block.getLocation();
/* 26 */       if (!block.getRelative(BlockFace.DOWN).getType().equals(Material.GRASS)) {
/* 27 */         return;
/*    */       }
/* 29 */       if (random.nextInt(10) < 1) {
/* 30 */         world.generateTree(high, TreeType.TALL_REDWOOD);
/*    */       }
/*    */       else
/*    */       {
/* 34 */         world.generateTree(high, TreeType.REDWOOD);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Baran\Downloads\Nordic.jar
 * Qualified Name:     me.simplex.nordic.populators.Populator_Trees
 * JD-Core Version:    0.6.2
 */