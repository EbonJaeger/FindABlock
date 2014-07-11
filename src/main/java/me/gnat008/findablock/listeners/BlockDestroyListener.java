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
        
        this.blockManager = BlockManager.getManager(plugin);
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