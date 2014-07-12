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

package me.gnat008.findablock;

import java.util.List;
import java.util.logging.Logger;
import me.gnat008.findablock.commands.FindABlockCommand;
import me.gnat008.findablock.configuration.ConfigAccessManager;
import me.gnat008.findablock.listeners.BlockDestroyListener;
import me.gnat008.findablock.listeners.BlockPlaceListener;
import me.gnat008.findablock.listeners.PlayerInteractListener;
import me.gnat008.findablock.managers.BlockManager;
import me.gnat008.findablock.util.Printer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FindABlockPlugin extends JavaPlugin {

    private Printer printer;

    //private ConfigurationManager configuration;
    private ConfigAccessManager config;
    private ConfigAccessManager blocksConfig;
    
    // Configuration values start
    public boolean woolEnabled;
    public List<String> woolBlacklist;

    public boolean clayEnabled;
    public List<String> clayBlacklist;

    public boolean glassEnabled;
    public List<String> glassBlacklist;

    public List<String> reward;
    // Configuration values end

    public Logger logger;
    public PluginManager pm;

    @Override
    public void onEnable() {
        this.printer = new Printer(this);
        this.logger = getServer().getLogger();
        this.pm = getServer().getPluginManager();
        //this.configuration = ConfigurationManager.getInstance(this);

        // Register listener events
        pm.registerEvents(new BlockPlaceListener(this), this);
        pm.registerEvents(new PlayerInteractListener(this), this);
        pm.registerEvents(new BlockDestroyListener(this), this);

        // Set command executor
        getCommand("findablock").setExecutor(new FindABlockCommand(this));
        
        // Create/load the config files
        setupConfiguration();
        this.blocksConfig = new ConfigAccessManager(this, "blocks.yml");
        blocksConfig.saveConfig();
        
        // Load blocks from the blocks config file
        BlockManager.getManager(this).loadBlocks();
    }

    public boolean hasPermission(Player player, String type) {
        return player.hasPermission("findablock." + type);
    }
    
    public ConfigAccessManager getMainConfig() {
        return this.config;
    }
    
    public ConfigAccessManager getBlocksConfig() {
        return this.blocksConfig;
    }

    public Printer getPrinter() {
        return printer;
    }
    
    public void setupConfiguration() {
        this.config = new ConfigAccessManager(this, "config.yml");
        config.saveDefaultConfig();
        
        // Load the configuration values into memory
        woolEnabled = config.getConfig().getBoolean("blocks.wool.enabled");
        woolBlacklist = config.getConfig().getStringList("blocks.wool.blacklist");
        clayEnabled = config.getConfig().getBoolean("blocks.clay.enabled");
        clayBlacklist = config.getConfig().getStringList("blocks.clay.enabled");
        glassEnabled = config.getConfig().getBoolean("blocks.glass.enabled");
        glassBlacklist = config.getConfig().getStringList("blocks.glass.blacklist");
        reward = config.getConfig().getStringList("reward");
        
        config.reloadConfig();
    }
}
