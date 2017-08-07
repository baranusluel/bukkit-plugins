package com.barancode.itemsandvisibility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin{
	List<CustomItem> customItems = new LinkedList<CustomItem>();
	List<ItemMeta> metas = new LinkedList<ItemMeta>();
	List<CustomConfig> configs = new LinkedList<CustomConfig>();
	ItemListener listener = new ItemListener(this);
	CustomConfig menuconfig = null;
	Menu menu = null;
	HashMap<String, String> menuItems = new HashMap<String, String>();
	Visibility visibility = new Visibility(this);
	BukkitScheduler scheduler;
	
	public void onEnable(){
	    getServer().getPluginManager().registerEvents(listener, this);
	    scheduler = Bukkit.getScheduler();
	    
		if (!getDataFolder().exists()){
			getDataFolder().mkdir();
			saveDefaultConfig();
			CustomConfig customconfig = new CustomConfig(this, "sampleItem.yml");
			customconfig.saveDefaultConfig();
			configs.add(customconfig);
			customconfig = new CustomConfig(this, "menuItem.yml");
			customconfig.saveDefaultConfig();
			configs.add(customconfig);
		} else {
			saveDefaultConfig();
			CustomConfig customconfig = null;
			for (String s : getConfig().getStringList("active-items")){
				customconfig = new CustomConfig(this, s + ".yml");
				configs.add(customconfig);
			}
		}
		menuconfig = new CustomConfig(this, "visibilityMenu.yml");
		menuconfig.saveDefaultConfig();
		
		for (CustomConfig config : configs){
			String name = ChatColor.translateAlternateColorCodes('&', config.getCustomConfig().getString("item-name"));
			String loreString = ChatColor.translateAlternateColorCodes('&', config.getCustomConfig().getString("item-lore"));
			List<String> lore = Arrays.asList(loreString.split("\n"));
			String material = config.getCustomConfig().getString("material");
			ItemStack item = new ItemStack(Material.getMaterial(material));
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(name);
			meta.setLore(lore);
			item.setItemMeta(meta);
			metas.add(meta);
			
			int slot = config.getCustomConfig().getInt("slot");
			String command = config.getCustomConfig().getString("command");
			boolean menu = config.getCustomConfig().getBoolean("custom-menu");
			customItems.add(new CustomItem(item, command, slot, menu));
		}
		
		setupMenu();
	}
	
	public void setupMenu(){
		menu = new Menu(ChatColor.translateAlternateColorCodes('&', menuconfig.getCustomConfig().getString("menu.name")), 9, new Menu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(Menu.OptionClickEvent event) {
                event.setWillClose(true);
            	String name = menuItems.get(event.getName());
            	if (name.equals("info")){
                    event.setWillClose(false);
            		return;
            	} else if (name.equals("exit")){
            		return;
            	} else if (name.equals("show")){
            		visibility.showPlayers(event.getPlayer());
            		event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', menuconfig.getCustomConfig().getString("show-item.selected")));
            	} else if (name.equals("hide")){
            		visibility.hidePlayers(event.getPlayer());
            		event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', menuconfig.getCustomConfig().getString("hide-item.selected")));
            	}
                showMenu(event.getPlayer());
            }
        }, this);
	}
	
	public void showMenu(final Player p){
		scheduler.scheduleSyncDelayedTask(this, new Runnable(){
			@Override
			public void run(){
				menu.removeOptions();
				setMenuItem(menu, "info-item", "info", p);
				setMenuItem(menu, "exit-item", "exit", p);
				setMenuItem(menu, "show-item", "show", p);
				setMenuItem(menu, "hide-item", "hide", p);
				menu.open(p);
			}
		}, 1);
	}
	
	public void setMenuItem(Menu menu, String path, String item, Player p){
		String displayName = ChatColor.translateAlternateColorCodes('&', menuconfig.getCustomConfig().getString(path + ".name"));
		menuItems.put(displayName, item);
		String material = menuconfig.getCustomConfig().getString(path + ".material");
		ItemStack itemstack = new ItemStack(Material.getMaterial(material));
		if ((item.equalsIgnoreCase("show") && !p.hasMetadata("hiding")) || (item.equalsIgnoreCase("hide") && p.hasMetadata("hiding"))) itemstack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		menu.setOption(menuconfig.getCustomConfig().getInt(path + ".slot"), itemstack, displayName, ChatColor.translateAlternateColorCodes('&', menuconfig.getCustomConfig().getString(path + ".lore")).split("\n"));
	}
}
