package com.barancode.mc.globalcommands.bungee;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class SendCommand extends Command{
	Main plugin;
    public SendCommand(Main plugin) {
        super("globalsend", "bcmc-bungee.globalsend");
        this.plugin = plugin;
    }
    public void execute(CommandSender sender, String[] args) {
        if ((sender instanceof ProxiedPlayer)) {
          ProxiedPlayer p = (ProxiedPlayer)sender;
          if (p.hasPermission("bcmc-bungee.globalsend")){
        	  String message = "";
        	  for (String s : args){
        		  message += s + " ";
        	  }
        	  message = message.trim();
        	  sendToAll(message);
        	  sendToCurrent(message, p);
          }
        }
    }
    public void sendToAll(String message){
  	  ByteArrayOutputStream b = new ByteArrayOutputStream();
  	  DataOutputStream out = new DataOutputStream(b);
  	  try {
      	  out.writeUTF("Forward");
      	  out.writeUTF("ALL");
      	  out.writeUTF("GlobalCommands");

      	  ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
      	  DataOutputStream msgout = new DataOutputStream(msgbytes);
      	  msgout.writeUTF(message);
      	  //msgout.writeShort(123);

      	  out.writeShort(msgbytes.toByteArray().length);
      	  out.write(msgbytes.toByteArray());
  	  } catch (IOException e){
  		  e.printStackTrace();
  	  }
    }
    public void sendToCurrent(String message,ProxiedPlayer p){
    	  ByteArrayOutputStream b = new ByteArrayOutputStream();
      	  DataOutputStream out = new DataOutputStream(b);
      	  try {
          	  out.writeUTF("Forward");
          	  out.writeUTF(p.getServer().getInfo().getName());
          	  out.writeUTF("GlobalCommands");

          	  ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
          	  DataOutputStream msgout = new DataOutputStream(msgbytes);
          	  msgout.writeUTF(message);
          	  //msgout.writeShort(123);

          	  out.writeShort(msgbytes.toByteArray().length);
          	  out.write(msgbytes.toByteArray());
      	  } catch (IOException e){
      		  e.printStackTrace();
      	  }
    }
}
