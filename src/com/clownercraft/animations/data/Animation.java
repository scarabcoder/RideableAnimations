package com.clownercraft.animations.data;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.clownercraft.animations.RideableAnimations;

public class Animation {
	
	private String name;
	private Location l1, l2;
	private List<Player> playerBuffer = new ArrayList<Player>();
	private HashMap<ArmorStand, Integer> rides = new HashMap<ArmorStand, Integer>();
	private List<Frame> frames = new ArrayList<Frame>();
	private Location leaveLocation;
	private int currentFrame;
	private int speed;
	private boolean animated;
	private boolean animating = false;
	private int animationTaskID = -1;
	
	
	public Animation(String name){
		this.currentFrame = 1;
		this.name = name;
		File frameFolder = new File(RideableAnimations.getPlugin().getDataFolder(), name + "/");
		
		
		ConfigurationSection s = Animations.getAnimationsData().getConfigurationSection(name);
		l1 = (Location) s.get("location1");
		l2 =  (Location) s.get("location2");
		leaveLocation = (Location) s.get("leaveLocation");
		this.speed = s.getInt("speed");
		this.animated = s.getBoolean("animated");
		
		FileFilter filter = new WildcardFileFilter(Arrays.asList("frame*.yml"));
		File[] files = frameFolder.listFiles(filter);
		for(File file : files){
			frames.add(new Frame(file));
		}
		Collections.sort(frames, new Comparator<Frame>(){

			@Override
			public int compare(Frame o1, Frame o2) {
				return Integer.compare(o1.getFrameNumber(), o2.getFrameNumber());
			}
			
		});
	}
	
	public void setLeaveLocation(Location location){
		this.leaveLocation = location;
	}
	
	public Location getLeaveLocation(){
		return leaveLocation;
	}
	
	public Frame getFrame(int id){
		for(Frame frame : this.getFrames()){
			if(frame.getFrameNumber() == id){
				return frame;
			}
		}
		return null;
	}
	
	public void animate(int times){
		if(!animating){
			animating = true;
			if(frames.get(0).getRideLocations().size() > 0){
				for(int id : frames.get(0).getSortedRideIDs()){
					Location l = frames.get(0).getRideLocation(id);
					ArmorStand stand = (ArmorStand) l.getWorld().spawnEntity(l, EntityType.ARMOR_STAND);
					stand.setMarker(true);
					stand.setVisible(false);
					stand.setInvulnerable(true);
					stand.setGravity(false);
					this.rides.put(stand, id);
				}
			}
			for(Player pl : this.playerBuffer){
				for(ArmorStand stand : rides.keySet()){
					if(stand.getPassengers() == null || stand.getPassengers().size() == 0){
						stand.addPassenger(pl);
					}
				}
			}
			Animation an = this;
			BukkitRunnable br = new BukkitRunnable(){
				
				int x = 0;
				
				@Override
				public void run() {
					if(x != times){
						Frame f = an.nextFrame();
						if(f.getFrameNumber() == 1){
							x++;
						}
					}else{
						an.stopAnimation();
					}
				}
				
			};
			br.runTaskTimer(RideableAnimations.getPlugin(), 0, speed);
			this.animationTaskID = br.getTaskId();
		}
	}
	
	public void animate(){
		this.animate(1);
	}
	
	public Frame newFrame(){
		List<SBlock> blocks = new ArrayList<SBlock>();
		for(int x = l1.getBlockX(); x != l2.getBlockX() + 1; x++){
			for(int y = l1.getBlockY(); y != l2.getBlockY() + 1; y++){
				for(int z = l1.getBlockZ(); z != l2.getBlockZ() + 1; z++){
					blocks.add(new SBlock(new Location(l1.getWorld(), x, y, z).getBlock()));
				}
			}
		}
		Frame frame = new Frame(blocks, this.frames.size() + 1, this);
		frame.save();
		return frame;
	}
	
	public void stopAnimation(){
		if(this.animationTaskID != -1){
			animating = false;
			Bukkit.getScheduler().cancelTask(animationTaskID);
			animationTaskID = -1;
			for(ArmorStand stand : rides.keySet()){
				if(stand.getPassengers() != null && stand.getPassengers().size() > 0){
					Entity en = stand.getPassengers().get(0);
					stand.removePassenger(en);
					en.teleport(this.getLeaveLocation());
					this.playerBuffer.clear();
				}
				stand.remove();
			}
		}
	}
	
	public Frame getCurrentFrame(){
		return frames.get(currentFrame - 1);
	}
	
	public Frame nextFrame(){
		if(frames.size() == currentFrame){
			currentFrame = 1;
		}else{
			currentFrame++;
		}
		for(ArmorStand stand : rides.keySet()){
			if(getCurrentFrame().getRideLocation(rides.get(stand)) != null){
				if(stand.getPassengers() != null && stand.getPassengers().size() > 0){
					Entity p = stand.getPassengers().get(0);
					stand.removePassenger(p);
					stand.teleport(getCurrentFrame().getRideLocation(rides.get(stand)));
					stand.addPassenger(p);
				}
			}
		}
		getCurrentFrame().loadFrame();
		return getCurrentFrame();
	}
	
	public int getAnimationSpeed(){
		return this.speed;
	}
	
	public boolean isAnimating(){
		return animationTaskID != -1;
	}
	
	public List<Frame> getFrames(){
		return frames;
	}
	
	public void setAnimationSpeed(int speed){
		this.speed = speed;
	}
	
	public boolean isAnimatedDefault(){
		return animated;
	}
	
	public void setAnimatedDefault(boolean animated){
		this.animated = animated;
	}
	
	public void addFrame(Frame frame){
		this.frames.add(frame);
	}
	
	public void save(){
		Animations.saveAnimation(this);
	}
	
	public String getName(){
		return name;
	}
	
	public Location getMinimumLocation(){
		return l1;
	}
	
	public Location getMaximumLocation(){
		return l2;
	}
	
	public void addPlayerToRide(Player p){
		this.playerBuffer.add(p);
	}
	
	public List<ArmorStand> getArmorStands(){
		return new ArrayList<ArmorStand>(rides.keySet());
	}
	
}
