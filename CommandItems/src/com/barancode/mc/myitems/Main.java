package com.barancode.mc.myitems;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInventoryEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	
	ItemFile ItemFile;
	HashMap<ItemMeta, String> items = new HashMap<ItemMeta, String>();
	HashMap<Integer, String> globalitems = new HashMap<Integer, String>();
	HashMap<Integer, String> singleitems = new HashMap<Integer, String>();
	List<String> events = new LinkedList<String>();
	
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		ItemFile = new ItemFile(this);
		File demoitem = new File(getDataFolder(), "demo.txt");
		
		if (!getDataFolder().exists()){
			try {
				Files.createDirectory(Paths.get(getDataFolder().getAbsolutePath()));
				demoitem.createNewFile();
				
				BufferedWriter out = new BufferedWriter(new FileWriter(demoitem));
				out.write("id: 280");
				out.newLine();
				out.write("displayname: &2DemoItem");
				out.newLine();
				out.write("lore: &3This is a && &3demo item");
				out.newLine();
				out.write("damageentityevent: me hit a <clicked>");
				out.newLine();
				out.write("rightclickblockevent: me right-clicked a block");
				out.newLine();
				out.write("leftclickairevent: me left-clicked the air");
				out.newLine();
				out.write("holdevent: me held a special item");
				out.newLine();
				out.write("global: false");
				out.newLine();
				out.write("single-use: false");
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		saveDefaultConfig();
		
		for (File file : getDataFolder().listFiles()){
			if (!file.getName().equals("config.yml")){
				
				String name = file.getName();
				name = name.replace(".txt", "");
				
				
				String displayname = ItemFile.read(name, "displayname");
				displayname = replaceColors(displayname);
				
				String lore = ItemFile.read(name, "lore");
				String[] loreparts = lore.split("&&");
				List<String> list = new LinkedList<String>();
				for (int i = 0; i < loreparts.length; i++){
					list.add(replaceColors(loreparts[i]));
				}
				
				String id = ItemFile.read(name, "id");
				String[] idparts = id.split(":");
				
				ItemStack itemstack = new ItemStack(Material.AIR, 1);
				
				if (idparts.length == 2){
					itemstack = new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1, (byte)Integer.parseInt(idparts[1]));
				} else if (idparts.length == 1){
					itemstack = new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1);
				}

				ItemMeta meta = itemstack.getItemMeta();
				meta.setDisplayName(displayname);
				meta.setLore(list);
				items.put(meta, name);
				
				String global = ItemFile.read(name, "global");
				if (global.equalsIgnoreCase("true")){
					globalitems.put(Integer.parseInt(id), name);
				}
				
				String single = ItemFile.read(name, "single-use");
				if (single.equalsIgnoreCase("true")){
					singleitems.put(Integer.parseInt(id), name);
				}
			}
		}
		getLogger().info("Loaded items:");
		for (String name : items.values()){
			getLogger().info("- " + name);
		}
		
		events.add("damageentityevent");
		events.add("rightclickentityevent");
		events.add("leftclickblockevent");
		events.add("rightclickblockevent");
		events.add("leftclickairevent");
		events.add("rightclickairevent");
		events.add("consumeevent");
		events.add("blockplaceevent");
		events.add("holdevent");
		events.add("throwevent");
		events.add("bedenterevent");
	}
	
	@EventHandler
	public void throwEvent(ProjectileLaunchEvent event){
		if (!(event.getEntity() instanceof Player)){
			return;
		}
		ItemStack itemstack = ((Player)event.getEntity().getShooter()).getItemInHand();
		if (items.containsKey(itemstack.getItemMeta())){
			CommandSender sender;
			if (getConfig().getBoolean("console-executor")){
				sender = Bukkit.getConsoleSender();
			} else {
				sender = (Player)event.getEntity().getShooter();
			}
			String command = ItemFile.read(items.get(itemstack.getItemMeta()), "throwevent");
			if (command != null){
				if (!sender.hasPermission("myitems.use." + items.get(itemstack.getItemMeta()))){
					sender.sendMessage(ChatColor.RED + "You do not have permission");
					return;
				}
				String[] commands = command.split(" && ");
				for (String s : commands){
					s = s.replaceAll("<user>", ((Player)event.getEntity()).getName());
					Bukkit.getServer().dispatchCommand(sender, s);
				}
				
				String single = ItemFile.read(items.get(itemstack.getItemMeta()), "single-use");
				if (single.equals("true")){
					if (itemstack.getAmount() > 1) itemstack.setAmount(itemstack.getAmount() - 1);
					else ((Player)event.getEntity().getShooter()).setItemInHand(new ItemStack(Material.AIR));
				}
			}
		} else if (globalitems.containsKey(itemstack.getTypeId()) && ItemFile.read(globalitems.get(itemstack.getTypeId()), "global").equals("true")){
			CommandSender sender;
			if (getConfig().getBoolean("console-executor")){
				sender = Bukkit.getConsoleSender();
			} else {
				sender = (Player)event.getEntity().getShooter();
			}
			if (!sender.hasPermission("myitems.use." + items.get(itemstack.getItemMeta()))){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return;
			}
			String command = ItemFile.read(globalitems.get(itemstack.getTypeId()), "throwevent");
			if (command != null){
				String[] commands = command.split(" && ");
				for (String s : commands){
					s = s.replaceAll("<user>", ((Player)event.getEntity()).getName());
					Bukkit.getServer().dispatchCommand(sender, s);
				}
				
				String single = ItemFile.read(globalitems.get(itemstack.getTypeId()), "single-use");
				if (single.equals("true")){
					if (itemstack.getAmount() > 1) itemstack.setAmount(itemstack.getAmount() - 1);
					else ((Player)event.getEntity().getShooter()).setItemInHand(new ItemStack(Material.AIR));
				}
			}
		}
	}
	
	@EventHandler
	public void holdEvent(PlayerItemHeldEvent event){
		ItemStack itemstack = event.getPlayer().getInventory().getItem(event.getNewSlot());
		if (itemstack == null) return;
		if (items.containsKey(itemstack.getItemMeta())){
			CommandSender sender;
			if (getConfig().getBoolean("console-executor")){
				sender = Bukkit.getConsoleSender();
			} else {
				sender = event.getPlayer();
			}
			if (!sender.hasPermission("myitems.use." + items.get(itemstack.getItemMeta()))){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return;
			}
			String command = ItemFile.read(items.get(itemstack.getItemMeta()), "holdevent");
			if (command != null){
				String[] commands = command.split(" && ");
				for (String s : commands){
					s = s.replaceAll("<user>", event.getPlayer().getName());
					Bukkit.getServer().dispatchCommand(sender, s);
				}
				
				String single = ItemFile.read(items.get(itemstack.getItemMeta()), "single-use");
				if (single.equals("true")){
					if (itemstack.getAmount() > 1) itemstack.setAmount(itemstack.getAmount() - 1);
					else event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
				}
			}
		} else if (globalitems.containsKey(itemstack.getTypeId()) && ItemFile.read(globalitems.get(itemstack.getTypeId()), "global").equals("true")){
			CommandSender sender;
			if (getConfig().getBoolean("console-executor")){
				sender = Bukkit.getConsoleSender();
			} else {
				sender = event.getPlayer();
			}
			if (!sender.hasPermission("myitems.use." + items.get(itemstack.getItemMeta()))){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return;
			}
			String command = ItemFile.read(globalitems.get(itemstack.getTypeId()), "holdevent");
			if (command != null){
				String[] commands = command.split(" && ");
				for (String s : commands){
					s = s.replaceAll("<user>", event.getPlayer().getName());
					Bukkit.getServer().dispatchCommand(sender, s);
				}
				
				String single = ItemFile.read(globalitems.get(itemstack.getTypeId()), "single-use");
				if (single.equals("true")){
					if (itemstack.getAmount() > 1) itemstack.setAmount(itemstack.getAmount() - 1);
					else event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
				}
			}
		}
	}
	
	@EventHandler
	public void consumeEvent(PlayerItemConsumeEvent event){
		ItemStack itemstack = event.getPlayer().getItemInHand();
		if (items.containsKey(itemstack.getItemMeta())){
			CommandSender sender;
			if (getConfig().getBoolean("console-executor")){
				sender = Bukkit.getConsoleSender();
			} else {
				sender = event.getPlayer();
			}
			if (!sender.hasPermission("myitems.use." + items.get(itemstack.getItemMeta()))){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return;
			}
			String command = ItemFile.read(items.get(itemstack.getItemMeta()), "consumeevent");
			if (command != null){
				String[] commands = command.split(" && ");
				for (String s : commands){
					s = s.replaceAll("<user>", event.getPlayer().getName());
					Bukkit.getServer().dispatchCommand(sender, s);
				}
				
				String single = ItemFile.read(items.get(itemstack.getItemMeta()), "single-use");
				if (single.equals("true")){
					if (itemstack.getAmount() > 1) itemstack.setAmount(itemstack.getAmount() - 1);
					else event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
				}
			}
		} else if (globalitems.containsKey(itemstack.getTypeId()) && ItemFile.read(globalitems.get(itemstack.getTypeId()), "global").equals("true")){
			CommandSender sender;
			if (getConfig().getBoolean("console-executor")){
				sender = Bukkit.getConsoleSender();
			} else {
				sender = event.getPlayer();
			}
			if (!sender.hasPermission("myitems.use." + items.get(itemstack.getItemMeta()))){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return;
			}
			String command = ItemFile.read(globalitems.get(itemstack.getTypeId()), "consumeevent");
			if (command != null){
				String[] commands = command.split(" && ");
				for (String s : commands){
					s = s.replaceAll("<user>", event.getPlayer().getName());
					Bukkit.getServer().dispatchCommand(sender, s);
				}
				
				String single = ItemFile.read(globalitems.get(itemstack.getTypeId()), "single-use");
				if (single.equals("true")){
					if (itemstack.getAmount() > 1) itemstack.setAmount(itemstack.getAmount() - 1);
					else event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
				}
			}
		}
	}
	
	@EventHandler
	public void interactEvent(PlayerInteractEvent event){
		ItemStack itemstack = event.getPlayer().getItemInHand();
		if (items.containsKey(itemstack.getItemMeta())){
			CommandSender sender;
			if (getConfig().getBoolean("console-executor")){
				sender = Bukkit.getConsoleSender();
			} else {
				sender = event.getPlayer();
			}
			
			if (!sender.hasPermission("myitems.use." + items.get(itemstack.getItemMeta()))){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return;
			}
			
			if (event.getAction() == Action.LEFT_CLICK_AIR){
				String command = ItemFile.read(items.get(itemstack.getItemMeta()), "leftclickairevent");
				if (command != null){
					String[] commands = command.split(" && ");
					for (String s : commands){
						s = s.replaceAll("<user>", event.getPlayer().getName());
						Bukkit.getServer().dispatchCommand(sender, s);
					}
					
					String single = ItemFile.read(items.get(itemstack.getItemMeta()), "single-use");
					if (single.equals("true")){
						if (itemstack.getAmount() > 1) itemstack.setAmount(itemstack.getAmount() - 1);
						else event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
					}
				}
			} else if (event.getAction() == Action.RIGHT_CLICK_AIR){
				String command = ItemFile.read(items.get(itemstack.getItemMeta()), "rightclickairevent");
				if (command != null){
					String[] commands = command.split(" && ");
					for (String s : commands){
						s = s.replaceAll("<user>", event.getPlayer().getName());
						Bukkit.getServer().dispatchCommand(sender, s);
					}
					
					String single = ItemFile.read(items.get(itemstack.getItemMeta()), "single-use");
					if (single.equals("true")){
						if (itemstack.getAmount() > 1) itemstack.setAmount(itemstack.getAmount() - 1);
						else event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
					}
				}
			} else if (event.getAction() == Action.LEFT_CLICK_BLOCK){
				String command = ItemFile.read(items.get(itemstack.getItemMeta()), "leftclickblockevent");
				if (command != null){
					String[] commands = command.split(" && ");
					for (String s : commands){
						s = s.replaceAll("<user>", event.getPlayer().getName());
						Bukkit.getServer().dispatchCommand(sender, s);
					}
					
					String single = ItemFile.read(items.get(itemstack.getItemMeta()), "single-use");
					if (single.equals("true")){
						if (itemstack.getAmount() > 1) itemstack.setAmount(itemstack.getAmount() - 1);
						else event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
					}
				}
			} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
				String command = ItemFile.read(items.get(itemstack.getItemMeta()), "rightclickblockevent");
				if (command != null){
					String[] commands = command.split(" && ");
					for (String s : commands){
						s = s.replaceAll("<user>", event.getPlayer().getName());
						Bukkit.getServer().dispatchCommand(sender, s);
					}
					
					String single = ItemFile.read(items.get(itemstack.getItemMeta()), "single-use");
					if (single.equals("true")){
						if (itemstack.getAmount() > 1) itemstack.setAmount(itemstack.getAmount() - 1);
						else event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
					}
				}
			}
		} else if (globalitems.containsKey(itemstack.getTypeId()) && ItemFile.read(globalitems.get(itemstack.getTypeId()), "global").equals("true")){
			CommandSender sender;
			if (getConfig().getBoolean("console-executor")){
				sender = Bukkit.getConsoleSender();
			} else {
				sender = event.getPlayer();
			}
			
			if (!sender.hasPermission("myitems.use." + items.get(itemstack.getItemMeta()))){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return;
			}
			
			if (event.getAction() == Action.LEFT_CLICK_AIR){
				String command = ItemFile.read(globalitems.get(itemstack.getTypeId()), "leftclickairevent");
				if (command != null){
					String[] commands = command.split(" && ");
					for (String s : commands){
						s = s.replaceAll("<user>", event.getPlayer().getName());
						Bukkit.getServer().dispatchCommand(sender, s);
					}
					
					String single = ItemFile.read(globalitems.get(itemstack.getTypeId()), "single-use");
					if (single.equals("true")){
						if (itemstack.getAmount() > 1) itemstack.setAmount(itemstack.getAmount() - 1);
						else event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
					}
				}
			} else if (event.getAction() == Action.RIGHT_CLICK_AIR){
				String command = ItemFile.read(globalitems.get(itemstack.getTypeId()), "rightclickairevent");
				if (command != null){
					String[] commands = command.split(" && ");
					for (String s : commands){
						s = s.replaceAll("<user>", event.getPlayer().getName());
						Bukkit.getServer().dispatchCommand(sender, s);
					}
					
					String single = ItemFile.read(globalitems.get(itemstack.getTypeId()), "single-use");
					if (single.equals("true")){
						if (itemstack.getAmount() > 1) itemstack.setAmount(itemstack.getAmount() - 1);
						else event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
					}
				}
			} else if (event.getAction() == Action.LEFT_CLICK_BLOCK){
				String command = ItemFile.read(globalitems.get(itemstack.getTypeId()), "leftclickblockevent");
				if (command != null){
					String[] commands = command.split(" && ");
					for (String s : commands){
						s = s.replaceAll("<user>", event.getPlayer().getName());
						Bukkit.getServer().dispatchCommand(sender, s);
					}
					
					String single = ItemFile.read(globalitems.get(itemstack.getTypeId()), "single-use");
					if (single.equals("true")){
						if (itemstack.getAmount() > 1) itemstack.setAmount(itemstack.getAmount() - 1);
						else event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
					}
				}
			} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
				String command = ItemFile.read(globalitems.get(itemstack.getTypeId()), "rightclickblockevent");
				if (command != null){
					String[] commands = command.split(" && ");
					for (String s : commands){
						s = s.replaceAll("<user>", event.getPlayer().getName());
						Bukkit.getServer().dispatchCommand(sender, s);
					}
					
					String single = ItemFile.read(globalitems.get(itemstack.getTypeId()), "single-use");
					if (single.equals("true")){
						if (itemstack.getAmount() > 1) itemstack.setAmount(itemstack.getAmount() - 1);
						else event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
					}
				}
			}
		}
	}
	
	@EventHandler
	public void entityinteractEvent(PlayerInteractEntityEvent event){
		ItemStack itemstack = event.getPlayer().getItemInHand();
		if (items.containsKey(itemstack.getItemMeta())){
			CommandSender sender;
			if (getConfig().getBoolean("console-executor")){
				sender = Bukkit.getConsoleSender();
			} else {
				sender = event.getPlayer();
			}
			if (!sender.hasPermission("myitems.use." + items.get(itemstack.getItemMeta()))){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return;
			}
			String command = ItemFile.read(items.get(itemstack.getItemMeta()), "rightclickentityevent");
			if (command != null){
				String[] commands = command.split(" && ");
				for (String s : commands){
					s = s.replaceAll("<user>", event.getPlayer().getName());
					if (event.getRightClicked().getType() == EntityType.PLAYER)
						s = s.replaceAll("<clicked>", ((Player) event.getRightClicked()).getName());
					else {
						String name = event.getRightClicked().getType().getName();
						s = s.replaceAll("<clicked>", name);
					}
					Bukkit.getServer().dispatchCommand(sender, s);
				}
				
				String single = ItemFile.read(items.get(itemstack.getItemMeta()), "single-use");
				if (single.equals("true")){
					if (itemstack.getAmount() > 1) itemstack.setAmount(itemstack.getAmount() - 1);
					else event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
				}
			}
		} else if (globalitems.containsKey(itemstack.getTypeId()) && ItemFile.read(globalitems.get(itemstack.getTypeId()), "global").equals("true")){
			CommandSender sender;
			if (getConfig().getBoolean("console-executor")){
				sender = Bukkit.getConsoleSender();
			} else {
				sender = event.getPlayer();
			}
			if (!sender.hasPermission("myitems.use." + items.get(itemstack.getItemMeta()))){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return;
			}
			String command = ItemFile.read(globalitems.get(itemstack.getTypeId()), "rightclickentityevent");
			if (command != null){
				String[] commands = command.split(" && ");
				for (String s : commands){
					s = s.replaceAll("<user>", event.getPlayer().getName());
					if (event.getRightClicked().getType() == EntityType.PLAYER)
						s = s.replaceAll("<clicked>", ((Player) event.getRightClicked()).getName());
					else {
						String name = event.getRightClicked().getType().getName();
						s = s.replaceAll("<clicked>", name);
					}
					Bukkit.getServer().dispatchCommand(sender, s);
				}
				
				String single = ItemFile.read(globalitems.get(itemstack.getTypeId()), "single-use");
				if (single.equals("true")){
					if (itemstack.getAmount() > 1) itemstack.setAmount(itemstack.getAmount() - 1);
					else event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
				}
			}
		}
	}
	
	@EventHandler
	public void enterBedEvent(PlayerBedEnterEvent event){
		ItemStack itemstack = event.getPlayer().getItemInHand();
		if (items.containsKey(itemstack.getItemMeta())){
			CommandSender sender;
			if (getConfig().getBoolean("console-executor")){
				sender = Bukkit.getConsoleSender();
			} else {
				sender = event.getPlayer();
			}
			if (!sender.hasPermission("myitems.use." + items.get(itemstack.getItemMeta()))){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return;
			}
			String command = ItemFile.read(items.get(itemstack.getItemMeta()), "bedenterevent");
			if (command != null){
				String[] commands = command.split(" && ");
				for (String s : commands){
					s = s.replaceAll("<user>", event.getPlayer().getName());
					Bukkit.getServer().dispatchCommand(sender, s);
				}
				
				String single = ItemFile.read(items.get(itemstack.getItemMeta()), "single-use");
				if (single.equals("true")){
					if (itemstack.getAmount() > 1) itemstack.setAmount(itemstack.getAmount() - 1);
					else event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
				}
			}
		} else if (globalitems.containsKey(itemstack.getTypeId()) && ItemFile.read(globalitems.get(itemstack.getTypeId()), "global").equals("true")){
			CommandSender sender;
			if (getConfig().getBoolean("console-executor")){
				sender = Bukkit.getConsoleSender();
			} else {
				sender = event.getPlayer();
			}
			if (!sender.hasPermission("myitems.use." + items.get(itemstack.getItemMeta()))){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return;
			}
			String command = ItemFile.read(globalitems.get(itemstack.getTypeId()), "bedenterevent");
			if (command != null){
				String[] commands = command.split(" && ");
				for (String s : commands){
					s = s.replaceAll("<user>", event.getPlayer().getName());
					Bukkit.getServer().dispatchCommand(sender, s);
				}
				
				String single = ItemFile.read(globalitems.get(itemstack.getTypeId()), "single-use");
				if (single.equals("true")){
					if (itemstack.getAmount() > 1) itemstack.setAmount(itemstack.getAmount() - 1);
					else event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
				}
			}
		}
	}
	
	@EventHandler
	public void damageEvent(EntityDamageByEntityEvent event){
		if (!(event.getDamager() instanceof Player)){
			return;
		}
		ItemStack itemstack = ((Player) (event.getDamager())).getItemInHand();
		if (items.containsKey(itemstack.getItemMeta())){
			CommandSender sender;
			if (getConfig().getBoolean("console-executor")){
				sender = Bukkit.getConsoleSender();
			} else {
				sender = ((Player) (event.getDamager()));
			}
			if (!sender.hasPermission("myitems.use." + items.get(itemstack.getItemMeta()))){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return;
			}
			String command = ItemFile.read(items.get(itemstack.getItemMeta()), "damageentityevent");
			if (command != null){
				String[] commands = command.split(" && ");
				for (String s : commands){
					s = s.replaceAll("<user>", ((Player)event.getDamager()).getName());
					if (event.getEntity().getType() == EntityType.PLAYER)
						s = s.replaceAll("<clicked>", ((Player) event.getEntity()).getName());
					else {
						String name = event.getEntity().getType().getName();
						s = s.replaceAll("<clicked>", name);
					}
					Bukkit.getServer().dispatchCommand(sender, s);
				}
				
				String single = ItemFile.read(items.get(itemstack.getItemMeta()), "single-use");
				if (single.equals("true") && sender instanceof Player){
					if (itemstack.getAmount() > 1) itemstack.setAmount(itemstack.getAmount() - 1);
					else ((Player)event.getDamager()).setItemInHand(new ItemStack(Material.AIR));
				}
			}
		} else if (globalitems.containsKey(itemstack.getTypeId()) && ItemFile.read(globalitems.get(itemstack.getTypeId()), "global").equals("true")){
			CommandSender sender;
			if (getConfig().getBoolean("console-executor")){
				sender = Bukkit.getConsoleSender();
			} else {
				sender = ((Player) (event.getDamager()));
			}
			if (!sender.hasPermission("myitems.use." + items.get(itemstack.getItemMeta()))){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return;
			}
			String command = ItemFile.read(globalitems.get(itemstack.getTypeId()), "damageentityevent");
			if (command != null){
				String[] commands = command.split(" && ");
				for (String s : commands){
					s = s.replaceAll("<user>", ((Player)event.getEntity()).getName());
					if (event.getEntity().getType() == EntityType.PLAYER)
						s = s.replaceAll("<clicked>", ((Player) event.getEntity()).getName());
					else {
						String name = event.getEntity().getType().getName();
						s = s.replaceAll("<clicked>", name);
					}
					Bukkit.getServer().dispatchCommand(sender, s);
				}
				
				String single = ItemFile.read(globalitems.get(itemstack.getTypeId()), "single-use");
				if (single.equals("true") && sender instanceof Player){
					if (itemstack.getAmount() > 1) itemstack.setAmount(itemstack.getAmount() - 1);
					else ((Player)event.getDamager()).setItemInHand(new ItemStack(Material.AIR));
				}
			}
		}
	}
	
	@EventHandler
	public void blockplaceEvent(BlockPlaceEvent event){
		ItemStack itemstack = event.getPlayer().getItemInHand();
		if (items.containsKey(itemstack.getItemMeta())){
			CommandSender sender;
			if (getConfig().getBoolean("console-executor")){
				sender = Bukkit.getConsoleSender();
			} else {
				sender = event.getPlayer();
			}
			if (!sender.hasPermission("myitems.use")){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return;
			}
			String command = ItemFile.read(items.get(itemstack.getItemMeta()), "blockplaceevent");
			if (command != null){
				String[] commands = command.split(" && ");
				for (String s : commands){
					s = s.replaceAll("<user>", event.getPlayer().getName());
					Bukkit.getServer().dispatchCommand(sender, s);
				}
				
				String single = ItemFile.read(items.get(itemstack.getItemMeta()), "single-use");
				if (single.equals("true")){
					if (itemstack.getAmount() > 1) itemstack.setAmount(itemstack.getAmount() - 1);
					else event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
				}
			}
		} else if (globalitems.containsKey(itemstack.getTypeId()) && ItemFile.read(globalitems.get(itemstack.getTypeId()), "global").equals("true")){
			CommandSender sender;
			if (getConfig().getBoolean("console-executor")){
				sender = Bukkit.getConsoleSender();
			} else {
				sender = event.getPlayer();
			}
			if (!sender.hasPermission("myitems.use")){
				sender.sendMessage(ChatColor.RED + "You do not have permission");
				return;
			}
			String command = ItemFile.read(globalitems.get(itemstack.getTypeId()), "blockplaceevent");
			if (command != null){
				String[] commands = command.split(" && ");
				for (String s : commands){
					s = s.replaceAll("<user>", event.getPlayer().getName());
					Bukkit.getServer().dispatchCommand(sender, s);
				}
				
				String single = ItemFile.read(globalitems.get(itemstack.getTypeId()), "single-use");
				if (single.equals("true")){
					if (itemstack.getAmount() > 1) itemstack.setAmount(itemstack.getAmount() - 1);
					else event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
				}
			}
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0){
			sender.sendMessage(ChatColor.BLUE + "-------------MyItems By BaranCODE-------------");
			sender.sendMessage(ChatColor.AQUA + "/mi get <item> [player]");
			sender.sendMessage(ChatColor.DARK_GREEN + "- Gives you an item");
			sender.sendMessage(ChatColor.AQUA + "/mi list");
			sender.sendMessage(ChatColor.DARK_GREEN + "- Lists the items");
			sender.sendMessage(ChatColor.AQUA + "/mi create <item name> <item's display name> <item's ID> [-g] [-s]");
			sender.sendMessage(ChatColor.DARK_GREEN + "- Create a new custom item");
			sender.sendMessage(ChatColor.AQUA + "/mi setcommand <item name> <event> <command>");
			sender.sendMessage(ChatColor.DARK_GREEN + "- Set the command of an item for a certain event");
			sender.sendMessage(ChatColor.AQUA + "/mi setlore <item name> <lore>");
			sender.sendMessage(ChatColor.DARK_GREEN + "- Set the lore of an item");
			sender.sendMessage(ChatColor.AQUA + "/mi delete <item name>");
			sender.sendMessage(ChatColor.DARK_GREEN + "- Delete an item. NOT REVERSABLE");
			return true;
		} else if (args.length == 1){
			if (args[0].equalsIgnoreCase("list")){
				if (!sender.hasPermission("myitems.list")){
					sender.sendMessage(ChatColor.RED + "You do not have permission");
					return true;
				}
				sender.sendMessage(ChatColor.BLUE + "The items are:");
				for (File file : getDataFolder().listFiles()){
					if (!file.getName().equals("config.yml")){
						String name = file.getName();
						name = name.replace(".txt", "");
						sender.sendMessage(ChatColor.AQUA + name);
					}
				}
				return true;
			}
		} else if (args.length == 2){
			if (args[0].equalsIgnoreCase("get")){
				Player player;
				if (sender instanceof Player) {
			        player = (Player) sender;
			    } else {
			        sender.sendMessage(ChatColor.RED + "You must be a player!");
			        return true;
			    }
				
				if (!sender.hasPermission("myitems.get")){
					sender.sendMessage(ChatColor.RED + "You do not have permission");
					return true;
				}
				
				if (!items.containsValue(args[1])) {
					sender.sendMessage(ChatColor.RED + "That item does not exist!");
					return true;
				}
				
				String displayname = ItemFile.read(args[1], "displayname");
				displayname = replaceColors(displayname);
				
				String lore = ItemFile.read(args[1], "lore");
				String[] loreparts = lore.split("&&");
				List<String> list = new LinkedList<String>();
				for (int i = 0; i < loreparts.length; i++){
					list.add(replaceColors(loreparts[i]));
				}
				
				String id = ItemFile.read(args[1], "id");
				String[] idparts = id.split(":");
				
				ItemStack itemstack = new ItemStack(Material.AIR, 1);
				
				if (idparts.length == 2){
					itemstack = new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1, (byte)Integer.parseInt(idparts[1]));
				} else if (idparts.length == 1){
					itemstack = new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1);
				}
				
				PlayerInventory inventory = player.getInventory();
				
				ItemMeta meta = itemstack.getItemMeta();
				meta.setDisplayName(displayname);
				meta.setLore(list);
				itemstack.setItemMeta(meta);
				inventory.addItem(itemstack);
				sender.sendMessage(ChatColor.GREEN + "You have been given the " + args[1] + " item");
				return true;
			} else if (args[0].equalsIgnoreCase("delete")){
				if (!sender.hasPermission("myitems.delete")){
					sender.sendMessage(ChatColor.RED + "You do not have permission");
					return true;
				}
				File saveTo = new File(getDataFolder(), args[1] + ".txt");
				saveTo.delete();
				sender.sendMessage(ChatColor.GREEN + "That item has been deleted!");
				return true;
			}
		} else {
			if (args[0].equalsIgnoreCase("get") && args.length == 3){
				Player player = Bukkit.getPlayer(args[2]);
				if (!player.isOnline()){
					sender.sendMessage(ChatColor.RED + "That player isn't online");
					return true;
				}
				
				if (!sender.hasPermission("myitems.get")){
					sender.sendMessage(ChatColor.RED + "You do not have permission");
					return true;
				}
				
				if (!items.containsValue(args[1])) {
					sender.sendMessage(ChatColor.RED + "That item does not exist!");
					return true;
				}
				
				String displayname = ItemFile.read(args[1], "displayname");
				displayname = replaceColors(displayname);
				
				String lore = ItemFile.read(args[1], "lore");
				String[] loreparts = lore.split("&&");
				List<String> list = new LinkedList<String>();
				for (int i = 0; i < loreparts.length; i++){
					list.add(replaceColors(loreparts[i]));
				}
				
				String id = ItemFile.read(args[1], "id");
				String[] idparts = id.split(":");
				
				ItemStack itemstack = new ItemStack(Material.AIR, 1);
				
				if (idparts.length == 2){
					itemstack = new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1, (byte)Integer.parseInt(idparts[1]));
				} else if (idparts.length == 1){
					itemstack = new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1);
				}
				
				PlayerInventory inventory = player.getInventory();
				
				ItemMeta meta = itemstack.getItemMeta();
				meta.setDisplayName(displayname);
				meta.setLore(list);
				itemstack.setItemMeta(meta);
				inventory.addItem(itemstack);
				sender.sendMessage(ChatColor.GREEN + "You have given the " + args[1] + " item");
				return true;
			}
			
			
			if (args[0].equalsIgnoreCase("create")){				
				if (!sender.hasPermission("myitems.create")){
					sender.sendMessage(ChatColor.RED + "You do not have permission");
					return true;
				}
				ItemFile.write(args[1], "displayname", args[2]);
				ItemFile.write(args[1], "id", args[3]);
				if (args.length == 5) {
					if (args[4].equalsIgnoreCase("-g")){
						ItemFile.write(args[1], "global", "true");
						globalitems.put(Integer.parseInt(args[3]), args[1]);
					} else {
						ItemFile.write(args[1], "global", "false");
					}
					
					if (args[4].equalsIgnoreCase("-s")){
						ItemFile.write(args[1], "single-use", "true");
						singleitems.put(Integer.parseInt(args[3]), args[1]);
					} else {
						ItemFile.write(args[1], "single-use", "false");
					}
				} else if (args.length == 6){
					if (args[4].equalsIgnoreCase("-g") || args[5].equalsIgnoreCase("-g")){
						ItemFile.write(args[1], "global", "true");
						globalitems.put(Integer.parseInt(args[3]), args[1]);
					} else {
						ItemFile.write(args[1], "global", "false");
					}
					
					if (args[4].equalsIgnoreCase("-s") || args[5].equalsIgnoreCase("-s")){
						ItemFile.write(args[1], "single-use", "true");
						singleitems.put(Integer.parseInt(args[3]), args[1]);
					} else {
						ItemFile.write(args[1], "single-use", "false");
					}
				} else {
					ItemFile.write(args[1], "global", "false");
					ItemFile.write(args[1], "single-use", "false");
				}
				ItemFile.write(args[1], "lore", "Default item lore");
				
				String id = args[3];
				String[] idparts = id.split(":");
				
				ItemStack itemstack = new ItemStack(Material.AIR, 1);
				
				if (idparts.length == 2){
					itemstack = new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1, (byte)Integer.parseInt(idparts[1]));
				} else if (idparts.length == 1){
					itemstack = new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1);
				}
				
				ItemMeta meta = itemstack.getItemMeta();
				meta.setDisplayName(args[2]);
				List<String> list = new LinkedList<String>();
				list.add("Default item lore");
				meta.setLore(list);
				itemstack.setItemMeta(meta);
				
				items.put(meta, args[1]);
				sender.sendMessage(ChatColor.GREEN + "You have created an item");
				return true;
			}
			else if (args[0].equalsIgnoreCase("setcommand")){
				if (!sender.hasPermission("myitems.setcommand")){
					sender.sendMessage(ChatColor.RED + "You do not have permission");
					return true;
				}
				String finalString = "";
    			for (int i = 3; i < args.length; i++) {
    			    finalString += args[i] + ' ';
    			}
    			finalString = finalString.trim();
    			String event = args[2];
    			if (events.contains(event.toLowerCase())){
    				ItemFile.write(args[1], args[2], finalString);
    				sender.sendMessage(ChatColor.GREEN + "You have set a command for the event " + event + " for the item " + args[1]);
    				return true;
    			} else {
    				if (event.equalsIgnoreCase("rightclickevent")){
	    				ItemFile.write(args[1], "rightclickentityevent", finalString);
	    				ItemFile.write(args[1], "rightclickblockevent", finalString);
	    				ItemFile.write(args[1], "rightclickairevent", finalString);
	    				sender.sendMessage(ChatColor.GREEN + "You have set a command for the event group rightclickevent for the item " + args[1]);
	    				return true;
    				} else if (event.equalsIgnoreCase("leftclickevent")){
	    				ItemFile.write(args[1], "damageentityevent", finalString);
	    				ItemFile.write(args[1], "leftclickblockevent", finalString);
	    				ItemFile.write(args[1], "leftclickairevent", finalString);
	    				sender.sendMessage(ChatColor.GREEN + "You have set a command for the event group leftclickevent for the item " + args[1]);
	    				return true;
    				}
    			}
    			sender.sendMessage(ChatColor.RED + "That is not a valid event");
				return true;
			} else if (args[0].equalsIgnoreCase("setlore")){
				if (!sender.hasPermission("myitems.setlore")){
					sender.sendMessage(ChatColor.RED + "You do not have permission");
					return true;
				}
				String finalString = "";
    			for (int i = 2; i < args.length; i++) {
    			    finalString += args[i] + ' ';
    			}
    			finalString = finalString.trim();
				ItemFile.write(args[1], "lore", finalString);
				
				
				String displayname = ItemFile.read(args[1], "displayname");
				displayname = replaceColors(displayname);
				
				String[] loreparts = finalString.split("&&");
				List<String> list = new LinkedList<String>();
				for (int i = 0; i < loreparts.length; i++){
					list.add(replaceColors(loreparts[i]));
				}
				
				String id = ItemFile.read(args[1], "id");
				String[] idparts = id.split(":");
				
				ItemStack itemstack = new ItemStack(Material.AIR, 1);
				
				if (idparts.length == 2){
					itemstack = new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1, (byte)Integer.parseInt(idparts[1]));
				} else if (idparts.length == 1){
					itemstack = new ItemStack(Material.getMaterial(Integer.parseInt(idparts[0])), 1);
				}
				
				ItemMeta meta = itemstack.getItemMeta();
				meta.setDisplayName(displayname);
				meta.setLore(list);
				
				
				items.put(meta, args[1]);
				
				sender.sendMessage(ChatColor.GREEN + "You have set the lore of the item " + args[1]);
				return true;
			}
		}
		return false;
	}
	
	  public String replaceColors(String message){
		    message = message.replaceAll("&0", ChatColor.BLACK + "");
		    message = message.replaceAll("&1", ChatColor.DARK_BLUE + "");
		    message = message.replaceAll("&2", ChatColor.DARK_GREEN + "");
		    message = message.replaceAll("&3", ChatColor.DARK_AQUA + "");
		    message = message.replaceAll("&4", ChatColor.DARK_RED + "");
		    message = message.replaceAll("&5", ChatColor.DARK_PURPLE + "");
		    message = message.replaceAll("&6", ChatColor.GOLD + "");
		    message = message.replaceAll("&7", ChatColor.GRAY + "");
		    message = message.replaceAll("&8", ChatColor.DARK_GRAY + "");
		    message = message.replaceAll("&9", ChatColor.BLUE + "");
		    message = message.replaceAll("(?i)&a", ChatColor.GREEN + "");
		    message = message.replaceAll("(?i)&b", ChatColor.AQUA + "");
		    message = message.replaceAll("(?i)&c", ChatColor.RED + "");
		    message = message.replaceAll("(?i)&d", ChatColor.LIGHT_PURPLE + "");
		    message = message.replaceAll("(?i)&e", ChatColor.YELLOW + "");
		    message = message.replaceAll("(?i)&f", ChatColor.WHITE + "");
		    message = message.replaceAll("(?i)&l", ChatColor.BOLD + "");
		    message = message.replaceAll("(?i)&o", ChatColor.ITALIC + "");
		    message = message.replaceAll("(?i)&m", ChatColor.STRIKETHROUGH + "");
		    message = message.replaceAll("(?i)&n", ChatColor.UNDERLINE + "");
		    message = message.replaceAll("(?i)&k", ChatColor.MAGIC + "");
		    message = message.replaceAll("(?i)&r", ChatColor.RESET + "");
		    return message;
	  }
}
