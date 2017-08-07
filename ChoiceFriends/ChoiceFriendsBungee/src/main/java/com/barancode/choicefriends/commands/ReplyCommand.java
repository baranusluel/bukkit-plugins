package com.barancode.choicefriends.commands;

import java.util.List;
import java.util.UUID;

import com.barancode.choicefriends.Main;
import com.barancode.choicefriends.database.DatabaseFriend;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ReplyCommand extends Command{
	
	public ReplyCommand(){
		super("cr");
	}
	
    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (strings.length < 1){
        	commandSender.sendMessage(new ComponentBuilder("/cr <message>").color(ChatColor.RED).create());
        } else {
        	if (!MsgCommand.conversations.containsKey(((ProxiedPlayer)commandSender).getUniqueId())){
        		commandSender.sendMessage(new ComponentBuilder("You have no one to reply to!").color(ChatColor.RED).create());
        		return;
        	}
        	UUID uuid = MsgCommand.conversations.get(((ProxiedPlayer)commandSender).getUniqueId());
        	if (!Main.isOnline(uuid)){
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
        	for (int i = 0; i < strings.length; i++){
        		message += strings[i] + ' ';
        	}
        	message = message.trim();
        	target.sendMessage(new ComponentBuilder(commandSender.getName() + " -> You: ").color(ChatColor.GOLD).append(message).color(ChatColor.RESET).create());
        	commandSender.sendMessage(new ComponentBuilder("You -> " + target.getName() + ": ").color(ChatColor.GOLD).append(message).color(ChatColor.RESET).create());
        }
    }
}
