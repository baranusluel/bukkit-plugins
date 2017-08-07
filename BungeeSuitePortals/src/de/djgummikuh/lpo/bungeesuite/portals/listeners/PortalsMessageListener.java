package de.djgummikuh.lpo.bungeesuite.portals.listeners;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import de.djgummikuh.lpo.bungeesuite.portals.managers.PortalsManager;

public class PortalsMessageListener implements PluginMessageListener {

	private final Plugin hostPlugin;

	public PortalsMessageListener(Plugin p) {
		this.hostPlugin = p;
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player,
			byte[] message) {
		hostPlugin.getLogger().log(
				Level.INFO,
				"Receiving Server Message on Channel " + channel + "!"
						+ message);
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(
				message));
		String task;

		try {
			task = in.readUTF();
			switch (task) {
			case "TeleportPlayer": {
				String name = in.readUTF();
				Player p = Bukkit.getPlayer(name);
				Location l = new Location(Bukkit.getWorld(in.readUTF()),
						in.readDouble(), in.readDouble(), in.readDouble(),
						in.readFloat(), in.readFloat());
				if (p == null) {
					PortalsManager.pendingTeleports.put(name, l);
				} else {
					p.teleport(l);
				}
				break;
			}
			case "SendPortal":
				PortalsManager
						.addPortal(
								in.readUTF(),
								in.readUTF(),
								in.readUTF(),
								in.readUTF(),
								new Location(Bukkit.getWorld(in.readUTF()), in
										.readDouble(), in.readDouble(), in
										.readDouble()),
								new Location(Bukkit.getWorld(in.readUTF()), in
										.readDouble(), in.readDouble(), in
										.readDouble()));
				break;
			case "DeletePortal":
				PortalsManager.removePortal(in.readUTF());
				break;
			case "GetVersion": {
				String name = null;
				try {
					name = in.readUTF();
				} catch (IOException e) {

				}
				if (name != null) {
					Player p = Bukkit.getPlayer(name);
					p.sendMessage(ChatColor.RED + "Portals - " + ChatColor.GOLD
							+ hostPlugin.getDescription().getVersion());
				}
				Bukkit.getConsoleSender().sendMessage(
						ChatColor.RED + "Portals - " + ChatColor.GOLD
								+ hostPlugin.getDescription().getVersion());
				PortalsManager.sendVersion();
				break;
			}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}