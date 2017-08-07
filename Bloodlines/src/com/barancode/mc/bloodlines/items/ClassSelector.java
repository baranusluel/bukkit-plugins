package com.barancode.mc.bloodlines.items;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.barancode.mc.bloodlines.Main;
import com.barancode.mc.bloodlines.tools.Utils;

public class ClassSelector {
	
	public static ItemStack item = null;
	
	public static void initialize(){
		item = new ItemStack(Material.getMaterial(Main.config.getString("items.classselector.material")));
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Utils.colorize(Main.config.getString("items.classselector.name")));
		meta.setLore(Arrays.asList(Utils.colorize(Main.config.getString("items.classselector.lore")).split("--")));
		item.setItemMeta(meta);
	}
}
