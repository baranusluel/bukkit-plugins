package com.barancode.mc.bungeemotd;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ReloadCommand extends Command{
	Main plugin;
    public ReloadCommand(Main plugin) {
        super("motdreload", "bcmc-bungee.reload");
        this.plugin = plugin;
    }
    public void execute(CommandSender sender, String[] args) {
        if ((sender instanceof ProxiedPlayer)) {
          ProxiedPlayer p = (ProxiedPlayer)sender;
          if (p.hasPermission("bcmc-bungee.reload")){
    		  plugin.reloadConfig();
        	  p.sendMessage("Reloaded MOTD");
          }
        }
    }
}
