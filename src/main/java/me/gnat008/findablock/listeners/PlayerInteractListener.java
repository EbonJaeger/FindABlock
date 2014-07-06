package me.gnat008.findablock.listeners;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import me.gnat008.findablock.FindABlockPlugin;
import me.gnat008.findablock.configuration.ConfigurationManager;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

public class PlayerInteractListener implements Listener {

    private ConfigurationManager config;
    private FindABlockPlugin plugin;
    private Printer printer;
    private YAMLConfig blocksConfig;
    private YAMLConfig playersConfig;

    public PlayerInteractListener(FindABlockPlugin plugin) {
        this.plugin = plugin;
        this.printer = plugin.getPrinter();
        this.blocksConfig = plugin.getBlocksConfig();
        this.config = plugin.getMainConfig();
        this.playersConfig = plugin.getPlayersConfig();
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Check if either the player or the blocked clicked is null
        if (event.getPlayer() != null && event.getClickedBlock() != null) {
            Player player = event.getPlayer();
            String uuid = player.getUniqueId().toString();
            Block block = event.getClickedBlock();
            Material material = block.getType();

            // Check if the action was a right click
            if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                // Check the type of block clicked
                if (event.getClickedBlock().getType().equals(Material.WOOL)) {
                    Wool wool = new Wool(block.getType(), block.getData());
                    DyeColor color = wool.getColor();
                    String blockName = color.toString().toLowerCase() + material.toString().toLowerCase();

                    // Check if the block log file contains the name of the clicked block
                    if (blocksConfig.contains("blocks." + material.toString().toUpperCase() + "." + blockName)) {
                        List<String> loggedLoc = blocksConfig.getStringList("blocks." + material.toString().toUpperCase() + "." + blockName);
                        Location location = block.getLocation();

                        // Check if the clicked block's location matches the one in file
                        if (loggedLoc.contains("world=" + location.getWorld().getName()) &&
                                loggedLoc.contains("x=" + location.getX()) &&
                                loggedLoc.contains("y=" + location.getY()) &&
                                loggedLoc.contains("z=" + location.getZ())
                                ) {
                            // Check if the player log file contains the player and block data; if no, add it to the file
                            if (!(playersConfig.getBoolean("players." + uuid + "." + material.toString().toUpperCase() +
                                    "." + blockName))) {
                                printer.printToPlayer(player, "You have found a hidden block!", false);

                                playersConfig.set("players." + uuid + "." + material.toString().toUpperCase() +
                                        "." + blockName, true);
                                playersConfig.saveConfig();
                                playersConfig.reloadConfig();

                                HashMap<String, Boolean> found = new HashMap<String, Boolean>();

                                for (DyeColor dyeColor : DyeColor.values()) {
                                    String configColor = DyeColor.getByColor(dyeColor.getColor()).toString();

                                    String configBlockName = configColor + material.toString();

                                    if (playersConfig.getBoolean("players." + uuid + "." + material.toString().toUpperCase() +
                                            "." + configBlockName.toLowerCase())) {
                                        found.put(configBlockName.toLowerCase(), true);
                                    } else {
                                        found.put(configBlockName.toLowerCase(), false);
                                    }
                                }

                                if (!(found.containsValue(false))) {
                                    for (String item : config.reward) {
                                        String[] rewardData = item.split(":", 2);

                                        String rewardType;
                                        int rewardAmount;

                                        try {
                                            rewardType = rewardData[0];
                                            rewardAmount = Integer.parseInt(rewardData[1]);

                                            ItemStack reward = new ItemStack(Material.valueOf(rewardType.toUpperCase()), rewardAmount);

                                            player.getInventory().addItem(reward);
                                        } catch (NumberFormatException ex) {
                                            printer.printToPlayer(player, "An error has occurred while giving reward! " +
                                                    "Please notify a server administrator!", true);

                                            printer.printToConsole("Incorrect item format '" + item + "', skipping!", false);
                                        }
                                    }

                                    printer.printToPlayer(player, "Congratulations, you found all blocks of this type!", false);
                                }
                            } else {
                                printer.printToPlayer(player, "You have already found a " + ChatColor.WHITE + blockName + ChatColor.RED + "!", true);
                            }
                        }
                    }
                } else if (event.getClickedBlock().getType().equals(Material.STAINED_CLAY)) {
                    DyeColor color = DyeColor.getByDyeData(block.getData());
                    String blockName = color.toString().toLowerCase() + material.toString().toLowerCase();

                    // Check if the block log file contains the name of the clicked block
                    if (blocksConfig.contains("blocks." + material.toString().toUpperCase() + "." + blockName)) {
                        List<String> loggedLoc = blocksConfig.getStringList("blocks." + material.toString().toUpperCase() + "." + blockName);
                        Location location = block.getLocation();

                        // Check if the clicked block's location matches the one in file
                        if (loggedLoc.contains("world=" + location.getWorld().getName()) &&
                                loggedLoc.contains("x=" + location.getX()) &&
                                loggedLoc.contains("y=" + location.getY()) &&
                                loggedLoc.contains("z=" + location.getZ())
                                ) {
                            // Check if the player log file contains the player and block data; if no, add it to the file
                            if (!(playersConfig.getBoolean("players." + uuid + "." + material.toString().toUpperCase() +
                                    "." + blockName))) {
                                printer.printToPlayer(player, "You have found a hidden block!", false);

                                playersConfig.set("players." + uuid + "." + material.toString().toUpperCase() + "." + blockName, true);
                                playersConfig.saveConfig();
                                playersConfig.reloadConfig();

                                HashMap<String, Boolean> found = new HashMap<String, Boolean>();

                                for (DyeColor dyeColor : DyeColor.values()) {
                                    String configColor = DyeColor.getByColor(dyeColor.getColor()).toString();

                                    String configBlockName = configColor + material.toString();

                                    if (playersConfig.getBoolean("players." + uuid + "." + material.toString().toUpperCase() +
                                            "." + configBlockName.toLowerCase())) {
                                        found.put(configBlockName.toLowerCase(), true);
                                    } else {
                                        found.put(configBlockName.toLowerCase(), false);
                                    }
                                }

                                if (!(found.containsValue(false))) {
                                    for (Iterator<String> it = config.reward.iterator(); it.hasNext();) {
                                        String item = it.next();
                                        String[] rewardData = item.split(":", 2);
                                        String rewardType;
                                        int rewardAmount;
                                        try {
                                            rewardType = rewardData[0];
                                            rewardAmount = Integer.parseInt(rewardData[1]);

                                            ItemStack reward = new ItemStack(Material.valueOf(rewardType.toUpperCase()), rewardAmount);

                                            player.getInventory().addItem(reward);
                                        } catch (NumberFormatException ex) {
                                            printer.printToPlayer(player, "An error has occurred while giving reward! " +
                                                    "Please notify a server administrator!", true);

                                            printer.printToConsole("Incorrect item format '" + item + "', skipping!", false);
                                        }
                                    }

                                    printer.printToPlayer(player, "Congratulations, you found all blocks of this type!", false);
                                }
                            } else {
                                printer.printToPlayer(player, "You have already found a " + ChatColor.WHITE + blockName + ChatColor.RED + "!", true);
                            }
                        }
                    }
                }
            }
        }
    }
}
