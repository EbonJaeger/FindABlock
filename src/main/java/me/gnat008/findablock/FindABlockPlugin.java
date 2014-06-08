package me.gnat008.findablock;

import me.gnat008.findablock.commands.FindABlockCommand;
import me.gnat008.findablock.configuration.ConfigurationManager;
import me.gnat008.findablock.configuration.FindABlockConfig;
import me.gnat008.findablock.configuration.YAMLConfig;
import me.gnat008.findablock.configuration.YAMLConfigManager;
import me.gnat008.findablock.exceptions.FatalConfigurationLoadingException;
import me.gnat008.findablock.listeners.BlockDestroyListener;
import me.gnat008.findablock.listeners.BlockPlaceListener;
import me.gnat008.findablock.listeners.PlayerInteractListener;
import me.gnat008.findablock.util.Printer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

public class FindABlockPlugin extends JavaPlugin {

    private static FindABlockPlugin plugin;

    private Printer printer;
    private YAMLConfigManager configManager;
    private YAMLConfig blocksConfig;
    private YAMLConfig playersConfig;

    private final ConfigurationManager configuration;

    public FindABlockConfig config;
    public Logger logger;
    public PluginManager pm;

    public FindABlockPlugin() {
        this.configuration = new ConfigurationManager(this);
    }

    @Override
    public void onEnable() {
        start();
    }

    public boolean hasPermission(Player player, String type) {
        return player.hasPermission("findablock." + type);
    }

    public void start() {
        this.plugin = this;
        this.printer = new Printer(this);

        this.logger = getServer().getLogger();
        this.pm = getServer().getPluginManager();

        this.configManager = new YAMLConfigManager(this);

        // Generate the config file where placed blocks get logged
        String[] blocksHeader = {"FindABlock Logged Blocks", "---------------------", "When a block from this plugin is placed,", "its type and location are stored here."};
        try {
            blocksConfig = configManager.getNewConfig("data/blocks.yml", blocksHeader);
            blocksConfig.reloadConfig();
        } catch (Exception e) {
            printer.printToConsole("Configuration file 'blocks.yml' generation failed.", true);
            e.printStackTrace();
        }

        // Generate the config file where player data is stored
        String[] playersHeader = {"FindABlock Player Data", "---------------------", "Logs when a player finds a hidden block."};
        try {
            playersConfig = configManager.getNewConfig("data/players.yml", playersHeader);
            playersConfig.reloadConfig();
        } catch (Exception e) {
            printer.printToConsole("Configuration file 'players.yml' generation failed.", true);
            e.printStackTrace();
        }

        // Register listener events
        pm.registerEvents(new BlockPlaceListener(), this);
        pm.registerEvents(new PlayerInteractListener(), this);
        pm.registerEvents(new BlockDestroyListener(), this);

        // Set command executor
        getCommand("findablock").setExecutor(new FindABlockCommand());
        printer.printToConsole("Commands initialized.", false);

        // Load the configuration
        try {
            configuration.load();
        } catch (FatalConfigurationLoadingException e) {
            e.printStackTrace();
            pm.disablePlugin(this);
        }
    }

    public static FindABlockPlugin getInstance() {
        return plugin;
    }

    public Printer getPrinter() {
        return printer;
    }

    public ConfigurationManager getMainConfig() {
        return configuration;
    }

    public YAMLConfig getBlocksConfig() {
        return blocksConfig;
    }

    public YAMLConfig getPlayersConfig() {
        return playersConfig;
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
