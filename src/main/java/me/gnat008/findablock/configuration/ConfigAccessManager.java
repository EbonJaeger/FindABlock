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

package me.gnat008.findablock.configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import me.gnat008.findablock.FindABlockPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Gnat008
 */
public class ConfigAccessManager {
    
    private final String filename;
    private final FindABlockPlugin plugin;
    
    private File configFile;
    private FileConfiguration fileConfiguration;
    
    public ConfigAccessManager(FindABlockPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.filename = fileName;
        
        File dataFolder = plugin.getDataFolder();
        if (dataFolder == null) {
            throw new IllegalStateException();
        }
        
        this.configFile = new File(dataFolder, filename);
    }
    
    public void reloadConfig() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), filename);
        }
        
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
        
        // Looks for defaults in the Jar
        Reader configStream;
        try {
            configStream = new InputStreamReader(plugin.getResource(filename), "UTF8");
            if (configStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(configStream);
                fileConfiguration.setDefaults(defConfig);
                
                configStream.close();
            }
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "There was a problem: " + ex.getMessage());
        }
    }
    
    public FileConfiguration getConfig() {
        if (fileConfiguration == null) {
            this.reloadConfig();
        }
        
        return fileConfiguration;
    }
    
    public void saveConfig() {
        if (fileConfiguration == null || configFile == null) {
            return;
        }
        
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            plugin.logger.log(Level.SEVERE, "Could not save config to " + configFile, ex);
        }
    }
    
    public void saveDefaultConfig() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), filename);
        }
        
        if (!configFile.exists()) {
            plugin.saveResource(filename, false);
        }
    }
}
