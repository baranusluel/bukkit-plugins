/*     */ package me.simplex.nordic.populators;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Random;
/*     */ import java.util.Set;
/*     */ import org.bukkit.Chunk;
/*     */ import org.bukkit.Location;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.block.BlockFace;
/*     */ import org.bukkit.generator.BlockPopulator;
/*     */ import org.bukkit.util.Vector;
/*     */ 
/*     */ public class Populator_Lakes extends BlockPopulator
/*     */ {
/*     */   private static final int MIN_BLOCK_COUNT = 450;
/*     */   private static final int MAX_BLOCK_COUNT = 900;
/*     */   private static final int LAKE_CHANCE = 4;
/*     */   private static final int CREEK_CHANCE = 85;
/*     */   private static final int MAX_CREEK_LENGTH = 125;
/*     */ 
/*     */   public void populate(World world, Random random, Chunk source)
/*     */   {
/*  29 */     if (random.nextInt(100) >= 4) {
/*  30 */       return;
/*     */     }
/*     */ 
/*  33 */     int start_x = random.nextInt(16);
/*  34 */     int start_z = random.nextInt(16);
/*     */ 
/*  36 */     Block lake_start = world.getHighestBlockAt(source.getX() * 16 + start_x, source.getZ() * 16 + start_z);
/*     */ 
/*  38 */     if (lake_start.getY() - 1 <= 48) {
/*  39 */       return;
/*     */     }
/*     */ 
/*  42 */     Set lake_form = collectLakeLayout(world, lake_start, random);
/*  43 */     Set[] form_result = startLakeBuildProcess(world, lake_form);
/*  44 */     if (form_result == null) {
/*  45 */       return;
/*     */     }
/*  47 */     Block creek_start = buildLake(form_result[0], random);
/*  48 */     buildAirAndWaterfall(form_result[0], form_result[1], random);
/*     */ 
/*  50 */     if ((creek_start == null) || (random.nextInt(100) >= 85)) {
/*  51 */       return;
/*     */     }
/*     */ 
/*  54 */     List creekblocks = collectCreekBlocks(world, creek_start, random);
/*  55 */     if (creekblocks != null) {
/*  56 */       buildCreek(world, creekblocks);
/*     */     }
/*  58 */     System.gc();
/*     */   }
/*     */ 
/*     */   private List<Block> collectCreekBlocks(World world, Block creekStart, Random random)
/*     */   {
/*  66 */     int check_radius = 7;
/*  67 */     Vector main_dir = null;
/*     */ 
/*  70 */     int highest_diff = 0;
/*  71 */     for (int mod_x = -check_radius; mod_x <= check_radius; mod_x++) {
/*  72 */       for (int mod_z = -check_radius; mod_z <= check_radius; mod_z++) {
/*  73 */         Block toCheck = world.getHighestBlockAt(mod_x + creekStart.getX(), mod_z + creekStart.getZ());
/*  74 */         int diff = creekStart.getY() - toCheck.getY();
/*  75 */         if (diff > highest_diff) {
/*  76 */           highest_diff = diff;
/*  77 */           main_dir = new Vector(toCheck.getX() - creekStart.getX(), 0, toCheck.getZ() - creekStart.getZ());
/*     */         }
/*     */       }
/*     */     }
/*  81 */     if (main_dir != null) {
/*  82 */       List creekblocks = new ArrayList();
/*  83 */       Location creek_current_center = creekStart.getRelative(0, 10, 0).getLocation();
/*  84 */       main_dir = main_dir.normalize().multiply(2);
/*  85 */       int steps = 0;
/*  86 */       while ((!world.getHighestBlockAt(creek_current_center).getRelative(0, -1, 0).isLiquid()) && (steps < 125)) {
/*  87 */         creekblocks.add(creek_current_center.getBlock());
/*  88 */         creek_current_center = creek_current_center.add(main_dir);
/*  89 */         main_dir = rotateVector(main_dir, random.nextDouble() * 0.5D - 0.25D);
/*  90 */         steps++;
/*     */       }
/*  92 */       if (steps < 125) {
/*  93 */         return creekblocks;
/*     */       }
/*     */     }
/*  96 */     return null;
/*     */   }
/*     */ 
/*     */   private void buildCreek(World world, List<Block> center_blocks)
/*     */   {
/* 104 */     Set collected_blocks_air = new HashSet();
/* 105 */     Set collected_blocks_water = new HashSet();
/*     */ 
/* 107 */     int radius = 3;
/* 108 */     int radius_squared = 9;
/* 109 */     int last_y = world.getMaxHeight();
/*     */ 
/* 111 */     Set circle = new HashSet();
/* 112 */     for (Iterator localIterator1 = center_blocks.iterator(); localIterator1.hasNext(); 
/* 146 */       ???.hasNext())
/*     */     {
/* 112 */       Block center = (Block)localIterator1.next();
/* 113 */       circle.clear();
/* 114 */       for (int x_mod = -radius; x_mod <= radius; x_mod++) {
/* 115 */         for (int z_mod = -radius; z_mod <= radius; z_mod++) {
/* 116 */           if (x_mod * x_mod + z_mod * z_mod < radius_squared) {
/* 117 */             circle.add(center.getRelative(x_mod, 0, z_mod));
/*     */           }
/*     */         }
/*     */       }
/* 121 */       int lowest = world.getMaxHeight();
/* 122 */       int highest = 0;
/* 123 */       for (Block block : circle)
/*     */       {
/* 125 */         int x = block.getX();
/* 126 */         int z = block.getZ();
/* 127 */         int compare = world.getHighestBlockYAt(x, z);
/*     */ 
/* 129 */         if (compare < lowest) {
/* 130 */           lowest = compare;
/*     */         }
/* 132 */         if (compare > highest) {
/* 133 */           highest = compare;
/*     */         }
/*     */       }
/*     */ 
/* 137 */       if (lowest > last_y) {
/* 138 */         lowest = last_y;
/*     */       } else {
/* 140 */         last_y = lowest;
/* 141 */         if (last_y < 48) {
/* 142 */           last_y = 48;
/* 143 */           lowest = 48;
/*     */         }
/*     */       }
/* 146 */       ??? = circle.iterator(); continue; Block block = (Block)???.next();
/* 147 */       collected_blocks_water.add(world.getBlockAt(block.getX(), lowest - 3, block.getZ()));
/* 148 */       for (int y = lowest - 2; y <= highest; y++) {
/* 149 */         collected_blocks_air.add(world.getBlockAt(block.getX(), y, block.getZ()));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 155 */     for (Block toWater : collected_blocks_water) {
/* 156 */       toWater.setType(Material.WATER);
/*     */     }
/* 158 */     for (Block toAir : collected_blocks_air)
/* 159 */       if (toAir.getY() <= 48)
/* 160 */         toAir.setType(Material.WATER);
/* 161 */       else if ((toAir.getType() != Material.LOG) && 
/* 162 */         (toAir.getType() != Material.LEAVES) && 
/* 163 */         (toAir.getType() != Material.RED_MUSHROOM) && 
/* 164 */         (toAir.getType() != Material.VINE) && 
/* 165 */         (toAir.getType() != Material.GLOWSTONE))
/* 166 */         toAir.setType(Material.AIR);
/*     */   }
/*     */ 
/*     */   private Vector rotateVector(Vector dir, double angle)
/*     */   {
/* 178 */     double new_x = dir.getX() * Math.cos(angle) - dir.getZ() * Math.sin(angle);
/* 179 */     double new_z = dir.getX() * Math.sin(angle) + dir.getZ() * Math.cos(angle);
/* 180 */     return new Vector(new_x, 0.0D, new_z);
/*     */   }
/*     */ 
/*     */   private Set<Block> collectLakeLayout(World world, Block start, Random random)
/*     */   {
/* 190 */     Set result = new HashSet();
/* 191 */     int sizelimit = 450 + random.nextInt(450);
/* 192 */     int blockX = start.getX();
/* 193 */     int blockY = start.getY();
/* 194 */     int blockZ = start.getZ();
/* 195 */     while (result.size() < sizelimit) {
/* 196 */       int radius = 1 + random.nextInt(5);
/* 197 */       int radius_squared = radius * radius + 1;
/*     */ 
/* 199 */       for (int x_mod = -radius; x_mod <= radius; x_mod++) {
/* 200 */         for (int z_mod = -radius; z_mod <= radius; z_mod++) {
/* 201 */           if (x_mod * x_mod + z_mod * z_mod <= radius_squared) {
/* 202 */             Block collected = world.getBlockAt(blockX + x_mod, blockY, blockZ + z_mod);
/* 203 */             result.add(collected);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 208 */       if (random.nextBoolean()) {
/* 209 */         if (random.nextBoolean())
/* 210 */           blockX++;
/*     */         else {
/* 212 */           blockZ++;
/*     */         }
/*     */       }
/* 215 */       else if (random.nextBoolean())
/* 216 */         blockX--;
/*     */       else {
/* 218 */         blockZ--;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 223 */     return result;
/*     */   }
/*     */ 
/*     */   private void buildAirAndWaterfall(Set<Block> ground, Set<Block> blocks, Random random)
/*     */   {
/* 233 */     List candidates = new ArrayList();
/* 234 */     int ground_height = ((Block)ground.iterator().next()).getY();
/* 235 */     for (Block block : blocks) {
/* 236 */       if ((block.getType() != Material.LOG) && (block.getType() != Material.LEAVES) && (block.getType() != Material.RED_MUSHROOM) && (block.getType() != Material.VINE) && (block.getType() != Material.GLOWSTONE)) {
/* 237 */         block.setType(Material.AIR);
/* 238 */         if ((checkBlockIsOnBorderOfSlice(block, blocks)) && (isWaterfallQualified(block)) && (block.getY() >= ground_height + 3)) {
/* 239 */           candidates.add(block);
/*     */         }
/*     */       }
/*     */     }
/* 243 */     if (!candidates.isEmpty()) {
/* 244 */       buildWaterfall((Block)candidates.get(random.nextInt(candidates.size())));
/* 245 */       if (random.nextInt(100) < 20)
/* 246 */         buildWaterfall((Block)candidates.get(random.nextInt(candidates.size())));
/*     */     }
/*     */   }
/*     */ 
/*     */   private Set<Block>[] startLakeBuildProcess(World world, Set<Block> blocks)
/*     */   {
/* 258 */     int lowest = world.getMaxHeight();
/* 259 */     int highest = 0;
/* 260 */     for (Block block : blocks)
/*     */     {
/* 262 */       x = block.getX();
/* 263 */       int z = block.getZ();
/* 264 */       int compare = world.getHighestBlockYAt(x, z);
/*     */ 
/* 266 */       if (compare < lowest) {
/* 267 */         lowest = compare;
/*     */       }
/* 269 */       if (compare > highest) {
/* 270 */         highest = compare;
/*     */       }
/* 272 */       if (compare < 48) {
/* 273 */         return null;
/*     */       }
/*     */     }
/*     */ 
/* 277 */     if ((lowest < 48) || (highest - lowest > 25)) {
/* 278 */       return null;
/*     */     }
/*     */ 
/* 281 */     Set[] result = new Set[2];
/* 282 */     result[0] = new HashSet();
/* 283 */     result[1] = new HashSet();
/*     */     int y;
/* 285 */     for (int x = blocks.iterator(); x.hasNext(); 
/* 287 */       y <= highest)
/*     */     {
/* 285 */       Block block = (Block)x.next();
/* 286 */       result[0].add(world.getBlockAt(block.getX(), lowest - 1, block.getZ()));
/* 287 */       y = lowest; continue;
/* 288 */       result[1].add(world.getBlockAt(block.getX(), y, block.getZ()));
/*     */ 
/* 287 */       y++;
/*     */     }
/*     */ 
/* 291 */     return result;
/*     */   }
/*     */ 
/*     */   private Block buildLake(Set<Block> top_layer, Random random)
/*     */   {
/* 300 */     int max_lake_depth = random.nextInt(2) + 3;
/*     */ 
/* 303 */     Set to_air = new HashSet();
/* 304 */     int lowering = 0;
/* 305 */     while ((!sliceHasBorder(top_layer)) && (lowering <= 3)) {
/* 306 */       to_air.addAll(top_layer);
/* 307 */       top_layer = lower_layer(top_layer);
/* 308 */       lowering++;
/*     */     }
/* 310 */     for (Block block : to_air) {
/* 311 */       block.setType(Material.AIR);
/*     */     }
/*     */ 
/* 315 */     for (Block block : top_layer) {
/* 316 */       block.setType(Material.STATIONARY_WATER);
/*     */     }
/*     */ 
/* 320 */     Set working_layer = lower_layer(top_layer);
/*     */     Set next_layer;
/* 322 */     for (int mod_y = 0; mod_y > -max_lake_depth; mod_y--) {
/* 323 */       next_layer = new HashSet();
/* 324 */       for (Block block : working_layer) {
/* 325 */         if (checkBlockIsOnBorderOfSlice(block, working_layer)) {
/* 326 */           if (!block.isLiquid())
/* 327 */             block.setType(Material.DIRT);
/*     */         }
/*     */         else {
/* 330 */           next_layer.add(block.getRelative(0, -1, 0));
/* 331 */           block.setType(Material.STATIONARY_WATER);
/*     */         }
/*     */       }
/* 334 */       working_layer = next_layer;
/*     */     }
/*     */ 
/* 338 */     for (Block block : working_layer) {
/* 339 */       if (!block.isLiquid()) {
/* 340 */         block.setType(Material.DIRT);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 345 */     for (Block block : top_layer) {
/* 346 */       if (checkBlockIsOnBorderOfSlice(block, top_layer)) {
/* 347 */         Block candidate = block.getRelative(getUncontainedBlockFace(block, top_layer));
/* 348 */         if (candidate.getRelative(BlockFace.UP).isEmpty())
/*     */         {
/* 351 */           return candidate;
/*     */         }
/*     */       }
/*     */     }
/* 355 */     return null;
/*     */   }
/*     */ 
/*     */   private Set<Block> lower_layer(Set<Block> waterLayer)
/*     */   {
/* 364 */     Set result = new HashSet();
/* 365 */     for (Block block : waterLayer) {
/* 366 */       result.add(block.getRelative(0, -1, 0));
/*     */     }
/* 368 */     return result;
/*     */   }
/*     */ 
/*     */   private boolean checkBlockIsOnBorderOfSlice(Block block, Set<Block> slice)
/*     */   {
/* 377 */     BlockFace[] faces = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
/* 378 */     if ((slice.contains(block.getRelative(faces[0]))) && 
/* 379 */       (slice.contains(block.getRelative(faces[1]))) && 
/* 380 */       (slice.contains(block.getRelative(faces[2]))) && 
/* 381 */       (slice.contains(block.getRelative(faces[3])))) {
/* 382 */       return false;
/*     */     }
/* 384 */     return true;
/*     */   }
/*     */ 
/*     */   private BlockFace getUncontainedBlockFace(Block block, Set<Block> slice)
/*     */   {
/* 393 */     BlockFace[] faces = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
/* 394 */     for (BlockFace face : faces) {
/* 395 */       if (!slice.contains(block.getRelative(face))) {
/* 396 */         return face;
/*     */       }
/*     */     }
/*     */ 
/* 400 */     return null;
/*     */   }
/*     */ 
/*     */   private boolean sliceHasBorder(Set<Block> slice)
/*     */   {
/* 408 */     for (Block block : slice) {
/* 409 */       if (!hasNeighbors(block)) {
/* 410 */         return false;
/*     */       }
/*     */     }
/* 413 */     return true;
/*     */   }
/*     */ 
/*     */   private boolean hasNeighbors(Block block)
/*     */   {
/* 421 */     if ((!block.getRelative(BlockFace.WEST).isEmpty()) && 
/* 422 */       (!block.getRelative(BlockFace.EAST).isEmpty()) && 
/* 423 */       (!block.getRelative(BlockFace.NORTH).isEmpty()) && 
/* 424 */       (!block.getRelative(BlockFace.SOUTH).isEmpty())) {
/* 425 */       return true;
/*     */     }
/* 427 */     return false;
/*     */   }
/*     */ 
/*     */   private boolean isWaterfallQualified(Block block)
/*     */   {
/* 435 */     BlockFace[] faces = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
/* 436 */     for (BlockFace f : faces) {
/* 437 */       Block r = block.getRelative(f);
/* 438 */       if ((!r.isEmpty()) && (!r.getRelative(BlockFace.UP).isEmpty()) && (
/* 439 */         (r.getType().equals(Material.DIRT)) || (r.getType().equals(Material.STONE)))) {
/* 440 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 444 */     return false;
/*     */   }
/*     */ 
/*     */   private void buildWaterfall(Block block)
/*     */   {
/* 451 */     BlockFace[] faces = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
/* 452 */     for (BlockFace f : faces) {
/* 453 */       Block r = block.getRelative(f);
/* 454 */       if (!r.isEmpty()) {
/* 455 */         r.setType(Material.WATER);
/* 456 */         return;
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Baran\Downloads\Nordic.jar
 * Qualified Name:     me.simplex.nordic.populators.Populator_Lakes
 * JD-Core Version:    0.6.2
 */