package com.barancode.choicefriends.bukkit;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class ConsoleSender implements ConsoleCommandSender{

	@Override
	public String getName() {
		
		return null;
	}

	@Override
	public Server getServer() {
		
		return null;
	}

	@Override
	public void sendMessage(String arg0) {
		sendRawMessage(arg0);
	}

	@Override
	public void sendMessage(String[] arg0) {
		
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0) {
		
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
		
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1,
			boolean arg2) {
		
		return null;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1,
			boolean arg2, int arg3) {
		
		return null;
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		
		return null;
	}

	@Override
	public boolean hasPermission(String arg0) {
		
		return true;
	}

	@Override
	public boolean hasPermission(Permission arg0) {
		
		return true;
	}

	@Override
	public boolean isPermissionSet(String arg0) {
		
		return false;
	}

	@Override
	public boolean isPermissionSet(Permission arg0) {
		
		return false;
	}

	@Override
	public void recalculatePermissions() {
		
		
	}

	@Override
	public void removeAttachment(PermissionAttachment arg0) {
		
		
	}

	@Override
	public boolean isOp() {
		
		return true;
	}

	@Override
	public void setOp(boolean arg0) {
		
		
	}

	@Override
	public void abandonConversation(Conversation arg0) {
		
		
	}

	@Override
	public void abandonConversation(Conversation arg0,
			ConversationAbandonedEvent arg1) {
		
		
	}

	@Override
	public void acceptConversationInput(String arg0) {
		
		
	}

	@Override
	public boolean beginConversation(Conversation arg0) {
		
		return false;
	}

	@Override
	public boolean isConversing() {
		
		return false;
	}
	
	boolean listening = false;
	String player = "";

	@Override
	public void sendRawMessage(String arg0) {
		arg0 = ChatColor.stripColor(arg0);
		if (arg0.contains("===info====")){
			listening = true;
			return;
		}
		if (arg0.contains("Current Chat Mode") && !arg0.contains("PUBLIC")){
			Bukkit.dispatchCommand(this, "fcadmin change " + player + " p");
			listening = false;
			player = "";
			return;
		}
		if (listening){
			player = arg0;
		}
	}

}
