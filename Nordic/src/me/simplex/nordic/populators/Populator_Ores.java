/*    */ package me.simplex.nordic.populators;
/*    */ 
/*    */ import java.util.Random;
/*    */ import org.bukkit.Chunk;
/*    */ import org.bukkit.Material;
/*    */ import org.bukkit.World;
/*    */ import org.bukkit.block.Block;
/*    */ import org.bukkit.generator.BlockPopulator;
/*    */ 
/*    */ public class Populator_Ores extends BlockPopulator
/*    */ {
/* 19 */   private static final int[] iterations = { 10, 16, 20, 20, 2, 8, 1, 1, 1 };
/* 20 */   private static final int[] amount = { 32, 32, 16, 8, 8, 7, 7, 6 };
/* 21 */   private static final int[] type = { Material.GRAVEL.getId(), Material.SAND.getId(), Material.COAL_ORE.getId(), Material.IRON_ORE.getId(), Material.GOLD_ORE.getId(), Material.REDSTONE_ORE.getId(), Material.DIAMOND_ORE.getId(), Material.LAPIS_ORE.getId() };
/* 22 */   private static final int[] maxHeight = { 128, 45, 128, 128, 32, 32, 32, 32, 16, 16, 32 };
/* 23 */   private static final int STONE = Material.STONE.getId();
/*    */ 
/*    */   public void populate(World world, Random random, Chunk source)
/*    */   {
/* 30 */     for (int i = 0; i < type.length; i++)
/* 31 */       for (int j = 0; j < iterations[i]; j++)
/* 32 */         internal(source, random, random.nextInt(16), random.nextInt(maxHeight[i]), random.nextInt(16), amount[i], type[i]);
/*    */   }
/*    */ 
/*    */   private static void internal(Chunk source, Random random, int originX, int originY, int originZ, int amount, int type)
/*    */   {
/* 38 */     for (int i = 0; i < amount; i++) {
/* 39 */       int x = originX + random.nextInt(amount / 2) - amount / 4;
/* 40 */       int y = originY + random.nextInt(amount / 4) - amount / 8;
/* 41 */       int z = originZ + random.nextInt(amount / 2) - amount / 4;
/* 42 */       x &= 15;
/* 43 */       z &= 15;
/* 44 */       if ((y <= 127) && (y >= 0))
/*    */       {
/* 47 */         Block block = source.getBlock(x, y, z);
/* 48 */         if (block.getTypeId() == STONE)
/* 49 */           block.setTypeId(type, false);
/*    */       }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Baran\Downloads\Nordic.jar
 * Qualified Name:     me.simplex.nordic.populators.Populator_Ores
 * JD-Core Version:    0.6.2
 */