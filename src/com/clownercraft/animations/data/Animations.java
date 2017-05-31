package com.clownercraft.animations.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.clownercraft.animations.RideableAnimations;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class Animations {
	
	private static FileConfiguration anms;
	
	private static HashMap<String, Animation> animations = new HashMap<String, Animation>();
	
	private static File file;
	
	public static FileConfiguration getAnimationsData(){
		return anms;
	}
	
	public static List<Animation> getAnimations(){
		List<Animation> animations = new ArrayList<Animation>();
		for(String key : Animations.animations.keySet()){
			animations.add(Animations.animations.get(key));
		}
		return animations;
	}
	
	public static void initConfig(File f){
		file = f;
		if(!f.exists())
			try {
				f.createNewFile();
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		anms = YamlConfiguration.loadConfiguration(f);
		for(String key : anms.getKeys(false)){
			registerAnimation(new Animation(key));
		}
	}
	
	public static boolean exists(String name){
		return getAnimation(name) != null;
	}
	
	public static Animation createAnimation(String name, Player p){
		File folder = new File(RideableAnimations.getPlugin().getDataFolder(), name + "/");
		folder.mkdir();
		ConfigurationSection s = anms.createSection(name);
		s.set("animated", false);
		s.set("speed", 15);
		
		List<SBlock> blocks = new ArrayList<SBlock>();
		Selection se = RideableAnimations.getWorldEdit().getSelection(p);
		Location l1 = se.getMinimumPoint();
		Location l2 = se.getMaximumPoint();
		s.set("location1", l1);
		s.set("location2", l2);
		for(int x = l1.getBlockX(); x != l2.getBlockX() + 1; x++){
			for(int z = l1.getBlockZ(); z != l2.getBlockZ() + 1; z++){
				for(int y = l1.getBlockY(); y != l2.getBlockY() + 1; y++){
					Location l = new Location(l1.getWorld(), x, y, z);
					blocks.add(new SBlock(l.getBlock()));
				}
			}
		}
		
		
		Animation an = new Animation(name);
		animations.put(an.getName(), an);
		new Frame(blocks, 1, an);
		
		saveConfig();
		return an;
	}
	
	
	public static void saveAnimation(Animation animation){
		ConfigurationSection s = anms.getConfigurationSection(animation.getName());
		s.set("animated", animation.isAnimatedDefault());
		s.set("speed", animation.getAnimationSpeed());
		s.set("leaveLocation", animation.getLeaveLocation());
		for(Frame frame : animation.getFrames()){
			frame.save();
		}
	}
	
	public static void remove(Animation an){
		animations.remove(an.getName());
	}
	
	public static void registerAnimation(Animation an){
		animations.put(an.getName(), an);
	}
	
	public static void saveConfig(){
		try {
			anms.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Animation getAnimation(String name){
		return animations.get(name);
	}
	
}
