/*     */ package me.simplex.nordic.noise;
/*     */ 
/*     */ import java.util.Random;
/*     */ 
/*     */ public class Voronoi
/*     */ {
/*     */   private float[][][][][] grid;
/*     */   private Random r;
/*     */   private int density;
/*     */   private int size;
/*     */   private int zsize;
/*     */   private int dimensions;
/*     */   private boolean is2D;
/*     */   private DistanceMetric metric;
/*     */   private int level;
/*  79 */   static final int[][] order3D = { 
/*  80 */     new int[3], { 1 }, { 0, 1 }, { 0, 0, 1 }, { -1 }, { 0, -1 }, { 0, 0, -1 }, 
/*  81 */     { 1, 1 }, { 1, 0, 1 }, { 0, 1, 1 }, { -1, 1 }, { -1, 0, 1 }, { 0, -1, 1 }, 
/*  82 */     { 1, -1 }, { 1, 0, -1 }, { 0, 1, -1 }, { -1, -1 }, { -1, 0, -1 }, { 0, -1, -1 }, 
/*  83 */     { 1, 1, 1 }, { -1, 1, 1 }, { 1, -1, 1 }, { 1, 1, -1 }, { -1, -1, 1 }, { -1, 1, -1 }, 
/*  84 */     { 1, -1, -1 }, { -1, -1, -1 } };
/*     */ 
/*  86 */   static final int[][] order2D = { 
/*  87 */     new int[2], { 1 }, { 0, 1 }, { -1 }, { 0, -1 }, { 1, 1 }, { -1, 1 }, { 1, -1 }, { -1, -1 } };
/*     */ 
/*     */   public Voronoi(int size, boolean is2D, long seed, int density, DistanceMetric metric, int level)
/*     */   {
/*  25 */     this.zsize = (is2D ? 1 : size);
/*  26 */     this.dimensions = (is2D ? 2 : 3);
/*  27 */     this.grid = new float[size][size][this.zsize][density][this.dimensions];
/*  28 */     this.r = new Random(seed);
/*  29 */     this.size = size;
/*  30 */     this.density = density;
/*  31 */     this.metric = metric;
/*  32 */     this.level = level;
/*  33 */     this.is2D = is2D;
/*  34 */     for (int i = 0; i < size; i++)
/*  35 */       for (int j = 0; j < size; j++)
/*  36 */         for (int k = 0; k < this.zsize; k++)
/*  37 */           for (int d = 0; d < density; d++)
/*  38 */             for (int e = 0; e < this.dimensions; e++)
/*  39 */               this.grid[i][j][k][d][e] = this.r.nextFloat();
/*     */   }
/*     */ 
/*     */   private float distance(float[] a, int[] offset, float[] b) {
/*  43 */     float[] m = new float[this.dimensions];
/*  44 */     for (int i = 0; i < this.dimensions; i++) {
/*  45 */       b[i] -= a[i] + offset[i];
/*     */     }
/*  47 */     float d = 0.0F;
/*  48 */     switch (this.DistanceMetric()[this.metric.ordinal()]) {
/*     */     case 1:
/*  50 */       for (int i = 0; i < this.dimensions; i++)
/*  51 */         d += m[i] * m[i];
/*  52 */       return (float)Math.sqrt(d);
/*     */     case 2:
/*  54 */       for (int i = 0; i < this.dimensions; i++)
/*  55 */         d += m[i] * m[i];
/*  56 */       return d;
/*     */     case 3:
/*  58 */       for (int i = 0; i < this.dimensions; i++)
/*  59 */         d += Math.abs(m[i]);
/*  60 */       return d;
/*     */     case 5:
/*  62 */       for (int i = 0; i < this.dimensions; i++)
/*  63 */         d = Math.max(Math.abs(m[i]), d);
/*  64 */       return d;
/*     */     case 4:
/*  66 */       for (int i = 0; i < this.dimensions; i++)
/*  67 */         for (int j = i; j < this.dimensions; j++)
/*  68 */           d += m[i] * m[j];
/*  69 */       return d;
/*     */     case 6:
/*  71 */       for (int i = 0; i < this.dimensions; i++)
/*  72 */         d = (float)(d + Math.pow(Math.abs(m[i]), 15.0D));
/*  73 */       return (float)Math.pow(d, 0.06666667014360428D);
/*     */     }
/*  75 */     return (1.0F / 1.0F);
/*     */   }
/*     */ 
/*     */   public float get(float xin, float yin, float zin)
/*     */   {
/*  92 */     if (this.is2D) {
/*  93 */       throw new UnsupportedOperationException(
/*  94 */         "Cannot create 3D Voronoi basis when instantiated with is2D = true.");
/*     */     }
/*  96 */     int[] cell = { fastfloor(xin), fastfloor(yin), fastfloor(zin) };
/*  97 */     float[] pos = { xin - cell[0], yin - cell[1], zin - cell[2] };
/*  98 */     for (int i = 0; i < 3; i++) cell[i] %= this.size;
/*     */ 
/* 100 */     float[] distances = new float[this.level];
/* 101 */     for (int i = 0; i < this.level; i++) distances[i] = 3.4028235E+38F;
/* 102 */     for (int i = 0; i < order3D.length; i++)
/*     */     {
/* 104 */       boolean possible = true;
/* 105 */       float farDist = distances[(this.level - 1)];
/* 106 */       if (farDist < 3.4028235E+38F)
/* 107 */         for (int j = 0; j < 3; j++)
/* 108 */           if (((order3D[i][j] < 0) && (farDist < pos[j])) || (
/* 109 */             (order3D[i][j] > 0) && (farDist < 1.0F - pos[j]))) {
/* 110 */             possible = false;
/* 111 */             break;
/*     */           }
/* 113 */       if (possible) {
/* 114 */         int cx = (order3D[i][0] + cell[0]) % this.size;
/* 115 */         int cy = (order3D[i][1] + cell[1]) % this.size;
/* 116 */         int cz = (order3D[i][2] + cell[2]) % this.size;
/* 117 */         for (int j = 0; j < this.density; j++) {
/* 118 */           float d = distance(this.grid[cx][cy][cz][j], order3D[i], pos);
/* 119 */           for (int k = 0; k < this.level; k++)
/* 120 */             if (d < distances[k]) {
/* 121 */               for (int l = this.level - 1; l > k; l--)
/* 122 */                 distances[l] = distances[(l - 1)];
/* 123 */               distances[k] = d;
/* 124 */               break;
/*     */             }
/*     */         }
/*     */       }
/*     */     }
/* 129 */     return distances[(this.level - 1)];
/*     */   }
/*     */ 
/*     */   public float get(float xin, float yin)
/*     */   {
/* 134 */     if (!this.is2D) {
/* 135 */       throw new UnsupportedOperationException(
/* 136 */         "Cannot create 2D Voronoi basis when instantiated with is2D = false.");
/*     */     }
/* 138 */     int[] cell = { fastfloor(xin), fastfloor(yin) };
/* 139 */     float[] pos = { xin - cell[0], yin - cell[1] };
/* 140 */     for (int i = 0; i < 2; i++) cell[i] %= this.size;
/*     */ 
/* 142 */     float[] distances = new float[this.level];
/* 143 */     for (int i = 0; i < this.level; i++) distances[i] = 3.4028235E+38F;
/* 144 */     for (int i = 0; i < order2D.length; i++)
/*     */     {
/* 146 */       boolean possible = true;
/* 147 */       float farDist = distances[(this.level - 1)];
/* 148 */       if (farDist < 3.4028235E+38F)
/* 149 */         for (int j = 0; j < this.dimensions; j++)
/* 150 */           if (((order2D[i][j] < 0) && (farDist < pos[j])) || (
/* 151 */             (order2D[i][j] > 0) && (farDist < 1.0F - pos[j]))) {
/* 152 */             possible = false;
/* 153 */             break;
/*     */           }
/* 155 */       if (possible) {
/* 156 */         int cx = (order2D[i][0] + cell[0] + this.size) % this.size;
/* 157 */         int cy = (order2D[i][1] + cell[1] + this.size) % this.size;
/* 158 */         for (int j = 0; j < this.density; j++) {
/* 159 */           float d = distance(this.grid[cx][cy][0][j], order2D[i], pos);
/* 160 */           for (int k = 0; k < this.level; k++)
/* 161 */             if (d < distances[k]) {
/* 162 */               for (int l = this.level - 1; l > k; l--)
/* 163 */                 distances[l] = distances[(l - 1)];
/* 164 */               distances[k] = d;
/* 165 */               break;
/*     */             }
/*     */         }
/*     */       }
/*     */     }
/* 170 */     return distances[(this.level - 1)];
/*     */   }
/*     */ 
/*     */   private int fastfloor(float x) {
/* 174 */     return x > 0.0F ? (int)x : (int)x - 1;
/*     */   }
/*     */ 
/*     */   public static enum DistanceMetric
/*     */   {
/*  22 */     Linear, Squared, Manhattan, Quadratic, Chebyshev, Wiggly;
/*     */   }
/*     */ }

/* Location:           C:\Users\Baran\Downloads\Nordic.jar
 * Qualified Name:     me.simplex.nordic.noise.Voronoi
 * JD-Core Version:    0.6.2
 */