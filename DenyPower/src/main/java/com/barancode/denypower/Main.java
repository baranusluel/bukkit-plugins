package com.barancode.denypower;

import java.util.List;
import java.util.UUID;

import net.choicecraft.ChoiceWorks.utils.CString;
import net.choicecraft.ChoiceWorks.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.massivecraft.factions.event.EventFactionsPowerChange;
import com.massivecraft.factions.event.EventFactionsPowerChange.PowerChangeReason;
import com.massivecraft.massivecore.store.Entity;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;

public class Main extends JavaPlugin implements Listener{
	WorldGuardPlugin wg = null;
	List<String> blocked = null;
	
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
		wg = (WorldGuardPlugin)plugin;
		saveDefaultConfig();
		reloadBlocked();
	}
	
	@EventHandler
	public void onPower(EventFactionsPowerChange e){
		if (e.getReason() != PowerChangeReason.TIME) return;
		String id = ((Entity<?>)e.getUPlayer()).getId();
		if (id == null) return;
		UUID uuid = CString.formatUUID(id);
		if (!Utils.isOnline(uuid)) return;
		Player p = Bukkit.getPlayer(uuid);
		if (p == null) return;
		
		RegionManager regionManager = wg.getRegionManager(p.getWorld());
		List<String> ids = regionManager.getApplicableRegionsIDs(BukkitUtil.toVector(p.getLocation()));
		if (ids == null || ids.size() == 0) return;
		for (String region : ids){
			if (blocked.contains(region)){
				e.setCancelled(true);
				break;
			}
		}
	}
	
	public void reloadBlocked(){
		blocked = getConfig().getStringList("blocked");
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
	{
		reloadConfig();
		reloadBlocked();
		sender.sendMessage("The DenyPower config has been reloaded");
		return true;
	}
}
