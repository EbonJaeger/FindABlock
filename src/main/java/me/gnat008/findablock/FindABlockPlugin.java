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
import me.gnat008.findablock.configuration.FindABlockConfig;
import me.gnat008.findablock.configuration.YAMLConfigManager;
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
    private YAMLConfigManager configManager;

    private ConfigurationManager configuration;

    public FindABlockConfig config;
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
        this.configManager = new YAMLConfigManager(this);
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
        if (getConfig() == null) {
            saveDefaultConfig();
        }
        
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
