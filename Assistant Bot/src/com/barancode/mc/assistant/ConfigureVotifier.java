package com.barancode.mc.assistant;

import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.LinkedList;

public class ConfigureVotifier {
	public ConfigureVotifier() throws Exception{
		int portCount = 8193;
		File directory = new File(".");
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(directory.getPath() + "/Hub/plugins/VoteSend/config.yml")));
		for (File file : directory.listFiles()){
			if (file.getName().equalsIgnoreCase("Hub")) continue;
			
			if (!new File(file.getPath() + "/plugins/Votifier").exists()) continue;
			
			out.println("  " + file.getName() + ":");
			out.println("    IP: '127.0.0.1'");
			out.println("    Custom: ''");
			out.println("    Port: " + portCount);
			BufferedReader br = new BufferedReader(new FileReader(file.getPath() + "/plugins/Votifier/config.yml"));
			List<String> lines = new LinkedList<String>();
			String line = null;
			while ((line = br.readLine()) != null) {
				if (!line.contains("port:")) lines.add(line);
				else lines.add("port: " + portCount);
			}
			br.close();
			
			File file2 = new File(file.getPath() + "/plugins/Votifier/config.yml");
			file2.delete();
			file2.createNewFile();
			PrintWriter out2 = new PrintWriter(new BufferedWriter(new FileWriter(file2)));
			for (String ln : lines){
				out2.println(ln);
			}
			out2.close();
			
			BufferedReader br2 = new BufferedReader(new FileReader(file.getPath() + "/plugins/Votifier/rsa/public.key"));
			out.println("    Key: " + br2.readLine());
			br2.close();
			
			portCount++;
		}
		out.close();
	}
}
