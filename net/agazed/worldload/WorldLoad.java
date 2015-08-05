package net.agazed.worldload;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldLoad extends JavaPlugin {
	
	List<String> worldlist = this.getConfig().getStringList("worldlist");
	ArrayList<String> worldlistloaded = new ArrayList<String>();
	
	@Override
	public void onEnable() {
	getConfig().options().copyDefaults(true);
	for(String worlds : worldlist) {
		getLogger().info("Preparing level \"" + worlds + "\"");
		new WorldCreator(worlds).createWorld();
	}
}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			System.out.println("Command can only be run in-game.");
			return true;
		}
			Player player = (Player) sender;
				
				//WorldLoad Help
				
				if(cmd.getName().equalsIgnoreCase("worldload")) {
					if(args.length == 0 || args[0].equalsIgnoreCase("help")) {
						if(!player.hasPermission("worldload.help")) {
							player.sendMessage("§cNo permission");
							return true;
						}
							player.sendMessage("----- §3§lWorldLoad Help §f-----");
							player.sendMessage("§3/worldload §7help §f- Displays this page");
							player.sendMessage("§3/worldload §7tp <world> §f- Teleport to a world");
							player.sendMessage("§3/worldload §7create <world> [-flat] §f- Create a world");
							player.sendMessage("§3/worldload §7load <world> §f- Load a world for one time use");
							player.sendMessage("§3/worldload §7remove <world> §f- Remove a world");
							player.sendMessage("§3/worldload §7list §f- List your worlds");
							return true;
						}
					}
						
				//WorldLoad TP
						
				if(args[0].equalsIgnoreCase("tp")) {
					if(!player.hasPermission("worldload.tp")) {
						player.sendMessage("§cNo permission");
						return true;
					}
					if(args.length == 1) {
						player.sendMessage("§cCorrect usage: /worldload tp <world>");
						return true;
					}
					if(player.getServer().getWorld(args[1]) == null) {
						player.sendMessage("§cInvalid world name or world not loaded");
						return true;
					}
						World world = player.getServer().getWorld(args[1]);			
						Location a = new Location(world, 0, 0, 0);
						Location b = new Location(world, 0, world.getHighestBlockYAt(a), 0);
						player.teleport(b);
						player.sendMessage("§aTeleported to world \"" + args[1] + "\"");
						return true;
					}
				
				//WorldLoad Create
				
				if(args[0].equalsIgnoreCase("create")) {
					if(!player.hasPermission("worldload.create")) {
						player.sendMessage("§cNo permission");
						return true;
					}
					if(args.length == 1) {
						player.sendMessage("§cCorrect usage: /worldload create <world>");
						return true;
					}
					if(args.length == 2) {
						if(worldlist.contains(args[1])) {
							player.sendMessage("§cWorld already exists");
							return true;
						}
							player.sendMessage("§aPreparing level \"" + args[1] + "\"");
							worldlist.add(args[1]);
							getConfig().set("worldlist", worldlist);
							saveConfig();
							new WorldCreator(args[1]).createWorld();
							player.sendMessage("§aSuccessfully created world \"" + args[1] + "\"");
							return true;
						}
					if(args[2].equalsIgnoreCase("-flat")) {
						if(!player.hasPermission("worldload.create.flat")) {
							player.sendMessage("§cNo permission");
							return true;
						}
						if(worldlist.contains(args[1]) || getServer().getWorld(args[1])!=null) {
							player.sendMessage("§cWorld already exists");
							return true;
						}
							player.sendMessage("§aPreparing flat level \"" + args[1] + "\"");
							worldlist.add(args[1]);
							getConfig().set("worldlist", worldlist);
							saveConfig();
							new WorldCreator(args[1]).type(WorldType.FLAT).generateStructures(false).createWorld();
							player.sendMessage("§aSuccessfully created flat world \"" + args[1] + "\"");
							return true;
						}
					}
					
				//WorldLoad Load	
				   
				if(args[0].equalsIgnoreCase("load")) {
					if(!player.hasPermission("worldload.load")) {
						player.sendMessage("§cNo permission");
						return true;
					}
					if(args.length == 1) {
						player.sendMessage("§cCorrect usage: /worldload load <world>");
						return true;
					}
					if(worldlistloaded.contains(args[1])) {
						player.sendMessage("§cWorld already exists");
						return true;
					}
						worldlistloaded.add(args[1]);
						player.sendMessage("§aLoading level \"" + args[1] + "\"");
						new WorldCreator(args[1]).createWorld();
						player.sendMessage("§aSuccessfully loaded world \"" + args[1] + "\"");
						return true;
					}
					
				//WorldLoad Remove	
					
				if(args[0].equalsIgnoreCase("remove")) {
					if(!player.hasPermission("worldload.remove")) {
						player.sendMessage("§cNo permission");
						return true;
					}
					if(args.length == 1) {
						player.sendMessage("§cCorrect usage: /worldload remove <world>");
						return true;
					}
						worldlist.remove(args[1]);
						getConfig().set("worldlist", worldlist);
						saveConfig();
						player.sendMessage("§aSuccessfully removed world \"" + args[1] + "\"");
						return true;
					}
				
				//WorldLoad List
					
				if(args[0].equalsIgnoreCase("list")) {
					if(!player.hasPermission("worldload.list")) {
						player.sendMessage("§cNo permission");
						return true;
					}
					if(args.length == 1) {
						player.sendMessage("----- §3§lWorld List §f-----");
						player.sendMessage("§3Created: §7" + worldlist.toString());
						player.sendMessage("§3Loaded: §7" + worldlistloaded.toString());
						player.sendMessage("---------------------");
						return true;
					}
				}
		return true;
	}
}
