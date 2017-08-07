package com.barancode.mc.experia;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;

public class ExperiaListener implements Listener{
	Main plugin;
	Random random = new Random();
	
	public ExperiaListener(Main plugin){
		this.plugin = plugin;
	}
	
	
	// The following is added from BCMC-NoBuild, modified for Experia:
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onIgnite(BlockIgniteEvent e){
		if (e.getCause() != IgniteCause.FLINT_AND_STEEL) e.setCancelled(true);
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBurn(BlockBurnEvent e){
		e.setCancelled(true);
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent e){
		if (!e.getBlock().getWorld().getName().equalsIgnoreCase("Experia-game")){
			e.setCancelled(true);
			return;
		}
		/*if (e.getBlock().getType() == Material.CHEST){
			e.setCancelled(true);
			return;
		}*/
		Location loc = e.getBlock().getLocation();
		double y = loc.getY();
		for (Location l : plugin.spawns.values()){
			l.setY(y);
			if (loc.distance(l) < 30){
				e.setCancelled(true);
				e.getPlayer().sendMessage(plugin.utils.replace(plugin.getConfig().getString("messages.build-spawn")));
				return;
			}
		}
	}
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlace(BlockPlaceEvent e){
		if (!e.getBlock().getWorld().getName().equalsIgnoreCase("Experia-game")){
			e.setCancelled(true);
			return;
		}
		/*if (e.getBlock().getType() == Material.CHEST){
			e.setCancelled(true);
			return;
		}*/
		Location loc = e.getBlock().getLocation();
		double y = loc.getY();
		for (Location l : plugin.spawns.values()){
			l.setY(y);
			if (loc.distance(l) < 30){
				e.setCancelled(true);
				e.getPlayer().sendMessage(plugin.utils.replace(plugin.getConfig().getString("messages.build-spawn")));
				return;
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onFoodChange(FoodLevelChangeEvent e){
		if (e.getEntity().getWorld().getName().equalsIgnoreCase("Experia")){
			if (e.getFoodLevel() < ((Player)e.getEntity()).getFoodLevel()){
				e.setFoodLevel(20);
			}
		}
	}
	// End of code from BCMC-NoBuild
	
	
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		if (!e.getTo().getWorld().getName().equalsIgnoreCase("Experia-game")) return;
		if (e.getFrom().getChunk() == e.getTo().getChunk()) return;
		int i = random.nextInt(50);
		if (i == 0){
			Chunk c = e.getTo().getChunk();
			int x = random.nextInt(16);
			int z = random.nextInt(16);
			Location l = c.getBlock(x, 0, z).getLocation();
			l.setY(c.getWorld().getHighestBlockYAt(l));
			l.getBlock().setTypeIdAndData(33, (byte)6, false);
			Arrow.indicate(l);
		}
	}
	
	
	@EventHandler
	public void command(PlayerCommandPreprocessEvent e) {
		if (e.getPlayer().getWorld().getName().equalsIgnoreCase("Experia-game")){
			List<String> commands = plugin.getConfig().getStringList("command-blacklist");
			String command = e.getMessage();
			command = command.replaceFirst("/", "");
			String[] commandparts = command.split(" ");
			command = "";
			for (int i = 0; i < commandparts.length; i++){
				  command += " " + commandparts[i];
				  command = command.trim();
				  if (commands.contains(command.toLowerCase())){
						e.getPlayer().sendMessage(plugin.utils.replace(plugin.getConfig().getString("cannot-use")));
						e.setCancelled(true);
						return;
				  }
			}
		} else if (e.getPlayer().getWorld().getName().equalsIgnoreCase("Experia")){
			List<String> commands = plugin.getConfig().getStringList("lobby-command-blacklist");
			String command = e.getMessage();
			command = command.replaceFirst("/", "");
			String[] commandparts = command.split(" ");
			command = "";
			for (int i = 0; i < commandparts.length; i++){
				  command += " " + commandparts[i];
				  command = command.trim();
				  if (commands.contains(command.toLowerCase())){
						e.getPlayer().sendMessage(plugin.utils.replace(plugin.getConfig().getString("lobby-cannot-use")));
						e.setCancelled(true);
						return;
				  }
			}
		}
	}
	
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onInventoryOpenEvent(InventoryOpenEvent e){
    	if (!e.getPlayer().getWorld().getName().equalsIgnoreCase("Experia-game")) return;
    	
        /*if (e.getInventory().getHolder() instanceof Chest){
        	e.setCancelled(true);
        	plugin.itemmanager.dropChestLoot((Chest) e.getInventory().getHolder());
        } else if (e.getInventory().getHolder() instanceof DoubleChest){
	    	e.setCancelled(true);
	    	plugin.itemmanager.dropDoubleChestLoot((DoubleChest) e.getInventory().getHolder());
        } else */if (e.getView().getType() != InventoryType.PLAYER
        		&& e.getView().getType() != InventoryType.FURNACE
        		&& e.getView().getType() != InventoryType.CRAFTING
        		&& e.getView().getType() != InventoryType.WORKBENCH
        		&& e.getView().getType() != InventoryType.ANVIL
        		&& e.getView().getType() != InventoryType.ENCHANTING
        		&& e.getView().getType() != InventoryType.CHEST){
        	e.setCancelled(true);
        }
    }
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void respawn(final PlayerRespawnEvent event){
		double x = plugin.getConfig().getDouble("worldspawn.x");
		double y = plugin.getConfig().getDouble("worldspawn.y");
		double z = plugin.getConfig().getDouble("worldspawn.z");
		float yaw = (float)plugin.getConfig().getDouble("worldspawn.yaw");
		float pitch = (float)plugin.getConfig().getDouble("worldspawn.pitch");
		World world = Bukkit.getWorld(plugin.getConfig().getString("worldspawn.world"));
		
		Location loc = new Location(world, x, y, z, yaw, pitch);
		
		event.setRespawnLocation(loc);
		
		final String name = event.getPlayer().getName();
		
		plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
		    @Override
		    public void run() {
				PlayerInventory inventory = event.getPlayer().getInventory();
				inventory.clear();
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mi get kit " + name);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mi get spawn " + name);
				event.getPlayer().getInventory().setItem(plugin.getConfig().getInt("book.slot"), plugin.book.createDefaultBook("book"));
		    }
		}, 10L);
	}

	
	@EventHandler(priority = EventPriority.HIGH)
	public void join(final PlayerJoinEvent event){
		if (event.getPlayer().getWorld().getName().equalsIgnoreCase("Experia")){
			plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			    @Override
			    public void run() {
					double x = plugin.getConfig().getDouble("worldspawn.x");
					double y = plugin.getConfig().getDouble("worldspawn.y");
					double z = plugin.getConfig().getDouble("worldspawn.z");
					float yaw = (float)plugin.getConfig().getDouble("worldspawn.yaw");
					float pitch = (float)plugin.getConfig().getDouble("worldspawn.pitch");
					World world = Bukkit.getWorld(plugin.getConfig().getString("worldspawn.world"));
					Location loc = new Location(world, x, y, z, yaw, pitch);
					event.getPlayer().teleport(loc);
					
					PlayerInventory inventory = event.getPlayer().getInventory();
					inventory.clear();
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mi get kit " + event.getPlayer().getName());
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mi get spawn " + event.getPlayer().getName());
					event.getPlayer().getInventory().setItem(plugin.getConfig().getInt("book.slot"), plugin.book.createDefaultBook("book"));
			    }
			}, 10L);
		}
		
		if (plugin.teamMessages.containsKey(event.getPlayer().getUniqueId())){
			List<String> messages = plugin.teamMessages.get(event.getPlayer().getUniqueId());
			for (String s : messages){
				event.getPlayer().sendMessage(s);
			}
			plugin.teamMessages.remove(event.getPlayer().getUniqueId());
		}
		
		plugin.updateScoreboard(event.getPlayer());
		
		if (plugin.deleteInventories.contains(event.getPlayer().getUniqueId())){
			event.getPlayer().getInventory().clear();
			event.getPlayer().getInventory().setArmorContents(null);
			plugin.deleteInventories.remove(event.getPlayer().getUniqueId());
		}
		if (plugin.NPCs.containsKey(event.getPlayer().getName())){
			plugin.NPCs.get(event.getPlayer().getName()).remove();
			plugin.NPCs.remove(event.getPlayer().getName());
		}
	}
	
	@EventHandler
	public void blockSpread(BlockSpreadEvent e){
		if (e.getBlock().getType() == Material.FIRE) e.setCancelled(true);
	}
	
	/* We decided to disable single-slot items
	 * 
	 * @EventHandler
	public void playerPickupItem(PlayerPickupItemEvent e){
		ItemStack item = e.getItem().getItemStack();
		if (item.getType() == Material.ROTTEN_FLESH) return;
		PlayerInventory inv = e.getPlayer().getInventory();
		for (int i = 1; i <= item.getAmount(); i++){
			int count = 0;
			for (ItemStack is : inv.getContents()){
				if (is != null && is.isSimilar(item)){
					count++;
					continue;
				}
				else if (is == null){
					ItemStack tempitem = item.clone(); tempitem.setAmount(1);
					inv.setItem(count, tempitem);
					break;
				} else if (is.getType() == Material.AIR){
					ItemStack tempitem = item.clone(); tempitem.setAmount(1);
					inv.setItem(count, tempitem);
					break;
				}
				count++;
			}
		}
		e.getItem().remove();
		e.setCancelled(true);
	}*/
	
	@EventHandler
	public void inventoryClick(InventoryClickEvent e){
		if ((e.getCurrentItem() != null && plugin.MyItemsItems.containsKey(e.getCurrentItem().getItemMeta())) || (e.getCursor() != null && plugin.MyItemsItems.containsKey(e.getCursor().getItemMeta()))) e.setCancelled(true);
		ItemStack book = plugin.book.createDefaultBook("book");
		if ((e.getCurrentItem() != null && e.getCurrentItem().isSimilar(book)) || (e.getCursor() != null && e.getCursor().isSimilar(book))) e.setCancelled(true);
		
		/* We decided to disable single-slot items
		 * 
		 * if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR || e.getCursor() == null || e.getCursor().getType() == Material.AIR) return;
		if (e.getCursor().getType() == Material.ROTTEN_FLESH) return;
		if (e.getCursor().isSimilar(e.getCurrentItem())){
			e.setResult(Result.DENY);
			ItemStack item = e.getCursor();
			Inventory inv = e.getInventory();
			for (int i = 1; i <= item.getAmount() - 1; i++){
				int count = 0;
				for (ItemStack is : inv.getContents()){
					if (is != null && is.isSimilar(item)){
						count++;
						continue;
					}
					else if (is == null){
						ItemStack tempitem = item.clone(); tempitem.setAmount(1);
						inv.setItem(count, tempitem);
						break;
					} else if (is.getType() == Material.AIR){
						ItemStack tempitem = item.clone(); tempitem.setAmount(1);
						inv.setItem(count, tempitem);
						break;
					}
					count++;
				}
			}
		}*/
	}
	
	@EventHandler
	public void playerDeath(PlayerDeathEvent e){
	    for (Iterator<ItemStack> it = e.getDrops().iterator(); it.hasNext(); ){
	    	ItemStack next = it.next();
			ItemStack is = new ItemStack(next.getType(), 1);
			is.setItemMeta(next.getItemMeta());
    		if (plugin.MyItemsItems.containsKey(is.getItemMeta())) it.remove();
    		// plugin.kitItems.contains(is) || 
	    }
	    
		int deaths = 0;
		if (plugin.deaths.containsKey(e.getEntity().getUniqueId())){
			deaths = plugin.deaths.get(e.getEntity().getUniqueId()) + 1;
			plugin.deaths.put(e.getEntity().getUniqueId(), deaths);
			plugin.customconfig.getCustomConfig().set("players." + e.getEntity().getUniqueId() + ".deaths", deaths);
			plugin.customconfig.saveCustomConfig();
		} else {
			deaths = plugin.customconfig.getCustomConfig().getInt("players." + e.getEntity().getUniqueId() + ".deaths") + 1;
			plugin.deaths.put(e.getEntity().getUniqueId(), deaths);
			plugin.customconfig.getCustomConfig().set("players." + e.getEntity().getUniqueId() + ".deaths", deaths);
			plugin.customconfig.saveCustomConfig();
		}
		plugin.updateScoreboard(e.getEntity());
		
		if (e.getEntity().getKiller() != null && e.getEntity().getKiller().getType() == EntityType.PLAYER){
			Player player = (Player)e.getEntity().getKiller();
			int playerkills = 0;
			if (plugin.playerKills.containsKey(player.getUniqueId())){
				playerkills = plugin.playerKills.get(player.getUniqueId()) + 1;
				plugin.playerKills.put(player.getUniqueId(), playerkills);
				plugin.customconfig.getCustomConfig().set("players." + player.getUniqueId() + ".playerkills", playerkills);
				plugin.customconfig.saveCustomConfig();
			} else {
				playerkills = plugin.customconfig.getCustomConfig().getInt("players." + player.getUniqueId() + ".playerkills") + 1;
				plugin.playerKills.put(player.getUniqueId(), playerkills);
				plugin.customconfig.getCustomConfig().set("players." + player.getUniqueId() + ".playerkills", playerkills);
				plugin.customconfig.saveCustomConfig();
			}
			plugin.updateScoreboard(player);
		}
		
		if (plugin.PVPPlayers.contains(e.getEntity().getName())) plugin.PVPPlayers.remove(e.getEntity().getName());
	}
	
	@EventHandler
	public void playerDropItem(PlayerDropItemEvent e){
		ItemStack is = new ItemStack(e.getItemDrop().getItemStack().getType(), 1);
		is.setItemMeta(e.getItemDrop().getItemStack().getItemMeta());
		//if (plugin.kitItems.contains(is)) e.setCancelled(true);
		if (plugin.MyItemsItems.containsKey(is.getItemMeta())) {
			e.getItemDrop().remove();
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mi get " + plugin.MyItemsItems.get(is.getItemMeta()) + " " + e.getPlayer().getName());
		}
		ItemStack book = plugin.book.createDefaultBook("book");
		if (e.getItemDrop().getItemStack().isSimilar(book)){
			e.getItemDrop().remove();
			e.getPlayer().getInventory().setItem(plugin.getConfig().getInt("book.slot"), book);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void entityDeath(final EntityDeathEvent e){
		if (e.getEntityType() != EntityType.ZOMBIE) return;
		if (e.getEntity().getKiller() == null || e.getEntity().getKiller().getType() != EntityType.PLAYER) return;
		
		Player player = (Player)e.getEntity().getKiller();
		
		int zombiekills = 0;
		if (plugin.zombieKills.containsKey(player.getUniqueId())){
			zombiekills = plugin.zombieKills.get(player.getUniqueId()) + 1;
			plugin.zombieKills.put(player.getUniqueId(), zombiekills);
			plugin.customconfig.getCustomConfig().set("players." + player.getUniqueId() + ".zombiekills", zombiekills);
			plugin.customconfig.saveCustomConfig();
		} else {
			zombiekills = plugin.customconfig.getCustomConfig().getInt("players." + player.getUniqueId() + ".zombiekills") + 1;
			plugin.zombieKills.put(player.getUniqueId(), zombiekills);
			plugin.customconfig.getCustomConfig().set("players." + player.getUniqueId() + ".zombiekills", zombiekills);
			plugin.customconfig.saveCustomConfig();
		}
		plugin.updateScoreboard(player);
		
	    for (Iterator<ItemStack> it = e.getDrops().iterator(); it.hasNext(); ){
	    	ItemStack next = it.next();
			ItemStack is = new ItemStack(next.getType(), 1);
			is.setItemMeta(next.getItemMeta());
    		if (plugin.kitItems.contains(is) || plugin.MyItemsItems.containsKey(is.getItemMeta())) it.remove();
	    }
		
		if (e.getEntity().isCustomNameVisible()){
			ItemStack[] items = plugin.NPCInventories.get(e.getEntity().getCustomName());
			for (ItemStack item : items){
				if (item == null || item.getType() == Material.AIR) continue;
				ItemStack is = new ItemStack(item.getType(), 1);
				is.setItemMeta(item.getItemMeta());
	    		if (!plugin.kitItems.contains(is) && !plugin.MyItemsItems.containsKey(is.getItemMeta())) e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), item);
			}
			e.getDrops().clear();
			plugin.NPCInventories.remove(e.getEntity().getCustomName());
			plugin.NPCs.remove(e.getEntity().getCustomName());
			plugin.deleteInventories.add(plugin.utils.getUUID(e.getEntity().getCustomName()));
		}
	}
	
	@EventHandler
	public void playerLeave(final PlayerQuitEvent e){
		if (plugin.kicking.contains(e.getPlayer().getName())){
			plugin.kicking.remove(e.getPlayer().getName());
			return;
		}
		if (!plugin.PVPPlayers.contains(e.getPlayer().getName())){
			return;
		}
		
		LivingEntity entity = (LivingEntity)e.getPlayer().getWorld().spawnEntity(e.getPlayer().getLocation(), EntityType.ZOMBIE);
		EntityEquipment ee = entity.getEquipment();
		ee.setArmorContents(e.getPlayer().getInventory().getArmorContents());
		entity.setCustomName(e.getPlayer().getName());
		entity.setCustomNameVisible(true);
		plugin.NPCInventories.put(e.getPlayer().getName(), (ItemStack[])ArrayUtils.addAll(e.getPlayer().getInventory().getContents(), e.getPlayer().getInventory().getArmorContents()));
		plugin.NPCs.put(e.getPlayer().getName(), entity);
		
        plugin.scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run(){
        		LivingEntity entity = plugin.NPCs.get(e.getPlayer().getName());
        		if (entity != null) entity.remove();
        		if (plugin.NPCs.containsKey(e.getPlayer().getName())) plugin.NPCs.remove(e.getPlayer().getName());
        		if (plugin.NPCInventories.containsKey(e.getPlayer().getName())) plugin.NPCInventories.remove(e.getPlayer().getUniqueId());
            }
        }, 60 * 60 * 20L);
	}
}
