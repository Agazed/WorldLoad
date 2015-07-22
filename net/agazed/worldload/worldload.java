package net.agazed.worldload;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class worldload extends JavaPlugin {
	
	@Override
	public void onEnable() {
	getConfig().options().copyDefaults(true);
	saveDefaultConfig();
	List<String> worldlist = this.getConfig().getStringList("worldlist");
	for(String worlds : worldlist)
	System.out.println("[WorldLoad] Preparing level \"" + worlds + "\"");
	for(String worlds : worldlist)
	new WorldCreator(worlds).createWorld();
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		Player player = (Player) sender;
		
	                        //WorldLoad Help
		
		        if(cmd.getName().equalsIgnoreCase("worldload") && player.isOp()){
			        if(args.length == 0){
				        player.sendMessage("----- §3§lWorldLoad Help §f-----");
				        player.sendMessage("§3/worldload §7tp <world> §f- Teleport to a world");
				        player.sendMessage("§3/worldload §7create <world> §f- Create a standard world");
				        player.sendMessage("§3/worldload §7load <world> §f- Load a world for one time use");
				        player.sendMessage("§3/worldload §7remove <world> §f- Remove a world");
				} else if(args[0].equalsIgnoreCase("help")){
					player.sendMessage("----- §3§lWorldLoad Help §f-----");
					player.sendMessage("§3/worldload §7tp <world> §f- Teleport to a world");
					player.sendMessage("§3/worldload §7create <world> §f- Create a standard world");
					player.sendMessage("§3/worldload §7load <world> §f- Load a world for one time use");
					player.sendMessage("§3/worldload §7remove <world> §f- Remove a world");
					    
			        //WorldLoad TP
					    
		                } else if(args[0].equalsIgnoreCase("tp")){
				    if(args.length == 1) {
				        player.sendMessage("/worldload tp <world>");
				        } else if(player.getServer().getWorld(args[1]) == null){
					          player.sendMessage("Invalid world name");
				    } else {
				    World world = player.getServer().getWorld(args[1]);			
				    Location a = new Location(world, 0, 0, 0);
				    Location b = new Location(world, 0, world.getHighestBlockYAt(a), 0);
				    player.teleport(b);
				    player.sendMessage("§aTeleported to world \"" + args[1] + "\"");
				    }
				    
				//WorldLoad Create
				    
				} else if(args[0].equalsIgnoreCase("create")){
				    if(args.length == 1) {
				        player.sendMessage("/worldload create <world>");
				} else {
				    player.sendMessage("§aPreparing level \"" + args[1] + "\"");
				    List<String> worldlist = this.getConfig().getStringList("worldlist");
				    worldlist.add(args[1]);
				    getConfig().set("worldlist", worldlist);
				    saveConfig();
				    new WorldCreator(args[1]).createWorld();
				    player.sendMessage("§aSuccessfully created world \"" + args[1] + "\"");
				    }
				    
				//WorldLoad Load    
				   
				} else if(args[0].equalsIgnoreCase("load")){
				    if(args.length == 1) {
				        player.sendMessage("/worldload load <world>");
				} else {
				    player.sendMessage("§aLoading level \"" + args[1] + "\"");
				    new WorldCreator(args[1]).createWorld();
				    player.sendMessage("§aSuccessfully loaded world \"" + args[1] + "\"");
					}
				    
				//WorldLoad Remove    
				    
				} else if(args[0].equalsIgnoreCase("remove")){
				    if(args.length == 1) {
				        player.sendMessage("/worldload remove <world>");
				} else {
				    List<String> worldlist = this.getConfig().getStringList("worldlist");
				    worldlist.remove(args[1]);
				    getConfig().set("worldlist", worldlist);
				    saveConfig();
				    player.sendMessage("§aSuccessfully removed world \"" + args[1] + "\"");
				}
			}
		}
				return false;
	}
}
