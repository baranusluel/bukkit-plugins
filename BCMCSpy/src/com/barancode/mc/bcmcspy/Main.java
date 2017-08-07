package com.barancode.mc.bcmcspy;

import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin{
	
	Events events;
	
    @Override
    public void onEnable() {
        events = new Events(this);
    }
}
