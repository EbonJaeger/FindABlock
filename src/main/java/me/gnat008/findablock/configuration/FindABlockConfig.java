package me.gnat008.findablock.configuration;

import me.gnat008.findablock.configuration.options.Options;

import java.util.ArrayList;
import java.util.List;

public class FindABlockConfig extends Options {

    public FindABlockConfig instance;

    public FindABlockConfig(YAMLConfig config) {
        super(config);
        instance = this;
    }

    @Override
    public void setDefaults() {
        set("blocks.wool.enabled", true, "Select what lists you want to enable.", "Each list will use all colors by default.",
                "You can disable certain blocks by adding to the", "blacklist section.");
        set("blocks.wool.blacklist", new ArrayList<String>());

        set("blocks.clay.enabled", true);
        set("blocks.clay.blacklist", new ArrayList<String>());

        set("blocks.glass.enabled", false);
        set("blocks.glass.blacklist", new ArrayList<String>());

        List<String> defReward = new ArrayList<String>();
        defReward.add("IRON_INGOT:32");
        set("reward", defReward, "Set the reward for finding target blocks", "All blocks in a set must be found to get the reward");

        config.saveConfig();
    }
}
