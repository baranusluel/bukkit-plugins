/*    */ package me.simplex.nordic.populators;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Random;
/*    */ import org.bukkit.Chunk;
/*    */ import org.bukkit.ChunkSnapshot;
/*    */ import org.bukkit.Material;
/*    */ import org.bukkit.World;
/*    */ import org.bukkit.block.Block;
/*    */ import org.bukkit.generator.BlockPopulator;
/*    */ import org.bukkit.util.BlockVector;
/*    */ import org.bukkit.util.Vector;
/*    */ 
/*    */ public class Populator_Lava_Lakes extends BlockPopulator
/*    */ {
/*    */   public void populate(World world, Random random, Chunk source)
/*    */   {
/* 27 */     if (random.nextInt(100) >= 2) {
/* 28 */       return;
/*    */     }
/* 30 */     ChunkSnapshot snapshot = source.getChunkSnapshot();
/*    */ 
/* 32 */     int rx16 = random.nextInt(16);
/* 33 */     int rx = (source.getX() << 4) + rx16;
/* 34 */     int rz16 = random.nextInt(16);
/* 35 */     int rz = (source.getZ() << 4) + rz16;
/* 36 */     if (snapshot.getHighestBlockYAt(rx16, rz16) < 4)
/* 37 */       return;
/* 38 */     int ry = random.nextInt(20) + 20;
/* 39 */     int radius = 2 + random.nextInt(4);
/*    */ 
/* 41 */     Material solidMaterial = Material.STATIONARY_LAVA;
/*    */ 
/* 43 */     ArrayList lakeBlocks = new ArrayList();
/*    */     Vector center;
/* 44 */     for (int i = -1; i < 4; i++) {
/* 45 */       center = new BlockVector(rx, ry - i, rz);
/* 46 */       for (int x = -radius; x <= radius; x++) {
/* 47 */         for (int z = -radius; z <= radius; z++) {
/* 48 */           Vector position = center.clone().add(new Vector(x, 0, z));
/* 49 */           if (center.distance(position) <= radius + 0.5D - i) {
/* 50 */             lakeBlocks.add(world.getBlockAt(position.toLocation(world)));
/*    */           }
/*    */         }
/*    */       }
/*    */     }
/*    */ 
/* 56 */     for (Block block : lakeBlocks)
/*    */     {
/* 58 */       if ((!block.isEmpty()) && (!block.isLiquid()))
/* 59 */         if (block.getY() >= ry) {
/* 60 */           block.setType(Material.AIR);
/*    */         }
/*    */         else
/* 63 */           block.setType(solidMaterial);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\Baran\Downloads\Nordic.jar
 * Qualified Name:     me.simplex.nordic.populators.Populator_Lava_Lakes
 * JD-Core Version:    0.6.2
 */