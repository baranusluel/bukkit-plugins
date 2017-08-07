/*     */ package me.simplex.nordic;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Random;
/*     */ import java.util.logging.Logger;
/*     */ import me.simplex.nordic.populators.Populator_Caves;
/*     */ import me.simplex.nordic.populators.Populator_CustomTrees;
/*     */ import me.simplex.nordic.populators.Populator_Delayed;
/*     */ import me.simplex.nordic.populators.Populator_Flowers;
/*     */ import me.simplex.nordic.populators.Populator_Gravel;
/*     */ import me.simplex.nordic.populators.Populator_Lakes;
/*     */ import me.simplex.nordic.populators.Populator_Lava_Lakes;
/*     */ import me.simplex.nordic.populators.Populator_Longgrass;
/*     */ import me.simplex.nordic.populators.Populator_Mushrooms;
/*     */ import me.simplex.nordic.populators.Populator_Ores;
/*     */ import me.simplex.nordic.populators.Populator_Trees;
/*     */ import org.bukkit.ChatColor;
/*     */ import org.bukkit.Server;
/*     */ import org.bukkit.World;
/*     */ import org.bukkit.World.Environment;
/*     */ import org.bukkit.WorldCreator;
/*     */ import org.bukkit.command.Command;
/*     */ import org.bukkit.command.CommandSender;
/*     */ import org.bukkit.entity.Player;
/*     */ import org.bukkit.generator.BlockPopulator;
/*     */ import org.bukkit.generator.ChunkGenerator;
/*     */ import org.bukkit.plugin.java.JavaPlugin;
/*     */ 
/*     */ public class Nordic extends JavaPlugin
/*     */ {
/*  36 */   private Logger log = Logger.getLogger("Minecraft");
/*     */   private Nordic_ChunkGenerator wgen;
/*     */ 
/*     */   public void onDisable()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void onEnable()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
/*     */   {
/*  49 */     if (!(sender instanceof Player)) {
/*  50 */       sender.sendMessage("player only command");
/*  51 */       return true;
/*     */     }
/*  53 */     Player player = (Player)sender;
/*  54 */     if (!player.hasPermission("nordic.command")) {
/*  55 */       player.sendMessage("Y U TRY COMMAND IF U HAVE NO PERMISSION?");
/*  56 */       return true;
/*     */     }
/*  58 */     if (command.getName().equalsIgnoreCase("nordic")) {
/*  59 */       String worldname = "world_nordic";
/*  60 */       long seed = new Random().nextLong();
/*  61 */       switch (args.length) {
/*     */       case 0:
/*  63 */         break;
/*     */       case 1:
/*  65 */         worldname = args[0];
/*  66 */         break;
/*     */       case 2:
/*  68 */         worldname = args[0];
/*  69 */         seed = buildSeed(args[1]);
/*  70 */         break;
/*     */       default:
/*  71 */         return false;
/*     */       }
/*     */ 
/*  74 */       if (worldExists(worldname)) {
/*  75 */         player.sendMessage(ChatColor.BLUE + "[Nordic] World " + ChatColor.WHITE + worldname + ChatColor.BLUE + " already exists. Porting to this world...");
/*  76 */         World w = getServer().getWorld(worldname);
/*  77 */         player.teleport(w.getSpawnLocation());
/*  78 */         return true;
/*     */       }
/*     */ 
/*  81 */       player.sendMessage(ChatColor.BLUE + "[Nordic] Generating world " + ChatColor.WHITE + worldname + ChatColor.BLUE + " with seed " + ChatColor.WHITE + seed + ChatColor.BLUE + "...");
/*  82 */       this.wgen = new Nordic_ChunkGenerator(seed, buildPopulators());
/*  83 */       World w = WorldCreator.name(worldname).environment(World.Environment.NORMAL).seed(seed).generator(this.wgen).createWorld();
/*  84 */       this.log.info("[Nordic] " + player.getName() + " created a new world: " + worldname + " with seed " + seed);
/*  85 */       player.sendMessage("done. Porting to the generated world");
/*  86 */       player.teleport(w.getSpawnLocation());
/*  87 */       return true;
/*     */     }
/*     */ 
/*  90 */     return false;
/*     */   }
/*     */ 
/*     */   private ArrayList<BlockPopulator> buildPopulators()
/*     */   {
/*  98 */     ArrayList populators_delayed = new ArrayList();
/*  99 */     populators_delayed.add(new Populator_CustomTrees());
/* 100 */     populators_delayed.add(new Populator_Trees());
/* 101 */     populators_delayed.add(new Populator_Flowers());
/* 102 */     populators_delayed.add(new Populator_Mushrooms());
/* 103 */     populators_delayed.add(new Populator_Longgrass());
/*     */ 
/* 105 */     ArrayList populators_main = new ArrayList();
/* 106 */     populators_main.add(new Populator_Lakes());
/* 107 */     populators_main.add(new Populator_Gravel());
/* 108 */     populators_main.add(new Populator_Lava_Lakes());
/* 109 */     populators_main.add(new Populator_Caves());
/* 110 */     populators_main.add(new Populator_Ores());
/* 111 */     populators_main.add(new Populator_Delayed(populators_delayed, this, getServer().getScheduler()));
/*     */ 
/* 113 */     return populators_main;
/*     */   }
/*     */ 
/*     */   private long buildSeed(String s)
/*     */   {
/*     */     long ret;
/*     */     try
/*     */     {
/* 125 */       ret = Long.parseLong(s);
/*     */     }
/*     */     catch (NumberFormatException e)
/*     */     {
/* 127 */       ret = s.hashCode();
/*     */     }
/* 129 */     return ret;
/*     */   }
/*     */ 
/*     */   public ChunkGenerator getDefaultWorldGenerator(String worldName, String id)
/*     */   {
/* 134 */     if (this.wgen == null) {
/* 135 */       this.wgen = new Nordic_ChunkGenerator(0L, buildPopulators());
/*     */     }
/* 137 */     return this.wgen;
/*     */   }
/*     */ 
/*     */   private boolean worldExists(String wname)
/*     */   {
/* 147 */     List<World> worlds = getServer().getWorlds();
/* 148 */     for (World world : worlds) {
/* 149 */       if (world.getName().equalsIgnoreCase(wname)) {
/* 150 */         return true;
/*     */       }
/*     */     }
/* 153 */     return false;
/*     */   }
/*     */ }

/* Location:           C:\Users\Baran\Downloads\Nordic.jar
 * Qualified Name:     me.simplex.nordic.Nordic
 * JD-Core Version:    0.6.2
 */