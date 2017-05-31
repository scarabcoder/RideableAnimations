package com.clownercraft.animations;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.clownercraft.animations.command.AnimationsCommand;
import com.clownercraft.animations.data.Animation;
import com.clownercraft.animations.data.Animations;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class RideableAnimations extends JavaPlugin {
	
	private static Plugin plugin;
	private static WorldEditPlugin we;
	private static HashMap<UUID, Animation> editors = new HashMap<UUID, Animation>();
	
	@Override
	public void onEnable(){
		
		if(!this.getDataFolder().exists()) this.getDataFolder().mkdirs();
		
		RideableAnimations.plugin = this;
		
		this.getCommand("animations").setExecutor(new AnimationsCommand());
		Animations.initConfig(new File(this.getDataFolder(), "blocks.yml"));
		
		we = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		
		
		this.initConfigs();
		this.initListeners();
		this.initCommands();
		
	}
	
	public static List<Player> getEditingPlayers(){
		List<Player> players = new ArrayList<Player>();
		for(UUID id : editors.keySet()){
			players.add(Bukkit.getPlayer(id));
		}
		return players;
	}
	
	public static Animation getEditing(Player p){
		return editors.get(p.getUniqueId());
	}
	public static void setEditing(Player p, Animation an){
		editors.put(p.getUniqueId(), an);
	}
	
	public static WorldEditPlugin getWorldEdit(){
		return we;
	}
	
	public static Plugin getPlugin(){
		return plugin;
	}
	
	private void initCommands(){
		
	}
	
	private void initConfigs(){
		
	}
	
	private void initListeners(){
		
	}
	
}
