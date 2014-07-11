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
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * An object of this class is created when a user with the correct permission
 * places a 'hidden block.'
 * 
 * @author Gnat008
 */
public class HiddenBlock {
    
    private int id;
    private Location location;
    private Material type;    
    private List<String> foundBy;
    
    public HiddenBlock(int id, Location location, Material type) {
        this.id = id;
        this.location = location;
        this.type = type;
        
        this.foundBy = new ArrayList<String>();
    }
    
    /**
     * Get the ID of a block.
     * 
     * @return The block's ID.
     */
    public int getID() {
        return this.id;
    }
    
    /**
     * Gets the location of the object.
     * 
     * @return The block's location.
     */
    public Location getLocation() {
        return this.location;
    }
    
    /**
     * Gets the material type of the object.
     * 
     * @return The block's Material.
     */
    public Material getType() {
        return this.type;
    }
    
    /**
     * Gets a List of player UUIDs that have found the block. Used when checking if
     * a player has already found a specific block, and for determining which
     * blocks they have found.
     * 
     * @return The List of players that have found the block.
     */
    public List<String> getFoundBy() {
        return this.foundBy;
    }
    
    /**
     * Adds a player's UUID in String from to the list of players that have found
     * the block.
     * 
     * @param uuid The UUID String of the player who found the block.
     */
    public void addFound(String uuid) {
        this.foundBy.add(uuid);
    }
    
    /**
     * Sets the list of players who have found the block. Used when reloading
     * the plugin (server restart).
     * 
     * @param uuids The List of player UUIDs to use.
     */
    public void setFoundBy(List<String> uuids) {
        this.foundBy = uuids;
    }
    
    /**
     * Removes a player's UUID from the list of players that have found the block.
     * 
     * @param uuid The UUID String of the player.
     */
    public void removeFound(String uuid) {
        this.foundBy.remove(uuid);
    }
    
    /**
     * Sets the numerical ID of a block. Used when reloading the plugin (server
     * restart). Intended to preserve block to ID.
     * 
     * @param id The ID of the block.
     */
    public void setID(int id) {
        this.id = id;
    }
}
