package com.barancode.itemsandvisibility;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.PlayerInventory;

public class ItemListener implements Listener{
	Main plugin;
	
	public ItemListener(Main plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		PlayerInventory inventory = e.getPlayer().getInventory();
		for (CustomItem item : plugin.customItems){
			inventory.setItem(item.slot, item.item);
		}
		
		for (Player p : Bukkit.getOnlinePlayers()){
			if (p.hasMetadata("hiding")){
				p.hidePlayer(e.getPlayer());
			} else {
				p.showPlayer(e.getPlayer());
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		if (e.getAction() == Action.PHYSICAL) return;
		if (e.getItem() == null) return;
		if (!plugin.metas.contains(e.getItem().getItemMeta())) return;
		
		for (CustomItem item : plugin.customItems){
			if (item.item.isSimilar(e.getItem())) {
				if (!item.menu){
					if (!item.command.equals("")) Bukkit.dispatchCommand((CommandSender)e.getPlayer(), item.command);
				} else {
					if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK){
						if (!item.command.equals("")) Bukkit.dispatchCommand((CommandSender)e.getPlayer(), item.command);
					} else if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
						plugin.showMenu(e.getPlayer());
					}
				}
				break;
			}
		}
	}
	
	@EventHandler
	public void playerDropItems(PlayerDropItemEvent e){
		if (plugin.metas.contains(e.getItemDrop().getItemStack().getItemMeta())){
			for (CustomItem item : plugin.customItems){
				if (item.item.isSimilar(e.getItemDrop().getItemStack())){
					e.getPlayer().getInventory().setItem(item.slot, item.item);
					break;
				}
			}
			e.getItemDrop().remove();
		}
	}
	
	@EventHandler
	public void playerDie(PlayerDeathEvent e){
		for (CustomItem item : plugin.customItems){
			e.getDrops().remove(item.item);
		}
	}
	
	@EventHandler
	public void inventoryClick(InventoryClickEvent e){
		if ((e.getCursor() != null && plugin.metas.contains(e.getCursor().getItemMeta()))
				|| (e.getCurrentItem() != null && plugin.metas.contains(e.getCurrentItem().getItemMeta()))) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onMove(InventoryMoveItemEvent e){
		if (plugin.metas.contains(e.getItem().getItemMeta())){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		PlayerInventory inventory = e.getPlayer().getInventory();
		for (CustomItem item : plugin.customItems){
			inventory.setItem(item.slot, item.item);
		}
	}
}
