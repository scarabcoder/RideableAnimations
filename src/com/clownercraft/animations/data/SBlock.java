package com.clownercraft.animations.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

@SuppressWarnings("deprecation")
public class SBlock {

	private int x, y, z;
	private byte data;
	private String material;
	private String world;

	public SBlock(Block b) {
		x = b.getX();
		y = b.getY();
		z = b.getZ();
		data = b.getData();
		this.material = b.getType().toString();
		world = b.getWorld().getName();
	}
	
	public SBlock(String str){
		String[] parsed = str.split(":");
		world = parsed[0];
		material = parsed[1];
		data = Byte.valueOf(parsed[2]);
		x = Integer.parseInt(parsed[3]);
		y = Integer.parseInt(parsed[4]);
		z = Integer.parseInt(parsed[5]);
	}
	
	public String asString(){
		String s = "";
		s += world + ":";
		s += material + ":";
		s += data + ":";
		s += x + ":";
		s += y + ":";
		s += z + "";
		return s;
	}
	
	public void setBlock(){
		this.getLocation().getBlock().setType(this.getMaterial());
		this.getLocation().getBlock().setData(this.getData());
	}
	
	public Location getLocation(){
		return new Location(Bukkit.getWorld(world), x, y, z);
	}
	
	public byte getData(){
		return data;
	}
	
	public Material getMaterial(){
		return Material.valueOf(material);
	}
	
	public World getWorld(){
		return Bukkit.getWorld(world);
	}

}