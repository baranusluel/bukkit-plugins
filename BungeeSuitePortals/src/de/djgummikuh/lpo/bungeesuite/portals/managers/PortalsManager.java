package de.djgummikuh.lpo.bungeesuite.portals.managers;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

import de.djgummikuh.lpo.bungeesuite.portals.objects.Portal;
import de.djgummikuh.lpo.bungeesuite.portals.tasks.PluginMessageTask;

public class PortalsManager {

	private static volatile boolean initialized = false;
	private static Plugin hostPlugin = null;
	private final static Map<World, List<Portal>> portals = new HashMap<>();
	public final static Map<String, Location> pendingTeleports = new HashMap<>();
	private static WorldEditPlugin worldedit = null;

	public static void setWorldEditPlugin(WorldEditPlugin plugin) {
		worldedit = plugin;
	}

	public static void setPlugin(Plugin p) {
		PortalsManager.hostPlugin = p;
	}

	public static boolean isInitialized() {
		return initialized;
	}

	public static Map<World, List<Portal>> getPortals() {
		return portals;
	}

	public static void deletePortal(String name, String string) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("DeletePortal");
			out.writeUTF(name);
			out.writeUTF(string);

		} catch (IOException e) {
			e.printStackTrace();
		}
		new PluginMessageTask(b, hostPlugin).runTaskAsynchronously(hostPlugin);

	}

	public static void removePortal(String name) {
		Portal p = getPortal(name);
		System.out.println("removing portal " + name);
		if (p != null) {
			portals.get(p.getWorld()).remove(p);
			p.clearPortal();
		}
	}

	public static Portal getPortal(String name) {
		for (List<Portal> list : portals.values()) {
			for (Portal p : list) {
				if (p.getName().equals(name)) {
					return p;
				}
			}
		}
		return null;
	}

	public static void getPortalsList(String name) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("ListPortals");
			out.writeUTF(name);

		} catch (IOException e) {
			e.printStackTrace();
		}
		new PluginMessageTask(b, hostPlugin).runTaskAsynchronously(hostPlugin);

	}

	public static void teleportPlayer(Player p, Portal portal) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("TeleportPlayer");
			out.writeUTF(p.getName());
			out.writeUTF(portal.getType());
			out.writeUTF(portal.getDestination());
			out.writeBoolean(p.hasPermission("bungeesuite.portals.portal."
					+ portal.getName())
					|| p.hasPermission("bungeesuite.portals.portal.*"));

		} catch (IOException e) {
			e.printStackTrace();
		}
		new PluginMessageTask(b, hostPlugin).runTaskAsynchronously(hostPlugin);
	}

	public static void setPortal(CommandSender sender, String name,
			String type, String dest, String fill) {
		sender.getServer().getLogger().log(Level.INFO, "Creating a portal!");

		Player p = (Player) sender;
		Selection sel = worldedit.getSelection(p);

		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("SetPortal");
			out.writeUTF(sender.getName());
			if (sel == null || !(sel instanceof CuboidSelection)) {
				out.writeBoolean(false);
			} else {
				out.writeBoolean(true);
				out.writeUTF(name);
				out.writeUTF(type);
				out.writeUTF(dest);
				out.writeUTF(fill);
				Location max = sel.getMaximumPoint();
				Location min = sel.getMinimumPoint();
				out.writeUTF(max.getWorld().getName());
				out.writeDouble(max.getX());
				out.writeDouble(max.getY());
				out.writeDouble(max.getZ());
				out.writeUTF(min.getWorld().getName());
				out.writeDouble(min.getX());
				out.writeDouble(min.getY());
				out.writeDouble(min.getZ());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		new PluginMessageTask(b, hostPlugin).runTaskAsynchronously(hostPlugin);

	}

	public static void addPortal(String name, String type, String dest,
			String filltype, Location max, Location min) {
		hostPlugin.getLogger().log(Level.INFO,
				"Loading Portal " + name + ", " + type + ", " + filltype);
		if (max.getWorld() == null) {
			Bukkit.getConsoleSender().sendMessage(
					ChatColor.RED + "World does not exist portal " + name
							+ " will not load :(");
			return;
		}
		Portal portal = new Portal(name, type, dest, filltype, max, min);
		List<Portal> ps = portals.get(max.getWorld());
		if (ps == null) {
			ps = new ArrayList<>();
			portals.put(max.getWorld(), ps);
		} else {
			for (Portal p : ps) {
				if (p.getName().equals(name))
					return;
			}
		}
		ps.add(portal);
		portal.fillPortal();
	}

	public static void requestPortals() {
		if (isInitialized()) {
			hostPlugin
					.getLogger()
					.log(Level.INFO,
							"Not requesting portals because we are already initialized!");
			return;
		}
		hostPlugin.getLogger().log(Level.INFO,
				"Requesting portals because we are not initialized!");
		initialized = true;
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("RequestPortals");
		} catch (IOException e) {
			e.printStackTrace();
		}
		new PluginMessageTask(b, hostPlugin).runTaskAsynchronously(hostPlugin);

	}

	public static void sendVersion() {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("SendVersion");
			out.writeUTF(ChatColor.RED + "portals - " + ChatColor.GOLD
					+ hostPlugin.getDescription().getVersion());
		} catch (IOException e) {
			e.printStackTrace();
		}
		new PluginMessageTask(b, hostPlugin).runTaskAsynchronously(hostPlugin);
	}

	public static void shutdown() {
		for (List<Portal> list : portals.values()) {
			for (Portal p : list) {
				p.clearPortal();
			}
		}
		initialized = false;
		portals.clear();
	}
}