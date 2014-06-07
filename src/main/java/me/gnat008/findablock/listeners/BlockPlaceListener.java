package me.gnat008.findablock.listeners;

import me.gnat008.findablock.FindABlockPlugin;
import me.gnat008.findablock.configuration.YAMLConfig;
import me.gnat008.findablock.util.Printer;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.material.Wool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockPlaceListener implements Listener {

    HashMap<Location, List<String>> data = new HashMap<Location, List<String>>();

    private FindABlockPlugin plugin;
    private Printer printer;
    private YAMLConfig blocksConfig;

    public BlockPlaceListener() {
        this.plugin = FindABlockPlugin.getInstance();
        this.printer = plugin.getPrinter();
        this.blocksConfig = plugin.getBlocksConfig();
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getItemInHand() != null) {
            if (event.getItemInHand().hasItemMeta()) {
                if (event.getItemInHand().getItemMeta().hasLore()) {
                    if (event.getItemInHand().getItemMeta().getLore().contains(ChatColor.GRAY + "[FindABlock]")) {
                        data.put(event.getBlock().getLocation(), event.getItemInHand().getItemMeta().getLore());
                        Player player = event.getPlayer();

                        // Get the placed block's data
                        Block block = event.getBlockPlaced();
                        Location location = event.getBlockPlaced().getLocation();
                        Material material = block.getType();

                        // Check to see what the itemstack is
                        if (material.equals(Material.WOOL)) {
                            // Get the color of the wool
                            Wool wool = new Wool(block.getType(), block.getData());
                            DyeColor color = wool.getColor();

                            // Check if the same block has already been logged
                            if (!(blocksConfig.contains("blocks.WOOL." + color.toString().toLowerCase() + material.toString().toLowerCase()))) {
                                // Add the block's data to a String List
                                List<String> blocksPlaced = new ArrayList<String>();
                                blocksPlaced.add("world=" + location.getWorld().getName());
                                blocksPlaced.add("x=" + location.getX());
                                blocksPlaced.add("y=" + location.getY());
                                blocksPlaced.add("z=" + location.getZ());

                                // Update the blocks.yml file with the placed block's data
                                blocksConfig.set("blocks." + material.toString() + "." + color.toString().toLowerCase() + material.toString().toLowerCase(), blocksPlaced);
                                blocksConfig.saveConfig();
                                blocksConfig.reloadConfig();
                            } else {
                                printer.printToPlayer(player, "A " + ChatColor.WHITE + color.toString().toLowerCase() + material.toString().toLowerCase() +
                                        ChatColor.RED + " was already placed!", true);
                                event.setCancelled(true);
                            }
                        } else if (material.equals(Material.STAINED_CLAY)) {
                            // Get the color of the clay
                            DyeColor color = DyeColor.getByDyeData(block.getData());

                            // Check if the same block has already been logged
                            if (!(blocksConfig.contains("blocks.STAINED_CLAY." + color.toString().toLowerCase() + material.toString().toLowerCase()))) {
                                // Add the block's data to a String List
                                List<String> blocksPlaced = new ArrayList<String>();
                                blocksPlaced.add("world=" + location.getWorld().getName());
                                blocksPlaced.add("x=" + location.getX());
                                blocksPlaced.add("y=" + location.getY());
                                blocksPlaced.add("z=" + location.getZ());

                                // Update the blocks.yml file with the placed block's data
                                blocksConfig.set("blocks." + material.toString() + "." + color.toString().toLowerCase() + material.toString().toLowerCase(), blocksPlaced);
                                blocksConfig.saveConfig();
                                blocksConfig.reloadConfig();
                            } else {
                                printer.printToPlayer(player, "A " + ChatColor.WHITE + color.toString().toLowerCase() + material.toString().toLowerCase() +
                                        ChatColor.RED + " was already placed!", true);
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }
}
