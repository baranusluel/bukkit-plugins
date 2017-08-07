package com.barancode.blockprotection;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Listeners implements Listener{
	
	Main plugin;
	
	public Listeners(Main plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		if (!plugin.players.players.contains(e.getPlayer().getUniqueId())){
			plugin.pendingRules.add(e.getPlayer().getName());
			e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.ruleConfig.getCustomConfig().getString("rules")));
			e.getPlayer().setWalkSpeed(0.0F);
		} else {
			e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.ruleConfig.getCustomConfig().getString("join-message")));
			e.getPlayer().setWalkSpeed(0.2F);
		}
		plugin.boardman.updateScoreboard(e.getPlayer());
		int group = plugin.pman.getGroup(e.getPlayer().getName());
		if (plugin.groupPlayers.containsKey(group)) plugin.groupPlayers.put(group, plugin.groupPlayers.get(group) + 1);
		else plugin.groupPlayers.put(group, 1);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if (plugin.pendingRules.contains(e.getPlayer().getName()))
			plugin.pendingRules.remove(e.getPlayer().getName());
		int group = plugin.pman.getGroup(e.getPlayer().getName());
		plugin.groupPlayers.put(group, plugin.groupPlayers.get(group) - 1);
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		if (plugin.pendingRules.contains(e.getPlayer().getName()))
			e.getPlayer().setWalkSpeed(0.0F);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		if (e.isCancelled()) return;
		if (plugin.pendingRules.contains(e.getPlayer().getName())){
	        Location locFrom = e.getFrom();
	        Location locTo = e.getTo();
	        if (locFrom.getBlockX() != locTo.getBlockX() || locFrom.getBlockY() != locTo.getBlockY() || locFrom.getBlockZ() != locTo.getBlockZ()) {
	            e.setTo(e.getFrom());
	        }
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e){
		if (plugin.pendingRules.contains(e.getPlayer().getName())) e.setCancelled(true);
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e){
		if (plugin.override.contains(e.getPlayer().getName())) return;
		
		String placer = plugin.cpman.getPlacer(e.getBlock());
		if (placer == null || placer.equals("")) return;
		int placerGroup = plugin.pman.getGroup(placer);
		
		int breakerGroup = plugin.pman.getGroup(e.getPlayer().getName());
		if (placerGroup != breakerGroup){
			if (!plugin.groupPlayers.containsKey(placerGroup) || plugin.groupPlayers.get(placerGroup) < 2){
				e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("no-members-online")));
				e.setCancelled(true);
				return;
			}
			
			Material m = e.getBlock().getType();
			if (m == Material.CHEST || m == Material.TRAPPED_CHEST){
				e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("chest-break-error")));
				e.setCancelled(true);
				return;
			}
			
			if (plugin.blockPrices.containsKey(m.name())){
				int cost = plugin.blockPrices.get(m.name());
				if (cost == 0) return;
				if (plugin.dbman.takeAmount(e.getPlayer().getName(), cost)){
					e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("break-block").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
					plugin.boardman.updateScoreboard(e.getPlayer());
				} else {
					e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("break-block-error").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
					e.setCancelled(true);
				}
			} else {
				int cost = plugin.defaultPrice;
				if (plugin.dbman.takeAmount(e.getPlayer().getName(), cost)){
					e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("break-block").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
					plugin.boardman.updateScoreboard(e.getPlayer());
				} else {
					e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("break-block-error").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void entityExplode(EntityExplodeEvent e){
		int Mcost = 0;
		int MremainingCost = 0;
		
		boolean check = false;
		Player p = null;
		int group = 0;
		if (e.getEntityType() == EntityType.PRIMED_TNT){
			Entity source = ((TNTPrimed)e.getEntity()).getSource();
			if (source != null && source.getType() == EntityType.PLAYER){
				p = (Player)source;
				if (plugin.override.contains(p.getName())) return;
				group = plugin.pman.getGroup(p.getName());
				check = true;
			} else if (source == null){
				e.blockList().clear();
				return;
			}
		}
		
	    for (Iterator<Block> it = e.blockList().iterator(); it.hasNext();){
	    	Block b = it.next();
	    	if (b.getType() == Material.CHEST || b.getType() == Material.TRAPPED_CHEST){
	    		it.remove();
	    		continue;
	    	}
	    	if (check){
	    		String placer = plugin.cpman.getPlacer(b);
	    		if (placer == null || placer.equals("")) continue;
	    		int placerGroup = plugin.pman.getGroup(placer);
	    		
	    		if (placerGroup != group){
		    		if (!plugin.groupPlayers.containsKey(placerGroup) || plugin.groupPlayers.get(placerGroup) < 2){
		    			p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("no-members-online")));
		    			it.remove();
		    			continue;
		    		}
	    			
	    			Material m = b.getType();
	    			if (plugin.blockPrices.containsKey(m.name())){
	    				int cost = plugin.blockPrices.get(m.name());
	    				if (cost == 0) continue;
	    				if (plugin.dbman.takeAmount(p.getName(), cost)){
	    					Mcost = Mcost + cost;
	    					plugin.boardman.updateScoreboard(p);
	    				} else {
	    					MremainingCost = MremainingCost + cost;
	    					it.remove();
	    					continue;
	    				}
	    			} else {
	    				int cost = plugin.defaultPrice;
	    				if (plugin.dbman.takeAmount(p.getName(), cost)){
	    					Mcost = Mcost + cost;
	    					plugin.boardman.updateScoreboard(p);
	    				} else {
	    					MremainingCost = MremainingCost + cost;
	    					it.remove();
	    					continue;
	    				}
	    			}
	    		}
	    	}
	    }
	    
	    if (Mcost != 0) p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("tnt-destroy-blocks").replaceAll("<cost>", Mcost + "")));
	    if (MremainingCost != 0) p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("tnt-destroy-blocks-error").replaceAll("<cost>", MremainingCost + "")));
	}
	
	@EventHandler
	public void onRetract(BlockPistonRetractEvent e){
		Block b = e.getRetractLocation().getBlock();
		if (b == null) return;
		Player p = Utils.closestPlayer(e.getBlock().getLocation());
		if (p == null) return;
		
		if (plugin.override.contains(p.getName())) return;
		
		String placer = plugin.cpman.getPlacer(b);
		if (placer == null || placer.equals("")) return;
		int placerGroup = plugin.pman.getGroup(placer);
		
		int breakerGroup = plugin.pman.getGroup(p.getName());
		if (placerGroup != breakerGroup){
			if (!plugin.groupPlayers.containsKey(placerGroup) || plugin.groupPlayers.get(placerGroup) < 2){
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("no-members-online")));
				e.setCancelled(true);
				return;
			}
			
			Material m = b.getType();
			if (m == Material.CHEST || m == Material.TRAPPED_CHEST){
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("chest-move-error")));
				e.setCancelled(true);
				return;
			}
			
			if (plugin.blockPrices.containsKey(m.name())){
				int cost = plugin.blockPrices.get(m.name());
				if (cost == 0) return;
				if (plugin.dbman.takeAmount(p.getName(), cost)){
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("piston-move").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
					plugin.boardman.updateScoreboard(p);
				} else {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("piston-move-error").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
					e.setCancelled(true);
				}
			} else {
				int cost = plugin.defaultPrice;
				if (plugin.dbman.takeAmount(p.getName(), cost)){
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("piston-move").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
					plugin.boardman.updateScoreboard(p);
				} else {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("piston-move-error").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onExtend(BlockPistonExtendEvent e){
		int Mcost = 0;
		
		Player p = Utils.closestPlayer(e.getBlock().getLocation());
		if (p == null) return;
		
		if (e.getLength() == 0) return;
		
		for (Block b : e.getBlocks()){
			if (b == null) continue;
			
			if (plugin.override.contains(p.getName())) return;
			
			String placer = plugin.cpman.getPlacer(b);
			if (placer == null || placer.equals("")) return;
			int placerGroup = plugin.pman.getGroup(placer);
			
			int breakerGroup = plugin.pman.getGroup(p.getName());
			if (placerGroup != breakerGroup){
				if (!plugin.groupPlayers.containsKey(placerGroup) || plugin.groupPlayers.get(placerGroup) < 2){
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("no-members-online")));
					e.setCancelled(true);
					return;
				}
				
				Material m = e.getBlock().getType();
				if (m == Material.CHEST || m == Material.TRAPPED_CHEST){
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("chest-move-error")));
					e.setCancelled(true);
					return;
				}
				
				if (plugin.blockPrices.containsKey(m.name())){
					int cost = plugin.blockPrices.get(m.name());
					if (cost == 0) continue;
					Mcost = Mcost + cost;
				} else {
					Mcost = Mcost + plugin.defaultPrice;
				}
			}
		}
		
		if (plugin.dbman.takeAmount(p.getName(), Mcost)){
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("piston-move-group").replaceAll("<cost>", Mcost + "").replaceAll("<amount>", e.getLength() + "")));
			plugin.boardman.updateScoreboard(p);
		} else {
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("piston-move-group-error").replaceAll("<cost>", Mcost + "").replaceAll("<amount>", e.getLength() + "")));
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e){		
		Player p = (Player)e.getWhoClicked();
		if (plugin.override.contains(p.getName())) return;
		
        Inventory top = e.getView().getTopInventory();
        Inventory bottom = e.getView().getBottomInventory();
       
        if(top.getType() != InventoryType.CHEST || bottom.getType() != InventoryType.PLAYER) return;
        
        if (e.getInventory().getHolder() instanceof Chest){
        	Chest c = (Chest) e.getInventory().getHolder();
        	
            Block b = c.getBlock();
    		
    		String placer = plugin.cpman.getPlacer(b);
    		if (placer == null || placer.equals("")) return;
    		int placerGroup = plugin.pman.getGroup(placer);
    		
    		int breakerGroup = plugin.pman.getGroup(p.getName());
    		if (placerGroup != breakerGroup){
    	        if (e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT) {
    	        	e.setCancelled(true);
    	        	return;
    	        }
    	        
    	        if(e.getRawSlot() > 26) return;
    	        
    			e.setCancelled(true);
    			
    	        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
    	        
    			if (!plugin.groupPlayers.containsKey(placerGroup) || plugin.groupPlayers.get(placerGroup) < 2){
    				p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("no-members-online")));
    				return;
    		    }
    			
    			Material m = e.getCurrentItem().getType();
    			
    			if (plugin.chestPrices.containsKey(m.name())){
    				int cost = plugin.chestPrices.get(m.name());
    				if (cost == 0){
    					ItemStack i = e.getCurrentItem().clone();
    					i.setAmount(1);
    					p.getInventory().addItem(i);
    					if (e.getCurrentItem().getAmount() > 1) e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() - 1);
    					else top.remove(i);
    					return;
    				}
    				if (plugin.dbman.takeAmount(p.getName(), cost)){
    					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("chest-click").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
    					plugin.boardman.updateScoreboard(p);
    					ItemStack i = e.getCurrentItem().clone();
    					i.setAmount(1);
    					p.getInventory().addItem(i);
    					if (e.getCurrentItem().getAmount() > 1) e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() - 1);
    					else top.remove(i);
    				} else {
    					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("chest-click-error").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
    				}
    			} else {
    				int cost = plugin.defaultChestPrice;
    				if (plugin.dbman.takeAmount(p.getName(), cost)){
    					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("chest-click").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
    					plugin.boardman.updateScoreboard(p);
    					ItemStack i = e.getCurrentItem().clone();
    					i.setAmount(1);
    					p.getInventory().addItem(i);
    					if (e.getCurrentItem().getAmount() > 1) e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() - 1);
    					else top.remove(i);
    				} else {
    					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("chest-click-error").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
    				}
    			}
    		}
        } else if (e.getInventory().getHolder() instanceof DoubleChest){
        	DoubleChest dc = (DoubleChest)e.getInventory().getHolder();
            Block b = dc.getLocation().getBlock();
    		
    		String placer = plugin.cpman.getPlacer(b);
    		if (placer == null || placer.equals("")) return;
    		int placerGroup = plugin.pman.getGroup(placer);
    		
    		int breakerGroup = plugin.pman.getGroup(p.getName());
    		if (placerGroup != breakerGroup){
    	        if (e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT) {
    	        	e.setCancelled(true);
    	        	return;
    	        }
    	        
    	        if(e.getRawSlot() > 53) return;
    	        
    			e.setCancelled(true);
    			
    	        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
    	        
    			if (!plugin.groupPlayers.containsKey(placerGroup) || plugin.groupPlayers.get(placerGroup) < 2){
    				p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("no-members-online")));
    				return;
    		    }
    			
    			Material m = e.getCurrentItem().getType();
    			
    			if (plugin.chestPrices.containsKey(m.name())){
    				int cost = plugin.chestPrices.get(m.name());
    				if (cost == 0){
    					ItemStack i = e.getCurrentItem().clone();
    					i.setAmount(1);
    					p.getInventory().addItem(i);
    					if (e.getCurrentItem().getAmount() > 1) e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() - 1);
    					else top.remove(i);
    					return;
    				}
    				if (plugin.dbman.takeAmount(p.getName(), cost)){
    					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("chest-click").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
    					plugin.boardman.updateScoreboard(p);
    					ItemStack i = e.getCurrentItem().clone();
    					i.setAmount(1);
    					p.getInventory().addItem(i);
    					if (e.getCurrentItem().getAmount() > 1) e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() - 1);
    					else top.remove(i);
    				} else {
    					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("chest-click-error").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
    				}
    			} else {
    				int cost = plugin.defaultChestPrice;
    				if (plugin.dbman.takeAmount(p.getName(), cost)){
    					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("chest-click").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
    					plugin.boardman.updateScoreboard(p);
    					ItemStack i = e.getCurrentItem().clone();
    					i.setAmount(1);
    					p.getInventory().addItem(i);
    					if (e.getCurrentItem().getAmount() > 1) e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() - 1);
    					else top.remove(i);
    				} else {
    					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("chest-click-error").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
    				}
    			}
    		}
        }
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onItemMove(InventoryMoveItemEvent e){
		if (!(e.getDestination().getHolder() instanceof Hopper && e.getSource().getHolder() instanceof Chest)) return;
		
		Hopper hp = (Hopper)e.getDestination().getHolder();
		Block hpB = hp.getBlock();
		Chest c = (Chest)e.getSource().getHolder();
		Block cB = c.getBlock();
		
		String username = plugin.cpman.getPlacer(hpB);
		if (username == null || username.equals("")) return;
		OfflinePlayer op = Bukkit.getOfflinePlayer(username);
		if (!op.isOnline()){
			e.setCancelled(true);
			return;
		}
		Player p = op.getPlayer();
		if (plugin.override.contains(username)) return;
		
		String placer = plugin.cpman.getPlacer(cB);
		if (placer == null || placer.equals("")) return;
		int placerGroup = plugin.pman.getGroup(placer);
		
		int breakerGroup = plugin.pman.getGroup(username);
		if (placerGroup != breakerGroup){	        
			if (!plugin.groupPlayers.containsKey(placerGroup) || plugin.groupPlayers.get(placerGroup) < 2){
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("no-members-online")));
				e.setCancelled(true);
				return;
		    }
			
			Material m = e.getItem().getType();
			
			if (plugin.chestPrices.containsKey(m.name())){
				int cost = plugin.chestPrices.get(m.name());
				if (cost == 0){
					e.getItem().setAmount(1);
					return;
				}
				if (plugin.dbman.takeAmount(p.getName(), cost)){
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("chest-click").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
					plugin.boardman.updateScoreboard(p);
					e.getItem().setAmount(1);
				} else {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("chest-click-error").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
					e.setCancelled(true);
				}
			} else {
				int cost = plugin.defaultChestPrice;
				if (plugin.dbman.takeAmount(p.getName(), cost)){
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("chest-click").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
					plugin.boardman.updateScoreboard(p);
					e.getItem().setAmount(1);
				} else {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("chest-click-error").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onBurn(BlockBurnEvent e){
		String placer = plugin.cpman.getPlacer(e.getBlock());
		if (placer == null || placer.equals("")) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		if (e.getAction() != Action.PHYSICAL) return;
		
		if (plugin.override.contains(e.getPlayer().getName())) return;
		
		String placer = plugin.cpman.getPlacer(e.getClickedBlock());
		if (placer == null || placer.equals("")) return;
		int placerGroup = plugin.pman.getGroup(placer);
		
		int breakerGroup = plugin.pman.getGroup(e.getPlayer().getName());
		if (placerGroup != breakerGroup){
			if (!plugin.groupPlayers.containsKey(placerGroup) || plugin.groupPlayers.get(placerGroup) < 2){
				e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("no-members-online")));
				e.setCancelled(true);
				return;
			}
			
			Material m = e.getClickedBlock().getType();
			if (m == Material.CHEST || m == Material.TRAPPED_CHEST){
				e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("chest-break-error")));
				e.setCancelled(true);
				return;
			}
			
			if (plugin.blockPrices.containsKey(m.name())){
				int cost = plugin.blockPrices.get(m.name());
				if (cost == 0) return;
				if (plugin.dbman.takeAmount(e.getPlayer().getName(), cost)){
					e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("break-block").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
					plugin.boardman.updateScoreboard(e.getPlayer());
				} else {
					e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("break-block-error").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
					e.setCancelled(true);
				}
			} else {
				int cost = plugin.defaultPrice;
				if (plugin.dbman.takeAmount(e.getPlayer().getName(), cost)){
					e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("break-block").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
					plugin.boardman.updateScoreboard(e.getPlayer());
				} else {
					e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.stringConfig.getCustomConfig().getString("break-block-error").replaceAll("<cost>", cost + "").replaceAll("<material>", m.name())));
					e.setCancelled(true);
				}
			}
		}
	}
}
