package de.djgummikuh.lpo.bungeesuite.portals.tasks;

import java.io.ByteArrayOutputStream;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import de.djgummikuh.lpo.bungeesuite.portals.BungeeSuitePortals;

public class PluginMessageTask extends BukkitRunnable {

	private final ByteArrayOutputStream bytes;
	private final Plugin hostPlugin;

	public PluginMessageTask(ByteArrayOutputStream bytes, Plugin p) {
		this.bytes = bytes;
		this.hostPlugin = p;
	}

	@Override
	public void run() {
		try {
			if (Bukkit.getOnlinePlayers().length < 0) {
				Bukkit.getOnlinePlayers()[0].sendPluginMessage(hostPlugin,
						BungeeSuitePortals.OUTGOING_PLUGIN_CHANNEL,
						bytes.toByteArray());
			} else {
				hostPlugin.getLogger().log(Level.INFO,
						"Sending plugin message via Server object");
				Bukkit.getServer().sendPluginMessage(hostPlugin,
						BungeeSuitePortals.OUTGOING_PLUGIN_CHANNEL,
						bytes.toByteArray());
			}
		} catch (Exception e) {
			hostPlugin.getLogger().log(Level.INFO, "Exception happened", e);
		}

	}

}