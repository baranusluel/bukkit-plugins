package com.barancode.choicefriends.commands;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.barancode.choicefriends.Main;
import com.barancode.choicefriends.Utils;
import com.barancode.choicefriends.database.DatabaseFriend;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class MsgCommand extends Command{
	
	public static HashMap<UUID, UUID> conversations = new HashMap<UUID, UUID>();
	
	public MsgCommand(String command){
		super(command);
	}
	
    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (strings.length < 2){
        	commandSender.sendMessage(new ComponentBuilder("/cmsg <player> <message>").color(ChatColor.RED).create());
        } else {
        	String name = strings[0];
        	UUID uuid = Utils.isOnlineIgnoreCase(name);
        	if (uuid == null || !Main.isOnline(uuid)){
        		commandSender.sendMessage(new ComponentBuilder("That player is not online").color(ChatColor.RED).create());
        		return;
        	}
        	List<UUID> friends = DatabaseFriend.getFriends(((ProxiedPlayer)commandSender).getUniqueId());
        	if (!friends.contains(uuid)){
        		commandSender.sendMessage(new ComponentBuilder("You can only use global messaging with friends. To manage your friends, do /friend").color(ChatColor.RED).create());
        		return;
        	}
        	ProxiedPlayer target = Main.instance.getProxy().getPlayer(uuid);
        	
        	String message = "";
        	for (int i = 1; i < strings.length; i++){
        		message += strings[i] + ' ';
        	}
        	message = message.trim();
        	target.sendMessage(new ComponentBuilder(commandSender.getName() + " -> You: ").color(ChatColor.GOLD).append(message).color(ChatColor.RESET).create());
        	commandSender.sendMessage(new ComponentBuilder("You -> " + name + ": ").color(ChatColor.GOLD).append(message).color(ChatColor.RESET).create());
        	
        	if (!conversations.containsKey(uuid)) target.sendMessage(new ComponentBuilder("You can use /cr to reply to this message").color(ChatColor.DARK_GRAY).create());
        	conversations.put(uuid, ((ProxiedPlayer)commandSender).getUniqueId());
        	if (!conversations.containsKey(((ProxiedPlayer)commandSender).getUniqueId())) commandSender.sendMessage(new ComponentBuilder("You can use /cr to continue this conversation").color(ChatColor.DARK_GRAY).create());
        	conversations.put(((ProxiedPlayer)commandSender).getUniqueId(), uuid);
        }
    }
}
