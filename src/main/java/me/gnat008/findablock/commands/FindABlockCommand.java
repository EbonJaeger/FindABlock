/*
 * Copyright (C) 2014 Gnat008
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.gnat008.findablock.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.gnat008.findablock.FindABlockPlugin;
import me.gnat008.findablock.configuration.ConfigAccessManager;
import me.gnat008.findablock.managers.BlockManager;
import me.gnat008.findablock.util.Printer;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;

public class FindABlockCommand implements CommandExecutor {

    private FindABlockPlugin plugin;
    private ConfigAccessManager config;
    private BlockManager blockManager;
    private Printer printer;

    private PluginDescriptionFile pdf;
    private String version;
    private List<String> authors;

    private enum Action {HELP, RELOAD, SET, REMOVE}

    private enum Blockset {ALL, CLAY, WOOL, GLASS}

    public FindABlockCommand(FindABlockPlugin plugin) {
        this.plugin = plugin;
        this.printer = plugin.getPrinter();
        this.blockManager = BlockManager.getManager(plugin);
        this.config = plugin.getMainConfig();

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
                        if (plugin.woolEnabled) {
                            set(player, args[1]);
                            return true;
                        } else {
                            printer.printToPlayer(player, "Block set " + ChatColor.WHITE + args[1].toLowerCase() +
                                    ChatColor.RED + " is not enabled!", true);
                            return true;
                        }

                    case CLAY:
                        if (plugin.clayEnabled) {
                            set(player, args[1]);
                            return true;
                        } else {
                            printer.printToPlayer(player, "Block set " + ChatColor.WHITE + args[1].toLowerCase() +
                                    ChatColor.RED + " is not enabled!", true);
                            return true;
                        }

                    case GLASS:
                        if (plugin.glassEnabled) {
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
                            String bSet = "clay";
                            remove(player, bSet);
                            return true;

                        case WOOL:
                            bSet = "wool";
                            remove(player, bSet);
                            return true;
                            
                        case GLASS:
                            bSet = "glass";
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
            plugin.getMainConfig().reloadConfig();
            plugin.getMainConfig().saveConfig();
            plugin.loadConfiguration();
        } catch (Throwable t) {
            printer.printToConsole("Error while reloading config: " + t.getMessage(), true);
            if (player != null) {
                printer.printToPlayer(player, "Error while reloading config.", true);
            }
            
            return;
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
            if (plugin.woolBlacklist.size() < 1) {
                for (String item : plugin.woolBlacklist) {
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
            if (!(plugin.clayBlacklist.contains(null))) {
                for (String item : plugin.clayBlacklist) {
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
        } else if (bSet.equalsIgnoreCase("glass")) {
            if (!(plugin.glassBlacklist.isEmpty())) {
                for (String item : plugin.glassBlacklist) {
                    String[] blacklistItem = item.split(":", 2);
                    blacklist.put(Material.valueOf(blacklistItem[0]), Byte.valueOf(blacklistItem[1]));
                }
            }
            
            List<String> lore = new ArrayList<String>();
            lore.add(ChatColor.GRAY + "[FindABlock]");
            
            for (DyeColor dyeColor : DyeColor.values()) {
                if (!blacklist.containsValue(dyeColor.getDyeData())) {
                    ItemStack is = new ItemStack(Material.STAINED_GLASS, 1, (short) dyeColor.getDyeData());
                    ItemMeta meta = is.getItemMeta();
                    meta.setLore(lore);
                    is.setItemMeta(meta);
                    
                    player.getInventory().addItem(is);
                }
            }
        }
    }
    
    private void remove(Player player, String bset) {
        blockManager.removeBlocks(bset);
        
        printer.printToPlayer(player, "Blocks have been removed!", false);
    }
    
    private void remove(Player player) {
        blockManager.removeBlocks();
        
        printer.printToPlayer(player, "All blocks have been removed!", false);
    }
}
