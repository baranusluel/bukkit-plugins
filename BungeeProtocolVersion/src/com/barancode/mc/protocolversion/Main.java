package com.barancode.mc.protocolversion;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class Main extends Plugin implements Listener{
	  public void onEnable()
	  {
	    BungeeCord.getInstance().getPluginManager().registerListener(this, this);
	  }
	  @EventHandler
	  public void onHandshake(PlayerHandshakeEvent e) {
	    e.getHandshake().setProtocolVersion((byte)4);
	  }
}
