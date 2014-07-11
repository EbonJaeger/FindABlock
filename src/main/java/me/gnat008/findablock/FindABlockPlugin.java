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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import me.gnat008.findablock.commands.FindABlockCommand;
import me.gnat008.findablock.exceptions.FatalConfigurationLoadingException;
import me.gnat008.findablock.listeners.BlockDestroyListener;
import me.gnat008.findablock.listeners.BlockPlaceListener;
import me.gnat008.findablock.listeners.PlayerInteractListener;
import me.gnat008.findablock.managers.BlockManager;
import me.gnat008.findablock.managers.ConfigurationManager;
import me.gnat008.findablock.util.Printer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FindABlockPlugin extends JavaPlugin {

    private Printer printer;

    private ConfigurationManager configuration;

    public Logger logger;
    public PluginManager pm;

    @Override
    public void onEnable() {
        start();
    }

    public boolean hasPermission(Player player, String type) {
        return player.hasPermission("findablock." + type);
    }

    public void start() {
        this.printer = new Printer(this);
        this.logger = getServer().getLogger();
        this.pm = getServer().getPluginManager();
        this.configuration = ConfigurationManager.getInstance(this);

        // Register listener events
        pm.registerEvents(new BlockPlaceListener(this), this);
        pm.registerEvents(new PlayerInteractListener(this), this);
        pm.registerEvents(new BlockDestroyListener(this), this);

        // Set command executor
        getCommand("findablock").setExecutor(new FindABlockCommand(this));

        // Load the configuration
        try {
            configuration.load();
        } catch (FatalConfigurationLoadingException e) {
            e.printStackTrace();
            pm.disablePlugin(this);
        }
        
        // Create the data config file
        /*if (getConfig() == null) {
            saveDefaultConfig();
        }*/
        
        
        // Load data from the config file
        BlockManager.getManager(this).loadBlocks();
    }

    public Printer getPrinter() {
        return printer;
    }

    public ConfigurationManager getMainConfig() {
        return configuration;
    }

    public void createDefaultConfiguration(File actual, String defaultName) {
        // Make parent directories.
        File parent = actual.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        if (actual.exists()) {
            return;
        }

        InputStream input = null;
        try {
            JarFile file = new JarFile(getFile());
            ZipEntry copy = file.getEntry("defaults/" + defaultName);
            if (copy == null) throw new FileNotFoundException();
            input = file.getInputStream(copy);
        } catch (IOException e) {
            logger.severe("Unable to read default configuration: " + defaultName);
        }

        if (input != null) {
            FileOutputStream output = null;

            try {
                output = new FileOutputStream(actual);
                byte[] buf = new byte[8192];
                int length;

                while ((length = input.read(buf)) > 0) {
                    output.write(buf, 0, length);
                }

                printer.printToConsole("Default configuration written: " + actual.getAbsolutePath(), false);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    input.close();
                } catch (IOException ignore) {

                }

                try {
                    if (output != null) {
                        output.close();
                    }
                } catch (IOException ignore) {

                }
            }
        }
    }
}
