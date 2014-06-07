package me.gnat008.findablock.listeners;

import me.gnat008.findablock.FindABlockPlugin;
import me.gnat008.findablock.configuration.YAMLConfig;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

import java.util.ArrayList;
import java.util.List;

public class BlockDestroyListener implements Listener {

    private FindABlockPlugin plugin;
    private YAMLConfig blocksConfig;
    private YAMLConfig playersConfig;

    public BlockDestroyListener() {
        this.plugin = FindABlockPlugin.getInstance();
        this.blocksConfig = plugin.getBlocksConfig();
        this.playersConfig = plugin.getPlayersConfig();
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock() != null && event.getPlayer() != null) {
            Block block = event.getBlock();
            Player player = event.getPlayer();
            String uuid = player.getUniqueId().toString();

            // Check what type of block it is
            if (block.getType().equals(Material.WOOL)) {
                Material material = block.getType();
                Wool wool = new Wool(block.getType(), block.getData());
                DyeColor color = wool.getColor();
                String blockName = color.toString().toLowerCase() + material.toString().toLowerCase();

                // Check if the block log file contains the name of the clicked block
                if (blocksConfig.contains("blocks.WOOL." + blockName)) {
                    List<String> loggedLoc = blocksConfig.getStringList("blocks.WOOL." + blockName);
                    Location location = block.getLocation();

                    // Check if the clicked block's location matches the one in file
                    if (loggedLoc.contains("world=" + location.getWorld().getName()) &&
                            loggedLoc.contains("x=" + location.getX()) &&
                            loggedLoc.contains("y=" + location.getY()) &&
                            loggedLoc.contains("z=" + location.getZ())
                            ) {
                        // Check if the player has permission to remove
                        if (plugin.hasPermission(player, "remove")) {
                            // Remove entry from blocks.yml
                            blocksConfig.removeKey("blocks.WOOL." + blockName);

                            // Iterate through online players, and remove entry from players.yml
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (playersConfig.contains("players." + p.getName())) {
                                    playersConfig.removeKey("players." + p.getName() + ".WOOL." + blockName);
                                }
                            }

                            // Iterate through offline players, and remove entry from players.yml
                            for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                                if (playersConfig.contains("players." + p.getName())) {
                                    playersConfig.removeKey("players." + p.getName() + ".WOOL." + blockName);
                                }
                            }

                            block.setType(Material.AIR);

                            blocksConfig.saveConfig();
                            blocksConfig.reloadConfig();

                            playersConfig.saveConfig();
                            playersConfig.reloadConfig();

                            if (player.getGameMode() == GameMode.SURVIVAL) {
                                ItemStack is = new ItemStack(material, 1, color.getData());
                                ItemMeta meta = is.getItemMeta();

                                List<String> lore = new ArrayList<String>();
                                lore.add(ChatColor.GRAY + "[FindABlock]");

                                meta.setLore(lore);
                                is.setItemMeta(meta);

                                player.getInventory().addItem(is);
                            }

                            player.sendMessage(ChatColor.DARK_GREEN + "Block has been removed from logs.");
                        } else {
                            player.sendMessage(ChatColor.RED + "You do not have permission to do that!");
                            event.setCancelled(true);
                        }
                    }
                }
            } else if (block.getType().equals(Material.STAINED_CLAY)) {
                Material material = block.getType();
                DyeColor color = DyeColor.getByDyeData(block.getData());
                String blockName = color.toString().toLowerCase() + material.toString().toLowerCase();

                // Check if the block log file contains the name of the clicked block
                if (blocksConfig.contains("blocks.STAINED_CLAY." + blockName)) {
                    List<String> loggedLoc = blocksConfig.getStringList("blocks.STAINED_CLAY." + blockName);
                    Location location = block.getLocation();

                    // Check if the clicked block's location matches the one in file
                    if (loggedLoc.contains("world=" + location.getWorld().getName()) &&
                            loggedLoc.contains("x=" + location.getX()) &&
                            loggedLoc.contains("y=" + location.getY()) &&
                            loggedLoc.contains("z=" + location.getZ())
                            ) {
                        // Check if the player has permission to remove
                        if (plugin.hasPermission(player, "remove")) {
                            // Remove entry from blocks.yml
                            blocksConfig.removeKey("blocks.STAINED_CLAY." + blockName);

                            // Iterate through online players, and remove entry from players.yml
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (playersConfig.contains("players." + p.getName())) {
                                    playersConfig.removeKey("players." + p.getName() + ".STAINED_CLAY." + blockName);
                                }
                            }

                            // Iterate through offline players, and remove entry from players.yml
                            for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                                if (playersConfig.contains("players." + p.getName())) {
                                    playersConfig.removeKey("players." + p.getName() + ".STAINED_CLAY." + blockName);
                                }
                            }

                            block.setType(Material.AIR);

                            blocksConfig.saveConfig();
                            blocksConfig.reloadConfig();

                            playersConfig.saveConfig();
                            playersConfig.reloadConfig();

                            if (player.getGameMode() == GameMode.SURVIVAL) {
                                ItemStack is = new ItemStack(material, 1, color.getData());
                                ItemMeta meta = is.getItemMeta();

                                List<String> lore = new ArrayList<String>();
                                lore.add(ChatColor.GRAY + "[FindABlock]");

                                meta.setLore(lore);
                                is.setItemMeta(meta);

                                player.getInventory().addItem(is);
                            }

                            player.sendMessage(ChatColor.DARK_GREEN + "Block has been removed from logs.");
                        } else {
                            player.sendMessage(ChatColor.RED + "You do not have permission to do that!");
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }
}