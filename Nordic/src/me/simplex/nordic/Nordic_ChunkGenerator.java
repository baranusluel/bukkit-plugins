/*     */ package me.simplex.nordic;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Random;
/*     */ import me.simplex.nordic.noise.Voronoi;
/*     */ import me.simplex.nordic.noise.Voronoi.DistanceMetric;
/*     */ import org.bukkit.Material;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.block.Biome;
/*     */ import org.bukkit.generator.BlockPopulator;
/*     */ import org.bukkit.generator.ChunkGenerator;
/*     */ import org.bukkit.generator.ChunkGenerator.BiomeGrid;
/*     */ import org.bukkit.util.noise.SimplexOctaveGenerator;
/*     */ 
/*     */ public class Nordic_ChunkGenerator extends ChunkGenerator
/*     */ {
/*     */   private SimplexOctaveGenerator gen_highland;
/*     */   private SimplexOctaveGenerator gen_base1;
/*     */   private SimplexOctaveGenerator gen_base2;
/*     */   private SimplexOctaveGenerator gen_hills;
/*     */   private SimplexOctaveGenerator gen_ground;
/*     */   private Voronoi voronoi_gen_base1;
/*     */   private Voronoi voronoi_gen_base2;
/*     */   private Voronoi voronoi_gen_mountains;
/*     */   private ArrayList<BlockPopulator> populators;
/*     */   private long usedSeed;
/*     */ 
/*     */   public Nordic_ChunkGenerator(long seed, ArrayList<BlockPopulator> populators)
/*     */   {
/*  46 */     this.gen_highland = new SimplexOctaveGenerator(new Random(seed), 16);
/*  47 */     this.gen_base1 = new SimplexOctaveGenerator(new Random(seed), 16);
/*  48 */     this.gen_base2 = new SimplexOctaveGenerator(new Random(seed), 16);
/*  49 */     this.gen_hills = new SimplexOctaveGenerator(new Random(seed), 4);
/*  50 */     this.gen_ground = new SimplexOctaveGenerator(new Random(seed), 16);
/*     */ 
/*  52 */     this.voronoi_gen_base1 = new Voronoi(64, true, seed, 16, Voronoi.DistanceMetric.Squared, 4);
/*  53 */     this.voronoi_gen_base2 = new Voronoi(64, true, seed, 16, Voronoi.DistanceMetric.Quadratic, 4);
/*  54 */     this.voronoi_gen_mountains = new Voronoi(64, true, seed, 16, Voronoi.DistanceMetric.Squared, 4);
/*     */ 
/*  56 */     this.populators = populators;
/*  57 */     this.usedSeed = seed;
/*     */   }
/*     */ 
/*     */   private static void setMaterialAt(byte[][] chunk_data, int x, int y, int z, Material material)
/*     */   {
/*  75 */     int sec_id = y >> 4;
/*  76 */     int yy = y & 0xF;
/*  77 */     if (chunk_data[sec_id] == null) {
/*  78 */       chunk_data[sec_id] = new byte[4096];
/*     */     }
/*  80 */     chunk_data[sec_id][(yy << 8 | z << 4 | x)] = ((byte)material.getId());
/*     */   }
/*     */ 
/*     */   private static Material getMaterialAt(byte[][] chunk_data, int x, int y, int z) {
/*  84 */     int sec_id = y >> 4;
/*  85 */     int yy = y & 0xF;
/*  86 */     if (chunk_data[sec_id] == null) {
/*  87 */       return Material.AIR;
/*     */     }
/*  89 */     return Material.getMaterial(chunk_data[sec_id][(yy << 8 | z << 4 | x)]);
/*     */   }
/*     */ 
/*     */   public byte[][] generateBlockSections(World world, Random random, int x_chunk, int z_chunk, ChunkGenerator.BiomeGrid biomes)
/*     */   {
/*  95 */     checkSeed(Long.valueOf(world.getSeed()));
/*  96 */     byte[][] result = new byte[16][];
/*     */ 
/* 100 */     for (int x = 0; x < 16; x++) {
/* 101 */       for (int z = 0; z < 16; z++)
/*     */       {
/* 107 */         int currheight = 29;
/*     */ 
/* 110 */         currheight = Math.max(currheight, gen_Base(x, z, x_chunk, z_chunk, this.gen_base1, this.voronoi_gen_base1));
/*     */ 
/* 113 */         currheight = Math.max(currheight, gen_Base(x, z, x_chunk, z_chunk, this.gen_base2, this.voronoi_gen_base2));
/*     */ 
/* 116 */         currheight = Math.max(currheight, gen_Ground(x, z, x_chunk, z_chunk, this.gen_ground));
/*     */ 
/* 119 */         currheight = Math.max(currheight, gen_Hills(x, z, x_chunk, z_chunk, currheight, this.gen_hills));
/*     */ 
/* 122 */         currheight = Math.max(currheight, gen_Mountains(x, z, x_chunk, z_chunk, currheight, this.voronoi_gen_mountains));
/*     */ 
/* 125 */         currheight = Math.max(currheight, gen_Highlands(x, z, x_chunk, z_chunk, currheight, this.gen_highland));
/*     */ 
/* 134 */         applyHeightMap(x, z, result, currheight);
/*     */ 
/* 137 */         genFloor(x, z, result);
/*     */ 
/* 140 */         gen_TopLayer(x, z, result, currheight);
/*     */ 
/* 143 */         gen_Water(x, z, result);
/*     */ 
/* 151 */         biomes.setBiome(x, z, Biome.FOREST);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 157 */     return result;
/*     */   }
/*     */ 
/*     */   private void applyHeightMap(int x, int z, byte[][] chunk_data, int currheight)
/*     */   {
/* 169 */     for (int y = 0; y <= currheight; y++)
/* 170 */       setMaterialAt(chunk_data, x, y, z, Material.STONE);
/*     */   }
/*     */ 
/*     */   private void genFloor(int x, int z, byte[][] chunk_data)
/*     */   {
/* 182 */     for (int y = 0; y < 5; y++)
/* 183 */       if (y < 3) {
/* 184 */         setMaterialAt(chunk_data, x, y, z, Material.BEDROCK);
/*     */       } else {
/* 186 */         int rnd = new Random().nextInt(100);
/* 187 */         if (rnd < 40)
/* 188 */           setMaterialAt(chunk_data, x, y, z, Material.BEDROCK);
/*     */         else
/* 190 */           setMaterialAt(chunk_data, x, y, z, Material.STONE);
/*     */       }
/*     */   }
/*     */ 
/*     */   private int gen_Highlands(int x, int z, int xChunk, int zChunk, int current_height, SimplexOctaveGenerator gen)
/*     */   {
/* 208 */     if (current_height < 50) {
/* 209 */       return 0;
/*     */     }
/* 211 */     double noise = gen.noise((x + xChunk * 16) / 250.0F, (z + zChunk * 16) / 250.0F, 0.6D, 0.6D) * 25.0D;
/* 212 */     return (int)(34.0D + noise);
/*     */   }
/*     */ 
/*     */   private int gen_Hills(int x, int z, int xChunk, int zChunk, int current_height, SimplexOctaveGenerator gen)
/*     */   {
/* 227 */     double noise = gen.noise((x + xChunk * 16) / 250.0F, (z + zChunk * 16) / 250.0F, 0.6D, 0.6D) * 10.0D;
/* 228 */     return (int)(current_height - 2 + noise);
/*     */   }
/*     */ 
/*     */   private int gen_Ground(int x, int z, int xChunk, int zChunk, SimplexOctaveGenerator gen)
/*     */   {
/* 242 */     gen.setScale(0.0078125D);
/* 243 */     double noise = gen.noise(x + xChunk * 16, z + zChunk * 16, 0.01D, 0.5D) * 20.0D;
/* 244 */     int limit = (int)(34.0D + noise);
/* 245 */     return limit;
/*     */   }
/*     */ 
/*     */   private int gen_Mountains(int x, int z, int xChunk, int zChunk, int current_height, Voronoi noisegen)
/*     */   {
/* 260 */     double noise = noisegen.get((x + xChunk * 16) / 250.0F, (z + zChunk * 16) / 250.0F) * 100.0F;
/* 261 */     int limit = (int)(current_height + noise);
/* 262 */     if (limit < 30) {
/* 263 */       return 0;
/*     */     }
/* 265 */     return limit;
/*     */   }
/*     */ 
/*     */   private int gen_Base(int x, int z, int xChunk, int zChunk, SimplexOctaveGenerator gen, Voronoi noisegen)
/*     */   {
/* 280 */     double noise_raw1 = gen.noise((x + xChunk * 16) / 1200.0F, (z + zChunk * 16) / 1200.0F, 0.5D, 0.5D) * 600.0D;
/* 281 */     double noise_raw2 = noisegen.get((x + xChunk * 16) / 800.0F, (z + zChunk * 16) / 800.0F) * 500.0F;
/* 282 */     double noise = noise_raw1 * 0.5D + noise_raw2 * 0.5D;
/* 283 */     double limit = 29.0D + noise;
/* 284 */     if (limit > 55.0D) {
/* 285 */       limit = 55.0D;
/*     */     }
/* 287 */     return (int)limit;
/*     */   }
/*     */ 
/*     */   private void gen_TopLayer(int x, int z, byte[][] chunk_data, int height)
/*     */   {
/* 299 */     boolean grass = true;
/* 300 */     if (height < 48) {
/* 301 */       grass = false;
/*     */     }
/* 303 */     Random rnd = new Random();
/* 304 */     if (height > 80) {
/* 305 */       return;
/*     */     }
/* 307 */     if ((height > 77) && (rnd.nextBoolean())) {
/* 308 */       return;
/*     */     }
/* 310 */     int soil_depth = rnd.nextInt(4);
/* 311 */     if (grass)
/* 312 */       setMaterialAt(chunk_data, x, height, z, Material.GRASS);
/*     */     else {
/* 314 */       setMaterialAt(chunk_data, x, height, z, Material.DIRT);
/*     */     }
/* 316 */     for (int y = height - 1; y >= height - soil_depth; y--)
/* 317 */       setMaterialAt(chunk_data, x, y, z, Material.DIRT);
/*     */   }
/*     */ 
/*     */   private void gen_Water(int x, int z, byte[][] chunk_data)
/*     */   {
/* 330 */     int y = 48;
/* 331 */     while (y > 29) {
/* 332 */       if (getMaterialAt(chunk_data, x, y, z) == Material.AIR) {
/* 333 */         setMaterialAt(chunk_data, x, y, z, Material.STATIONARY_WATER);
/*     */       }
/* 335 */       y--;
/*     */     }
/*     */   }
/*     */ 
/*     */   public List<BlockPopulator> getDefaultPopulators(World world)
/*     */   {
/* 341 */     return this.populators;
/*     */   }
/*     */ 
/*     */   public void changeSeed(Long seed)
/*     */   {
/* 350 */     this.gen_highland = new SimplexOctaveGenerator(new Random(seed.longValue()), 16);
/* 351 */     this.gen_base1 = new SimplexOctaveGenerator(new Random(seed.longValue()), 16);
/* 352 */     this.gen_base2 = new SimplexOctaveGenerator(new Random(seed.longValue()), 16);
/* 353 */     this.gen_hills = new SimplexOctaveGenerator(new Random(seed.longValue()), 4);
/* 354 */     this.gen_ground = new SimplexOctaveGenerator(new Random(seed.longValue()), 16);
/*     */ 
/* 356 */     this.voronoi_gen_base1 = new Voronoi(64, true, seed.longValue(), 16, Voronoi.DistanceMetric.Squared, 4);
/* 357 */     this.voronoi_gen_base2 = new Voronoi(64, true, seed.longValue(), 16, Voronoi.DistanceMetric.Quadratic, 4);
/* 358 */     this.voronoi_gen_mountains = new Voronoi(64, true, seed.longValue(), 16, Voronoi.DistanceMetric.Squared, 4);
/*     */   }
/*     */ 
/*     */   private void checkSeed(Long worldSeed)
/*     */   {
/* 368 */     if (worldSeed.longValue() != this.usedSeed) {
/* 369 */       changeSeed(worldSeed);
/* 370 */       this.usedSeed = worldSeed.longValue();
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\Baran\Downloads\Nordic.jar
 * Qualified Name:     me.simplex.nordic.Nordic_ChunkGenerator
 * JD-Core Version:    0.6.2
 */