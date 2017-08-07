package com.gmail.favorlock.bungeeannouncer.cmd;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import com.gmail.favorlock.bungeeannouncer.BungeeAnnouncer;
import com.gmail.favorlock.bungeeannouncer.utils.FontFormat;

public class AnnounceRemove extends Command {
	
	BungeeAnnouncer plugin;

	public AnnounceRemove(BungeeAnnouncer plugin) {
		super("announce_remove");
		this.plugin = plugin;
	}

	
	public void execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission("bungeeannouncer.admin")) {
			sender.sendMessage(FontFormat.translateString("&4You do not have permission to use this command"));
			return;
		}
		if (args.length != 1) {
			sender.sendMessage(FontFormat.translateString("&7Usage: /announce_remove <message>"));
			return;
		}
		
		try {
			int id = Integer.parseInt(args[0]);
			plugin.getConfigStorage().removeAnnouncement(sender, id);
		} catch (NumberFormatException e) {
			sender.sendMessage(FontFormat.translateString("&4You can only use numeric values."));
			return;
		}
	}

}