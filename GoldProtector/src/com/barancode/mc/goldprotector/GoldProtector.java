package com.barancode.mc.goldprotector;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class GoldProtector extends JavaPlugin
  implements Listener
{
  Economy econ = null;
  GoldProtectorCommandExecutor commandExecutor = new GoldProtectorCommandExecutor(this);
  HashMap<String, Integer> playing = new HashMap<String, Integer>();
  HashMap<String, Boolean> athome = new HashMap<String, Boolean>();
  HashMap<String, Integer> previewing = new HashMap<String, Integer>();
  
  HashMap<String, Integer> scores = new HashMap<String, Integer>();

  public void onEnable()
  {
    getServer().getPluginManager().registerEvents(this, this);
    getLogger().info("Gold Protector has been enabled");

    getLogger().info("Enabling economy...");
    if (!setupEconomy()) {
      getLogger().severe("No Vault dependency found!");
      return;
    }
    getLogger().info("Economy enabled");

    saveDefaultConfig();

    getCommand("team").setExecutor(this.commandExecutor);
    getCommand("random").setExecutor(this.commandExecutor);
    getCommand("edit").setExecutor(this.commandExecutor);
    getCommand("public").setExecutor(this.commandExecutor);
    getCommand("start").setExecutor(this.commandExecutor);
    getCommand("home").setExecutor(this.commandExecutor);
    getCommand("play").setExecutor(this.commandExecutor);
    getCommand("collect").setExecutor(this.commandExecutor);
    getCommand("setspawn").setExecutor(this.commandExecutor);
    getCommand("join").setExecutor(this.commandExecutor);
    getCommand("vote").setExecutor(this.commandExecutor);
    
    for (int i = getConfig().getInt("spawncount"); i > 0; i--) {
    	scores.put(getConfig().getString("spawns.player." + i + ".name"), getConfig().getInt("spawns.player." + i + ".points"));
    }
  }

  private boolean setupEconomy()
  {
      RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
      if (economyProvider != null) {
    	  econ = economyProvider.getProvider();
      }

      return (econ != null);
  }

  public ChunkGenerator getDefaultWorldGenerator(String worldName, String id)
  {
    return new GoldProtectorGenerator();
  }
  
  public void fillHotbar(Player player){
	  player.getInventory().addItem(new ItemStack(Material.GOLD_BLOCK, 1));
	  player.getInventory().addItem(new ItemStack(Material.DIRT, 1));
	  player.getInventory().addItem(new ItemStack(Material.STONE, 1));
	  player.getInventory().addItem(new ItemStack(Material.COBBLESTONE, 1));
	  player.getInventory().addItem(new ItemStack(Material.WOOD, 1));
	  player.getInventory().addItem(new ItemStack(Material.GRAVEL, 1));
	  player.getInventory().addItem(new ItemStack(Material.SANDSTONE, 1));
	  player.getInventory().addItem(new ItemStack(Material.GLASS, 1));
	  player.getInventory().addItem(new ItemStack(Material.TORCH, 1));
	  // Although it is deprecated, the updateInventory() has to be used, or a bug
	  // occurs where the person sometimes can't see the blocks in his hotbar
	  // until he clicks on them
	  player.updateInventory();
  }
  
  public void clearHotbar(Player player){
	  player.getInventory().clear();
	  player.updateInventory();
  }
  
  
  @EventHandler
  public void playerJoin(PlayerJoinEvent event){
	  if (getConfig().getBoolean("players." + event.getPlayer().getName())) {
	      int i = getConfig().getInt("key." + event.getPlayer().getName());
	
	      World w = Bukkit.getWorld(getConfig().getString("spawns.player." + i + ".world"));
	      double x = getConfig().getDouble("spawns.player." + i + ".x");
	      double y = getConfig().getDouble("spawns.player." + i + ".y");
	      double z = getConfig().getDouble("spawns.player." + i + ".z");
	      float yaw = (float)getConfig().getDouble("spawns.player." + i + ".yaw");
	      float pitch = (float)getConfig().getDouble("spawns.player." + i + ".pitch");
	      Location loc = new Location(w, x, y, z, yaw, pitch);
	
	      event.getPlayer().teleport(loc);
	
	      event.getPlayer().setGameMode(GameMode.CREATIVE);
	      event.getPlayer().setAllowFlight(true);
	      event.getPlayer().setFlying(true);
	      fillHotbar(event.getPlayer());
	      athome.put(event.getPlayer().getName(), true);
	  } else {
		  event.getPlayer().sendMessage(ChatColor.GOLD + "Welcome to the GoldProtector server, " + event.getPlayer().getName() + "!");
	  }
  }
  
  @EventHandler
  public void playerRespawn(PlayerRespawnEvent event){
      int i = getConfig().getInt("key." + event.getPlayer().getName());
  	
      World w = Bukkit.getWorld(getConfig().getString("spawns.player." + i + ".world"));
      double x = getConfig().getDouble("spawns.player." + i + ".x");
      double y = getConfig().getDouble("spawns.player." + i + ".y");
      double z = getConfig().getDouble("spawns.player." + i + ".z");
      float yaw = (float)getConfig().getDouble("spawns.player." + i + ".yaw");
      float pitch = (float)getConfig().getDouble("spawns.player." + i + ".pitch");
      Location loc = new Location(w, x, y, z, yaw, pitch);

      event.setRespawnLocation(loc);

      event.getPlayer().setGameMode(GameMode.CREATIVE);
      event.getPlayer().setAllowFlight(true);
      event.getPlayer().setFlying(true);
      fillHotbar(event.getPlayer());
      athome.put(event.getPlayer().getName(), true);
      if (playing.containsKey(event.getPlayer().getName())) playing.remove(event.getPlayer().getName());
      if (previewing.containsKey(event.getPlayer().getName())) previewing.remove(event.getPlayer().getName());
  }
  
  @EventHandler
  public void onPVP(EntityDamageByEntityEvent event){
	  event.setCancelled(true);
  }
  
  @EventHandler
  public void hunger(FoodLevelChangeEvent event) {
	  if (event.getFoodLevel() != 20){
		  event.setFoodLevel(20);
	  }
  }

  @EventHandler(priority=EventPriority.HIGHEST)
  public void blockplace(BlockPlaceEvent event)
  {
    boolean result = true;

    boolean typeresult = false;
    int z = event.getBlock().getLocation().getBlockZ();
    int x = event.getBlock().getLocation().getBlockX();
    int y = event.getBlock().getLocation().getBlockY();

    if ((z == 0) || (z % 50 == 0) || (z == -1) || ((z + 1) % 50 == 0)) result = false;
    else if ((x == 0) || (x % 50 == 0) || (x == -1) || ((x + 1) % 50 == 0)) result = false;
    else if ((y == 0) || (y == 50)) result = false;
    else if (event.getBlock().getType() == Material.GOLD_BLOCK)
    {
      
      if (getConfig().getBoolean("goldblocks." + event.getPlayer().getName() + ".DoNotTouchThis")) {
    	Location loc = new Location(Bukkit.getWorld(getConfig().getString("goldblocks." + event.getPlayer().getName() + ".world")), getConfig().getInt("goldblocks." + event.getPlayer().getName() + ".x"), getConfig().getInt("goldblocks." + event.getPlayer().getName() + ".y"), getConfig().getInt("goldblocks." + event.getPlayer().getName() + ".z"));
        if (loc.getBlock().getType() == Material.GOLD_BLOCK) {
          event.getPlayer().sendMessage(ChatColor.RED + "You have already placed your challenge's gold block. Destroy it first to change it");
          result = false;
        } else {
          loc = event.getBlock().getLocation();
          getConfig().set("goldblocks." + event.getPlayer().getName() + ".world", loc.getWorld().getName().toString());
          getConfig().set("goldblocks." + event.getPlayer().getName() + ".x", Double.valueOf(loc.getX()));
          getConfig().set("goldblocks." + event.getPlayer().getName() + ".y", Double.valueOf(loc.getY()));
          getConfig().set("goldblocks." + event.getPlayer().getName() + ".z", Double.valueOf(loc.getZ()));
          getConfig().set("goldblocks." + event.getPlayer().getName() + ".DoNotTouchThis", Boolean.valueOf(true));
          saveConfig();
          event.getPlayer().sendMessage(ChatColor.AQUA + "You have successfully placed your challenge's gold block!");
        }
      }
      else {
        Location loc = event.getBlock().getLocation();
        getConfig().set("goldblocks." + event.getPlayer().getName() + ".world", loc.getWorld().getName().toString());
        getConfig().set("goldblocks." + event.getPlayer().getName() + ".x", Double.valueOf(loc.getX()));
        getConfig().set("goldblocks." + event.getPlayer().getName() + ".y", Double.valueOf(loc.getY()));
        getConfig().set("goldblocks." + event.getPlayer().getName() + ".z", Double.valueOf(loc.getZ()));
        getConfig().set("goldblocks." + event.getPlayer().getName() + ".DoNotTouchThis", Boolean.valueOf(true));
        saveConfig();
        event.getPlayer().sendMessage(ChatColor.AQUA + "You have successfully placed your challenge's gold block!");
      }

    }

    if (event.getBlock().getType() == Material.DIRT) typeresult = true;
    if (event.getBlock().getType() == Material.GOLD_BLOCK) typeresult = true;
    if (event.getBlock().getType() == Material.TORCH) typeresult = true;
    if (event.getBlock().getType() == Material.WOOD) typeresult = true;
    if (event.getBlock().getType() == Material.STONE) typeresult = true;
    if (event.getBlock().getType() == Material.COBBLESTONE) typeresult = true;
    if (event.getBlock().getType() == Material.GLASS) typeresult = true;
    if (event.getBlock().getType() == Material.GRAVEL) typeresult = true;
    if (event.getBlock().getType() == Material.SANDSTONE) typeresult = true;

    if (!result) {
      event.setCancelled(true);
    }
    if (!typeresult) {
      event.setCancelled(true);
      event.getPlayer().sendMessage(ChatColor.RED + "You can not place that block");
    }
  }

  @EventHandler(priority=EventPriority.HIGHEST)
  public void blockbreak(BlockBreakEvent event)
  {
    int z = event.getBlock().getLocation().getBlockZ();
    int x = event.getBlock().getLocation().getBlockX();
    int y = event.getBlock().getLocation().getBlockY();

    if ((z == 0) || (z % 50 == 0) || (z == -1) || ((z + 1) % 50 == 0)) event.setCancelled(true);
    if ((x == 0) || (x % 50 == 0) || (x == -1) || ((x + 1) % 50 == 0)) event.setCancelled(true);
    if ((y == 0) || (y == 50)) event.setCancelled(true);

    if ((event.getBlock().getType() == Material.GOLD_BLOCK) && (getConfig().getBoolean("goldblocks." + event.getPlayer().getName() + ".DoNotTouchThis"))) {
      getConfig().set("goldblocks." + event.getPlayer().getName(), "");
      event.getPlayer().sendMessage(ChatColor.AQUA + "You have destroyed your challenge's gold block");
      saveConfig();
    }
    
    if (previewing.containsKey(event.getPlayer().getName())) {
    	event.setCancelled(true);
    }
  }

  @EventHandler
  public void interact(PlayerInteractEvent event) {
    if ((event.getAction() == Action.LEFT_CLICK_BLOCK) || (event.getAction() == Action.RIGHT_CLICK_BLOCK))
    {
      if (event.getClickedBlock().getType() == Material.GOLD_BLOCK && playing.containsKey(event.getPlayer().getName())) {
        int count = getConfig().getInt("spawncount");
        Location loc = event.getClickedBlock().getLocation();
        for (int i = count; i > 0; i--)
        {
          if ((getConfig().getInt("goldblocks." + getConfig().getString(new StringBuilder("spawns.player.").append(i).append(".name").toString()) + ".x") == loc.getBlockX()) && 
            (getConfig().getInt("goldblocks." + getConfig().getString(new StringBuilder("spawns.player.").append(i).append(".name").toString()) + ".y") == loc.getBlockY()) && 
            (getConfig().getInt("goldblocks." + getConfig().getString(new StringBuilder("spawns.player.").append(i).append(".name").toString()) + ".z") == loc.getBlockZ()) && 
            (getConfig().getString("goldblocks." + getConfig().getString(new StringBuilder("spawns.player.").append(i).append(".name").toString()) + ".world").equals(loc.getWorld().getName())))
          {
            double money = 30.0D;

            this.econ.withdrawPlayer(getConfig().getString("spawns.player." + i + ".name"), 30.0D);

            this.econ.depositPlayer(event.getPlayer().getName(), 30.0D);

            double percentmultiplier = getConfig().getInt("percentOfJackpotOnWin") / 100;
            double percent = this.econ.getBalance(getConfig().getString(new StringBuilder("spawns.player.").append(i).append(".name").toString()) + "_jackpot") * percentmultiplier;
            this.econ.withdrawPlayer(getConfig().getString(new StringBuilder("spawns.player.").append(i).append(".name").toString()) + "_jackpot", percent);
            this.econ.depositPlayer(event.getPlayer().getName(), percent);
            money += percent;
            event.getPlayer().sendMessage(ChatColor.GREEN + "You have won " + money + " from this challenge, and have $" + this.econ.format(this.econ.getBalance(event.getPlayer().getName())));

            int key = getConfig().getInt("key." + event.getPlayer().getName());

            World w = Bukkit.getWorld(getConfig().getString("spawns.player." + key + ".world"));
            double x = getConfig().getDouble("spawns.player." + key + ".x");
            double y = getConfig().getDouble("spawns.player." + key + ".y");
            double z = getConfig().getDouble("spawns.player." + key + ".z");
            float yaw = (float)getConfig().getDouble("spawns.player." + key + ".yaw");
            float pitch = (float)getConfig().getDouble("spawns.player." + key + ".pitch");
            Location location = new Location(w, x, y, z, yaw, pitch);

            event.getPlayer().teleport(location);

            event.getPlayer().setGameMode(GameMode.CREATIVE);
            event.getPlayer().setAllowFlight(true);
            event.getPlayer().setFlying(true);
            fillHotbar(event.getPlayer());
            playing.remove(event.getPlayer().getName());
            athome.put(event.getPlayer().getName(), true);
          }
        }
      }
    }
  }
}