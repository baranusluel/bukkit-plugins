package com.gmail.favorlock.bungeeannouncer.cmd;

import com.gmail.favorlock.bungeeannouncer.BungeeAnnouncer;
import com.gmail.favorlock.bungeeannouncer.utils.FontFormat;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class AnnounceAdd extends Command {
	
	BungeeAnnouncer plugin;

	public AnnounceAdd(BungeeAnnouncer plugin) {
		super("announce_add");
		this.plugin = plugin;
	}

	
	public void execute(CommandSender sender, String[] args) {
		String announcement = "";
		
		if (!sender.hasPermission("bungeeannouncer.admin")) {
			sender.sendMessage(FontFormat.translateString("&4You do not have permission to use this command"));
			return;
		}
		if (args.length < 1) {
			sender.sendMessage(FontFormat.translateString("&7Usage: /announce_add <message>"));
			return;
		}
		for (String data : args) {
			announcement = announcement + data + " ";
		}
		
		announcement = announcement.substring(0, announcement.length() - 1);
		
		plugin.getConfigStorage().addAnnouncement(announcement);
		sender.sendMessage(FontFormat.translateString("&aThe announcement has been added!"));
		
	}

}
