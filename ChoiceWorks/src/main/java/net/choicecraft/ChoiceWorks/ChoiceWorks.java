package net.choicecraft.ChoiceWorks;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

import net.choicecraft.ChoiceWorks.database.CDatabase;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class ChoiceWorks extends JavaPlugin {
	/**
	 * Object from Bukkit to log messages.
	 */
	private static Logger pluginLogger;
	
	/**
	 * Instance to this plugin for self referencing outside of scope.
	 */
	private static ChoiceWorks instance;
	
	/**
	 * Runtime instance of this object, required for reflection.
	 */
	private static Class<ChoiceWorks> class_instance;
	
	/**
	 * Object for scheduling tasks
	 */
	static BukkitScheduler scheduler;
	
	/**
	 * An instance of CDatabase for internal ChoiceWorks functions.
	 * Plugins should create their own instances.
	 */
	private static CDatabase db_instance;

	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		// Object from Bukkit to log messages.
		pluginLogger = this.getLogger();
		// Instance to this plugin for self referencing outside of scope.
		instance = this;
		// Runtime instance of this object, required for reflection.
		class_instance = (Class<ChoiceWorks>) getClass();
		
		saveDefaultConfig();
		
		// Object for scheduling tasks
		scheduler = Bukkit.getScheduler();
		
		// Register bukkit events
		CListener cl = new CListener();
		getServer().getPluginManager().registerEvents(cl, this);
		getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", cl);
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		
		print("-- [ChoiceWorks] ----------");
		print("Initializing: CDatabase");
		CDatabase.setHostname(getConfig().getString("database.hostname"));
		CDatabase.setDatabase(getConfig().getString("database.database"));
		CDatabase.setUsername(getConfig().getString("database.username"));
		CDatabase.setPassword(getConfig().getString("database.password"));
		CDatabase.initialize();
		
		/*
		 * An instance of CDatabase for internal ChoiceWorks functions.
		 * Plugins should create their own instances.
		 */
		db_instance = new CDatabase();
		
		print("Initializing: CUserdata");
		CUserdata.initialize(db_instance);
		print("---------------------------");
	}

	@Override
	public void onDisable() {
		// invalidate this plug-in.
		pluginLogger = null;
		instance = null;
		class_instance = null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		/*CommandInterface handler = this.handlers.get(cmd.getLabel().toLowerCase());
		
		if(handler != null) {
			if(handler.hasPermission(sender)) {
				return handler.onCommand(sender, args);
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
				return true;
			}
		}
		*/
		return false;
	}

	/**
	 * Print function for ChoiceWorks, please use your own.
	 * @param message The string message to be output.
	 */
	public static void print(String message) {
		pluginLogger.info(message);
	}
	
	/**
	 * Print function for ChoiceWorks, please use your own.
	 * @param message The string message to be output.
	 */
	public static void print(String message, String origin) {
		pluginLogger.info(origin + " -> " + message);
	}
	
	/**
	 * Instance of the ChoiceWorks plug-in for self referencing outside of scope.
	 * This may return null if Bukkit has not instantiated this plug-in yet.
	 * @return The instance of the ChoiceWorks plug-in.
	 */
	public static ChoiceWorks getInstance() {
		return instance;
	}
	
	/**
	 * Runtime instance of this object, required for reflection.
	 * @return The runtime object instance.
	 */
	public static Class<ChoiceWorks> getClassInstance() {
		return class_instance;
	}
	
	/**
	 * Sends a message to another server from the BungeeCord plugin messaging channel.
	 * Note: A message can only be transferred between the sending server and bungeecord,
	 * as well as between bungeecord and the target server, if there is an online player on those servers.
	 * @param target The server to send to. Either the server name, or ALL (doesn't include sending server)
	 * @param label A label to identify your message with, so that the correct plugin can use it
	 * @param message The message or data you're sending
	 * @return Whether it successfully sent the message (it won't if there is no online player on this server)
	 */
	public static boolean sendBungeeMessage(String target, String label, String message){
		if (Bukkit.getOnlinePlayers().length == 0) return false;
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
	    
		out.writeUTF("Forward");
		out.writeUTF(target);
		out.writeUTF("ChoiceWorks");

		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		DataOutputStream msgout = new DataOutputStream(msgbytes);
		try {
			msgout.writeUTF(label + ":" + message);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		out.writeShort(msgbytes.toByteArray().length);
		out.write(msgbytes.toByteArray());
		
	    Player player = Bukkit.getOnlinePlayers()[0];
	    player.sendPluginMessage(getInstance(), "BungeeCord", out.toByteArray());
	    return true;
	}
}