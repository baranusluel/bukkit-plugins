package de.djgummikuh.lpo.bungeesuite.portals;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import de.djgummikuh.lpo.bungeesuite.portals.commands.DeletePortalCommand;
import de.djgummikuh.lpo.bungeesuite.portals.commands.ListPortalsCommand;
import de.djgummikuh.lpo.bungeesuite.portals.commands.SetPortalCommand;
import de.djgummikuh.lpo.bungeesuite.portals.listeners.PhysicsListener;
import de.djgummikuh.lpo.bungeesuite.portals.listeners.PlayerLoginListener;
import de.djgummikuh.lpo.bungeesuite.portals.listeners.PlayerMoveListener;
import de.djgummikuh.lpo.bungeesuite.portals.listeners.PortalsMessageListener;
import de.djgummikuh.lpo.bungeesuite.portals.managers.PermissionsManager;
import de.djgummikuh.lpo.bungeesuite.portals.managers.PortalsManager;

public class BungeeSuitePortals extends JavaPlugin {

	public final static String OUTGOING_PLUGIN_CHANNEL = "BSPortals";
	private final static String INCOMING_PLUGIN_CHANNEL = "BungeeSuitePorts";
	private static volatile boolean reloading = false;

	@Override
	public void onEnable() {
		PortalsManager.setPlugin(this);
		PermissionsManager.setPlugin(this);
		loadWorldEdit();
		if (!reloading) {
			registerChannels();
			registerListeners();
			registerCommands();
		}
		// This causes BungeeCoord to send us the portal coordinates and
		// initializes the portals (even after reload)
		if (getServer().getOnlinePlayers().length > 0) {
			PortalsManager.requestPortals();
		}
	}

	@Override
	public void onDisable() {
		// Clears the portals and set's initialized to false again
		PortalsManager.shutdown();
		reloading = true;
	}

	private void loadWorldEdit() {
		WorldEditPlugin plugin = (WorldEditPlugin) getServer()
				.getPluginManager().getPlugin("WorldEdit");
		if (plugin == null) {
			Bukkit.getLogger()
					.log(Level.INFO,
							"No worldedit found, You will not be able to create portals!");
		} else {
			PortalsManager.setWorldEditPlugin(plugin);
		}

	}

	private void registerCommands() {
		getCommand("setportal").setExecutor(new SetPortalCommand());
		getCommand("delportal").setExecutor(new DeletePortalCommand());
		getCommand("portals").setExecutor(new ListPortalsCommand());
	}

	private void registerChannels() {
		Bukkit.getMessenger().registerIncomingPluginChannel(this,
				INCOMING_PLUGIN_CHANNEL, new PortalsMessageListener(this));
		Bukkit.getMessenger().registerOutgoingPluginChannel(this,
				OUTGOING_PLUGIN_CHANNEL);
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	}

	private void registerListeners() {
		getServer().getPluginManager().registerEvents(new PlayerMoveListener(),
				this);
		getServer().getPluginManager().registerEvents(new PhysicsListener(),
				this);
		getServer().getPluginManager().registerEvents(
				new PlayerLoginListener(this), this);
	}
}