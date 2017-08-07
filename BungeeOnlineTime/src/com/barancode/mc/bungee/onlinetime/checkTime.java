package com.barancode.mc.bungee.onlinetime;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

class checkTime extends Command{

	public checkTime() {
        super("command", "permission");
    }

    public void execute(CommandSender sender, String[] args) {
        if ((sender instanceof ProxiedPlayer)) {
          ProxiedPlayer p = (ProxiedPlayer)sender;
          if (p.hasPermission("testproject.hi"))
              p.sendMessage(ChatColor.GOLD + "Created your project! :)");
        }
    }
}
