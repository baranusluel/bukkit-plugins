package com.gmail.favorlock.bungeeannouncer;

import java.io.File;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.logging.Level;

import com.gmail.favorlock.bungeeannouncer.utils.FontFormat;

import net.craftminecraft.bungee.bungeeyaml.bukkitapi.InvalidConfigurationException;
import net.craftminecraft.bungee.bungeeyaml.supereasyconfig.Config;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;

public class MainConfig extends Config {
	
	public MainConfig(BungeeAnnouncer plugin) {
		CONFIG_FILE = new File("plugins" + File.separator + plugin.getDescription().getName(), "config.yml");
		CONFIG_HEADER = "BungeeAnnouncer - Global Server Announcments";
	}
	
	public int settings_interval = 60;
	public String settings_prefix = "Announcer: ";
    public boolean settings_bouncybungeeon = false;
	public ArrayList<String> announcements_global = new ArrayList<String>(){{
		add("&6Hello there&f, welcome to the server!");
		add("&4Enjoy your stay!");
		add("&4Vote for &amoney!");
	}};
	
	public void addAnnouncement(String announcement) {
		announcements_global.add(announcement);
		try {
			this.save();
		} catch (InvalidConfigurationException e) {
			ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Failed to save the config!", e);
		}
		
	}
	
	public void removeAnnouncement(CommandSender sender, Integer id) {
		if (id > this.announcements_global.size() - 1 || id < 0) {
			sender.sendMessage(FontFormat.translateString("&7No announcement exist with id: &a" + id));
			return;
		}
		ListIterator<String> listIterator = this.announcements_global.listIterator();
		Integer counter = 0;
		
		listIterator.next();
		
		if (this.announcements_global.size() > 1) {
			for (String announcement : this.announcements_global) {
				if (counter == id) {
					listIterator.remove();
					try {
						this.save();
						sender.sendMessage(FontFormat.translateString("&aThe announcement has been deleted"));
					} catch (InvalidConfigurationException e) {
						ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Failed to save the config!", e);
					}
					return;
				} else {
					listIterator.next();
					counter++;
				}
			}
		} else {
			this.announcements_global = new ArrayList<String>();
			try {
				this.save();
				sender.sendMessage(FontFormat.translateString("&aThe announcement has been deleted"));
			} catch (InvalidConfigurationException e) {
				ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Failed to save the config!", e);
			}
		}
	}

}
