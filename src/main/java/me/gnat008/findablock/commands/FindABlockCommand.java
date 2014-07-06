package me.gnat008.findablock.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.gnat008.findablock.FindABlockPlugin;
import me.gnat008.findablock.configuration.ConfigurationManager;
import me.gnat008.findablock.configuration.YAMLConfig;
import me.gnat008.findablock.util.Printer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;

public class FindABlockCommand implements CommandExecutor {

    private FindABlockPlugin plugin;
    private Printer printer;

    private ConfigurationManager config;
    private YAMLConfig blocksConfig;
    private YAMLConfig playersConfig;

    private PluginDescriptionFile pdf;
    private String version;
    private List<String> authors;

    private enum Action {HELP, RELOAD, SET, REMOVE}

    private enum Blockset {ALL, CLAY, WOOL, GLASS}

    public FindABlockCommand(FindABlockPlugin plugin) {
        this.plugin = plugin;
        this.printer = plugin.getPrinter();
        this.config = plugin.getMainConfig();
        this.blocksConfig = plugin.getBlocksConfig();
        this.playersConfig = plugin.getPlayersConfig();

        this.pdf = plugin.getDescription();

        this.version = pdf.getVersion();
        this.authors = pdf.getAuthors();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Cancels the command if sent from console
        if (!(sender instanceof Player)) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                reload(null);
            }

            return true;
        }

        Player player = (Player) sender;

        // Displays the plugin's help message to the player
        if (args.length == 0) {
            displayHelp(player);
            return true;
        }

        Action action;

        try {
            action = Action.valueOf(args[0].toUpperCase());
        } catch (Exception notEnum) {
            displayHelp(player);
            return true;
        }

        // Check if the player has permission to use the command; Cancel if no
        if (!plugin.hasPermission(player, args[0]) && !args[0].equalsIgnoreCase("help")) {
            printer.printToPlayer(player, "You do not have permission to do that.", true);
            return true;
        }

        // Execute the correct command
        switch (action) {
            case HELP:
                displayHelp(player);
                return true;

            case RELOAD:
                if (args.length == 1) {
                    reload(player);
                } else {
                    displayHelp(player);
                }

                return true;

            case SET:
                if (args.length != 2) {
                    displayHelp(player);
                    return true;
                }

                Blockset set;
                try {
                    set = Blockset.valueOf(args[1].toUpperCase());
                } catch (Exception notEnum) {
                    printer.printToPlayer(player, "Invalid block set " + ChatColor.WHITE + args[1].toLowerCase() +
                            ChatColor.RED + ", please check your spelling.", true);
                    return true;
                }

                switch (set) {
                    case WOOL:
                        if (plugin.getMainConfig().woolEnabled) {
                            set(player, args[1]);
                            return true;
                        } else {
                            printer.printToPlayer(player, "Block set " + ChatColor.WHITE + args[1].toLowerCase() +
                                    ChatColor.RED + " is not enabled!", true);
                            return true;
                        }

                    case CLAY:
                        if (plugin.getMainConfig().clayEnabled) {
                            set(player, args[1]);
                            return true;
                        } else {
                            printer.printToPlayer(player, "Block set " + ChatColor.WHITE + args[1].toLowerCase() +
                                    ChatColor.RED + " is not enabled!", true);
                            return true;
                        }

                    case GLASS:
                        if (plugin.getMainConfig().glassEnabled) {
                            set(player, args[1]);
                            return true;
                        } else {
                            printer.printToPlayer(player, "Block set " + ChatColor.WHITE + args[1].toLowerCase() +
                                    ChatColor.RED + " is not enabled!", true);
                            return true;
                        }
                }

            case REMOVE:
                if (args.length < 2 || args.length > 2) {
                    displayHelp(player);
                    return true;
                } else {
                    Blockset blockSet;

                    try {
                        blockSet = Blockset.valueOf(args[1].toUpperCase());
                    } catch (Exception notEnum) {
                        printer.printToPlayer(player, "Invalid argument, please check your spelling.", true);
                        return true;
                    }

                    switch (blockSet) {
                        case ALL:
                            remove(player);
                            return true;

                        case CLAY:
                            String bSet = "stained_clay";
                            remove(player, bSet);
                            return true;

                        case WOOL:
                            bSet = "wool";
                            remove(player, bSet);
                            return true;
                    }
                }
        }
        return false;
    }

    private void displayHelp(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "     FindABlock Help Page:");
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "Version: " + ChatColor.GREEN + version);
        player.sendMessage(ChatColor.GOLD + "Author: " + ChatColor.GREEN + authors);
        player.sendMessage("");
        player.sendMessage(ChatColor.WHITE + "/fab help" + ChatColor.GOLD + " - Displays this help page.");
        player.sendMessage(ChatColor.WHITE + "/fab set <blockSet>" + ChatColor.GOLD + " - Gives you blocks to hide.");
        player.sendMessage(ChatColor.WHITE + "/fab remove <blockSet|all>" + ChatColor.GOLD + " - Removes all the blocks you hid.");
        player.sendMessage(ChatColor.WHITE + "/fab reload" + ChatColor.GOLD + " - Reloads the configuration file.");
    }

    private void reload(Player player) {
        try {
            plugin.getMainConfig().load();
        } catch (Throwable t) {
            printer.printToConsole("Error while reloading config: " + t.getMessage(), true);
        }

        printer.printToConsole("Configuration reloaded successfully!", false);
        if (player != null) {
            printer.printToPlayer(player, "Configuration reloaded!", false);
        }
    }

    @SuppressWarnings("deprecation")
    private void set(Player player, String bSet) {
        Map<Material, Byte> blacklist = new HashMap<Material, Byte>();

        if (bSet.equalsIgnoreCase("wool")) {
            if (plugin.getMainConfig().woolBlacklist.size() < 1) {
                for (String item : plugin.getMainConfig().woolBlacklist) {
                    String[] blackListItem = item.split(":", 2);
                    blacklist.put(Material.valueOf(blackListItem[0]), Byte.valueOf(blackListItem[1]));
                }
            }

            List<String> lore = new ArrayList<String>();
            lore.add(ChatColor.GRAY + "[FindABlock]");

            for (DyeColor dyeColor : DyeColor.values()) {
                if (!blacklist.containsValue(dyeColor.getDyeData())) {
                    ItemStack is = new ItemStack(Material.WOOL, 1, (short) dyeColor.getDyeData());

                    ItemMeta meta = is.getItemMeta();
                    meta.setLore(lore);
                    is.setItemMeta(meta);

                    player.getInventory().addItem(is);
                }
            }
        } else if (bSet.equalsIgnoreCase("clay")) {
            if (!(config.clayBlacklist.contains(null))) {
                for (String item : config.clayBlacklist) {
                    String[] blacklistItem = item.split(":", 2);
                    blacklist.put(Material.valueOf(blacklistItem[0]), Byte.valueOf(blacklistItem[1]));
                }
            }

            List<String> lore = new ArrayList<String>();
            lore.add(ChatColor.GRAY + "[FindABlock]");

            for (DyeColor dyeColor : DyeColor.values()) {
                if (!blacklist.containsValue(dyeColor.getDyeData())) {
                    ItemStack is = new ItemStack(Material.STAINED_CLAY, 1, (short) dyeColor.getDyeData());

                    ItemMeta meta = is.getItemMeta();
                    meta.setLore(lore);
                    is.setItemMeta(meta);

                    player.getInventory().addItem(is);
                }
            }
        }
    }

    private void remove(Player player, String bset) {
        if (blocksConfig.contains("blocks." + bset.toUpperCase())) {
            Map<String, World> worldMap = new HashMap<String, World>();
            Map<String, Double> coordMap = new HashMap<String, Double>();

            for (DyeColor color : DyeColor.values()) {
                String dyeColor = color.toString().toLowerCase();
                for (String e : blocksConfig.getStringList("blocks." + bset.toUpperCase() + "." + dyeColor + bset.toLowerCase())) {
                    if (e != null) {
                        if (e.contains("world=")) {
                            e = e.substring(6);
                            worldMap.put("world", Bukkit.getWorld(e));
                        } else if (e.contains("x=")) {
                            e = e.substring(2);
                            coordMap.put("x", Double.parseDouble(e));
                        } else if (e.contains("y=")) {
                            e = e.substring(2);
                            coordMap.put("y", Double.parseDouble(e));
                        } else if (e.contains("z=")) {
                            e = e.substring(2);
                            coordMap.put("z", Double.parseDouble(e));

                            World world = worldMap.get("world");
                            double x = coordMap.get("x");
                            double y = coordMap.get("y");
                            double z = coordMap.get("z");

                            Location location = new Location(world, x, y, z);
                            world.getBlockAt(location).setType(Material.AIR);

                            worldMap.clear();
                            coordMap.clear();

                            blocksConfig.removeKey("blocks." + bset.toUpperCase() + "." + dyeColor + bset.toLowerCase());

                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (playersConfig.contains("players." + p.getName())) {
                                    playersConfig.removeKey("players." + p.getName() + "." + bset.toUpperCase() + "." + dyeColor + bset.toLowerCase());
                                }
                            }

                            for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                                if (playersConfig.contains("players." + p.getName())) {
                                    playersConfig.removeKey("players." + p.getName() + "." + bset.toUpperCase() + "." + dyeColor + bset.toLowerCase());
                                }
                            }
                        }
                    }
                }
            }

            blocksConfig.removeKey("blocks." + bset.toUpperCase());
            blocksConfig.saveConfig();
            blocksConfig.reloadConfig();

            playersConfig.saveConfig();
            playersConfig.reloadConfig();

            player.sendMessage(ChatColor.GREEN + "Blocks have been removed!");
        }
    }

    private void remove(Player player) {
        boolean done = false;

        if (blocksConfig.contains("blocks.WOOL")) {
            Map<String, World> worldMap = new HashMap<String, World>();
            Map<String, Double> coordMap = new HashMap<String, Double>();

            for (DyeColor color : DyeColor.values()) {
                String dyeColor = color.toString().toLowerCase();
                for (String e : blocksConfig.getStringList("blocks.WOOL." + dyeColor + "wool")) {
                    if (e != null) {
                        if (e.contains("world=")) {
                            e = e.substring(6);
                            worldMap.put("world", Bukkit.getWorld(e));
                        } else if (e.contains("x=")) {
                            e = e.substring(2);
                            coordMap.put("x", Double.parseDouble(e));
                        } else if (e.contains("y=")) {
                            e = e.substring(2);
                            coordMap.put("y", Double.parseDouble(e));
                        } else if (e.contains("z=")) {
                            e = e.substring(2);
                            coordMap.put("z", Double.parseDouble(e));

                            World world = worldMap.get("world");
                            double x = coordMap.get("x");
                            double y = coordMap.get("y");
                            double z = coordMap.get("z");

                            Location location = new Location(world, x, y, z);
                            world.getBlockAt(location).setType(Material.AIR);

                            worldMap.clear();
                            coordMap.clear();

                            blocksConfig.removeKey("blocks.WOOL." + dyeColor + "wool");

                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (playersConfig.contains("players." + p.getName())) {
                                    playersConfig.removeKey("players." + p.getName() + ".WOOL." + dyeColor + "wool");
                                }
                            }

                            for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                                if (playersConfig.contains("players." + p.getName())) {
                                    playersConfig.removeKey("players." + p.getName() + ".WOOL." + dyeColor + "wool");
                                }
                            }
                        }
                    }
                }
            }

            blocksConfig.removeKey("blocks.WOOL");
            blocksConfig.saveConfig();
            blocksConfig.reloadConfig();

            playersConfig.saveConfig();
            playersConfig.reloadConfig();

            done = true;
            if (done) {
                printer.printToPlayer(player, "Blocks have been removed!", false);
            } else {
                printer.printToPlayer(player, "No blocks found to remove.", true);
            }
        }

        if (blocksConfig.contains("blocks.STAINED_CLAY")) {
            Map<String, World> worldMap = new HashMap<String, World>();
            Map<String, Double> coordMap = new HashMap<String, Double>();

            done = false;

            for (DyeColor color : DyeColor.values()) {
                String dyeColor = color.toString().toLowerCase();
                for (String e : blocksConfig.getStringList("blocks.STAINED_CLAY." + dyeColor + "stained_clay")) {
                    if (e != null) {
                        if (e.contains("world=")) {
                            e = e.substring(6);
                            worldMap.put("world", Bukkit.getWorld(e));
                        } else if (e.contains("x=")) {
                            e = e.substring(2);
                            coordMap.put("x", Double.parseDouble(e));
                        } else if (e.contains("y=")) {
                            e = e.substring(2);
                            coordMap.put("y", Double.parseDouble(e));
                        } else if (e.contains("z=")) {
                            e = e.substring(2);
                            coordMap.put("z", Double.parseDouble(e));

                            World world = worldMap.get("world");
                            double x = coordMap.get("x");
                            double y = coordMap.get("y");
                            double z = coordMap.get("z");

                            Location location = new Location(world, x, y, z);
                            world.getBlockAt(location).setType(Material.AIR);

                            worldMap.clear();
                            coordMap.clear();

                            blocksConfig.removeKey("blocks.STAINED_CLAY." + dyeColor + "stained_clay");

                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (playersConfig.contains("players." + p.getName())) {
                                    playersConfig.removeKey("players." + p.getName() + ".STAINED_CLAY." + dyeColor + "stained_clay");
                                }
                            }

                            for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                                if (playersConfig.contains("players." + p.getName())) {
                                    playersConfig.removeKey("players." + p.getName() + ".STAINED_CLAY." + dyeColor + "stained_clay");
                                }
                            }
                        }
                    }
                }
            }

            blocksConfig.removeKey("blocks.STAINED_CLAY");
            blocksConfig.saveConfig();
            blocksConfig.reloadConfig();

            playersConfig.saveConfig();
            playersConfig.reloadConfig();

            done = true;
        }

        if (done) {
            printer.printToPlayer(player, "Blocks have been removed!", false);
        } else {
            printer.printToPlayer(player, "No blocks found to remove.", true);
        }
    }
}
