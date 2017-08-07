package com.gmail.favorlock.bungeeannouncer.cmd;

import com.gmail.favorlock.bungeeannouncer.BungeeAnnouncer;
import com.gmail.favorlock.bungeeannouncer.utils.FontFormat;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class AnnounceList extends Command {
	
	BungeeAnnouncer plugin;

	public AnnounceList(BungeeAnnouncer plugin) {
		super("announce_list");
		this.plugin = plugin;
	}

	
	public void execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission("bungeeannouncer.admin")) {
			sender.sendMessage(FontFormat.translateString("&4You do not have permission to use this command"));
			return;
		}
		if (args.length != 0) {
			sender.sendMessage(FontFormat.translateString("&7Usage: /announce_list"));
			return;
		}
		sender.sendMessage(FontFormat.translateString("&a-----------------------------------------------------" +
										"&eID - &fMessage"));
		Integer id = 0;
		
		for (String announcement : plugin.getConfigStorage().announcements_global) {
			sender.sendMessage(FontFormat.translateString("&a" + id + " - &f" + announcement));
			id++;
		}
		
		sender.sendMessage(FontFormat.translateString("&a-----------------------------------------------------"));
	}		
}