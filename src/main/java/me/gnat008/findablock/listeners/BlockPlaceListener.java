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
import me.gnat008.findablock.managers.HiddenBlock;
import me.gnat008.findablock.util.Printer;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
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
        
        this.blockManager = BlockManager.getManager(plugin);
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
            DyeColor color = DyeColor.getByDyeData(block.getData());
            
            boolean duplicate = false;
            for (HiddenBlock hb : blockManager.getHiddenBlocks()) {
                if (hb.getType() == type && hb.getColor() == color) {
                    duplicate = true;
                    printer.printToPlayer(player, "A " + ChatColor.WHITE + color.toString() + "_" + type.toString() + ChatColor.RED + 
                            " was already placed!", true);
                    break;
                }
            }
            
            if (!duplicate) {
                blockManager.createBlock(location, type, color);
                printer.printToPlayer(player, "Hidden block placed!", false);
            } else {
                event.setCancelled(true);
            }
        }
    }
}
