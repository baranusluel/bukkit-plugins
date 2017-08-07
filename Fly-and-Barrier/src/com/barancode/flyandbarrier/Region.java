package com.barancode.flyandbarrier;

public class Region {
	double maxx;
	double maxy;
	double maxz;
	double minx;
	double miny;
	double minz;
	String world;
	String name;
	public Region(double minx, double miny, double minz, double maxx, double maxy, double maxz, String world, String name){
		this.minx = minx;
		this.miny = miny;
		this.minz = minz;
		this.maxx = maxx;
		this.maxy = maxy;
		this.maxz = maxz;
		this.world = world;
		this.name = name;
	}
}
