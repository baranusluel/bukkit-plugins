/*     */ package me.simplex.nordic.populators;
/*     */ 
/*     */ import java.util.HashSet;
/*     */ import java.util.Random;
/*     */ import java.util.Set;
/*     */ import me.simplex.nordic.util.XYZ;
/*     */ import org.bukkit.Chunk;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.generator.BlockPopulator;
/*     */ 
/*     */ public class Populator_Caves extends BlockPopulator
/*     */ {
/*     */   public void populate(World world, Random random, Chunk source)
/*     */   {
/*  30 */     if (random.nextInt(100) < 3) {
/*  31 */       int x = 4 + random.nextInt(8) + source.getX() * 16;
/*  32 */       int z = 4 + random.nextInt(8) + source.getZ() * 16;
/*  33 */       int maxY = world.getHighestBlockYAt(x, z);
/*  34 */       if (maxY < 16) {
/*  35 */         maxY = 32;
/*     */       }
/*     */ 
/*  38 */       int y = random.nextInt(maxY);
/*  39 */       Set snake = selectBlocksForCave(world, random, x, y, z);
/*  40 */       buildCave(world, (XYZ[])snake.toArray(new XYZ[0]));
/*  41 */       for (XYZ block : snake)
/*  42 */         world.unloadChunkRequest(block.x / 16, block.z / 16);
/*     */     }
/*     */   }
/*     */ 
/*     */   static Set<XYZ> selectBlocksForCave(World world, Random random, int blockX, int blockY, int blockZ)
/*     */   {
/*  48 */     Set snakeBlocks = new HashSet();
/*     */ 
/*  50 */     int airHits = 0;
/*  51 */     XYZ block = new XYZ();
/*     */ 
/*  53 */     while (airHits <= 1200)
/*     */     {
/*  57 */       if (random.nextInt(20) == 0) {
/*  58 */         blockY++;
/*     */       }
/*  60 */       else if (world.getBlockTypeIdAt(blockX, blockY + 2, blockZ) == 0) {
/*  61 */         blockY += 2;
/*     */       }
/*  63 */       else if (world.getBlockTypeIdAt(blockX + 2, blockY, blockZ) == 0) {
/*  64 */         blockX++;
/*     */       }
/*  66 */       else if (world.getBlockTypeIdAt(blockX - 2, blockY, blockZ) == 0) {
/*  67 */         blockX--;
/*     */       }
/*  69 */       else if (world.getBlockTypeIdAt(blockX, blockY, blockZ + 2) == 0) {
/*  70 */         blockZ++;
/*     */       }
/*  72 */       else if (world.getBlockTypeIdAt(blockX, blockY, blockZ - 2) == 0) {
/*  73 */         blockZ--;
/*     */       }
/*  75 */       else if (world.getBlockTypeIdAt(blockX + 1, blockY, blockZ) == 0) {
/*  76 */         blockX++;
/*     */       }
/*  78 */       else if (world.getBlockTypeIdAt(blockX - 1, blockY, blockZ) == 0) {
/*  79 */         blockX--;
/*     */       }
/*  81 */       else if (world.getBlockTypeIdAt(blockX, blockY, blockZ + 1) == 0) {
/*  82 */         blockZ++;
/*     */       }
/*  84 */       else if (world.getBlockTypeIdAt(blockX, blockY, blockZ - 1) == 0) {
/*  85 */         blockZ--;
/*     */       }
/*  87 */       else if (random.nextBoolean()) {
/*  88 */         if (random.nextBoolean())
/*  89 */           blockX++;
/*     */         else {
/*  91 */           blockZ++;
/*     */         }
/*     */       }
/*  94 */       else if (random.nextBoolean())
/*  95 */         blockX--;
/*     */       else {
/*  97 */         blockZ--;
/*     */       }
/*     */ 
/* 101 */       if (world.getBlockTypeIdAt(blockX, blockY, blockZ) != 0) {
/* 102 */         int radius = 1 + random.nextInt(2);
/* 103 */         int radius2 = radius * radius + 1;
/* 104 */         for (int x = -radius; x <= radius; x++) {
/* 105 */           for (int y = -radius; y <= radius; y++) {
/* 106 */             for (int z = -radius; z <= radius; z++) {
/* 107 */               if ((x * x + y * y + z * z <= radius2) && (y >= 0) && (y < 128))
/* 108 */                 if (world.getBlockTypeIdAt(blockX + x, blockY + y, blockZ + z) == 0) {
/* 109 */                   airHits++;
/*     */                 } else {
/* 111 */                   block.x = (blockX + x);
/* 112 */                   block.y = (blockY + y);
/* 113 */                   block.z = (blockZ + z);
/* 114 */                   if (snakeBlocks.add(block))
/* 115 */                     block = new XYZ();
/*     */                 }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */       else
/*     */       {
/* 123 */         airHits++;
/*     */       }
/*     */     }
/*     */ 
/* 127 */     return snakeBlocks;
/*     */   }
/*     */ 
/*     */   static void buildCave(World world, XYZ[] snakeBlocks) {
/* 131 */     for (XYZ loc : snakeBlocks) {
/* 132 */       Block block = world.getBlockAt(loc.x, loc.y, loc.z);
/* 133 */       if ((!block.isEmpty()) && (!block.isLiquid()) && (block.getType() != Material.BEDROCK))
/* 134 */         block.setType(Material.AIR);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Baran\Downloads\Nordic.jar
 * Qualified Name:     me.simplex.nordic.populators.Populator_Caves
 * JD-Core Version:    0.6.2
 */