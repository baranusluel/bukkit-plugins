package com.barancode.wands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	ItemStack wand = new ItemStack(Material.DIAMOND_SWORD);
	ItemStack eye = new ItemStack(Material.EYE_OF_ENDER);
	ItemStack earthwand = new ItemStack(Material.DIAMOND_HOE);
	HashMap<UUID, Integer> selectedSpell = new HashMap<UUID, Integer>();
	HashSet<UUID> freezers = new HashSet<UUID>();
	
	public void onEnable(){
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
		
		ItemMeta meta = wand.getItemMeta();
		meta.setDisplayName(Utils.color(getConfig().getString("wand-name")));
		meta.setLore(Utils.color(getConfig().getStringList("wand-lore")));
		wand.setItemMeta(meta);
		
		meta = earthwand.getItemMeta();
		meta.setDisplayName(Utils.color(getConfig().getString("earthwand-name")));
		meta.setLore(Utils.color(getConfig().getStringList("earthwand-lore")));
		earthwand.setItemMeta(meta);

		meta = eye.getItemMeta();
		meta.setDisplayName(Utils.color(getConfig().getString("eyeoftime-name")));
		meta.setLore(Utils.color(getConfig().getStringList("eyeoftime-lore")));
		eye.setItemMeta(meta);
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
	{
		if (!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "You need to be a player to do that!");
			return true;
		}
		Player p = (Player)sender;
		if (cmd.getName().equalsIgnoreCase("wand")){
			p.getInventory().addItem(wand);
			p.sendMessage(Utils.color(getConfig().getString("give-wand")));
		} else if (cmd.getName().equalsIgnoreCase("wandearth")){
			p.getInventory().addItem(earthwand);
			p.sendMessage(Utils.color(getConfig().getString("give-earthwand")));
		} else {
			p.getInventory().addItem(eye);
			p.sendMessage(Utils.color(getConfig().getString("give-eyeoftime")));
		}
		return true;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		if (e.getAction() == Action.PHYSICAL) return;
		if (e.getItem() == null) return;
		UUID uuid = e.getPlayer().getUniqueId();
		if (e.getItem().isSimilar(wand)){
			if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
				if (!selectedSpell.containsKey(uuid)){
					selectedSpell.put(uuid, 5);
					e.getPlayer().sendMessage(Utils.color(getConfig().getString("spell-changed")
							.replaceAll("<spell>", getConfig().getString("spells.5"))));
				} else {
					int oldSpell = selectedSpell.get(uuid);
					int newSpell = (oldSpell != 1) ? oldSpell - 1 : 5;
					selectedSpell.put(uuid, newSpell);
					e.getPlayer().sendMessage(Utils.color(getConfig().getString("spell-changed")
							.replaceAll("<spell>", getConfig().getString("spells." + newSpell))));
				}
			} else {
				if (!selectedSpell.containsKey(uuid)){
					e.getPlayer().sendMessage(Utils.color(getConfig().getString("must-bind")));
					return;
				}
				int spell = selectedSpell.get(uuid);
				if (spell == 5) five(e.getPlayer());
				else if (spell == 4) four(e.getPlayer());
				else if (spell == 3) three(e.getPlayer());
				else if (spell == 2) two(e.getPlayer());
				else one(e.getPlayer());
			}
		} else if (e.getItem().isSimilar(earthwand)){
			
		} else if (e.getItem().isSimilar(eye)){
			eyeFreeze(e.getPlayer());
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e){
		if (e.getDamager() instanceof Player){
			Player p = (Player)e.getDamager();
			UUID uuid = p.getUniqueId();
			if (p.getItemInHand() == null) return;
			if (p.getItemInHand().isSimilar(wand)){
				e.setCancelled(true);
				if (!selectedSpell.containsKey(uuid)){
					p.sendMessage(Utils.color(getConfig().getString("must-bind")));
					return;
				}
				int spell = selectedSpell.get(uuid);
				Block b = e.getEntity().getLocation().getBlock();
				if (spell == 5) five(p);
				else if (spell == 4) four(p, b);
				else if (spell == 3) three(p, b);
				else if (spell == 2) two(p);
				else one(p);
			} else if (p.getItemInHand().isSimilar(earthwand)){
				
			} else if (p.getItemInHand().isSimilar(eye)){
				eyeFreeze(p);
			}
		}
	}
	
	public void eyeFreeze(Player p){
		frozen++;
		Bukkit.broadcastMessage(Utils.color(getConfig().getString("freeze-broadcast").replaceAll("<player>", p.getName())));
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		if (frozen > 0) e.setCancelled(true);
	}
	
	public void five(Player p){
		p.setVelocity(p.getLocation().getDirection().multiply(8).setY(1.5));
	}
	
	public void four(Player p, Block b){
		Location effectLoc = b.getLocation(); effectLoc.setY(effectLoc.getY() + 2);
		ParticleEffect.PORTAL.display(effectLoc, (float)0.5, (float)0.5, (float)0.5, (float)1, 100);
		ParticleEffect.LARGE_SMOKE.display(effectLoc, (float)0.2, (float)0.2, (float)0.2, (float)0, 100);
		Entity[] entities = b.getChunk().getEntities();
		for (Entity e : entities){
			if (e.getLocation().distanceSquared(b.getLocation()) <= 4){
				e.setVelocity(e.getVelocity().setY(2));
				if (e instanceof Damageable){
					Damageable d = (Damageable)e;
					if (d.getHealth() > 2) d.setHealth(d.getHealth() - 2);
					else d.setHealth(0);
				}
			}
		}
	}
	
	public void four(Player p){
		Block b = Utils.getTargetBlock(p, 200);
		four(p, b);
	}
	
	public void three(Player p, Block b){
		Location effectLoc = b.getLocation(); effectLoc.setY(effectLoc.getY() + 2);
		ParticleEffect.PORTAL.display(effectLoc, (float)1.5, (float)1.5, (float)1.5, (float)1, 100);
		ParticleEffect.LARGE_SMOKE.display(effectLoc, (float)1, (float)1, (float)1, (float)0, 100);
		Entity[] entities = b.getChunk().getEntities();
		for (Entity e : entities){
			if (e.getLocation().distanceSquared(b.getLocation()) <= 16){
				if (e instanceof Damageable){
					Damageable d = (Damageable)e;
					if (d.getHealth() > 2) d.setHealth(d.getHealth() - 5);
					else d.setHealth(5);
				}
			}
		}
	}
	
	public void three(Player p){
		Block b = Utils.getTargetBlock(p, 200);
		three(p, b);
	}
	
	public void two(Player p){
		
	}
	
	public void one(Player p){
		
	}
}
