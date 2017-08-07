package com.barancode.mc.experia;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class Items {
	Main plugin;
	Random random = new Random();
	List<String> chestLoot = new LinkedList<String>();
	List<String> giantLoot = new LinkedList<String>();
	
	public Items(Main plugin){
		this.plugin = plugin;
	}
	
	public void prepareChestLoot(){
		List<String> items = plugin.getConfig().getStringList("loot");
		for (String s : items){
			String[] parts = s.split(", ");
			if (parts.length == 3){
				int chance = Integer.parseInt(parts[2]);
				for (int i = 0; i < chance; i++){
					chestLoot.add(parts[0] + ", " + parts[1]);
				}
			} else if (parts.length == 2){
				int chance = Integer.parseInt(parts[1]);
				for (int i = 0; i < chance; i++){
					chestLoot.add(parts[0]);
				}
			}
		}
		
		List<String> giantitems = plugin.getConfig().getStringList("giant-drops.loot");
		for (String s : giantitems){
			String[] parts = s.split(", ");
			if (parts.length == 4){
				int chance = Integer.parseInt(parts[3]);
				for (int i = 0; i < chance; i++){
					giantLoot.add(parts[0] + ", " + parts[1] + ", " + parts[2]);
				}
			} else if (parts.length == 3){
				int chance = Integer.parseInt(parts[2]);
				for (int i = 0; i < chance; i++){
					giantLoot.add(parts[0] + ", " + parts[1]);
				}
			}
		}
	}
	
	public void fillItemList(){
		List<String> kitnames = plugin.getConfig().getStringList("kitnames");
		for (String kitname : kitnames){
			List<String> itemlist = plugin.getConfig().getStringList("kits." + kitname + ".items");
			itemlist.add(plugin.getConfig().getString("kits." + kitname + ".helmet"));
			itemlist.add(plugin.getConfig().getString("kits." + kitname + ".chestplate"));
			itemlist.add(plugin.getConfig().getString("kits." + kitname + ".leggings"));
			itemlist.add(plugin.getConfig().getString("kits." + kitname + ".boots"));
			for (String s : itemlist){
				if (s.equals("")) continue;
				String[] parts = s.split(", ");
				String id = parts[1];
				String[] idparts = id.split(":");
				ItemStack item;
				if (idparts.length == 1){
					item = new ItemStack(Integer.parseInt(idparts[0]), 1);
				} else {
					item = new ItemStack(Integer.parseInt(idparts[0]), 1, (byte)Integer.parseInt(idparts[1]));
				}
				if (parts.length == 4){
					String enchantments = parts[3];
					String[] parts2 = enchantments.split(" & ");
					for (String enchant : parts2){
						String[] parts3 = enchant.split(" ");
						int type = Integer.parseInt(parts3[0]);
						int level = Integer.parseInt(parts3[1]);
						item.addUnsafeEnchantment(Enchantment.getById(type), level);
					}
				}
				ItemMeta meta = item.getItemMeta();
				String[] displayStrings = parts[0].split(" - ");
				meta.setDisplayName(plugin.utils.replace(displayStrings[0]));
				if (displayStrings.length > 1){
					List<String> lore = Arrays.asList(plugin.utils.replace(displayStrings[1]).split("&&"));
					meta.setLore(lore);
				}
				item.setItemMeta(meta);
				plugin.kitItems.add(item);
			}
		}
	}
	
	public void giveItems(Player player, String kitname){
		List<String> itemlist = plugin.getConfig().getStringList("kits." + kitname + ".items");
		for (String s : itemlist){
			String[] parts = s.split(", ");
			String id = parts[1];
			String[] idparts = id.split(":");
			int amount = Integer.parseInt(parts[2]);
			ItemStack item;
			if (idparts.length == 1){
				item = new ItemStack(Integer.parseInt(idparts[0]), amount);
			} else {
				item = new ItemStack(Integer.parseInt(idparts[0]), amount, (byte)Integer.parseInt(idparts[1]));
			}
			if (parts.length == 4){
				String enchantments = parts[3];
				String[] parts2 = enchantments.split(" & ");
				for (String enchant : parts2){
					String[] parts3 = enchant.split(" ");
					int type = Integer.parseInt(parts3[0]);
					int level = Integer.parseInt(parts3[1]);
					item.addUnsafeEnchantment(Enchantment.getById(type), level);
				}
			}
			ItemMeta meta = item.getItemMeta();
			String[] displayStrings = parts[0].split(" - ");
			meta.setDisplayName(plugin.utils.replace(displayStrings[0]));
			if (displayStrings.length > 1){
				List<String> lore = Arrays.asList(plugin.utils.replace(displayStrings[1]).split("&&"));
				meta.setLore(lore);
			}
			item.setItemMeta(meta);
			player.getInventory().addItem(item);
		}
	}
	
	
	public void giveHelmet(Player player, String kitname){
		String s = plugin.getConfig().getString("kits." + kitname + ".helmet");
		if (s.equals("")) return;
		String[] parts = s.split(", ");
		String id = parts[1];
		String[] idparts = id.split(":");
		int amount = Integer.parseInt(parts[2]);
		ItemStack item;
		if (idparts.length == 1){
			item = new ItemStack(Integer.parseInt(idparts[0]), amount);
		} else {
			item = new ItemStack(Integer.parseInt(idparts[0]), amount, (byte)Integer.parseInt(idparts[1]));
		}
		if (parts.length == 4){
			String enchantments = parts[3];
			String[] parts2 = enchantments.split(" & ");
			for (String enchant : parts2){
				String[] parts3 = enchant.split(" ");
				int type = Integer.parseInt(parts3[0]);
				int level = Integer.parseInt(parts3[1]);
				item.addUnsafeEnchantment(Enchantment.getById(type), level);
			}
		}
		ItemMeta meta = item.getItemMeta();
		String[] displayStrings = parts[0].split(" - ");
		meta.setDisplayName(plugin.utils.replace(displayStrings[0]));
		if (displayStrings.length > 1){
			List<String> lore = Arrays.asList(plugin.utils.replace(displayStrings[1]).split("&&"));
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		player.getInventory().setHelmet(item);
	}
	
	
	public void giveChestplate(Player player, String kitname){
		String s = plugin.getConfig().getString("kits." + kitname + ".chestplate");
		if (s.equals("")) return;
		String[] parts = s.split(", ");
		String id = parts[1];
		String[] idparts = id.split(":");
		int amount = Integer.parseInt(parts[2]);
		ItemStack item;
		if (idparts.length == 1){
			item = new ItemStack(Integer.parseInt(idparts[0]), amount);
		} else {
			item = new ItemStack(Integer.parseInt(idparts[0]), amount, (byte)Integer.parseInt(idparts[1]));
		}
		if (parts.length == 4){
			String enchantments = parts[3];
			String[] parts2 = enchantments.split(" & ");
			for (String enchant : parts2){
				String[] parts3 = enchant.split(" ");
				int type = Integer.parseInt(parts3[0]);
				int level = Integer.parseInt(parts3[1]);
				item.addUnsafeEnchantment(Enchantment.getById(type), level);
			}
		}
		ItemMeta meta = item.getItemMeta();
		String[] displayStrings = parts[0].split(" - ");
		meta.setDisplayName(plugin.utils.replace(displayStrings[0]));
		if (displayStrings.length > 1){
			List<String> lore = Arrays.asList(plugin.utils.replace(displayStrings[1]).split("&&"));
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		player.getInventory().setChestplate(item);
	}
	
	
	public void giveLeggings(Player player, String kitname){
		String s = plugin.getConfig().getString("kits." + kitname + ".leggings");
		if (s.equals("")) return;
		String[] parts = s.split(", ");
		String id = parts[1];
		String[] idparts = id.split(":");
		int amount = Integer.parseInt(parts[2]);
		ItemStack item;
		if (idparts.length == 1){
			item = new ItemStack(Integer.parseInt(idparts[0]), amount);
		} else {
			item = new ItemStack(Integer.parseInt(idparts[0]), amount, (byte)Integer.parseInt(idparts[1]));
		}
		if (parts.length == 4){
			String enchantments = parts[3];
			String[] parts2 = enchantments.split(" & ");
			for (String enchant : parts2){
				String[] parts3 = enchant.split(" ");
				int type = Integer.parseInt(parts3[0]);
				int level = Integer.parseInt(parts3[1]);
				item.addUnsafeEnchantment(Enchantment.getById(type), level);
			}
		}
		ItemMeta meta = item.getItemMeta();
		String[] displayStrings = parts[0].split(" - ");
		meta.setDisplayName(plugin.utils.replace(displayStrings[0]));
		if (displayStrings.length > 1){
			List<String> lore = Arrays.asList(plugin.utils.replace(displayStrings[1]).split("&&"));
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		player.getInventory().setLeggings(item);
	}
	
	
	public void giveBoots(Player player, String kitname){
		String s = plugin.getConfig().getString("kits." + kitname + ".boots");
		if (s.equals("")) return;
		String[] parts = s.split(", ");
		String id = parts[1];
		String[] idparts = id.split(":");
		int amount = Integer.parseInt(parts[2]);
		ItemStack item;
		if (idparts.length == 1){
			item = new ItemStack(Integer.parseInt(idparts[0]), amount);
		} else {
			item = new ItemStack(Integer.parseInt(idparts[0]), amount, (byte)Integer.parseInt(idparts[1]));
		}
		if (parts.length == 4){
			String enchantments = parts[3];
			String[] parts2 = enchantments.split(" & ");
			for (String enchant : parts2){
				String[] parts3 = enchant.split(" ");
				int type = Integer.parseInt(parts3[0]);
				int level = Integer.parseInt(parts3[1]);
				item.addUnsafeEnchantment(Enchantment.getById(type), level);
			}
		}
		ItemMeta meta = item.getItemMeta();
		String[] displayStrings = parts[0].split(" - ");
		meta.setDisplayName(plugin.utils.replace(displayStrings[0]));
		if (displayStrings.length > 1){
			List<String> lore = Arrays.asList(plugin.utils.replace(displayStrings[1]).split("&&"));
			meta.setLore(lore);
		}
		item.setItemMeta(meta);
		player.getInventory().setBoots(item);
	}
	
	
	public void dropChestLoot(Block chest){
    	final Location loc = chest.getLocation();
    	loc.getBlock().setType(Material.AIR);
    	int min = plugin.getConfig().getInt("min-loot.single"); int max = plugin.getConfig().getInt("max-loot.single");
    	int itemCount = random.nextInt(max - min + 1) + min;
    	
    	for (int i = 0; i < itemCount; i++){
    		String s = chestLoot.get(random.nextInt(chestLoot.size()));
    		
    		String[] parts = s.split(", ");
    		String id = parts[0];
    		int amount = 1;
    		if (parts.length == 2) amount = Integer.parseInt(parts[1]);
    		String[] idparts = id.split(":");
    		ItemStack item;
    		if (idparts.length == 1){
    			item = new ItemStack(Integer.parseInt(idparts[0]), amount);
    		} else {
    			item = new ItemStack(Integer.parseInt(idparts[0]), amount, (byte)Integer.parseInt(idparts[1]));
    		}
    		
    		if (id.equals("259")){
    			ItemMeta meta = item.getItemMeta();
    			meta.setDisplayName(plugin.utils.replace("&4Flame Thrower"));
        		item.setItemMeta(meta);
    		} else if (id.equals("403")){
    			EnchantmentStorageMeta meta = (EnchantmentStorageMeta)item.getItemMeta();
    			Enchantment e = Enchantment.getByName(plugin.utils.getRandomEnchantment());
    			meta.addStoredEnchant(e, random.nextInt(e.getMaxLevel()) + 1, false);
    			item.setItemMeta(meta);
    		}
    		/*if (parts.length == 3){
    			String enchantments = parts[2];
    			String[] parts2 = enchantments.split(" & ");
    			for (String enchant : parts2){
    				String[] parts3 = enchant.split(" ");
    				int type = Integer.parseInt(parts3[0]);
    				int level = Integer.parseInt(parts3[1]);
    				item.addEnchantment(Enchantment.getById(type), level);
    			}
    		}
    		ItemMeta meta = item.getItemMeta();
			String[] displayStrings = parts[0].split(" - ");
			meta.setDisplayName(plugin.utils.replace(displayStrings[0]));
			if (displayStrings.length > 1){
				List<String> lore = Arrays.asList(plugin.utils.replace(displayStrings[1]).split("&&"));
				meta.setLore(lore);
			}
    		item.setItemMeta(meta);*/
    		
    		loc.getWorld().dropItemNaturally(loc, item);
    	}
    	/*Runnable runnable = new Runnable() {
		    @Override
		    public void run() {
				loc.getBlock().setType(Material.CHEST);
				plugin.pendingChests.remove(loc);
		    }
		};
    	final int id = plugin.scheduler.scheduleSyncDelayedTask(plugin, runnable, 6000L);
    	plugin.pendingChests.put(loc, runnable);*/
	}
	
	/*public void dropDoubleChestLoot(DoubleChest chest){
    	final Chest leftChest = (Chest)chest.getLeftSide();
    	final Chest rightChest = (Chest)chest.getRightSide();
    	if (leftChest.getType() == Material.TRAPPED_CHEST) return;
    	final Location loc = leftChest.getLocation();
    	loc.getBlock().setType(Material.AIR);
    	rightChest.getLocation().getBlock().setType(Material.AIR);
    	int min = plugin.getConfig().getInt("min-loot.double"); int max = plugin.getConfig().getInt("max-loot.double");
    	int itemCount = random.nextInt(max - min + 1) + min;
    	
    	for (int i = 0; i < itemCount; i++){
    		String s = chestLoot.get(random.nextInt(chestLoot.size()));
    		
    		String[] parts = s.split(", ");
    		String id = parts[0];
    		int amount = 1;
    		if (parts.length == 2) amount = Integer.parseInt(parts[1]);
    		String[] idparts = id.split(":");
    		ItemStack item;
    		if (idparts.length == 1){
    			item = new ItemStack(Integer.parseInt(idparts[0]), amount);
    		} else {
    			item = new ItemStack(Integer.parseInt(idparts[0]), amount, (byte)Integer.parseInt(idparts[1]));
    		}
    		
    		if (id.equals("259")){
    			ItemMeta meta = item.getItemMeta();
    			meta.setDisplayName(plugin.utils.replace("&4Flame Thrower"));
        		item.setItemMeta(meta);
    		} else if (id.equals("403")){
    			EnchantmentStorageMeta meta = (EnchantmentStorageMeta)item.getItemMeta();
    			Enchantment e = Enchantment.getByName(plugin.utils.getRandomEnchantment());
    			meta.addStoredEnchant(e, random.nextInt(e.getMaxLevel()) + 1, false);
    		}
    		/*if (parts.length == 3){
    			String enchantments = parts[2];
    			String[] parts2 = enchantments.split(" & ");
    			for (String enchant : parts2){
    				String[] parts3 = enchant.split(" ");
    				int type = Integer.parseInt(parts3[0]);
    				int level = Integer.parseInt(parts3[1]);
    				item.addEnchantment(Enchantment.getById(type), level);
    			}
    		}
    		ItemMeta meta = item.getItemMeta();
			String[] displayStrings = parts[0].split(" - ");
			meta.setDisplayName(plugin.utils.replace(displayStrings[0]));
			if (displayStrings.length > 1){
				List<String> lore = Arrays.asList(plugin.utils.replace(displayStrings[1]).split("&&"));
				meta.setLore(lore);
			}
    		item.setItemMeta(meta);*/
	
	/*
    		
    		loc.getWorld().dropItemNaturally(loc, item);
    	}
    	/*Runnable runnable = new Runnable() {
		    @Override
		    public void run() {
				loc.getBlock().setType(Material.CHEST);
				rightChest.getLocation().getBlock().setType(Material.CHEST);
				plugin.pendingChests.remove(loc);
		    }
		};
    	final int id = plugin.scheduler.scheduleSyncDelayedTask(plugin, runnable, 6000L);
    	plugin.pendingChests.put(loc, runnable);*/
	/*
	}
	*/
	
	public void dropGiantLoot(Entity e){
    	final Location loc = e.getLocation();
    	loc.getBlock().setType(Material.AIR);
    	int min = plugin.getConfig().getInt("giant-drops.min-loot"); int max = plugin.getConfig().getInt("giant-drops.max-loot");
    	int itemCount = random.nextInt(max - min + 1) + min;
    	
    	for (int i = 0; i < itemCount; i++){
    		String s = giantLoot.get(random.nextInt(giantLoot.size()));
    		
    		String[] parts = s.split(", ");
    		String id = parts[0];
    		int amount = Integer.parseInt(parts[1]);
    		String[] idparts = id.split(":");
    		ItemStack item;
    		if (idparts.length == 1){
    			item = new ItemStack(Integer.parseInt(idparts[0]), amount);
    		} else {
    			item = new ItemStack(Integer.parseInt(idparts[0]), amount, (byte)Integer.parseInt(idparts[1]));
    		}
    		
    		if (id.equals("259")){
    			ItemMeta meta = item.getItemMeta();
    			meta.setDisplayName(plugin.utils.replace("&4Flame Thrower"));
        		item.setItemMeta(meta);
    		} else if (id.equals("403")){
    			EnchantmentStorageMeta meta = (EnchantmentStorageMeta)item.getItemMeta();
    			Enchantment en = Enchantment.getByName(plugin.utils.getRandomEnchantment());
    			meta.addStoredEnchant(en, random.nextInt(en.getMaxLevel()) + 1, false);
    		}
    		
    		if (parts.length == 3){
    			String enchantments = parts[2];
    			String[] parts2 = enchantments.split(" & ");
    			for (String enchant : parts2){
    				String[] parts3 = enchant.split(" ");
    				int type = Integer.parseInt(parts3[0]);
    				int level = Integer.parseInt(parts3[1]);
    				item.addUnsafeEnchantment(Enchantment.getById(type), level);
    			}
    		}
    		/*ItemMeta meta = item.getItemMeta();
			String[] displayStrings = parts[0].split(" - ");
			meta.setDisplayName(plugin.utils.replace(displayStrings[0]));
			if (displayStrings.length > 1){
				List<String> lore = Arrays.asList(plugin.utils.replace(displayStrings[1]).split("&&"));
				meta.setLore(lore);
			}
    		item.setItemMeta(meta);*/
    		
    		loc.getWorld().dropItemNaturally(loc, item);
    	}
	}
}
