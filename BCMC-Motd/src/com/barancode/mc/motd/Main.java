package com.barancode.mc.motd;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.barancode.mc.db.TokenDatabase;

public class Main extends JavaPlugin implements Listener{
	String motd;
	TokenDatabase tokendb;
	public void onEnable(){
		tokendb = new TokenDatabase();
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		motd = ChatColor.translateAlternateColorCodes('&', getConfig().getString("motd"));
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		e.getPlayer().sendMessage(motd.replaceAll("\\{tokens\\}", tokendb.getAmount(e.getPlayer().getUniqueId()) + ""));
	}
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		reloadConfig();
		motd = ChatColor.translateAlternateColorCodes('&', getConfig().getString("motd"));
		return true;
	}
}
