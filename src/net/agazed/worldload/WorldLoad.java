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
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldLoad extends JavaPlugin {

    List<String> worldlist = getConfig().getStringList("worldlist");
    List<String> flatworldlist = getConfig().getStringList("flatworldlist");
    List<String> worldlistloaded = new ArrayList<String>();
    List<String> worldlistunloaded = new ArrayList<String>();

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        for (String world : worldlist) {
            getLogger().info("Preparing level \"" + world + "\"");
            new WorldCreator(world).createWorld();
        }
        for (String flatworld : flatworldlist) {
            getLogger().info("Preparing flat level \"" + flatworld + "\"");
            new WorldCreator(flatworld).type(WorldType.FLAT)
                    .generateStructures(getConfig().getBoolean("generateStructuresInFlatWorlds")).createWorld();
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

        // WorldLoad Help

        if (cmd.getName().equalsIgnoreCase("worldload")) {
            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                if (!sender.hasPermission("worldload.help")) {
                    sender.sendMessage(ChatColor.RED + "No permission!");
                    return true;
                }
                sender.sendMessage("----- " + ChatColor.DARK_AQUA + ChatColor.BOLD + "WorldLoad Help " + ChatColor.WHITE
                        + "-----");
                sender.sendMessage(ChatColor.DARK_AQUA + "/worldload " + ChatColor.GRAY + "help " + ChatColor.WHITE
                        + "- Displays this page");
                sender.sendMessage(ChatColor.DARK_AQUA + "/worldload " + ChatColor.GRAY + "tp <world> "
                        + ChatColor.WHITE + "- Teleport to a world");
                sender.sendMessage(ChatColor.DARK_AQUA + "/worldload " + ChatColor.GRAY + "create <world> [-flat] "
                        + ChatColor.WHITE + "- Create a world");
                sender.sendMessage(ChatColor.DARK_AQUA + "/worldload " + ChatColor.GRAY + "remove <world> "
                        + ChatColor.WHITE + "- Remove a world from the config");
                sender.sendMessage(ChatColor.DARK_AQUA + "/worldload " + ChatColor.GRAY + "delete <world> "
                        + ChatColor.WHITE + "- Permanently delete a world's files");
                sender.sendMessage(ChatColor.DARK_AQUA + "/worldload " + ChatColor.GRAY + "load <world> "
                        + ChatColor.WHITE + "- Load a world one time");
                sender.sendMessage(ChatColor.DARK_AQUA + "/worldload " + ChatColor.GRAY + "unload <world> "
                        + ChatColor.WHITE + "- Unload a world one time");
                sender.sendMessage(ChatColor.DARK_AQUA + "/worldload " + ChatColor.GRAY + "stats " + ChatColor.WHITE
                        + "- Get world statistics");
                sender.sendMessage(ChatColor.DARK_AQUA + "/worldload " + ChatColor.GRAY + "list " + ChatColor.WHITE
                        + "- List your worlds");
                sender.sendMessage(ChatColor.DARK_AQUA + "/worldload " + ChatColor.GRAY + "reload " + ChatColor.WHITE
                        + "- Reload the config");
                return true;
            }
        }

        // WorldLoad TP

        if (args[0].equalsIgnoreCase("tp")) {
            if (sender instanceof ConsoleCommandSender) {
                getServer().getConsoleSender().sendMessage(ChatColor.RED + "Command can only be run as a player!");
                return true;
            }
            if (!sender.hasPermission("worldload.tp")) {
                sender.sendMessage(ChatColor.RED + "No permission!");
                return true;
            }
            if (args.length == 1) {
                sender.sendMessage(ChatColor.RED + "Correct usage: /worldload tp <world>");
                return true;
            }
            if (getServer().getWorld(args[1]) == null) {
                sender.sendMessage(ChatColor.RED + "World does not exist!");
                return true;
            }
            Player player = (Player) sender;
            Location loc = new Location(getServer().getWorld(args[1]), 0,
                    getServer().getWorld(args[1]).getHighestBlockYAt(0, 0), 0);
            player.teleport(loc);
            sender.sendMessage(ChatColor.GREEN + "Teleported to world \"" + args[1] + "\"");
            return true;
        }

        // WorldLoad Create

        if (args[0].equalsIgnoreCase("create")) {
            if (!sender.hasPermission("worldload.create")) {
                sender.sendMessage(ChatColor.RED + "No permission!");
                return true;
            }
            if (args.length == 1) {
                sender.sendMessage(ChatColor.RED + "Correct usage: /worldload create <world>");
                return true;
            }
            if (args.length == 2) {
                if (flatworldlist.contains(args[1]) || worldlist.contains(args[1])
                        || getServer().getWorld(args[1]) == getServer().getWorlds().get(0)
                        || getServer().getWorld(args[1]) == getServer().getWorlds().get(1)
                        || getServer().getWorld(args[1]) == getServer().getWorlds().get(2)) {
                    sender.sendMessage(ChatColor.RED + "World already exists!");
                    return true;
                }
                if (worldlistloaded.contains(args[1])) {
                    worldlistloaded.remove(args[1]);
                }
                if (worldlistunloaded.contains(args[1])) {
                    worldlistunloaded.remove(args[1]);
                }
                sender.sendMessage(ChatColor.GREEN + "Preparing level \"" + args[1] + "\"");
                worldlist.add(args[1]);
                getConfig().set("worldlist", worldlist);
                saveConfig();
                new WorldCreator(args[1]).createWorld();
                sender.sendMessage(ChatColor.GREEN + "Successfully created world \"" + args[1] + "\"");
                return true;
            }
            if (args[2].equalsIgnoreCase("-flat")) {
                if (!sender.hasPermission("worldload.create.flat")) {
                    sender.sendMessage(ChatColor.RED + "No permission!");
                    return true;
                }
                if (flatworldlist.contains(args[1]) || worldlist.contains(args[1])
                        || getServer().getWorld(args[1]) == getServer().getWorlds().get(0)
                        || getServer().getWorld(args[1]) == getServer().getWorlds().get(1)
                        || getServer().getWorld(args[1]) == getServer().getWorlds().get(2)) {
                    sender.sendMessage(ChatColor.RED + "World already exists!");
                    return true;
                }
                if (worldlistloaded.contains(args[1])) {
                    worldlistloaded.remove(args[1]);
                }
                if (worldlistunloaded.contains(args[1])) {
                    worldlistunloaded.remove(args[1]);
                }
                sender.sendMessage(ChatColor.GREEN + "Preparing flat level \"" + args[1] + "\"");
                flatworldlist.add(args[1]);
                getConfig().set("flatworldlist", flatworldlist);
                saveConfig();
                new WorldCreator(args[1]).type(WorldType.FLAT)
                        .generateStructures(getConfig().getBoolean("generateStructuresInFlatWorlds")).createWorld();
                sender.sendMessage(ChatColor.GREEN + "Successfully created flat world \"" + args[1] + "\"");
                return true;
            }
        }

        // WorldLoad Remove

        if (args[0].equalsIgnoreCase("remove")) {
            if (!sender.hasPermission("worldload.remove")) {
                sender.sendMessage(ChatColor.RED + "No permission!");
                return true;
            }
            if (args.length == 1) {
                sender.sendMessage(ChatColor.RED + "Correct usage: /worldload remove <world>");
                return true;
            }
            if (!worldlist.contains(args[1]) && !flatworldlist.contains(args[1])) {
                sender.sendMessage(ChatColor.RED + "World is not on the world list!");
                return true;
            }
            if (worldlist.contains(args[1])) {
                worldlist.remove(args[1]);
                getConfig().set("worldlist", worldlist);
                saveConfig();
                if (getServer().getWorld(args[1]) == null) {
                    sender.sendMessage(ChatColor.GREEN + "Successfully removed unloaded world \"" + args[1]
                            + "\" from the world list.");
                    return true;
                }
                worldlistloaded.add(args[1]);
                sender.sendMessage(
                        ChatColor.GREEN + "Successfully removed world \"" + args[1] + "\" from the world list.");
                return true;
            }
            if (flatworldlist.contains(args[1])) {
                flatworldlist.remove(args[1]);
                getConfig().set("flatworldlist", flatworldlist);
                saveConfig();
                if (getServer().getWorld(args[1]) == null) {
                    sender.sendMessage(ChatColor.GREEN + "Successfully removed unloaded flat world \"" + args[1]
                            + "\" from the flat world list.");
                    return true;
                }
                worldlistloaded.add(args[1]);
                sender.sendMessage(ChatColor.GREEN + "Successfully removed flat world \"" + args[1]
                        + "\" from the flat world list.");
                return true;
            }
        }

        // WorldLoad Delete

        if (args[0].equalsIgnoreCase("delete")) {
            if (!sender.hasPermission("worldload.delete")) {
                sender.sendMessage(ChatColor.RED + "No permission!");
                return true;
            }
            if (args.length == 1) {
                sender.sendMessage(ChatColor.RED + "Correct usage: /worldload delete <world>");
                return true;
            }
            if (getServer().getWorld(args[1]) == null) {
                File unloaded = new File(args[1]);
                if (!unloaded.exists()) {
                    sender.sendMessage(ChatColor.RED + "World does not exist!");
                    return true;
                }
                if (worldlistunloaded.contains(args[1])) {
                    worldlistunloaded.remove(args[1]);
                }
                delete(unloaded);
                sender.sendMessage(ChatColor.GREEN + "Successfully deleted unloaded world \"" + args[1] + "\"");
                return true;
            }
            if (getServer().getWorld(args[1]) == getServer().getWorlds().get(0)
                    || getServer().getWorld(args[1]) == getServer().getWorlds().get(1)
                    || getServer().getWorld(args[1]) == getServer().getWorlds().get(2)) {
                sender.sendMessage(ChatColor.RED + "Cannot delete a main world!");
                return true;
            }
            if (getServer().getWorld(args[1]).getPlayers().size() > 0) {
                sender.sendMessage(ChatColor.RED + "Cannot delete a world with players inside!");
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
            if (worldlistunloaded.contains(args[1])) {
                worldlistunloaded.remove(args[1]);
            }
            File world = getServer().getWorld(args[1]).getWorldFolder();
            getServer().unloadWorld(args[1], true);
            delete(world);
            sender.sendMessage(ChatColor.GREEN + "Successfully deleted world \"" + args[1] + "\"");
            return true;
        }

        // WorldLoad Load

        if (args[0].equalsIgnoreCase("load")) {
            if (!sender.hasPermission("worldload.load")) {
                sender.sendMessage(ChatColor.RED + "No permission!");
                return true;
            }
            if (args.length == 1) {
                sender.sendMessage(ChatColor.RED + "Correct usage: /worldload load <world>");
                return true;
            }
            if (worldlist.contains(args[1]) || flatworldlist.contains(args[1]) || worldlistloaded.contains(args[1])
                    || getServer().getWorld(args[1]) == getServer().getWorlds().get(0)
                    || getServer().getWorld(args[1]) == getServer().getWorlds().get(1)
                    || getServer().getWorld(args[1]) == getServer().getWorlds().get(2)) {
                sender.sendMessage(ChatColor.RED + "World already exists!");
                return true;
            }
            if (worldlistunloaded.contains(args[1])) {
                worldlistunloaded.remove(args[1]);
            }
            worldlistloaded.add(args[1]);
            sender.sendMessage(ChatColor.GREEN + "Loading level \"" + args[1] + "\"");
            new WorldCreator(args[1]).createWorld();
            sender.sendMessage(ChatColor.GREEN + "Successfully loaded world \"" + args[1] + "\"");
            return true;
        }

        // WorldLoad Unload

        if (args[0].equalsIgnoreCase("unload")) {
            if (!sender.hasPermission("worldload.unload")) {
                sender.sendMessage(ChatColor.RED + "No permission!");
                return true;
            }
            if (args.length == 1) {
                sender.sendMessage(ChatColor.RED + "Correct usage: /worldload unload <world>");
                return true;
            }
            if (worldlistunloaded.contains(args[1])) {
                sender.sendMessage(ChatColor.RED + "World is already unloaded!");
                return true;
            }
            if (getServer().getWorld(args[1]) == null) {
                sender.sendMessage(ChatColor.RED + "World does not exist!");
                return true;
            }
            if (getServer().getWorld(args[1]) == getServer().getWorlds().get(0)
                    || getServer().getWorld(args[1]) == getServer().getWorlds().get(1)
                    || getServer().getWorld(args[1]) == getServer().getWorlds().get(2)) {
                sender.sendMessage(ChatColor.RED + "Cannot unload a main world!");
                return true;
            }
            if (getServer().getWorld(args[1]).getPlayers().size() > 0) {
                sender.sendMessage(ChatColor.RED + "Cannot unload a world with players inside!");
                return true;
            }
            if (worldlistloaded.contains(args[1])) {
                worldlistloaded.remove(args[1]);
            }
            if (!worldlistunloaded.contains(args[1])) {
                worldlistunloaded.add(args[1]);
            }
            getServer().unloadWorld(args[1], true);
            sender.sendMessage(ChatColor.GREEN + "Successfully unloaded world \"" + args[1] + "\"");
            return true;
        }

        // WorldLoad Stats

        if (args[0].equalsIgnoreCase("stats")) {
            if (sender instanceof ConsoleCommandSender) {
                getServer().getConsoleSender().sendMessage(ChatColor.RED + "Command can only be run as a player!");
                return true;
            }
            if (!sender.hasPermission("worldload.stats")) {
                sender.sendMessage(ChatColor.RED + "No permission!");
                return true;
            }
            if (args.length == 1) {
                Player player = (Player) sender;
                int entities = player.getWorld().getEntities().size() - player.getWorld().getPlayers().size();
                sender.sendMessage("----- " + ChatColor.DARK_AQUA + ChatColor.BOLD + "Current World Statistics "
                        + ChatColor.WHITE + "-----");
                sender.sendMessage(ChatColor.GRAY + "World: " + ChatColor.DARK_AQUA + player.getWorld().getName());
                sender.sendMessage(ChatColor.GRAY + "World Type: " + ChatColor.DARK_AQUA
                        + player.getWorld().getWorldType().getName());
                sender.sendMessage(ChatColor.GRAY + "Environment: " + ChatColor.DARK_AQUA
                        + player.getWorld().getEnvironment().toString());
                sender.sendMessage(ChatColor.GRAY + "Seed: " + ChatColor.DARK_AQUA + player.getWorld().getSeed());
                sender.sendMessage(ChatColor.GRAY + "Difficulty: " + ChatColor.DARK_AQUA
                        + player.getWorld().getDifficulty().toString());
                sender.sendMessage(ChatColor.GRAY + "Entities: " + ChatColor.DARK_AQUA + entities);
                sender.sendMessage(
                        ChatColor.GRAY + "Players: " + ChatColor.DARK_AQUA + player.getWorld().getPlayers().size());
                sender.sendMessage(ChatColor.GRAY + "Time: " + ChatColor.DARK_AQUA + player.getWorld().getTime());
                sender.sendMessage(
                        ChatColor.GRAY + "Max Height: " + ChatColor.DARK_AQUA + player.getWorld().getMaxHeight());
                sender.sendMessage(ChatColor.GRAY + "Generate Structures: " + ChatColor.DARK_AQUA
                        + player.getWorld().canGenerateStructures());
                sender.sendMessage(ChatColor.GRAY + "World Border: " + ChatColor.DARK_AQUA
                        + player.getWorld().getWorldBorder().getSize());
                sender.sendMessage(ChatColor.GRAY + "Spawn: " + ChatColor.DARK_AQUA
                        + player.getWorld().getSpawnLocation().getBlockX() + " / "
                        + player.getWorld().getSpawnLocation().getBlockY() + " / "
                        + player.getWorld().getSpawnLocation().getBlockZ());
                return true;
            }
        }

        // WorldLoad List

        if (args[0].equalsIgnoreCase("list")) {
            if (!sender.hasPermission("worldload.list")) {
                sender.sendMessage(ChatColor.RED + "No permission!");
                return true;
            }
            if (args.length == 1) {
                sender.sendMessage(
                        "----- " + ChatColor.DARK_AQUA + ChatColor.BOLD + "World List " + ChatColor.WHITE + "-----");
                sender.sendMessage(ChatColor.DARK_AQUA + "Normal: " + ChatColor.GRAY + worldlist.toString());
                sender.sendMessage(ChatColor.DARK_AQUA + "Flat: " + ChatColor.GRAY + flatworldlist.toString());
                sender.sendMessage(ChatColor.DARK_AQUA + "Loaded: " + ChatColor.GRAY + worldlistloaded.toString());
                sender.sendMessage(ChatColor.DARK_AQUA + "Unloaded: " + ChatColor.GRAY + worldlistunloaded.toString());
                return true;
            }
        }

        // WorldLoad Reload

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("worldload.reload")) {
                sender.sendMessage(ChatColor.RED + "No permission!");
                return true;
            }
            if (args.length == 1) {
                reloadConfig();
                List<String> worldlist = getConfig().getStringList("worldlist");
                List<String> flatworldlist = getConfig().getStringList("flatworldlist");
                for (String world : worldlist) {
                    if (getServer().getWorld(world) == null) {
                        sender.sendMessage(ChatColor.GREEN + "Preparing level \"" + world + "\"");
                        new WorldCreator(world).createWorld();
                        sender.sendMessage(ChatColor.GREEN + "Successfully created world \"" + world + "\"");
                    }
                }
                for (String flatworld : flatworldlist) {
                    if (getServer().getWorld(flatworld) == null) {
                        sender.sendMessage(ChatColor.GREEN + "Preparing level \"" + flatworld + "\"");
                        new WorldCreator(flatworld).type(WorldType.FLAT)
                                .generateStructures(getConfig().getBoolean("generateStructuresInFlatWorlds"))
                                .createWorld();
                        sender.sendMessage(ChatColor.GREEN + "Successfully created flat world \"" + flatworld + "\"");
                    }
                }
                sender.sendMessage(ChatColor.GREEN + "Successfully reloaded config!");
                return true;
            }
        }
        sender.sendMessage(ChatColor.RED + "Unknown argument!");
        return true;
    }
}
