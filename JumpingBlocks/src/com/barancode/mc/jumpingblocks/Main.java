package com.barancode.mc.jumpingblocks;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

public class Main extends JavaPlugin implements Listener{
	
	List<FallingBlock> entities = new LinkedList<FallingBlock>();
	double vertSpeedMin = 1;
	double vertSpeedMax = 1;
	double horSpeedMin = 0;
	double horSpeedMax = 0;
	long delay = 40;
	
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();

		File configFile = new File(getDataFolder(), "config.yml"); // saving it this way instead of saveDefaultConfig() because that messes up comments
		if(!configFile.exists()){
			configFile.getParentFile().mkdirs();
			copy(getResource("config.yml"), configFile);
		}
		
		delay = getConfig().getLong("delay");
		vertSpeedMin = getConfig().getDouble("vertical-speed.min");
		vertSpeedMax = getConfig().getDouble("vertical-speed.max");
		horSpeedMin = getConfig().getDouble("horizontal-speed.min");
		horSpeedMax = getConfig().getDouble("horizontal-speed.max");
		
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @SuppressWarnings("deprecation")
			@Override
            public void run(){
            	for (int i = 1; i <= getConfig().getInt("lastid"); i++){
            		int x1 = getConfig().getInt("blocks." + i + ".x");
            		int y1 = getConfig().getInt("blocks." + i + ".y");
            		int z1 = getConfig().getInt("blocks." + i + ".z");
            		String world = getConfig().getString("blocks." + i + ".world");
            		String materialID = getConfig().getString("blocks." + i + ".type");
            		
            		Material material;
            		if (isInteger(materialID))
            			material = Material.getMaterial(Integer.parseInt(materialID));
            		else
            			material = Material.getMaterial(materialID.toUpperCase());
            			
        			FallingBlock block = Bukkit.getWorld(world).spawnFallingBlock(new Location(Bukkit.getWorld(world), x1, y1, z1), material, (byte) 0);
        			float x = (float) (Math.random() * (horSpeedMax - horSpeedMin) + horSpeedMin);
            		float y = (float) (Math.random() * (vertSpeedMax - vertSpeedMin) + vertSpeedMin);
            		float z = (float) (Math.random() * (horSpeedMax - horSpeedMin) + horSpeedMin);
                    block.setVelocity(new Vector(x, y, z));
                    entities.add(block);
                    block.setDropItem(false);
            	}
            }
        }, 0L, (long)delay);
	}
	
	public void onDisable(){
		for (Entity entity : entities){
			entity.remove();
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void entityBlockForm(EntityChangeBlockEvent event){
		if (entities.contains(event.getEntity())){
			event.setCancelled(true);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0){
			sender.sendMessage(ChatColor.BLUE + "===== JumpingBlocks by BaranCODE =====");
			sender.sendMessage(ChatColor.AQUA + "/jumpingblock create <block type ID>" + ChatColor.BLUE + " - Create a JumpingBlock");
			sender.sendMessage(ChatColor.AQUA + "/jumpingblock delete <JumpingBlock ID>" + ChatColor.BLUE + " - Delete a JumpingBlock");
			sender.sendMessage(ChatColor.AQUA + "/jumpingblock deletelast" + ChatColor.BLUE + " - Delete the most recent JumpingBlock");
			return true;
		}
		
		if (!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This command can't be executed from the console!");
			return true;
		}
		
		if (args.length == 2 && args[0].equalsIgnoreCase("create")){
			if (!sender.hasPermission("jumpingblock.create")){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return true;
			}
			if (isInteger(args[1])){
				if (isValidMaterial(Integer.parseInt(args[1]))){
					getConfig().set("lastid", getConfig().getInt("lastid") + 1);
					Player player = (Player) sender;
					getConfig().set("blocks." + getConfig().getInt("lastid") + ".world", player.getWorld().getName());
					getConfig().set("blocks." + getConfig().getInt("lastid") + ".x", player.getLocation().getBlockX());
					getConfig().set("blocks." + getConfig().getInt("lastid") + ".y", player.getLocation().getBlockY());
					getConfig().set("blocks." + getConfig().getInt("lastid") + ".z", player.getLocation().getBlockZ());
					getConfig().set("blocks." + getConfig().getInt("lastid") + ".type", Integer.parseInt(args[1]));
					saveConfig();
					player.sendMessage(ChatColor.GOLD + "You have created a JumpingBlock. The JumpingBlock ID is: " + getConfig().getInt("lastid"));
				} else {
					sender.sendMessage(ChatColor.RED + "That is not a valid material");
				}
			} else {
				String response = isValidMaterial(args[1]);
				if (response.equals("bad")){
					sender.sendMessage(ChatColor.RED + "That is not a valid material");
					return true;
				}
				String material = "";
				if (response.equals("good"))
					material = args[1];
				else
					material = response;
					
				getConfig().set("lastid", getConfig().getInt("lastid") + 1);
				Player player = (Player) sender;
				getConfig().set("blocks." + getConfig().getInt("lastid") + ".world", player.getWorld().getName());
				getConfig().set("blocks." + getConfig().getInt("lastid") + ".x", player.getLocation().getBlockX());
				getConfig().set("blocks." + getConfig().getInt("lastid") + ".y", player.getLocation().getBlockY());
				getConfig().set("blocks." + getConfig().getInt("lastid") + ".z", player.getLocation().getBlockZ());
				getConfig().set("blocks." + getConfig().getInt("lastid") + ".type", material);
				saveConfig();
				player.sendMessage(ChatColor.GOLD + "You have created a JumpingBlock. The JumpingBlock ID is: " + getConfig().getInt("lastid"));
			}
			return true;
		} else if (args.length == 2 && args[0].equalsIgnoreCase("delete")){
			if (!sender.hasPermission("jumpingblock.delete")){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return true;
			}
			getConfig().set("blocks." + Integer.parseInt(args[1]), "");
			if (Integer.parseInt(args[1]) == getConfig().getInt("lastid")){
				getConfig().set("lastid", getConfig().getInt("lastid") - 1);
			}
			saveConfig();
			sender.sendMessage(ChatColor.GOLD + "You have deleted JumpingBlock #" + args[1]);
			return true;
		} else if (args.length == 1 && args[0].equalsIgnoreCase("deletelast")){
			if (!sender.hasPermission("jumpingblock.delete")){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return true;
			}
			int id = getConfig().getInt("lastid");
			if (id == 0){
				sender.sendMessage(ChatColor.RED + "There are no JumpingBlocks left");
				return true;
			}
			getConfig().set("blocks." + id, "");
			getConfig().set("lastid", id - 1);
			saveConfig();
			sender.sendMessage(ChatColor.GOLD + "You have deleted the most recently created JumpingBlock");
			return true;
		}
		return false;
	}
	
	boolean isInteger(String s){
		try{
			Integer.parseInt(s);
		} catch (NumberFormatException e){
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	boolean isValidMaterial(int id){
	    try{
	        for(Material m : Material.values()){
	            if (id == m.getId()){
	                return true;
	            }
	        }
	        return false;
	    } catch (Exception e){
	    	return false;
	    }
	}
	
	String isValidMaterial(String name){
	    try{
	        for(Material m : Material.values()){
	            if (name.equalsIgnoreCase(m.name())){
	                return "good";
	            } else if ((m.name().replaceAll("_", "")).equalsIgnoreCase(name)){
	            	return m.name();
	            }
	        }
	        return "bad";
	    } catch (Exception e){
	    	return "bad";
	    }
	}
	
	// Function by Ne0nx3r0, from https://bukkit.org/threads/yaml-comments.75923/
	private void copy(InputStream in, File file) {
		try {
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while((len=in.read(buf))>0){
				out.write(buf,0,len);
			}
			out.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
