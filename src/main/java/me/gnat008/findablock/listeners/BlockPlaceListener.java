package me.gnat008.findablock.listeners;

import me.gnat008.findablock.FindABlockPlugin;
import me.gnat008.findablock.blocks.HiddenBlock;
import me.gnat008.findablock.managers.BlockManager;
import me.gnat008.findablock.util.Printer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    private FindABlockPlugin plugin;
    private BlockManager blockManager;
    private Printer printer;

    public BlockPlaceListener(FindABlockPlugin plugin) {
        this.plugin = plugin;
        this.printer = plugin.getPrinter();
        
        this.blockManager = BlockManager.getManager();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if ((event.getItemInHand() == null) ||
                (!(event.getItemInHand().hasItemMeta())) || 
                (!(event.getItemInHand().getItemMeta().hasLore()))) {
            return;
        }
        
        if (event.getItemInHand().getItemMeta().getLore().contains(ChatColor.GRAY + "[FindABlock]")) {
            Player player = event.getPlayer();
            
            Block block = event.getBlockPlaced();
            Location location = block.getLocation();
            Material type = block.getType();
            
            boolean duplicate = false;
            for (HiddenBlock hb : blockManager.getHiddenBlocks()) {
                if (hb.getType() == type) {
                    duplicate = true;
                    printer.printToPlayer(player, "A " + ChatColor.WHITE + type.toString() + ChatColor.RED + 
                            " was already placed!", true);
                    break;
                }
            }
            
            if (!duplicate) {
                blockManager.createBlock(location, type);
                printer.printToPlayer(player, "Hidden block placed!", false);
            } else {
                event.setCancelled(true);
            }
        }
    }
}
