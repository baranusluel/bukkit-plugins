package com.barancode.choicefriends.bukkit;

import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import net.choicecraft.ChoiceWorks.CEvent;
import net.choicecraft.ChoiceWorks.CUserdata;
import net.choicecraft.ChoiceWorks.exception.PlayerNotFoundException;
import net.choicecraft.ChoiceWorks.utils.CString;
import net.choicecraft.ChoiceWorks.utils.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.earth2me.essentials.Essentials;
import com.google.common.collect.Sets;

public class Main extends JavaPlugin implements Listener{
	BukkitScheduler scheduler = null;
	static Essentials es = null;
	boolean factions = false;
	HashSet<Integer> events = new HashSet<Integer>();
	ConsoleCommandSender sender = new ConsoleSender();
	static Main instance;
	{
		instance = this;
	}
	
	private static Class<?> nmsChatSerializer = Reflection.getNMSClass("ChatSerializer");
	private static Class<?> nmsPacketPlayOutChat = Reflection.getNMSClass("PacketPlayOutChat");
	
	@Override
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		scheduler = Bukkit.getScheduler();
		es = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
		factions = Bukkit.getPluginManager().getPlugin("Factions") != null;
		if (factions) getServer().getPluginManager().registerEvents(new FactionsListener(), this);
	}
	
	@EventHandler
	public void onChoiceWorksEvent(CEvent e){
		if (!e.getType().equals("bungee.incoming")) return;
		Map<String, String> arguments = e.getArguments();
		if (!arguments.get("label").equals("ChoiceFriends")) return;
		final String[] data = arguments.get("data").split(" --> ");
		scheduler.runTaskAsynchronously(this, new Runnable(){
			@Override
			public void run(){
				final UUID uuid = CString.formatUUID(data[0]);
				final String name;
				try {
					name = CUserdata.getUsername(uuid);
				} catch (PlayerNotFoundException e) {
					return;
				}
				
				if (factions){
					Bukkit.dispatchCommand(sender, "fcadmin info " + name);
					scheduler.runTaskLaterAsynchronously(instance, new Runnable(){
						@Override
						public void run(){
							sendChat(name, uuid, data);
						}
					}, 1);
				}
				else sendChat(name, uuid, data);
			}
		});
	}
	
	public void sendChat(String name, UUID uuid, String[] data){
		Player p = new DummyPlayer(name, uuid, getServer());
		HashSet<Player> targets = new HashSet<Player>();
		for (String stringUUID : data[1].split(",")){
			UUID targetUUID = CString.formatUUID(stringUUID);
			if (Utils.isOnline(targetUUID)) targets.add(Bukkit.getPlayer(targetUUID));
		}
		AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(true, p, data[2], Sets.newHashSet(p));
		events.add(event.hashCode());
		getServer().getPluginManager().callEvent(event);
		// Send the message yourself if the server doesn't have Factions.
		// Factions likes to send the messages itself (so that it can color the tags).
		String message = "{text:\"" + String.format(event.getFormat(), p.getDisplayName(), event.getMessage()).replaceAll("\"", "\\\"") + "\",hoverEvent:{action:show_text,value:\"" + data[3] + "\"}}";
		for (Player target : targets){
			try {
				sendRawMessage(target, message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
		targets.clear();
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e){
		if (events.contains(e.hashCode())){
			String format = ChatColor.GREEN + "[CR] " + ChatColor.GRAY + ChatColor.stripColor(e.getFormat());
			if (factions) format = ChatColor.stripColor(format);
			e.setFormat(format);
			events.remove(e.hashCode());
		}
	}
	
	/*
	 * This method has been taken from
	 * inventivetalent's plugin TellRawAPI
	 */
	public static void sendRawMessage(Player player, String message) throws Exception
	{
	    if (message != null)
	      try {
	        Object handle = Reflection.getHandle(player);
	        Object connection = Reflection.getField(handle.getClass(), "playerConnection").get(handle);
	        Object serialized = Reflection.getMethod(nmsChatSerializer, "a", new Class[] { String.class }).invoke(null, new Object[] { message });
	        Object packet = nmsPacketPlayOutChat.getConstructor(new Class[] { Reflection.getNMSClass("IChatBaseComponent") }).newInstance(new Object[] { serialized });
	        Reflection.getMethod(connection.getClass(), "sendPacket", new Class[0]).invoke(connection, new Object[] { packet });
	      } catch (Exception e) {
	        e.printStackTrace();
	      }
	    else
	      throw new Exception("Message must not be null! It will cause game crashes!");
	}
}
