package net.agazed.worldload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldLoad extends JavaPlugin {

    List<String> worldlist = this.getConfig().getStringList("worldlist");
    List<String> worldlistloaded = new ArrayList<String>();

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        for (String worlds : worldlist) {
            getLogger().info("Preparing level \"" + worlds + "\"");
            new WorldCreator(worlds).createWorld();
        }
    }

    public boolean delete(File path) {
        if (path.exists()) {
            File files[] = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    delete(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            System.out.println("Command can only be run in-game.");
            return true;
        }
        Player player = (Player) sender;

        // WorldLoad Help

        if (cmd.getName().equalsIgnoreCase("worldload")) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                if (!player.hasPermission("worldload.help")) {
                    player.sendMessage("§cNo permission!");
                    return true;
                }
                player.sendMessage("----- §3§lWorldLoad Help §f-----");
                player.sendMessage("§3/worldload §7help §f- Displays this page");
                player.sendMessage("§3/worldload §7tp <world> §f- Teleport to a world");
                player.sendMessage("§3/worldload §7create <world> [-flat] §f- Create a world");
                player.sendMessage("§3/worldload §7remove <world> §f- Remove a world from the config");
                player.sendMessage("§3/worldload §7delete <world> §f- Permanently delete a world's files");
                player.sendMessage("§3/worldload §7load <world> §f- Load a world one time");
                player.sendMessage("§3/worldload §7unload <world> §f- Unload a world one time");
                player.sendMessage("§3/worldload §7stats §f- Get world statistics");
                player.sendMessage("§3/worldload §7list §f- List your worlds");
                return true;
            }
        }

        // WorldLoad TP

        if (args[0].equalsIgnoreCase("tp")) {
            if (!player.hasPermission("worldload.tp")) {
                player.sendMessage("§cNo permission!");
                return true;
            }
            if (args.length == 1) {
                player.sendMessage("§cCorrect usage: /worldload tp <world>");
                return true;
            }
            if (getServer().getWorld(args[1]) == null) {
                player.sendMessage("§cWorld does not exist!");
                return true;
            }
            Location loc = new Location(getServer().getWorld(args[1]), 0,
                    getServer().getWorld(args[1]).getHighestBlockYAt(0, 0), 0);
            player.teleport(loc);
            player.sendMessage("§aTeleported to world \"" + args[1] + "\"");
            return true;
        }

        // WorldLoad Create

        if (args[0].equalsIgnoreCase("create")) {
            if (!player.hasPermission("worldload.create")) {
                player.sendMessage("§cNo permission!");
                return true;
            }
            if (args.length == 1) {
                player.sendMessage("§cCorrect usage: /worldload create <world>");
                return true;
            }
            if (args.length == 2) {
                if (worldlist.contains(args[1]) || getServer().getWorld(args[1]) == getServer().getWorlds().get(0)
                        || getServer().getWorld(args[1]) == getServer().getWorlds().get(1)
                        || getServer().getWorld(args[1]) == getServer().getWorlds().get(2)) {
                    player.sendMessage("§cWorld already exists!");
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
            if (args[2].equalsIgnoreCase("-flat")) {
                if (!player.hasPermission("worldload.create.flat")) {
                    player.sendMessage("§cNo permission!");
                    return true;
                }
                if (worldlist.contains(args[1]) || getServer().getWorld(args[1]) == getServer().getWorlds().get(0)
                        || getServer().getWorld(args[1]) == getServer().getWorlds().get(1)
                        || getServer().getWorld(args[1]) == getServer().getWorlds().get(2)) {
                    player.sendMessage("§cWorld already exists!");
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

        // WorldLoad Remove

        if (args[0].equalsIgnoreCase("remove")) {
            if (!player.hasPermission("worldload.remove")) {
                player.sendMessage("§cNo permission!");
                return true;
            }
            if (args.length == 1) {
                player.sendMessage("§cCorrect usage: /worldload remove <world>");
                return true;
            }
            if (!worldlist.contains(args[1])) {
                player.sendMessage("§cWorld is not on the world list!");
                return true;
            }
            worldlist.remove(args[1]);
            getConfig().set("worldlist", worldlist);
            saveConfig();
            player.sendMessage("§aSuccessfully removed world \"" + args[1] + "\" from the world list.");
            return true;
        }

        // WorldLoad Delete

        if (args[0].equalsIgnoreCase("delete")) {
            if (!player.hasPermission("worldload.delete")) {
                player.sendMessage("§cNo permission!");
                return true;
            }
            if (args.length == 1) {
                player.sendMessage("§cCorrect usage: /worldload delete <world>");
                return true;
            }
            if (getServer().getWorld(args[1]) == null) {
                player.sendMessage("§cWorld does not exist!");
                return true;
            }
            if (getServer().getWorld(args[1]) == getServer().getWorlds().get(0)
                    || getServer().getWorld(args[1]) == getServer().getWorlds().get(1)
                    || getServer().getWorld(args[1]) == getServer().getWorlds().get(2)) {
                player.sendMessage("§cCannot delete a main world!");
                return true;
            }
            if (getServer().getWorld(args[1]).getPlayers().size() > 0) {
                player.sendMessage("§cCannot delete a world with players inside!");
                return true;
            }
            if (worldlist.contains(args[1])) {
                worldlist.remove(args[1]);
                getConfig().set("worldlist", worldlist);
                saveConfig();
            }
            if (worldlistloaded.contains(args[1])) {
                worldlistloaded.remove(args[1]);
            }
            File world = getServer().getWorld(args[1]).getWorldFolder();
            getServer().unloadWorld(args[1], true);
            delete(world);
            player.sendMessage("§aSuccessfully deleted world \"" + args[1] + "\"");
            return true;
        }

        // WorldLoad Load

        if (args[0].equalsIgnoreCase("load")) {
            if (!player.hasPermission("worldload.load")) {
                player.sendMessage("§cNo permission!");
                return true;
            }
            if (args.length == 1) {
                player.sendMessage("§cCorrect usage: /worldload load <world>");
                return true;
            }
            if (worldlistloaded.contains(args[1]) || worldlist.contains(args[1])
                    || getServer().getWorld(args[1]) == getServer().getWorlds().get(0)
                    || getServer().getWorld(args[1]) == getServer().getWorlds().get(1)
                    || getServer().getWorld(args[1]) == getServer().getWorlds().get(2)) {
                player.sendMessage("§cWorld already exists!");
                return true;
            }
            worldlistloaded.add(args[1]);
            player.sendMessage("§aLoading level \"" + args[1] + "\"");
            new WorldCreator(args[1]).createWorld();
            player.sendMessage("§aSuccessfully loaded world \"" + args[1] + "\"");
            return true;
        }

        // WorldLoad Unload

        if (args[0].equalsIgnoreCase("unload")) {
            if (!player.hasPermission("worldload.unload")) {
                player.sendMessage("§cNo permission!");
                return true;
            }
            if (args.length == 1) {
                player.sendMessage("§cCorrect usage: /worldload unload <world>");
                return true;
            }
            if (getServer().getWorld(args[1]) == null) {
                player.sendMessage("§cWorld does not exist!");
                return true;
            }
            if (getServer().getWorld(args[1]) == getServer().getWorlds().get(0)
                    || getServer().getWorld(args[1]) == getServer().getWorlds().get(1)
                    || getServer().getWorld(args[1]) == getServer().getWorlds().get(2)) {
                player.sendMessage("§cCannot unload a main world!");
                return true;
            }
            if (getServer().getWorld(args[1]).getPlayers().size() > 0) {
                player.sendMessage("§cCannot unload a world with players inside!");
                return true;
            }
            if (worldlistloaded.contains(args[1])) {
                worldlistloaded.remove(args[1]);
            }
            getServer().unloadWorld(args[1], true);
            player.sendMessage("§aSuccessfully unloaded world \"" + args[1] + "\"");
            return true;
        }

        // WorldLoad Status

        if (args[0].equalsIgnoreCase("stats")) {
            if (!player.hasPermission("worldload.stats")) {
                player.sendMessage("§cNo permission!");
                return true;
            }
            if (args.length == 1) {
                int entities = player.getWorld().getEntities().size() - player.getWorld().getPlayers().size();
                player.sendMessage("----- §3§lCurrent World Statistics §f-----");
                player.sendMessage("§7World: §3" + player.getWorld().getName());
                player.sendMessage("§7World Type: §3" + player.getWorld().getWorldType().getName());
                player.sendMessage("§7Environment: §3" + player.getWorld().getEnvironment().toString());
                player.sendMessage("§7Biome: §3" + player.getWorld().getBiome(player.getLocation().getBlockX(),
                        player.getLocation().getBlockZ()));
                player.sendMessage("§7Seed: §3" + player.getWorld().getSeed());
                player.sendMessage("§7Difficulty: §3" + player.getWorld().getDifficulty().toString());
                player.sendMessage("§7Entities: §3" + entities);
                player.sendMessage("§7Players: §3" + player.getWorld().getPlayers().size());
                player.sendMessage("§7Time: §3" + player.getWorld().getTime());
                player.sendMessage("§7Max Height: §3" + player.getWorld().getMaxHeight());
                player.sendMessage("§7Generate Structures: §3" + player.getWorld().canGenerateStructures());
                player.sendMessage("§7World Border: §3" + player.getWorld().getWorldBorder().getSize());
                player.sendMessage("§7Spawn: §3" + player.getWorld().getSpawnLocation().getBlockX() + " / "
                        + player.getWorld().getSpawnLocation().getBlockY() + " / "
                        + player.getWorld().getSpawnLocation().getBlockZ());
                player.sendMessage("§7Coordinates: §3" + player.getLocation().getBlockX() + " / "
                        + player.getLocation().getBlockY() + " / " + player.getLocation().getBlockZ());
                return true;
            }
        }

        // WorldLoad List

        if (args[0].equalsIgnoreCase("list")) {
            if (!player.hasPermission("worldload.list")) {
                player.sendMessage("§cNo permission!");
                return true;
            }
            if (args.length == 1) {
                player.sendMessage("----- §3§lWorld List §f-----");
                player.sendMessage("§3Created: §7" + worldlist.toString());
                player.sendMessage("§3Loaded: §7" + worldlistloaded.toString());
                player.sendMessage("---------------------");
                return true;
            }
        }
        player.sendMessage("§cUnknown argument!");
        return true;
    }
}