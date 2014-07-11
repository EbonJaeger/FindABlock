/*
 * Copyright (C) 2014 Gnat008
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package me.gnat008.findablock.managers;

import java.util.ArrayList;
import java.util.List;
import me.gnat008.findablock.FindABlockPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * This class keeps track of Hidden Blocks in the world(s).
 * 
 * @author Gnat008
 */
public class BlockManager {
    
    private int numBlocks = 0;
    private List<HiddenBlock> hiddenBlocks = new ArrayList<HiddenBlock>();
    
    private static BlockManager blockManager;
    
    private FindABlockPlugin plugin;
    
    private BlockManager(FindABlockPlugin plugin) {
        this.plugin = plugin;
    }
    
    public static BlockManager getManager(FindABlockPlugin plugin) {
        if (blockManager == null) {
            blockManager = new BlockManager(plugin);
        }
        
        return blockManager;
    }
    
    /**
     * Gets the Hidden Block at a location. It will return null if the block
     * is not there.
     * 
     * @param loc The location of the block.
     * @return The HiddenBlock, or null if there is none there.
     */
    public HiddenBlock getHiddenBlock(Location loc) {
        for (HiddenBlock hb : hiddenBlocks) {
            if (hb.getLocation().equals(loc) && hb.getType() == loc.getBlock().getType()) {
                return hb;
            }
        }
        
        return null;
    }
    
    /**
     * Creates a new Hidden Block at a location.
     * 
     * @param loc The location of the block.
     * @param type The block's Material.
     * @return The created Hidden Block.
     */
    public HiddenBlock createBlock(Location loc, Material type) {        
        int id = numBlocks + 1;
        numBlocks++;
        
        HiddenBlock hb = new HiddenBlock(id, loc, type);
        hiddenBlocks.add(hb);
        
        plugin.getConfig().set("Blocks." + id + ".location", serializeLoc(loc));
        plugin.getConfig().set("Blocks." + id + ".type", type);
        plugin.getConfig().set("Blocks." + id + ".foundBy", hb.getFoundBy());
        
        List<Integer> list = plugin.getConfig().getIntegerList("Blocks.Blocks");
        list.add(id);
        plugin.getConfig().set("Blocks.Blocks", list);
        plugin.saveConfig();
        
        return hb;
    }
    
    /**
     * Reloads a block from a location into memory.
     * 
     * @param loc The location of the block.
     * @return The Hidden Block.
     */
    public HiddenBlock reloadBlock(Location loc) {
        int id = numBlocks + 1;
        numBlocks++;
        
        HiddenBlock hb = new HiddenBlock(id, loc, loc.getBlock().getType());
        hb.setFoundBy(plugin.getConfig().getStringList("Blocks." + id + ".foundBy"));
        hiddenBlocks.add(hb);
        
        return hb;
    }
    
    /**
     * Gets the current list of hidden blocks.
     * 
     * @return The List of hidden blocks.
     */
    public List<HiddenBlock> getHiddenBlocks() {
        return this.hiddenBlocks;
    }
    
    /**
     * Removes a Hidden Block from memory and database.
     * 
     * @param loc The location of the block.
     */
    public void removeBlock(Location loc) {
        HiddenBlock hb = getHiddenBlock(loc);
        if (hb == null) {
            return;
        }
        
        hiddenBlocks.remove(hb);
        
        plugin.getConfig().set("Blocks." + hb.getType(), null);
        
        List<Integer> list = plugin.getConfig().getIntegerList("Blocks.Blocks");
        list.remove(hb.getID());
        plugin.getConfig().set("Blocks.Blocks", list);
        plugin.saveConfig();
    }
    
    /**
     * Adds a player to a block's List of players that have found the block.
     * 
     * @param p The Player who found the block.
     * @param loc The location of the block.
     */
    public void addFound(Player p, Location loc) {
        HiddenBlock hb = getHiddenBlock(loc);
        if (hb == null) {
            return;
        }
        
        hb.addFound(p.getUniqueId().toString());
        
        plugin.getConfig().set("Blocks." + hb.getType() + ".foundBy", hb.getFoundBy());
        plugin.saveConfig();
    }
    
    /**
     * Removes a player from a block's List of players that have found the block.
     * 
     * @param p The player we are removing.
     * @param loc The location of the block
     */
    public void removeFound(Player p, Location loc) {
        HiddenBlock hb = getHiddenBlock(loc);
        if (hb == null) {
            return;
        }
        
        hb.removeFound(p.getUniqueId().toString());
        
        plugin.getConfig().set("Blocks." + hb.getType() + ".foundBy", hb.getFoundBy());
        plugin.saveConfig();
    }
    
    /**
     * Loads the blocks from a file into memory.
     */
    public void loadBlocks() {
        numBlocks = 0;
        
        if (plugin.getConfig().getIntegerList("Blocks.Blocks").isEmpty()) {
            return;
        }
        
        for (int i : plugin.getConfig().getIntegerList("Blocks.Blocks")) {
            HiddenBlock hb = reloadBlock(deserializeLoc(plugin.getConfig().getString("Blocks." + i + ".location")));
            hb.setID(i);
        }
    }
    
    public String serializeLoc(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }
    
    public Location deserializeLoc(String str) {
        String[] st = str.split(",");
        return new Location(Bukkit.getWorld(st[0]), Integer.parseInt(st[1]), Integer.parseInt(st[2]), Integer.parseInt(st[3]));
    }
}
