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

package me.gnat008.findablock.util;

import java.util.EnumSet;
import java.util.Set;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 *
 * @author Gnat008
 */
public class ColorUtil {
    
    private static Set<Material> colorable = EnumSet.of(Material.WOOL, Material.CLAY, Material.GLASS);
    
    public static Color getColor(Block block) {
        if (!colorable.contains(block.getType())) {
            throw new IllegalArgumentException("Material is not colorable!");
        }
        
        Byte data = block.getData();
        return DyeColor.getByDyeData(data).getColor();
    }
}
