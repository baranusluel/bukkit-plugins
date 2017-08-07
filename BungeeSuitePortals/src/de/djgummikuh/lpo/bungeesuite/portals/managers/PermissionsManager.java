package de.djgummikuh.lpo.bungeesuite.portals.managers;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PermissionsManager {

	private static Plugin hostPlugin = null;

	public static void setPlugin(Plugin p) {
		PermissionsManager.hostPlugin = p;
	}

	public static void addAllPermissions(Player player) {
		player.addAttachment(hostPlugin, "bungeesuite.portals.*", true);
	}

	public static void addAdminPermissions(Player player) {
		player.addAttachment(hostPlugin, "bungeesuite.portals.admin", true);
	}

	public static void addUserPermissions(Player player) {
		player.addAttachment(hostPlugin, "bungeesuite.portals.user", true);
	}
}