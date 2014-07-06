package me.gnat008.findablock.configuration;

import me.gnat008.findablock.FindABlockPlugin;
import me.gnat008.findablock.util.yaml.YAMLFormat;
import me.gnat008.findablock.util.yaml.YAMLProcessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Gnat008 on 6/7/2014.
 */
public class ConfigurationManager {

    private final String CONFIG_HEADER = "# FindABlock's Configuration File\r\n" +
            "#\r\n" +
            "# About editing this file:\r\n" +
            "# - DO NOT USE TABS. You MUST use spaces or Bukkit will complain. If\r\n" +
            "#   you use an editor like Notepad++ (recommended for Windows users), you\r\n" +
            "#   must configure it to \"replace tabs with spaces.\" In Notepad++, this can\r\n" +
            "#   be changed in Settings > Preferences > Language Menu.\r\n" +
            "# - Don't get rid of the indents. They are indented so some entries are\r\n" +
            "#   in categories.\r\n" +
            "# - If you want to check the format of this file before putting it\r\n" +
            "#   into InfiniteBlocks, paste it into http://yaml-online-parser.appspot.com/\r\n" +
            "#   and see if it gives \"ERROR:\".\r\n" +
            "# - Lines starting with # are comments and so they are ignored.\r\n" +
            "#\r\n";

    private FindABlockPlugin plugin;
    private YAMLProcessor config;

    public HashMap<String, String> hostKeys = new HashMap<String, String>();

    // Configuration values start
    public boolean woolEnabled;
    public List<String> woolBlacklist;

    public boolean clayEnabled;
    public List<String> clayBlacklist;

    public boolean glassEnabled;
    public List<String> glassBlacklist;

    public List<String> reward;
    // Configuration values end

    public ConfigurationManager(FindABlockPlugin plugin) {
        this.plugin = plugin;
    }

    // Load the configuration
    @SuppressWarnings("unchecked")
    public void load() {
        // Create the default configuration file
        plugin.createDefaultConfiguration(new File(plugin.getDataFolder(), "config.yml"), "config.yml");

        config = new YAMLProcessor(new File(plugin.getDataFolder(), "config.yml"), true, YAMLFormat.EXTENDED);
        try {
            config.load();
        } catch (IOException e) {
            plugin.getLogger().severe("Error reading configuration file: ");
            e.printStackTrace();
        }

        hostKeys = new HashMap<String, String>();
        Object hostKeysRaw = config.getProperty("host-keys");
        if (hostKeysRaw == null || !(hostKeysRaw instanceof Map)) {
            config.setProperty("host-keys", new HashMap<String, String>());
        } else {
            for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) hostKeysRaw).entrySet()) {
                String key = String.valueOf(entry.getKey());
                String value = String.valueOf(entry.getValue());
                hostKeys.put(key.toLowerCase(), value);
            }
        }

        woolEnabled = config.getBoolean("blocks.wool.enabled", true);
        woolBlacklist = config.getStringList("blocks.wool.blacklist",
                new ArrayList<String>());
        clayEnabled = config.getBoolean("blocks.clay.enabled", true);
        clayBlacklist = config.getStringList("blocks.clay.blacklist",
                new ArrayList<String>());
        glassEnabled = config.getBoolean("blocks.glass.enabled", true);
        glassBlacklist = config.getStringList("blocks.glass.blacklist",
                new ArrayList<String>());

        List<String> def = new ArrayList<String>();
        def.add("IRON_INGOT:32");
        reward = config.getStringList("reward", def);

        config.setHeader(CONFIG_HEADER);

        if (!config.save()) {
            plugin.getLogger().severe("Could not save configuration!");
        }
    }

    // Unload the configuration
    public void unload() {

    }
}
