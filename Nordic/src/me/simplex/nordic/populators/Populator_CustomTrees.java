/*     */ package me.simplex.nordic.populators;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Random;
/*     */ import java.util.Set;
/*     */ import me.simplex.nordic.util.XYZ;
/*     */ import org.bukkit.Chunk;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.block.Block;
/*     */ import org.bukkit.block.BlockFace;
/*     */ import org.bukkit.generator.BlockPopulator;
/*     */ 
/*     */ public class Populator_CustomTrees extends BlockPopulator
/*     */ {
/*     */   public void populate(World world, Random random, Chunk source)
/*     */   {
/*  34 */     if (random.nextInt(100) < 2) {
/*  35 */       int x = 4 + random.nextInt(8) + source.getX() * 16;
/*  36 */       int z = 4 + random.nextInt(8) + source.getZ() * 16;
/*  37 */       Block high = world.getHighestBlockAt(x, z);
/*  38 */       if (!high.getRelative(BlockFace.DOWN).getType().equals(Material.GRASS)) {
/*  39 */         return;
/*     */       }
/*  41 */       int maxY = high.getY();
/*  42 */       if (maxY < 55) {
/*  43 */         return;
/*     */       }
/*  45 */       Set snake = selectBlocksForTree(world, random, x, maxY - 5, z);
/*  46 */       buildTree(world, (XYZ[])snake.toArray(new XYZ[0]));
/*  47 */       for (XYZ block : snake)
/*  48 */         world.unloadChunkRequest(block.x / 16, block.z / 16);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Set<XYZ> selectBlocksForTree(World world, Random r, int blockX, int blockY, int blockZ)
/*     */   {
/*  54 */     Set snakeBlocks = new HashSet();
/*  55 */     int height = blockY + 20 + r.nextInt(5);
/*  56 */     XYZ block = new XYZ();
/*     */     int radius;
/*     */     int x;
/*  70 */     for (; blockY <= height; 
/*  70 */       x <= radius)
/*     */     {
/*  62 */       blockY++;
/*     */ 
/*  64 */       radius = 1;
/*     */ 
/*  66 */       if (blockY + 3 > height) {
/*  67 */         radius = 0;
/*     */       }
/*  69 */       int radius2 = radius * radius + 1;
/*  70 */       x = -radius; continue;
/*  71 */       for (int z = -radius; z <= radius; z++)
/*  72 */         if (x * x + z * z < radius2) {
/*  73 */           block.x = (blockX + x);
/*  74 */           block.y = blockY;
/*  75 */           block.z = (blockZ + z);
/*  76 */           if (snakeBlocks.add(block))
/*  77 */             block = new XYZ();
/*     */         }
/*  70 */       x++;
/*     */     }
/*     */ 
/*  84 */     return snakeBlocks;
/*     */   }
/*     */ 
/*     */   private static void buildTree(World world, XYZ[] snakeBlocks)
/*     */   {
/*  89 */     HashMap slices = new HashMap();
/*     */ 
/*  91 */     for (XYZ loc : snakeBlocks) {
/*  92 */       Block block = world.getBlockAt(loc.x, loc.y, loc.z);
/*  93 */       if ((block.isEmpty()) && (!block.isLiquid()) && (block.getType() != Material.BEDROCK)) {
/*  94 */         if (slices.containsKey(Integer.valueOf(loc.y))) {
/*  95 */           ((ArrayList)slices.get(Integer.valueOf(loc.y))).add(block);
/*     */         }
/*     */         else {
/*  98 */           slices.put(Integer.valueOf(loc.y), new ArrayList());
/*  99 */           ((ArrayList)slices.get(Integer.valueOf(loc.y))).add(block);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 104 */     ArrayList sortedKeys = new ArrayList(slices.keySet());
/* 105 */     Collections.sort(sortedKeys);
/* 106 */     int low = ((Integer)sortedKeys.get(0)).intValue();
/* 107 */     int high = ((Integer)sortedKeys.get(sortedKeys.size() - 1)).intValue();
/*     */ 
/* 109 */     boolean buildLayer2 = false;
/* 110 */     boolean buildLayer3 = false;
/* 111 */     boolean buildLayer4 = false;
/* 112 */     for (Integer key : sortedKeys) {
/* 113 */       ArrayList slice = (ArrayList)slices.get(key);
/* 114 */       for (Block b : slice) {
/* 115 */         b.setTypeIdAndData(17, (byte)1, false);
/*     */       }
/*     */ 
/* 127 */       if (!buildLayer2) {
/* 128 */         ArrayList toBranches = new ArrayList();
/* 129 */         for (Block b : slice) {
/* 130 */           if ((b.getY() - low >= high - low - 8) && (checkBlockIsOnBorderOfSlice(b, slice))) {
/* 131 */             toBranches.add(b);
/* 132 */             buildLayer2 = true;
/*     */           }
/*     */         }
/* 135 */         buildTreeLayer2(toBranches);
/*     */       }
/* 137 */       if (!buildLayer3) {
/* 138 */         ArrayList toBranches = new ArrayList();
/* 139 */         for (Block b : slice) {
/* 140 */           if ((b.getY() - low >= high - low - 4) && (checkBlockIsOnBorderOfSlice(b, slice))) {
/* 141 */             toBranches.add(b);
/* 142 */             buildLayer3 = true;
/*     */           }
/*     */         }
/* 145 */         buildTreeLayer3(toBranches);
/*     */       }
/* 147 */       if (!buildLayer4) {
/* 148 */         ArrayList toBranches = new ArrayList();
/* 149 */         for (Block b : slice) {
/* 150 */           if ((b.getY() - low >= high - low) && (checkBlockIsOnBorderOfSlice(b, slice))) {
/* 151 */             toBranches.add(b);
/* 152 */             buildLayer4 = true;
/*     */           }
/*     */         }
/* 155 */         buildTreeLayer4(toBranches);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void buildTreeLayer2(ArrayList<Block> blocks)
/*     */   {
/* 204 */     ArrayList branches = new ArrayList();
/*     */ 
/* 206 */     for (Block b : blocks) {
/* 207 */       BlockFace dir = getBuildDirection(b);
/* 208 */       Block handle = b.getRelative(dir);
/* 209 */       handle.setTypeIdAndData(17, (byte)1, false);
/* 210 */       branches.add(handle);
/* 211 */       switch (dir) {
/*     */       case DOWN:
/* 213 */         branches.add(handle.getRelative(-1, 0, 1));
/* 214 */         branches.add(handle.getRelative(-1, 0, -1));
/* 215 */         break;
/*     */       case EAST:
/* 217 */         branches.add(handle.getRelative(-1, 0, -1));
/* 218 */         branches.add(handle.getRelative(1, 0, -1));
/* 219 */         break;
/*     */       case EAST_NORTH_EAST:
/* 221 */         branches.add(handle.getRelative(1, 0, 1));
/* 222 */         branches.add(handle.getRelative(1, 0, -1));
/* 223 */         break;
/*     */       case EAST_SOUTH_EAST:
/* 225 */         branches.add(handle.getRelative(-1, 0, 1));
/* 226 */         branches.add(handle.getRelative(1, 0, 1));
/*     */       }
/*     */     }
/*     */ 
/* 230 */     if (!branches.isEmpty())
/* 231 */       for (Block branch : branches) {
/* 232 */         branch.setTypeIdAndData(17, (byte)1, false);
/* 233 */         populateTreeBranch(branch, 2);
/*     */       }
/*     */   }
/*     */ 
/*     */   private static void buildTreeLayer3(ArrayList<Block> blocks)
/*     */   {
/* 239 */     ArrayList branches = new ArrayList();
/*     */ 
/* 241 */     for (Block b : blocks) {
/* 242 */       BlockFace dir = getBuildDirection(b);
/* 243 */       Block handle = b.getRelative(dir);
/* 244 */       handle.setTypeIdAndData(17, (byte)1, false);
/* 245 */       branches.add(handle);
/*     */     }
/* 247 */     if (!branches.isEmpty())
/* 248 */       for (Block branch : branches) {
/* 249 */         branch.setTypeIdAndData(17, (byte)1, false);
/* 250 */         populateTreeBranch(branch, 2);
/*     */       }
/*     */   }
/*     */ 
/*     */   private static void buildTreeLayer4(ArrayList<Block> blocks)
/*     */   {
/* 256 */     ArrayList branches = new ArrayList();
/* 257 */     for (Block block : blocks) {
/* 258 */       branches.add(block);
/*     */     }
/* 260 */     if (!branches.isEmpty())
/* 261 */       for (Block branch : branches) {
/* 262 */         branch.setTypeIdAndData(17, (byte)1, false);
/* 263 */         populateTreeBranch(branch, 2);
/*     */       }
/*     */   }
/*     */ 
/*     */   private static BlockFace getBuildDirection(Block b)
/*     */   {
/* 269 */     BlockFace[] faces = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
/* 270 */     for (BlockFace blockFace : faces) {
/* 271 */       if (!b.getRelative(blockFace).isEmpty()) {
/* 272 */         return blockFace.getOppositeFace();
/*     */       }
/*     */     }
/* 275 */     return BlockFace.SELF;
/*     */   }
/*     */ 
/*     */   private static boolean checkBlockIsOnBorderOfSlice(Block block, ArrayList<Block> slice) {
/* 279 */     BlockFace[] faces = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
/* 280 */     if ((slice.contains(block.getRelative(faces[0]))) && 
/* 281 */       (slice.contains(block.getRelative(faces[1]))) && 
/* 282 */       (slice.contains(block.getRelative(faces[2]))) && 
/* 283 */       (slice.contains(block.getRelative(faces[3])))) {
/* 284 */       return false;
/*     */     }
/* 286 */     return true;
/*     */   }
/*     */ 
/*     */   private static void populateTreeBranch(Block block, int radius) {
/* 290 */     int centerX = block.getX();
/* 291 */     int centerZ = block.getZ();
/* 292 */     int centerY = block.getY();
/* 293 */     World w = block.getWorld();
/*     */ 
/* 295 */     int radius_check = radius * radius + 1;
/*     */ 
/* 297 */     for (int x = -radius; x <= radius; x++)
/* 298 */       for (int z = -radius; z <= radius; z++)
/* 299 */         for (int y = -radius; y <= radius; y++)
/* 300 */           if (x * x + y * y + z * z <= radius_check) {
/* 301 */             Block b = w.getBlockAt(centerX + x, centerY + y, centerZ + z);
/* 302 */             if (b.isEmpty())
/* 303 */               b.setTypeIdAndData(18, (byte)1, false);
/*     */           }
/*     */   }
/*     */ }

/* Location:           C:\Users\Baran\Downloads\Nordic.jar
 * Qualified Name:     me.simplex.nordic.populators.Populator_CustomTrees
 * JD-Core Version:    0.6.2
 */