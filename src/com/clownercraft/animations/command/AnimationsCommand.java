package com.clownercraft.animations.command;

import java.io.File;
import java.io.IOException;

import net.md_5.bungee.api.ChatColor;

import org.apache.commons.io.FileUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.clownercraft.animations.RideableAnimations;
import com.clownercraft.animations.data.Animation;
import com.clownercraft.animations.data.Animations;
import com.clownercraft.animations.data.Frame;
import com.sk89q.worldedit.bukkit.selections.Selection;


public class AnimationsCommand implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] args) {
		
		if(sender.hasPermission("animations.admin")){
			if(sender instanceof Player){
				Player p = (Player) sender;
				Selection s = RideableAnimations.getWorldEdit().getSelection(p);
				Animation an = RideableAnimations.getEditing(p);
				if(args.length == 3){
					if(args[0].equalsIgnoreCase("setride")){
						if(an != null){
							try {
								int frame = Integer.parseInt(args[1]);
								int ride = Integer.parseInt(args[2]);
								if(an.getFrame(frame) != null){
									an.getFrame(frame).setRideLocation(ride, p.getLocation());
									an.getFrame(frame).save();
									p.sendMessage(ChatColor.GREEN + "Set ride location " + ride + " for frame " + frame + ".");
								}else{
									p.sendMessage(ChatColor.RED + "Frame not found!");
								}
							}catch(NumberFormatException e){
								p.sendMessage(ChatColor.RED + "Second and third arguments must be numbers!");
							}
						}else{
							p.sendMessage(ChatColor.RED + "Not editing an animation!");
						}
					}
				}else if(args.length == 2){
					if(args[0].equalsIgnoreCase("edit")){
						Animation anm = Animations.getAnimation(args[1]);
						if(anm != null){
							RideableAnimations.setEditing(p, anm);
							p.sendMessage(ChatColor.GREEN + "Now editing animation \"" + anm.getName() + "\".");
						}else{
							p.sendMessage(ChatColor.RED + "Animation not found!");
						}
					}else if(args[0].equalsIgnoreCase("create")){
						if(Animations.getAnimation(args[1]) == null){
							if(s != null){
								Animation anm = Animations.createAnimation(args[1], p);
								RideableAnimations.setEditing(p, anm);
								p.sendMessage(ChatColor.GREEN + "Created animation \"" + anm.getName() + "\".");
								p.sendMessage(ChatColor.GREEN + "Saved frame 1.");
								p.sendMessage(ChatColor.GREEN + "Now editing animation \"" + anm.getName() + "\".");
							}else{
								p.sendMessage(ChatColor.RED + "Make a selection first!");
							}
						}else{
							p.sendMessage(ChatColor.RED + "Animation already exists!");
						}
					}else if(args[0].equalsIgnoreCase("loadframe")){
						if(an != null){
							try {
								int num = Integer.parseInt(args[1]);
								if(an.getFrame(num) != null){
									an.getFrame(num).loadFrame();
									p.sendMessage(ChatColor.GREEN + "Loaded frame " + num + ".");
								}else{
									p.sendMessage(ChatColor.RED + "Frame not found!");
								}
							} catch(NumberFormatException e){
								p.sendMessage(ChatColor.RED + "Frame not found!");
							}
						}else{
							p.sendMessage(ChatColor.RED + "Not editing an animation!");
						}
					}else if(args[0].equals("delete")){
						Animation anm = Animations.getAnimation(args[1]);
						if(anm != null){
							Animations.getAnimationsData().set(anm.getName(), null);
							Animations.remove(anm);
							Animations.saveConfig();
							try {
								FileUtils.deleteDirectory(new File(RideableAnimations.getPlugin().getDataFolder(), anm.getName() + "/"));
							} catch (IOException e) {
								e.printStackTrace();
							}
							p.sendMessage(ChatColor.GREEN + "Successfully deleted animation \"" + anm.getName() + "\".");
							for(Player p2 : RideableAnimations.getEditingPlayers()){
								if(RideableAnimations.getEditing(p2).getName().equals(anm.getName())){
									RideableAnimations.setEditing(p2, null);
								}
							}
						}else{
							p.sendMessage(ChatColor.RED + "Animation does not exist.");
						}
					}else if(args[0].equalsIgnoreCase("setspeed")){
						if(an != null){
							try {
								int spd = Integer.parseInt(args[1]);
								an.setAnimationSpeed(spd);
								an.save();
								p.sendMessage(ChatColor.GREEN + "Set animation speed to " + spd + ".");
							} catch(NumberFormatException e){
								p.sendMessage(ChatColor.RED + "Number expected, got string.");
							}
						}else{
							p.sendMessage(ChatColor.RED + "Not editing an animation!");
						}
					}else{
						p.sendMessage(ChatColor.RED + "Invalid command usage, \"/animations help\" for usage.");
					}
				}else if(args.length == 1){
					if(args[0].equalsIgnoreCase("addframe")){
						if(an != null){
							Frame f = an.newFrame();
							p.sendMessage(ChatColor.GREEN + "Created frame " + f.getFrameNumber() + ".");
						}else{
							p.sendMessage(ChatColor.RED + "Not editing an animation!");
						}
					}else if(args[0].equalsIgnoreCase("animate")){
						if(an != null){
							an.animate();
							p.sendMessage(ChatColor.GREEN + "Animating \"" + an.getName() + "\" once.");
						}else{
							p.sendMessage(ChatColor.RED + "Not editing an animation!");
						}
					}else if(args[0].equals("leavelocation")){
						if(an != null){
							an.setLeaveLocation(p.getLocation());
							an.save();
							p.sendMessage(ChatColor.GREEN + "Set ride leave location for \"" + an.getName() + "\".");
						}else{
							p.sendMessage(ChatColor.RED + "Not editing an animation!");
						}
					}else if(args[0].equalsIgnoreCase("ride")){
						if(an != null){
							an.addPlayerToRide(p);
							p.sendMessage(ChatColor.GREEN + "Added to player buffer. Use /animation animate to start.");
						}else{
							p.sendMessage(ChatColor.RED + "Not editing an animation!");
						}
					}else if(args[0].equalsIgnoreCase("help")){
						
						p.sendMessage(ChatColor.GREEN + "/animations create <animation>");
						p.sendMessage(ChatColor.GREEN + "/animations edit <animation>");
						p.sendMessage(ChatColor.GREEN + "/animations delete <animation>");
						p.sendMessage(ChatColor.GREEN + "/animations loadframe <frame>");
						p.sendMessage(ChatColor.GREEN + "/animations addframe");
						p.sendMessage(ChatColor.GREEN + "/animations setspeed");
						p.sendMessage(ChatColor.GREEN + "/animations leavelocation");
						p.sendMessage(ChatColor.GREEN + "/animations animate");
						p.sendMessage(ChatColor.GREEN + "/animations ride");
						
					}else{
						p.sendMessage(ChatColor.RED + "Invalid command usage, \"/animations help\" for usage.");
					}
						
				}else{
					p.sendMessage(ChatColor.RED + "Invalid command usage, \"/animations help\" for usage.");
				}
			}else{
				sender.sendMessage(ChatColor.RED + "Player-only command!");
			}
		}else{
			sender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
		}
		
		return true;
	}

}
