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

package me.gnat008.findablock.managers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.gnat008.findablock.FindABlockPlugin;
import me.gnat008.findablock.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
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
     * @param color The block's color.
     * @return The created Hidden Block.
     */
    public HiddenBlock createBlock(Location loc, Material type, Color color) {        
        int id = numBlocks;
        numBlocks++;
        
        HiddenBlock hb = new HiddenBlock(id, loc, type, color);
        hiddenBlocks.add(hb);
        
        plugin.getBlocksConfig().getConfig().set("Blocks." + id + ".location", serializeLoc(loc));
        plugin.getBlocksConfig().getConfig().set("Blocks." + id + ".type", type.toString());
        plugin.getBlocksConfig().getConfig().set("Blocks." + id + ".color", color);
        plugin.getBlocksConfig().getConfig().set("Blocks." + id + ".foundBy", hb.getFoundBy());
        
        List<Integer> list = plugin.getBlocksConfig().getConfig().getIntegerList("Blocks.Blocks");
        list.add(id);
        plugin.getBlocksConfig().getConfig().set("Blocks.Blocks", list);
        plugin.getBlocksConfig().saveConfig();
        
        return hb;
    }
    
    /**
     * Reloads a block from a location into memory.
     * 
     * @param loc The location of the block.
     * @return The Hidden Block.
     */
    public HiddenBlock reloadBlock(Location loc) {
        int id = numBlocks;
        numBlocks++;
        
        HiddenBlock hb = new HiddenBlock(id,
                loc, 
                loc.getBlock().getType(), 
                ColorUtil.getColor(loc.getBlock()));
        hb.setFoundBy(plugin.getBlocksConfig().getConfig().getStringList("Blocks." + id + ".foundBy"));
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
        
        plugin.getBlocksConfig().getConfig().set("Blocks." + hb.getID(), null);
        
        List<Integer> list = plugin.getBlocksConfig().getConfig().getIntegerList("Blocks.Blocks");
        list.remove(hb.getID());
        plugin.getBlocksConfig().getConfig().set("Blocks.Blocks", list);
        plugin.getBlocksConfig().saveConfig();
    }
    
    public void removeBlocks(String type) {
        Iterator itr = hiddenBlocks.listIterator();
        while (itr.hasNext()) {
            HiddenBlock hb = (HiddenBlock) itr.next();
            if (hb.getType() == Material.valueOf(type.toUpperCase())) {
                plugin.getBlocksConfig().getConfig().set("Blocks." + hb.getID(), null);
        
                List<Integer> list = plugin.getBlocksConfig().getConfig().getIntegerList("Blocks.Blocks");
                list.remove(hb.getID());
                plugin.getBlocksConfig().getConfig().set("Blocks.Blocks", list);
                plugin.getBlocksConfig().saveConfig();
                
                Bukkit.getWorld(hb.getLocation().getWorld().getName()).getBlockAt(hb.getLocation()).setType(Material.AIR);
                
                itr.remove();
            }
        }
    }
    
    public void removeBlocks() {
        Iterator itr = hiddenBlocks.listIterator();
        while (itr.hasNext()) {
            HiddenBlock hb = (HiddenBlock) itr.next();
            
            plugin.getBlocksConfig().getConfig().set("Blocks." + hb.getID(), null);
        
            List<Integer> list = plugin.getBlocksConfig().getConfig().getIntegerList("Blocks.Blocks");
            list.clear();
            plugin.getBlocksConfig().getConfig().set("Blocks.Blocks", list);
            plugin.getBlocksConfig().saveConfig();
            
            Bukkit.getWorld(hb.getLocation().getWorld().getName()).getBlockAt(hb.getLocation()).setType(Material.AIR);
            
            itr.remove();
        }
        
        numBlocks = 0;
    }
    
    /**
     * Adds a player to a block's List of players that have found the block.
     * 
     * @param p The Player who found the block.
     * @param hb The found block.
     */
    public void addFound(Player p, HiddenBlock hb) {
        if (hb == null) {
            return;
        }
        
        hb.addFound(p.getUniqueId().toString());
        
        plugin.getBlocksConfig().getConfig().set("Blocks." + hb.getID() + ".foundBy", hb.getFoundBy());
        plugin.getBlocksConfig().saveConfig();
    }
    
    /**
     * Removes a player from a block's List of players that have found the block.
     * 
     * @param p The player we are removing.
     * @param hb The found block.
     */
    public void removeFound(Player p, HiddenBlock hb) {
        if (hb == null) {
            return;
        }
        
        hb.removeFound(p.getUniqueId().toString());
        
        plugin.getBlocksConfig().getConfig().set("Blocks." + hb.getID() + ".foundBy", hb.getFoundBy());
        plugin.getBlocksConfig().saveConfig();
    }
    
    /**
     * Loads the blocks from a file into memory.
     */
    public void loadBlocks() {
        numBlocks = 0;
        
        if (plugin.getBlocksConfig().getConfig().getIntegerList("Blocks.Blocks").isEmpty()) {
            return;
        }
        
        for (int i : plugin.getBlocksConfig().getConfig().getIntegerList("Blocks.Blocks")) {
            HiddenBlock hb = reloadBlock(deserializeLoc(plugin.getBlocksConfig().getConfig().getString("Blocks." + i + ".location")));
            hb.setID(i);
        }
    }
    
    /**
     * Called from the main plugin via the onDisable() method.
     */
    public void unload() {
        plugin.getBlocksConfig().saveConfig();
        hiddenBlocks.clear();
    }
    
    public String serializeLoc(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }
    
    public Location deserializeLoc(String str) {
        String[] st = str.split(",");
        return new Location(Bukkit.getWorld(st[0]), Integer.parseInt(st[1]), Integer.parseInt(st[2]), Integer.parseInt(st[3]));
    }
}
