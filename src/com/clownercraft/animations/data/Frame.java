package com.clownercraft.animations.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.clownercraft.animations.RideableAnimations;

public class Frame {
	
	private List<SBlock> blocks;
	private HashMap<Integer, Location> rideLocations;
	private int frameNum;
	private Animation an;
	private File frameFile;
	
	public Frame(List<SBlock> blocks, int frameNum, Animation animation){
		this.blocks = blocks;
		this.rideLocations = new HashMap<Integer, Location>();
		this.frameNum = frameNum;
		this.an = animation;
		an.addFrame(this);
		frameFile = new File(RideableAnimations.getPlugin().getDataFolder(), an.getName() + "/frame" + frameNum + ".yml");
		this.save();
	}
	
	public Frame(File file){
		frameFile = file;
		FileConfiguration fc = YamlConfiguration.loadConfiguration(file);
		blocks = new ArrayList<SBlock>();
		for(String str : fc.getStringList("blocks")){
			blocks.add(new SBlock(str));
		}
		rideLocations = new HashMap<Integer, Location>();
		if(fc.contains("ride")){
			ConfigurationSection cs = fc.getConfigurationSection("ride");
			for(String key : cs.getKeys(false)){
				rideLocations.put(Integer.valueOf(key), (Location) cs.get(key));
			}
		}
		this.an = Animations.getAnimation(fc.getString("animation"));
		this.frameNum = fc.getInt("frame");
	}
	
	public void setRideLocation(int ride, Location l){
		this.rideLocations.put(ride, l);
	}
	
	public void save(){
		if(!frameFile.exists()){
			try {
				frameFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileConfiguration fc = YamlConfiguration.loadConfiguration(frameFile);
		List<String> strs = new ArrayList<String>();
		for(SBlock b : blocks){
			strs.add(b.asString());
		}
		fc.set("blocks", strs);
		fc.set("frame", frameNum);
		for(int key : rideLocations.keySet()){
			fc.set("ride." + key, rideLocations.get(key));
		}
		try {
			fc.save(frameFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getFrameNumber(){
		return frameNum;
	}
	
	public List<SBlock> getBlocks(){
		return blocks;
	}
	
	public HashMap<Integer, Location> getRideLocations(){
		return rideLocations;
	}
	
	public List<Integer> getSortedRideIDs(){
		List<Integer> ints = new ArrayList<Integer>();
		for(Integer key : rideLocations.keySet()){
			ints.add(key);
		}
		Collections.sort(ints, new Comparator<Integer>(){

			@Override
			public int compare(Integer arg0, Integer arg1) {
				return Integer.compare(arg0, arg1);
			}
			
		});
		
		return ints;
	}
	
	public Location getRideLocation(int id){
		return rideLocations.get(id);
	}
	
	public void loadFrame(){
		for(SBlock b : blocks){
			b.setBlock();
		}
	}
	
}
