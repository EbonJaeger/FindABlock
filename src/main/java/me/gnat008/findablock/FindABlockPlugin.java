package me.gnat008.findablock;

import me.gnat008.findablock.commands.FindABlockCommand;
import me.gnat008.findablock.configuration.FindABlockConfig;
import me.gnat008.findablock.configuration.YAMLConfig;
import me.gnat008.findablock.configuration.YAMLConfigManager;
import me.gnat008.findablock.listeners.BlockDestroyListener;
import me.gnat008.findablock.listeners.BlockPlaceListener;
import me.gnat008.findablock.listeners.PlayerInteractListener;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FindABlockPlugin extends JavaPlugin {

    private static FindABlockPlugin plugin;

    private YAMLConfigManager configManager;
    private YAMLConfig mainConfig;
    private YAMLConfig blocksConfig;
    private YAMLConfig playersConfig;

    public FindABlockConfig config;
    public Logger logger;
    public PluginManager pm;

    @Override
    public void onEnable() {
        start();
    }

    public Block getTargetBlock(Player player, int range) { // Method to get the block a player is looking at
        World world = player.getWorld();
        Location loc = player.getEyeLocation();
        Vector start = loc.toVector();
        Vector dir = loc.getDirection().normalize();

        BlockIterator bIt = new BlockIterator(world, start, dir, 0, range);

        return bIt.next();
    }

    public boolean hasPermission(Player player, String type) {
        return player.hasPermission("findablock." + type);
    }

    public void start() {
        this.plugin = this;

        this.logger = getServer().getLogger();
        this.pm = getServer().getPluginManager();

        this.configManager = new YAMLConfigManager(this);

        // Generate the config.yml file
        String[] header = {"FindABlock Configuration File", "---------------------", "Created by Gnat008"};
        try {
            mainConfig = configManager.getNewConfig("config.yml", header);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Configuration file 'config.yml' generation failed.", e);
        }

        config = new FindABlockConfig(mainConfig);
        mainConfig.reloadConfig();

        // Generate the config file where placed blocks get logged
        String[] blocksHeader = {"FindABlock Logged Blocks", "---------------------", "When a block from this plugin is placed,", "its type and location are stored here."};
        try {
            blocksConfig = configManager.getNewConfig("data/blocks.yml", blocksHeader);
            blocksConfig.reloadConfig();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Configuration file 'blocks.yml' generation failed.", e);
        }

        // Generate the config file where player data is stored
        String[] playersHeader = {"FindABlock Player Data", "---------------------", "Logs when a player finds a hidden block."};
        try {
            playersConfig = configManager.getNewConfig("data/players.yml", playersHeader);
            playersConfig.reloadConfig();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Configuration file 'players.yml' generation failed.", e);
        }

        // Register listener events
        pm.registerEvents(new BlockPlaceListener(), this);
        pm.registerEvents(new PlayerInteractListener(), this);
        pm.registerEvents(new BlockDestroyListener(), this);

        // Set command executor
        getCommand("findablock").setExecutor(new FindABlockCommand());
        logger.log(Level.INFO, "[FindABlock] Findablock commands initialized.");
    }

    public static FindABlockPlugin getInstance() {
        return plugin;
    }

    public YAMLConfig getMainConfig() {
        return mainConfig;
    }

    public YAMLConfig getBlocksConfig() {
        return blocksConfig;
    }

    public YAMLConfig getPlayersConfig() {
        return playersConfig;
    }
}
