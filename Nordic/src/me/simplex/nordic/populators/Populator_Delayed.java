/*    */ package me.simplex.nordic.populators;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Random;
/*    */ import org.bukkit.Chunk;
/*    */ import org.bukkit.World;
/*    */ import org.bukkit.generator.BlockPopulator;
/*    */ import org.bukkit.plugin.java.JavaPlugin;
/*    */ import org.bukkit.scheduler.BukkitScheduler;
/*    */ 
/*    */ public class Populator_Delayed extends BlockPopulator
/*    */ {
/*    */   private ArrayList<BlockPopulator> toProcess;
/*    */   private JavaPlugin p;
/*    */   private BukkitScheduler s;
/*    */ 
/*    */   public Populator_Delayed(ArrayList<BlockPopulator> toProcess, JavaPlugin p, BukkitScheduler s)
/*    */   {
/* 18 */     this.toProcess = toProcess;
/* 19 */     this.p = p;
/* 20 */     this.s = s;
/*    */   }
/*    */ 
/*    */   public void populate(final World world, final Random random, final Chunk source)
/*    */   {
/* 25 */     this.s.scheduleSyncDelayedTask(this.p, new Runnable()
/*    */     {
/*    */       public void run()
/*    */       {
/* 29 */         for (BlockPopulator p : Populator_Delayed.this.toProcess)
/* 30 */           p.populate(world, random, source);
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           C:\Users\Baran\Downloads\Nordic.jar
 * Qualified Name:     me.simplex.nordic.populators.Populator_Delayed
 * JD-Core Version:    0.6.2
 */