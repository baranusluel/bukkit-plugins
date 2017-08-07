package com.barancode.myitems;

import java.util.HashMap;
import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class MyItem {
	String name, displayname;
	Material material;
	int data, damage;
	HashMap<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
	boolean single, global, console;
	String[] lore;
	LinkedList<MyEvent> events = new LinkedList<MyEvent>();
	
	public MyItem(String name, Main plugin){
		this.name = name;
		material = Material.getMaterial(plugin.config.getString("items." + name + ".material"));
		data = plugin.config.getInt("items." + name + ".data-id");
		data = plugin.config.getInt("items." + name + ".damage");
		for (String s : plugin.config.getConfigurationSection("items." + name + ".enchantments").getKeys(false))
			enchantments.put(Enchantment.getByName(s), plugin.config.getInt("items." + name + ".enchantments." + s));
		single = plugin.config.getBoolean("items." + name + ".single-use");
		global = plugin.config.getBoolean("items." + name + ".global");
		console = plugin.config.getBoolean("items." + name + ".as-console");
		displayname = ChatColor.translateAlternateColorCodes('&', plugin.config.getString("items." + name + ".displayname"));
		lore = ChatColor.translateAlternateColorCodes('&', plugin.config.getString("items." + name + ".lore")).split("\\|");
		for (String s : plugin.config.getConfigurationSection("items." + name + ".events").getKeys(false))
			events.add(new MyEvent(plugin.config.getString("items." + name + ".events." + s + ".command"),
					plugin.config.getBoolean("items." + name + ".events." + s + ".cancel-event")));
	}
}
