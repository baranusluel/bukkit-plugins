package com.barancode.query;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import query.MCQuery;
import query.QueryResponse;

public class Main {
	public static void main(String[] args)
	{
		MCQuery mcQuery = new MCQuery("127.0.0.1", 25565);
		QueryResponse response = mcQuery.basicStat();
		int players = response.getOnlinePlayers();
		
		try {
			File file = new File("/usr/share/nginx/html/bcmcnetwork/status/value.php");
 
			file.delete();
			file.createNewFile();
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write("<?php $players = " + players + "; ?>");
			
			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
