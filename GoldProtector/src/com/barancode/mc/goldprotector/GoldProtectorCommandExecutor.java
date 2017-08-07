package com.barancode.mc.goldprotector;


import java.util.Random;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GoldProtectorCommandExecutor
  implements CommandExecutor
{
  public GoldProtector plugin;

  public GoldProtectorCommandExecutor(GoldProtector plugin)
  {
    this.plugin = plugin;
  }

  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    Player player = (Player)sender;

    if (cmd.getName().equalsIgnoreCase("team"))
    {
      if (args.length == 2)
      {
        if (args[0].equalsIgnoreCase("create")) {
          plugin.getConfig().set("teams." + args[1] + ".DoNotTouchThis", Boolean.valueOf(true));
          plugin.saveConfig();
          player.sendMessage(ChatColor.AQUA + "You have created a team called " + args[1]);
          return true;
        }
      }
    }

    if (cmd.getName().equalsIgnoreCase("random"))
    {
      if (plugin.econ.getBalance(player.getName()) < 12) {
    	  // It's 12 because 2 is the /random price, and the /play price is 10. There's no point
    	  // letting them preview if they won't be able to play it
    	  
    	  player.sendMessage(ChatColor.RED + "You don't have enough money. You only have $ " + plugin.econ.getBalance(player.getName()));
    	  return true;
      }
      Random random = new Random();

      Random randomtype = new Random();

      if (plugin.getConfig().getInt("spawncount") == 0) {
        player.sendMessage(ChatColor.RED + "There aren't any available spawns");
        return true;
      }

      int count = 0;

      String typename = "player.";
      do
      {
        int i = random.nextInt(plugin.getConfig().getInt("spawncount"));

        int type = randomtype.nextInt(2);
        if (type == 0)
          typename = "player.";
        else if (type == 1) {
          typename = "team.";
        }

        i++;

        count++;

        if (plugin.getConfig().getBoolean("spawns." + typename + i + ".public") && plugin.getConfig().getString("spawns." + typename + i + ".name") != player.getName())
        {
          World w = Bukkit.getWorld(plugin.getConfig().getString("spawns." + typename + i + ".world"));
          double x = plugin.getConfig().getDouble("spawns." + typename + i + ".x");
          double y = plugin.getConfig().getDouble("spawns." + typename + i + ".y");
          double z = plugin.getConfig().getDouble("spawns." + typename + i + ".z");
          float yaw = (float)plugin.getConfig().getDouble("spawns." + typename + i + ".yaw");
          float pitch = (float)plugin.getConfig().getDouble("spawns." + typename + i + ".pitch");
          Location loc = new Location(w, x, y, z, yaw, pitch);

          player.sendMessage(ChatColor.GREEN + "Builder: " + plugin.getConfig().getString(new StringBuilder("spawns.").append(typename).append(i).append(".name").toString()) + ", Points: " + plugin.getConfig().getInt(new StringBuilder("spawns.").append(typename).append(i).append(".points").toString()));

          player.teleport(loc);

          EconomyResponse r = plugin.econ.withdrawPlayer(player.getName(), 2.0D);
          if (r.transactionSuccess())
            sender.sendMessage(ChatColor.GREEN + "You have payed $2 to visit a challenge, and have $" + plugin.econ.format(r.balance) + " left");
          else {
            sender.sendMessage(String.format("An error occured: %s", new Object[] { r.errorMessage }));
          }

          plugin.previewing.put(player.getName(), Integer.valueOf(i));

          player.setGameMode(GameMode.ADVENTURE);
          player.setAllowFlight(true);
          player.setFlying(true);
          plugin.clearHotbar(player);
          if (plugin.playing.containsKey(player.getName())) {
              plugin.playing.remove(player.getName());
           }
          if (plugin.athome.containsKey(player.getName())) {
          	plugin.athome.remove(player.getName());
          }
          return true;
        }
      }
      while (count < 100);

      player.sendMessage(ChatColor.RED + "An error has occured. Try again");
      return true;
    }

    if (cmd.getName().equalsIgnoreCase("edit")) {
      plugin.getConfig().set("spawns.player." + plugin.getConfig().getInt(new StringBuilder("key.").append(player.getName()).toString()) + ".public", Boolean.valueOf(false));
      plugin.saveConfig();
      player.sendMessage(ChatColor.AQUA + "Your challenge is now in edit mode, and people can't join");
      return true;
    }

    if (cmd.getName().equalsIgnoreCase("public")) {
      plugin.getConfig().set("spawns.player." + plugin.getConfig().getInt(new StringBuilder("key.").append(player.getName()).toString()) + ".public", Boolean.valueOf(true));
      plugin.saveConfig();
      player.sendMessage(ChatColor.AQUA + "You have made your challenge public!");
      return true;
    }

    if (cmd.getName().equalsIgnoreCase("start"))
    {
      if (plugin.getConfig().getBoolean("players." + sender.getName())) {
        Bukkit.getPlayer(sender.getName()).sendMessage(ChatColor.RED + "You have already started playing!");
        return true;
      }

      plugin.getConfig().set("players." + sender.getName(), Boolean.valueOf(true));

      player.teleport(new Location(Bukkit.getWorld(plugin.getConfig().getString("worldname")), plugin.getConfig().getInt("spawncount") * 50 + 3, 4.0D, 3.0D));

      int i = plugin.getConfig().getInt("spawncount");
      i++;
      plugin.getConfig().set("spawncount", Integer.valueOf(i));
      Location loc = player.getLocation();
      plugin.getConfig().set("spawns.player." + i + ".world", loc.getWorld().getName().toString());
      plugin.getConfig().set("spawns.player." + i + ".x", Double.valueOf(loc.getX()));
      plugin.getConfig().set("spawns.player." + i + ".y", Double.valueOf(loc.getY()));
      plugin.getConfig().set("spawns.player." + i + ".z", Double.valueOf(loc.getZ()));
      plugin.getConfig().set("spawns.player." + i + ".yaw", Float.valueOf(loc.getYaw()));
      plugin.getConfig().set("spawns.player." + i + ".pitch", Float.valueOf(loc.getPitch()));
      plugin.getConfig().set("spawns.player." + i + ".name", player.getName().toString());
      plugin.getConfig().set("spawns.player." + i + ".points", Integer.valueOf(0));
      plugin.getConfig().set("spawns.player." + i + ".voters", "");
      plugin.getConfig().set("spawns.player." + i + ".DoNotTouchThis", Boolean.valueOf(true));
      plugin.getConfig().set("spawns.player." + i + ".public", Boolean.valueOf(false));

      plugin.getConfig().set("key." + player.getName(), Integer.valueOf(i));
      plugin.saveConfig();
      player.sendMessage(ChatColor.AQUA + "This is your building area. Your challenge is currently in edit mode. When it is finished, do " + 
        ChatColor.WHITE + "/public" + ChatColor.AQUA + " to make it public. " + ChatColor.GREEN + "Make sure to place your challenge's gold block (finishing point)");
      plugin.econ.createPlayerAccount(player.getName() + "_jackpot");

      player.setGameMode(GameMode.CREATIVE);
      player.setAllowFlight(true);
      player.setFlying(true);
      plugin.fillHotbar(player);
      plugin.athome.put(player.getName(), true);
      
      //plugin.scores.put(player.getName(), 0);
      return true;
    }
    if (cmd.getName().equalsIgnoreCase("home"))
    {
      int i = plugin.getConfig().getInt("key." + sender.getName());

      World w = Bukkit.getWorld(plugin.getConfig().getString("spawns.player." + i + ".world"));
      double x = plugin.getConfig().getDouble("spawns.player." + i + ".x");
      double y = plugin.getConfig().getDouble("spawns.player." + i + ".y");
      double z = plugin.getConfig().getDouble("spawns.player." + i + ".z");
      float yaw = (float)plugin.getConfig().getDouble("spawns.player." + i + ".yaw");
      float pitch = (float)plugin.getConfig().getDouble("spawns.player." + i + ".pitch");
      Location loc = new Location(w, x, y, z, yaw, pitch);

      player.teleport(loc);

      if (plugin.previewing.containsKey(player.getName())) {
        plugin.previewing.remove(player.getName());
      }
      player.setGameMode(GameMode.CREATIVE);
      player.setAllowFlight(true);
      player.setFlying(true);
      plugin.fillHotbar(player);
      if (plugin.playing.containsKey(player.getName())) {
        plugin.playing.remove(player.getName());
      }
      plugin.athome.put(player.getName(), true);
      return true;
    }

    if (cmd.getName().equalsIgnoreCase("play"))
    {
      // Dont have to check if player has enough money, here, because already checked in /random
      // and /join if the player had enough money to do /play
      int i = 0;

      if (plugin.previewing.containsKey(player.getName()))
      {
        i = ((Integer)plugin.previewing.get(player.getName())).intValue();
      } else {
        player.sendMessage(ChatColor.RED + "You are not previewing a challenge!");
        return true;
      }

      World w = Bukkit.getWorld(plugin.getConfig().getString("spawns.player." + i + ".world"));
      double x = plugin.getConfig().getDouble("spawns.player." + i + ".x");
      double y = plugin.getConfig().getDouble("spawns.player." + i + ".y");
      double z = plugin.getConfig().getDouble("spawns.player." + i + ".z");
      float yaw = (float)plugin.getConfig().getDouble("spawns.player." + i + ".yaw");
      float pitch = (float)plugin.getConfig().getDouble("spawns.player." + i + ".pitch");
      Location loc = new Location(w, x, y, z, yaw, pitch);
      player.teleport(loc);

      EconomyResponse r = plugin.econ.withdrawPlayer(player.getName(), 10.0D);

      plugin.econ.depositPlayer(plugin.getConfig().getString(new StringBuilder("spawns.player.").append(i).append(".name").toString()) + "_jackpot", 10.0D);
      if (r.transactionSuccess())
        sender.sendMessage(ChatColor.GREEN + "You have payed $10 to play a challenge, and have $" + plugin.econ.format(r.balance) + " left");
      else {
        sender.sendMessage(String.format("An error occured: %s", new Object[] { r.errorMessage }));
      }
      player.setAllowFlight(false);
      player.setFlying(false);
      plugin.playing.put(player.getName(), i);
      plugin.previewing.remove(player.getName());
      
      player.sendMessage(ChatColor.DARK_GREEN + "You can do:");
      player.sendMessage(ChatColor.DARK_GREEN + "/vote <up or down>");
      player.sendMessage(ChatColor.DARK_GREEN + "To vote for this challenge. Up means you like it, down means you don't");
      return true;
    }

    if (cmd.getName().equalsIgnoreCase("collect"))
    {
      double amount = plugin.econ.getBalance(player.getName() + "_jackpot");

      plugin.econ.withdrawPlayer(player.getName() + "_jackpot", amount);

      plugin.econ.depositPlayer(player.getName(), amount);

      player.sendMessage(ChatColor.AQUA + "You have collected $" + amount + " from your jackpot");
      return true;
    }
    
    if (cmd.getName().equalsIgnoreCase("setspawn"))
    {
    	if (plugin.athome.containsKey(player.getName())) {
	    	int i = plugin.getConfig().getInt("key." + player.getName());
	    	
	        Location loc = player.getLocation();
	        plugin.getConfig().set("spawns.player." + i + ".world", loc.getWorld().getName().toString());
	        plugin.getConfig().set("spawns.player." + i + ".x", Double.valueOf(loc.getX()));
	        plugin.getConfig().set("spawns.player." + i + ".y", Double.valueOf(loc.getY()));
	        plugin.getConfig().set("spawns.player." + i + ".z", Double.valueOf(loc.getZ()));
	        plugin.getConfig().set("spawns.player." + i + ".yaw", Float.valueOf(loc.getYaw()));
	        plugin.getConfig().set("spawns.player." + i + ".pitch", Float.valueOf(loc.getPitch()));
	        
	        plugin.saveConfig();
	        player.sendMessage(ChatColor.GREEN + "You have successfully changed the spawn of your plot");
    	} else {
    		player.sendMessage(ChatColor.RED + "You're not in your own plot!");
    	}
    	return true;
    }
    
    if (cmd.getName().equalsIgnoreCase("join")) {
    	if (args.length != 1) {
    		return false;
    	}
    	
    	if (args[0].equals(player.getName())) {
    		player.sendMessage(ChatColor.RED + "You can't play your own challenge!");
    		return true;
    	}
    	
        if (plugin.econ.getBalance(player.getName()) < 12) {
      	  // It's 12 because 2 is the /random price, and the /play price is 10. There's no point
      	  // letting them preview if they won't be able to play it
      	  
      	  player.sendMessage(ChatColor.RED + "You don't have enough money. You only have $ " + plugin.econ.getBalance(player.getName()));
      	  return true;
        }
        
          int i = plugin.getConfig().getInt("key." + args[0]);
          
          String typename = "player.";
          
          if (plugin.getConfig().getBoolean("spawns." + typename + i + ".public"))
          {
            World w = Bukkit.getWorld(plugin.getConfig().getString("spawns." + typename + i + ".world"));
            double x = plugin.getConfig().getDouble("spawns." + typename + i + ".x");
            double y = plugin.getConfig().getDouble("spawns." + typename + i + ".y");
            double z = plugin.getConfig().getDouble("spawns." + typename + i + ".z");
            float yaw = (float)plugin.getConfig().getDouble("spawns." + typename + i + ".yaw");
            float pitch = (float)plugin.getConfig().getDouble("spawns." + typename + i + ".pitch");
            Location loc = new Location(w, x, y, z, yaw, pitch);

            player.sendMessage(ChatColor.GREEN + "Builder: " + plugin.getConfig().getString(new StringBuilder("spawns.").append(typename).append(i).append(".name").toString()));

            player.teleport(loc);

            EconomyResponse r = plugin.econ.withdrawPlayer(player.getName(), 2.0D);
            if (r.transactionSuccess())
              sender.sendMessage(ChatColor.GREEN + "You have payed $2 to visit a challenge, and have $" + plugin.econ.format(r.balance) + " left");
            else {
              sender.sendMessage(String.format("An error occured: %s", new Object[] { r.errorMessage }));
            }

            plugin.previewing.put(player.getName(), Integer.valueOf(i));

            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setFlying(true);
            plugin.clearHotbar(player);
            if (plugin.playing.containsKey(player.getName())) {
                plugin.playing.remove(player.getName());
             }
            if (plugin.athome.containsKey(player.getName())) {
            	plugin.athome.remove(player.getName());
            }
          } else if (plugin.getConfig().getBoolean("spawns." + typename + i + ".DoNotTouchThis")) {
        	  player.sendMessage(ChatColor.RED + "Challenge not public!");
          } else {
        	  player.sendMessage(ChatColor.RED + "That player doesn't have a challenge");
          }
        return true;
    }
    if (cmd.getName().equalsIgnoreCase("vote")) {
    	int operation = 0;
    	if (args.length != 1) {
    		return false;
    	}
    	if (!plugin.playing.containsKey(player.getName())) {
    		player.sendMessage(ChatColor.RED + "You are not playing a challenge!");
    		return true;
    	}
    	int i = plugin.playing.get(player.getName());
    	if (plugin.getConfig().getBoolean("spawns.player." + i + ".voters." + player.getName())) {
    		player.sendMessage(ChatColor.RED + "You already voted for this challenge!");
    		return true;
    	}
    	
    	if (args[0].equalsIgnoreCase("up")) operation = 1;
    	else if (args[0].equalsIgnoreCase("down")) operation = -1;
    	else {
    		return false;
    	}
    	plugin.getConfig().set("spawns.player." + i + ".voters." + player.getName(), true);
    	plugin.getConfig().set("spawns.player." + i + ".points", plugin.getConfig().getInt("spawns.player." + i + ".points") + operation);
    	plugin.saveConfig();
    	
    	if (plugin.getConfig().getInt("spawns.player." + i + ".points") == -10) {
    		plugin.getConfig().set("spawns.player." + i + ".public", false);
    		plugin.saveConfig();
    	}
    	
    	//plugin.scores.put(plugin.getConfig().getString("spawns.player." + i + ".name"), plugin.getConfig().getInt("spawns.player." + i + ".points"));
    	
    	player.sendMessage(ChatColor.GOLD + "You have voted " + args[0] + " for this challenge");
        return true;
    }
    return false;
  }
}