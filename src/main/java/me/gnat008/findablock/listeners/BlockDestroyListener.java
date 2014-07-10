package me.gnat008.findablock.listeners;

import me.gnat008.findablock.FindABlockPlugin;
import me.gnat008.findablock.managers.BlockManager;
import me.gnat008.findablock.util.Printer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockDestroyListener implements Listener {

    private FindABlockPlugin plugin;
    private BlockManager blockManager;
    private Printer printer;

    public BlockDestroyListener(FindABlockPlugin plugin) {
        this.plugin = plugin;
        this.printer = plugin.getPrinter();
        
        this.blockManager = BlockManager.getManager();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock() == null || event.getPlayer() == null) {
            return;
        }
        
        Block block = event.getBlock();
        Location location = block.getLocation();
        Player player = event.getPlayer();
        
        if (blockManager.getHiddenBlock(location) != null) {
            if (plugin.hasPermission(player, "remove")) {
                blockManager.removeBlock(location);
                printer.printToPlayer(player, "Block removed successfully!", false);
            } else {
                printer.printToPlayer(player, "You cannot remove this block!", true);
                event.setCancelled(true);
            }
        }
    }
}