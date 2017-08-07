package com.barancode.mc.ddg.guns;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.barancode.mc.ddg.guns.Menu;

public class Main extends JavaPlugin{
	
	GunListener events = new GunListener(this);
	GunCommands commands = new GunCommands(this);
	Utils utils = new Utils(this);
	Menu menu;
	boolean running = false;
	
	ArenaManager arenamanager = new ArenaManager(this);
	
	DataFile df = new DataFile(this);
	GunFile guf = new GunFile(this);
	GrenadeFile grf = new GrenadeFile(this);
	KitFile kf = new KitFile(this);
	
	HashMap<String, String> displaynames = new HashMap<String, String>();
	
	public void onEnable(){
		getCommand("setjoinspawn").setExecutor(this.commands);
		getCommand("setlobby").setExecutor(this.commands);
		getCommand("setspawnteam1").setExecutor(this.commands);
		getCommand("setspawnteam2").setExecutor(this.commands);
		getCommand("setobjective").setExecutor(this.commands);
		
		saveDefaultConfig();
		guf.saveDefaultConfig();
		grf.saveDefaultConfig();
		kf.saveDefaultConfig();
		
		getServer().getPluginManager().registerEvents(events, this);
		
		menu = new Menu(ChatColor.DARK_RED + "Kit Selector", 9, new Menu.OptionClickEventHandler() {
	        @Override
	        public void onOptionClick(Menu.OptionClickEvent event) {
	        	String name = displaynames.get(event.getName());
	        	
	        	String permission = kf.getCustomConfig().getString(name + ".permission");
	        	if (!permission.equals("")){
	        		if (!event.getPlayer().hasPermission(permission)){
	        			event.getPlayer().sendMessage(utils.replace(getConfig().getString("kit-permission")));
	        			event.setWillClose(false);
	        			return;
	        		}
	        	}
	        	
	        	// Give the kit
	        	event.getPlayer().sendMessage(utils.replace(kf.getCustomConfig().getString(name + ".menu.message")));
	        	
	            event.setWillClose(true);
	        }
	    }, this);
		
		for (String s : kf.getCustomConfig().getStringList("kits")){
			String displayname = utils.replace(kf.getCustomConfig().getString(s + ".display-name"));
			menu.setOption(kf.getCustomConfig().getInt(s + ".menu.iconlocation"), new ItemStack(Material.getMaterial(kf.getCustomConfig().getInt(s + ".menu.iconid")), 1), displayname, utils.replace(kf.getCustomConfig().getString(s + ".lore")));
			displaynames.put(displayname, s);
		}
	}
}
