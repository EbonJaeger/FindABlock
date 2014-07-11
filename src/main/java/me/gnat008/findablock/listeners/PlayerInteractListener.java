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
import me.gnat008.findablock.managers.ConfigurationManager;
import me.gnat008.findablock.managers.HiddenBlock;
import me.gnat008.findablock.util.Printer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    private ConfigurationManager config;
    private FindABlockPlugin plugin;
    private BlockManager blockManager;
    private Printer printer;

    public PlayerInteractListener(FindABlockPlugin plugin) {
        this.plugin = plugin;
        this.printer = plugin.getPrinter();
        this.config = plugin.getMainConfig();
        
        this.blockManager = BlockManager.getManager(plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer() == null || 
                event.getClickedBlock() == null || 
                event.getAction() == Action.LEFT_CLICK_AIR || 
                event.getAction() == Action.LEFT_CLICK_BLOCK) {
            return;
        }
        
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Location location = block.getLocation();
        Material type = block.getType();
        
        if (blockManager.getHiddenBlock(location) != null) {
            HiddenBlock hb = blockManager.getHiddenBlock(location);
            if (!(hb.getFoundBy().contains(player.getUniqueId().toString()))) {
                hb.addFound(player.getUniqueId().toString());
            } else {
                printer.printToPlayer(player, "You already found a " + ChatColor.WHITE + type + ChatColor.RED + "!", true);
            }
        }
    }
}
