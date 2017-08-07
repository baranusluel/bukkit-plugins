package com.barancode.mc.bcmcnobuild;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	Main plugin;
	public void initialize(Main plugin) throws Exception{
		this.plugin = plugin;
		File file = new File(plugin.getDataFolder() + "");
		if (!file.exists()){
			file.mkdir();
			file = new File(plugin.getDataFolder() + "/log.txt");
			file.createNewFile();
		}
	}
	public void write(String s) throws Exception{
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		FileWriter fw = new FileWriter(plugin.getDataFolder() + "/log.txt", true);
		PrintWriter pw = new PrintWriter(fw);
		pw.println("[" + dateFormat.format(date) + "] " + s);
		pw.close();
		fw.close();
	}
}
