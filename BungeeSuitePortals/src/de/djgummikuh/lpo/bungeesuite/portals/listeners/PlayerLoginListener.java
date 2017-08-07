package de.djgummikuh.lpo.bungeesuite.portals.listeners;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;

import de.djgummikuh.lpo.bungeesuite.portals.managers.PermissionsManager;
import de.djgummikuh.lpo.bungeesuite.portals.managers.PortalsManager;

public class PlayerLoginListener implements Listener {

	private final Plugin hostPlugin;

	public PlayerLoginListener(Plugin p) {
		this.hostPlugin = p;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void playerConnect(PlayerJoinEvent e) {
		if (!PortalsManager.isInitialized()) {
			hostPlugin.getLogger().log(Level.INFO,
					"Player joining, triggering portals");
			Bukkit.getScheduler().runTaskLaterAsynchronously(hostPlugin,
					new Runnable() {

						@Override
						public void run() {
							PortalsManager.requestPortals();
						}
					}, 10L);
		}
		if (PortalsManager.pendingTeleports
				.containsKey(e.getPlayer().getName())) {
			Location l = PortalsManager.pendingTeleports.get(e.getPlayer()
					.getName());
			PortalsManager.pendingTeleports.remove(e.getPlayer().getName());
			e.getPlayer().teleport(l);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void setPermissionGroup(final PlayerLoginEvent e) {
		if (e.getPlayer().hasPermission("bungeesuite.*")) {
			PermissionsManager.addAllPermissions(e.getPlayer());
		} else if (e.getPlayer().hasPermission("bungeesuite.admin")) {
			PermissionsManager.addAdminPermissions(e.getPlayer());
		} else if (e.getPlayer().hasPermission("bungeesuite.user")) {
			PermissionsManager.addUserPermissions(e.getPlayer());
		}
	}

}