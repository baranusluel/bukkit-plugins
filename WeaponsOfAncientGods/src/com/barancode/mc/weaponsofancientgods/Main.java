package com.barancode.mc.weaponsofancientgods;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Main extends JavaPlugin{
	
    PlayerInventory inventory;
    ItemStack hammer = new ItemStack(Material.STONE_AXE, 1);
    ItemStack lightning = new ItemStack(Material.FLINT_AND_STEEL, 1);
    ItemStack trident = new ItemStack(Material.TORCH, 1);
    ItemStack staff = new ItemStack(Material.STICK, 1);
    ItemStack sceptre = new ItemStack(Material.BLAZE_ROD, 1);
    ItemStack spear = new ItemStack(Material.ARROW, 1);
    ItemMeta meta;
	
	Commands commandclass = new Commands(this);
	Events eventclass = new Events(this);
	
	@Override
	public void onEnable(){
		getCommand("loki").setExecutor(this.commandclass);
		getCommand("thor").setExecutor(this.commandclass);
		getCommand("zeus").setExecutor(this.commandclass);
		getCommand("poseidon").setExecutor(this.commandclass);
		getCommand("hades").setExecutor(this.commandclass);
		getCommand("odin").setExecutor(this.commandclass);
		
		getServer().getPluginManager().registerEvents(eventclass, this);
		
		
		meta = hammer.getItemMeta();
	    meta.setDisplayName(ChatColor.GOLD + "Thor's Hammer");
	    meta.setLore(null);
	    hammer.setItemMeta(meta);
	    
	    meta = lightning.getItemMeta();
	    meta.setDisplayName(ChatColor.GOLD + "Zeus's Lightning");
	    lightning.setItemMeta(meta);
	    
	    meta = trident.getItemMeta();
	    meta.setDisplayName(ChatColor.GOLD + "Poseidon's Trident");
	    trident.setItemMeta(meta);
	    
	    meta = staff.getItemMeta();
	    meta.setDisplayName(ChatColor.GOLD + "Hades's Staff");
	    staff.setItemMeta(meta);
	    
	    meta = sceptre.getItemMeta();
	    meta.setDisplayName(ChatColor.GOLD + "Loki's Sceptre");
	    sceptre.setItemMeta(meta);
	    
	    meta = spear.getItemMeta();
	    meta.setDisplayName(ChatColor.GOLD + "Odin's Spear");
	    spear.setItemMeta(meta);
	}

	public void zeus(Player player){
		if (!player.getInventory().contains(lightning)) player.getInventory().addItem(lightning);
		else player.sendMessage(ChatColor.RED + "You already have that item");
	}
	
	public void poseidon(Player player){
		if (!player.getInventory().contains(trident)) player.getInventory().addItem(trident);
		else player.sendMessage(ChatColor.RED + "You already have that item");
	}
	
	public void hades(Player player){
		if (!player.getInventory().contains(staff)) player.getInventory().addItem(staff);
		else player.sendMessage(ChatColor.RED + "You already have that item");
	}
	
	public void thor(Player player){
		if (!player.getInventory().contains(hammer)) player.getInventory().addItem(hammer);
		else player.sendMessage(ChatColor.RED + "You already have that item");
	}
	
	public void loki(Player player){
		if (!player.getInventory().contains(sceptre)) player.getInventory().addItem(sceptre);
		else player.sendMessage(ChatColor.RED + "You already have that item");
	}
	
	public void odin(Player player){
		if (!player.getInventory().contains(spear)) player.getInventory().addItem(spear);
		else player.sendMessage(ChatColor.RED + "You already have that item");
	}
	
	
	public void knockback(Player p, Entity t)
    {
        Location l = t.getLocation().subtract(p.getLocation());
        double distance = t.getLocation().distance(p.getLocation());
        Vector v = l.toVector().multiply(4/distance);
        t.setVelocity(v);
    }
	
	public void bounceBlock(Block b)
    {
        if(b == null) return;
        
        FallingBlock fb = b.getWorld().spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
        
        b.setType(Material.AIR);
        
        float x = (float) -1 + (float) (Math.random() * ((1 - -1) + 1));
        float y = 2;//(float) -5 + (float)(Math.random() * ((5 - -5) + 1));
        float z = (float) -0.3 + (float)(Math.random() * ((0.3 - -0.3) + 1));
        
        fb.setVelocity(new Vector(x, y, z));
    }
}
