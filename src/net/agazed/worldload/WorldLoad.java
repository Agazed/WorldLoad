package net.agazed.worldload;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldLoad extends JavaPlugin {

    List<String> worldlist = this.getConfig().getStringList("worldlist");
    List<String> flatworldlist = this.getConfig().getStringList("flatworldlist");
    List<String> worldlistloaded = new ArrayList<String>();

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        for (String worlds : worldlist) {
            getLogger().info("Preparing level \"" + worlds + "\"");
            new WorldCreator(worlds).createWorld();
        }
        for (String flatworlds : flatworldlist) {
            getLogger().info("Preparing flat level \"" + flatworlds + "\"");
            new WorldCreator(flatworlds).type(WorldType.FLAT).generateStructures(false).createWorld();
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
                    player.sendMessage(ChatColor.RED + "No permission!");
                    return true;
                }
                player.sendMessage("----- " + ChatColor.DARK_AQUA + ChatColor.BOLD + "WorldLoad Help " + ChatColor.WHITE
                        + "-----");
                player.sendMessage(ChatColor.DARK_AQUA + "/worldload " + ChatColor.GRAY + "help " + ChatColor.WHITE
                        + "- Displays this page");
                player.sendMessage(ChatColor.DARK_AQUA + "/worldload " + ChatColor.GRAY + "tp <world> "
                        + ChatColor.WHITE + "- Teleport to a world");
                player.sendMessage(ChatColor.DARK_AQUA + "/worldload " + ChatColor.GRAY + "create <world> [-flat] "
                        + ChatColor.WHITE + "- Create a world");
                player.sendMessage(ChatColor.DARK_AQUA + "/worldload " + ChatColor.GRAY + "remove <world> "
                        + ChatColor.WHITE + "- Remove a world from the config");
                player.sendMessage(ChatColor.DARK_AQUA + "/worldload " + ChatColor.GRAY + "delete <world> "
                        + ChatColor.WHITE + "- Permanently delete a world's files");
                player.sendMessage(ChatColor.DARK_AQUA + "/worldload " + ChatColor.GRAY + "load <world> "
                        + ChatColor.WHITE + "- Load a world one time");
                player.sendMessage(ChatColor.DARK_AQUA + "/worldload " + ChatColor.GRAY + "unload <world> "
                        + ChatColor.WHITE + "- Unload a world one time");
                player.sendMessage(ChatColor.DARK_AQUA + "/worldload " + ChatColor.GRAY + "stats " + ChatColor.WHITE
                        + "- Get world statistics");
                player.sendMessage(ChatColor.DARK_AQUA + "/worldload " + ChatColor.GRAY + "list " + ChatColor.WHITE
                        + "- List your worlds");
                return true;
            }
        }

        // WorldLoad TP

        if (args[0].equalsIgnoreCase("tp")) {
            if (!player.hasPermission("worldload.tp")) {
                player.sendMessage(ChatColor.RED + "No permission!");
                return true;
            }
            if (args.length == 1) {
                player.sendMessage(ChatColor.RED + "Correct usage: /worldload tp <world>");
                return true;
            }
            if (getServer().getWorld(args[1]) == null) {
                player.sendMessage(ChatColor.RED + "World does not exist!");
                return true;
            }
            Location loc = new Location(getServer().getWorld(args[1]), 0,
                    getServer().getWorld(args[1]).getHighestBlockYAt(0, 0), 0);
            player.teleport(loc);
            player.sendMessage(ChatColor.GREEN + "Teleported to world \"" + args[1] + "\"");
            return true;
        }

        // WorldLoad Create

        if (args[0].equalsIgnoreCase("create")) {
            if (!player.hasPermission("worldload.create")) {
                player.sendMessage(ChatColor.RED + "No permission!");
                return true;
            }
            if (args.length == 1) {
                player.sendMessage(ChatColor.RED + "Correct usage: /worldload create <world>");
                return true;
            }
            if (args.length == 2) {
                if (flatworldlist.contains(args[1]) || worldlist.contains(args[1])
                        || getServer().getWorld(args[1]) == getServer().getWorlds().get(0)
                        || getServer().getWorld(args[1]) == getServer().getWorlds().get(1)
                        || getServer().getWorld(args[1]) == getServer().getWorlds().get(2)) {
                    player.sendMessage(ChatColor.RED + "World already exists!");
                    return true;
                }
                player.sendMessage(ChatColor.GREEN + "Preparing level \"" + args[1] + "\"");
                worldlist.add(args[1]);
                getConfig().set("worldlist", worldlist);
                saveConfig();
                new WorldCreator(args[1]).createWorld();
                player.sendMessage(ChatColor.GREEN + "Successfully created world \"" + args[1] + "\"");
                return true;
            }
            if (args[2].equalsIgnoreCase("-flat")) {
                if (!player.hasPermission("worldload.create.flat")) {
                    player.sendMessage(ChatColor.RED + "No permission!");
                    return true;
                }
                if (flatworldlist.contains(args[1]) || worldlist.contains(args[1])
                        || getServer().getWorld(args[1]) == getServer().getWorlds().get(0)
                        || getServer().getWorld(args[1]) == getServer().getWorlds().get(1)
                        || getServer().getWorld(args[1]) == getServer().getWorlds().get(2)) {
                    player.sendMessage(ChatColor.RED + "World already exists!");
                    return true;
                }
                player.sendMessage(ChatColor.GREEN + "Preparing flat level \"" + args[1] + "\"");
                flatworldlist.add(args[1]);
                getConfig().set("flatworldlist", flatworldlist);
                saveConfig();
                new WorldCreator(args[1]).type(WorldType.FLAT).generateStructures(false).createWorld();
                player.sendMessage(ChatColor.GREEN + "Successfully created flat world \"" + args[1] + "\"");
                return true;
            }
        }

        // WorldLoad Remove

        if (args[0].equalsIgnoreCase("remove")) {
            if (!player.hasPermission("worldload.remove")) {
                player.sendMessage(ChatColor.RED + "No permission!");
                return true;
            }
            if (args.length == 1) {
                player.sendMessage(ChatColor.RED + "Correct usage: /worldload remove <world>");
                return true;
            }
            if (!worldlist.contains(args[1]) || !flatworldlist.contains(args[1])) {
                player.sendMessage(ChatColor.RED + "World is not on the world list!");
                return true;
            }
            if (worldlist.contains(args[1])) {
                worldlist.remove(args[1]);
                getConfig().set("worldlist", worldlist);
                saveConfig();
                player.sendMessage(
                        ChatColor.GREEN + "Successfully removed world \"" + args[1] + "\" from the world list.");
                return true;
            }
            if (flatworldlist.contains(args[1])) {
                flatworldlist.remove(args[1]);
                getConfig().set("flatworldlist", flatworldlist);
                saveConfig();
                player.sendMessage(ChatColor.GREEN + "Successfully removed flat world \"" + args[1]
                        + "\" from the flat world list.");
                return true;
            }
        }

        // WorldLoad Delete

        if (args[0].equalsIgnoreCase("delete")) {
            if (!player.hasPermission("worldload.delete")) {
                player.sendMessage(ChatColor.RED + "No permission!");
                return true;
            }
            if (args.length == 1) {
                player.sendMessage(ChatColor.RED + "Correct usage: /worldload delete <world>");
                return true;
            }
            if (getServer().getWorld(args[1]) == null) {
                player.sendMessage(ChatColor.RED + "World does not exist!");
                return true;
            }
            if (getServer().getWorld(args[1]) == getServer().getWorlds().get(0)
                    || getServer().getWorld(args[1]) == getServer().getWorlds().get(1)
                    || getServer().getWorld(args[1]) == getServer().getWorlds().get(2)) {
                player.sendMessage(ChatColor.RED + "Cannot delete a main world!");
                return true;
            }
            if (getServer().getWorld(args[1]).getPlayers().size() > 0) {
                player.sendMessage(ChatColor.RED + "Cannot delete a world with players inside!");
                return true;
            }
            if (worldlist.contains(args[1])) {
                worldlist.remove(args[1]);
                getConfig().set("worldlist", worldlist);
                saveConfig();
            }
            if (flatworldlist.contains(args[1])) {
                flatworldlist.remove(args[1]);
                getConfig().set("flatworldlist", flatworldlist);
                saveConfig();
            }
            if (worldlistloaded.contains(args[1])) {
                worldlistloaded.remove(args[1]);
            }
            File world = getServer().getWorld(args[1]).getWorldFolder();
            getServer().unloadWorld(args[1], true);
            delete(world);
            player.sendMessage(ChatColor.GREEN + "Successfully deleted world \"" + args[1] + "\"");
            return true;
        }

        // WorldLoad Load

        if (args[0].equalsIgnoreCase("load")) {
            if (!player.hasPermission("worldload.load")) {
                player.sendMessage(ChatColor.RED + "No permission!");
                return true;
            }
            if (args.length == 1) {
                player.sendMessage(ChatColor.RED + "Correct usage: /worldload load <world>");
                return true;
            }
            if (worldlist.contains(args[1]) || flatworldlist.contains(args[1]) || worldlistloaded.contains(args[1])
                    || getServer().getWorld(args[1]) == getServer().getWorlds().get(0)
                    || getServer().getWorld(args[1]) == getServer().getWorlds().get(1)
                    || getServer().getWorld(args[1]) == getServer().getWorlds().get(2)) {
                player.sendMessage(ChatColor.RED + "World already exists!");
                return true;
            }
            worldlistloaded.add(args[1]);
            player.sendMessage(ChatColor.GREEN + "Loading level \"" + args[1] + "\"");
            new WorldCreator(args[1]).createWorld();
            player.sendMessage(ChatColor.GREEN + "Successfully loaded world \"" + args[1] + "\"");
            return true;
        }

        // WorldLoad Unload

        if (args[0].equalsIgnoreCase("unload")) {
            if (!player.hasPermission("worldload.unload")) {
                player.sendMessage(ChatColor.RED + "No permission!");
                return true;
            }
            if (args.length == 1) {
                player.sendMessage(ChatColor.RED + "Correct usage: /worldload unload <world>");
                return true;
            }
            if (getServer().getWorld(args[1]) == null) {
                player.sendMessage(ChatColor.RED + "World does not exist!");
                return true;
            }
            if (getServer().getWorld(args[1]) == getServer().getWorlds().get(0)
                    || getServer().getWorld(args[1]) == getServer().getWorlds().get(1)
                    || getServer().getWorld(args[1]) == getServer().getWorlds().get(2)) {
                player.sendMessage(ChatColor.RED + "Cannot unload a main world!");
                return true;
            }
            if (getServer().getWorld(args[1]).getPlayers().size() > 0) {
                player.sendMessage(ChatColor.RED + "Cannot unload a world with players inside!");
                return true;
            }
            if (worldlistloaded.contains(args[1])) {
                worldlistloaded.remove(args[1]);
            }
            getServer().unloadWorld(args[1], true);
            player.sendMessage(ChatColor.GREEN + "Successfully unloaded world \"" + args[1] + "\"");
            return true;
        }

        // WorldLoad Stats

        if (args[0].equalsIgnoreCase("stats")) {
            if (!player.hasPermission("worldload.stats")) {
                player.sendMessage(ChatColor.RED + "No permission!");
                return true;
            }
            if (args.length == 1) {
                int entities = player.getWorld().getEntities().size() - player.getWorld().getPlayers().size();
                player.sendMessage("----- " + ChatColor.DARK_AQUA + ChatColor.BOLD + "Current World Statistics "
                        + ChatColor.WHITE + "-----");
                player.sendMessage(ChatColor.GRAY + "World: " + ChatColor.DARK_AQUA + player.getWorld().getName());
                player.sendMessage(ChatColor.GRAY + "World Type: " + ChatColor.DARK_AQUA
                        + player.getWorld().getWorldType().getName());
                player.sendMessage(ChatColor.GRAY + "Environment: " + ChatColor.DARK_AQUA
                        + player.getWorld().getEnvironment().toString());
                player.sendMessage(ChatColor.GRAY + "Biome: " + ChatColor.DARK_AQUA + player.getWorld()
                        .getBiome(player.getLocation().getBlockX(), player.getLocation().getBlockZ()));
                player.sendMessage(ChatColor.GRAY + "Seed: " + ChatColor.DARK_AQUA + player.getWorld().getSeed());
                player.sendMessage(ChatColor.GRAY + "Difficulty: " + ChatColor.DARK_AQUA
                        + player.getWorld().getDifficulty().toString());
                player.sendMessage(ChatColor.GRAY + "Entities: " + ChatColor.DARK_AQUA + entities);
                player.sendMessage(
                        ChatColor.GRAY + "Players: " + ChatColor.DARK_AQUA + player.getWorld().getPlayers().size());
                player.sendMessage(ChatColor.GRAY + "Time: " + ChatColor.DARK_AQUA + player.getWorld().getTime());
                player.sendMessage(
                        ChatColor.GRAY + "Max Height: " + ChatColor.DARK_AQUA + player.getWorld().getMaxHeight());
                player.sendMessage(ChatColor.GRAY + "Generate Structures: " + ChatColor.DARK_AQUA
                        + player.getWorld().canGenerateStructures());
                player.sendMessage(ChatColor.GRAY + "World Border: " + ChatColor.DARK_AQUA
                        + player.getWorld().getWorldBorder().getSize());
                player.sendMessage(ChatColor.GRAY + "Spawn: " + ChatColor.DARK_AQUA
                        + player.getWorld().getSpawnLocation().getBlockX() + " / "
                        + player.getWorld().getSpawnLocation().getBlockY() + " / "
                        + player.getWorld().getSpawnLocation().getBlockZ());
                player.sendMessage(
                        ChatColor.GRAY + "Coordinates: " + ChatColor.DARK_AQUA + player.getLocation().getBlockX()
                                + " / " + player.getLocation().getBlockY() + " / " + player.getLocation().getBlockZ());
                return true;
            }
        }

        // WorldLoad List

        if (args[0].equalsIgnoreCase("list")) {
            if (!player.hasPermission("worldload.list")) {
                player.sendMessage(ChatColor.RED + "No permission!");
                return true;
            }
            if (args.length == 1) {
                player.sendMessage(
                        "----- " + ChatColor.DARK_AQUA + ChatColor.BOLD + "World List " + ChatColor.WHITE + "-----");
                player.sendMessage(ChatColor.DARK_AQUA + "Normal: " + ChatColor.GRAY + worldlist.toString());
                player.sendMessage(ChatColor.DARK_AQUA + "Flat: " + ChatColor.GRAY + flatworldlist.toString());
                player.sendMessage(ChatColor.DARK_AQUA + "Loaded: " + ChatColor.GRAY + worldlistloaded.toString());
                return true;
            }
        }
        player.sendMessage(ChatColor.RED + "Unknown argument!");
        return true;
    }
}
