package de.djgummikuh.lpo.bungeesuite.portals.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Portal {
	private String name;
	private String type;
	private String dest;
	private ArrayList<Loc> blocks = new ArrayList<>();
	private FillType fillType;
	private World world;

	public Portal(String name, String type, String dest, String fillType,
			Location max, Location min) {
		this.name = name;
		this.type = type;
		this.dest = dest;
		this.world = max.getWorld();
		try {
			this.fillType = FillType.valueOf(fillType.toUpperCase());
		} catch (Exception e) {
			this.fillType = FillType.AIR;
			System.out
					.println("Invalid fill type for the portal, setting to AIR");
		}
		if (this.fillType == null) {
			this.fillType = FillType.AIR;
		}
		for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
			for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
				for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
					blocks.add(new Loc(max.getWorld().getName(), x, y, z));
				}
			}
		}
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public String getDestination() {
		return dest;
	}

	public FillType getFillType() {
		return fillType;
	}

	public void fillPortal() {
		for (Loc locs : blocks) {
			Block b = locs.getBlock();
			if (b.isEmpty()) {
				b.setType(fillType.getBlockMaterial());
			}
		}
	}

	public void clearPortal() {
		for (Loc locs : blocks) {
			final Block block = locs.getBlock();
			if (block.getType() == fillType.getBlockMaterial()) {
				block.setType(FillType.AIR.getBlockMaterial());
			} else if (fillType.equals(FillType.LAVA)
					&& block.getType().equals(Material.LAVA)) {
				block.setType(FillType.AIR.getBlockMaterial());
			} else if (fillType.equals(FillType.WATER)
					&& block.getType().equals(Material.WATER)) {
				block.setType(FillType.AIR.getBlockMaterial());
			}

		}
	}

	public List<Loc> getBlocks() {
		return blocks;
	}

	public boolean isBlockInPortal(Block b) {
		for (Loc l : blocks) {
			if (l.equals(b)) {
				return true;
			}
		}
		return false;
	}

	public World getWorld() {
		return world;
	}

	public boolean isLocationInPortal(Location l) {
		for (Loc loc : blocks) {
			if (loc.equals(l)) {
				return true;
			}
		}
		return false;
	}

}