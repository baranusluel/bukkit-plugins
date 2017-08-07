package com.barancode.mc.bungeehub;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class HubCommand extends Command{
	Main plugin;
    public HubCommand(Main plugin) {
        super("hub", "bcmc-bungee.hub");
        this.plugin = plugin;
    }
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer pp = (ProxiedPlayer) sender;
        pp.connect(ProxyServer.getInstance().getServerInfo("Hub"));
    }
}
