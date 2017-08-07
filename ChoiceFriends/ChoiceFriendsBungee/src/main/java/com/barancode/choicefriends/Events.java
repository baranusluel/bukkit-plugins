package com.barancode.choicefriends;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import com.barancode.choicefriends.commands.CRTCommand;
import com.barancode.choicefriends.database.DatabaseFriend;
import com.barancode.choicefriends.database.DatabaseFriendRequest;
import com.barancode.choicefriends.database.DatabaseUUID;
import com.barancode.choicefriends.objects.ChatLocation;
import com.barancode.choicefriends.objects.Chatroom;

public class Events implements Listener{

	@EventHandler
	public void onDisconnect(final PlayerDisconnectEvent e){
		UUID uuid = e.getPlayer().getUniqueId();
		final boolean wasonline = Main.isOnline(uuid);
		Main.deleteServer(uuid);
		Main.deleteHiding(uuid);
		
		Main.scheduler.runAsync(Main.instance, new Runnable(){
			@Override
			public void run(){				
				if (wasonline){
					List<UUID> friends = DatabaseFriend.getFriends(e.getPlayer().getUniqueId());
					for (UUID uuid : friends){
						if (Utils.isOnline(uuid)){
							Main.instance.getProxy().getPlayer(uuid).sendMessage(new ComponentBuilder(e.getPlayer().getName() + " has disconnected from the network").color(ChatColor.GREEN).create());
						}
					}
				}
			}
		});
	}
	
	@EventHandler
	public void onJoin(final ServerConnectedEvent e){		
		Main.scheduler.runAsync(Main.instance, new Runnable(){
			@Override
			public void run(){
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Main.setServer(e.getPlayer().getUniqueId(), e.getPlayer().getServer().getInfo().getName());
				
				String server = Main.getServer(e.getPlayer().getUniqueId());
				if (!server.equals("")){
					List<UUID> friends = DatabaseFriend.getFriends(e.getPlayer().getUniqueId());
					for (UUID uuid : friends){
						if (Utils.isOnline(uuid)){
							Main.instance.getProxy().getPlayer(uuid).sendMessage(new ComponentBuilder(e.getPlayer().getName() + " has logged into " + server).color(ChatColor.GREEN).create());
						}
					}
				}
				
				List<UUID> list = DatabaseFriendRequest.getFriendRequests(e.getPlayer().getUniqueId());
				if (list.size() == 0) return;
				else if (list.size() == 1){
					UUID uuid = list.get(0);
					String name = DatabaseUUID.getUsername(uuid);
					e.getPlayer().sendMessage(new ComponentBuilder(name + " has sent you a friend request. Do '/friend accept " + name + "' or '/friend deny " + name + "'").color(ChatColor.DARK_AQUA).create());
				} else {
					e.getPlayer().sendMessage(new ComponentBuilder("You have received friend requests from the following players. Do '/friend accept <name>' or '/friend deny <name>'").color(ChatColor.DARK_AQUA).create());
					String message = "";
					for (UUID uuid : list){
						String name = DatabaseUUID.getUsername(uuid);
						if (message.equals("")) message = name;
						else message += ", " + name;
					}
					e.getPlayer().sendMessage(new ComponentBuilder(message).create());
				}
			}
		});
	}
	
	@EventHandler
	public void onChat(ChatEvent e){
		if (e.getMessage().startsWith("/")) return;
		
		if (e.getMessage().equals(".")){
			CRTCommand.toggle((CommandSender)(ProxiedPlayer)e.getSender());
			e.setCancelled(true);
			return;
		}
		
		UUID uuid = ((ProxiedPlayer)e.getSender()).getUniqueId();
		if (!ChatroomMan.locations.containsKey(uuid)) return;
		ChatLocation loc = ChatroomMan.locations.get(uuid);
		if (!loc.currentlyActive) return;
		e.setCancelled(true);
		Chatroom room = ChatroomMan.rooms.get(loc.name);
		List<UUID> users = room.users;
		String usersString = "";
		HashMap<ServerInfo, Set<UUID>> servers = new HashMap<ServerInfo, Set<UUID>>();
		for (UUID userUUID : users){
			ProxiedPlayer user = Main.instance.getProxy().getPlayer(userUUID);
			if (user == null){
				usersString += "§4" + DatabaseUUID.getUsername(userUUID) + "§r, ";
				continue;
			}
			usersString += "§2" + DatabaseUUID.getUsername(userUUID) + "§r, ";
			Set<UUID> serverPlayers = (servers.containsKey(user.getServer().getInfo())) ? servers.get(user.getServer().getInfo()) : new HashSet<UUID>();
			serverPlayers.add(userUUID);
			servers.put(user.getServer().getInfo(), serverPlayers);
		}
		usersString = usersString.substring(0, usersString.length() - 2);
		for (ServerInfo server : servers.keySet()){
			String targets = "";
			for (UUID userUUID : servers.get(server)){
				targets += userUUID.toString() + ",";
			}
			targets = targets.substring(0, targets.length() - 1);
			Utils.sendBungeeMessage(server.getName(), "ChoiceFriends", uuid + " --> " + targets + " --> " + e.getMessage().replaceAll(" --> ", " -> ") + " --> " + usersString);
		}
	}
}
