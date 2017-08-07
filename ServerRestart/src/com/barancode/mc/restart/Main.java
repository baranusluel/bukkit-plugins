package com.barancode.mc.restart;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	boolean shutdown = false;
	Log log = new Log();
	
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		try {
			log.initialize(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void onDisable(){
		try {
			log.write("onDisable is being triggered!");
			Bukkit.getLogger().info("onDisable is being triggered!");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (!shutdown){
			try {
				log.write("Will create new server process!");
				Bukkit.getLogger().info("Will create new server process!");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				ProcessBuilder builder = new ProcessBuilder("./start.sh");
				builder.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
	{
		if (sender instanceof ConsoleCommandSender || (sender instanceof Player && ((Player)sender).getUniqueId().toString().equalsIgnoreCase("395fe01a-7cf1-4003-89b7-1cf5de2460ca"))){
			if (!shutdown){
				shutdown = true;
				sender.sendMessage("The server will not restart, but will shutdown instead");
				try {
					log.write("Shutdown toggled to true (won't restart)");
					Bukkit.getLogger().info("Shutdown toggled to true (won't restart)");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else {
				shutdown = false;
				sender.sendMessage("The server will restart");
				try {
					log.write("Shutdown toggled to false (will restart)");
					Bukkit.getLogger().info("Shutdown toggled to false (will restart)");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
		return true;
	}
	
	@EventHandler
	public void playerCommand(PlayerCommandPreprocessEvent e){
		if (e.getPlayer().getUniqueId().toString().equalsIgnoreCase("395fe01a-7cf1-4003-89b7-1cf5de2460ca")){
			if (e.getMessage().replaceAll("/", "").equalsIgnoreCase("reload")){
				shutdown = true;
				try {
					log.write("BaranCODE did /reload");
					Bukkit.getLogger().info("BaranCODE did /reload");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	@EventHandler
	public void consoleCommand(ServerCommandEvent e){
		if (e.getCommand().replaceAll("/", "").equalsIgnoreCase("reload")){
			shutdown = true;
			try {
				log.write("Console did /reload");
				Bukkit.getLogger().info("Console did /reload");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}
