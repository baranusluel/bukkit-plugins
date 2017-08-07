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
/*    */ public class Populator_Longgrass extends BlockPopulator
/*    */ {
/*    */   public void populate(World world, Random random, Chunk source)
/*    */   {
/* 16 */     for (int x = 0; x < 16; x++)
/* 17 */       for (int z = 0; z < 16; z++) {
/* 18 */         int chance = random.nextInt(100);
/* 19 */         if (chance < 33) {
/* 20 */           Block handle = world.getHighestBlockAt(x + source.getX() * 16, z + source.getZ() * 16);
/* 21 */           if (handle.getRelative(BlockFace.DOWN).getType().equals(Material.GRASS))
/* 22 */             handle.setTypeIdAndData(Material.LONG_GRASS.getId(), (byte)1, false);
/*    */         }
/*    */       }
/*    */   }
/*    */ }

/* Location:           C:\Users\Baran\Downloads\Nordic.jar
 * Qualified Name:     me.simplex.nordic.populators.Populator_Longgrass
 * JD-Core Version:    0.6.2
 */