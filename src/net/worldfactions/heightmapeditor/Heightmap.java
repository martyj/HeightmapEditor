
package net.worldfactions.heightmapeditor;

/**
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
* 
* Version 0.1
* Created by WorldFactions.net
* For feature implementation suggestions please visit https://www.facebook.com/WorldFactions
**/

import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.*;
public class Heightmap
{
    public static final int SECTION_WIDTH = 64;
    public static final int SECTION_HEIGHT = 64;
    public static final float SECTION_ITEM_WIDTH = 8;
    public static final float SECTION_ITEM_DIVISOR = 212;
    public BufferedImage image;
	
    public Heightmap(String filename)
    {
        try
        {
            image = ImageIO.read(new File(filename));
        }
        catch(Exception e)
        {
            
        }
    }
    
    public int getXSections()
    {
        if(image == null)
        {
            return 0;
        }
        return image.getWidth()/SECTION_WIDTH;
    }
    
    public int getYSections()
    {
        if(image == null)
        {
            return 0;
        }
        return image.getHeight()/SECTION_HEIGHT;
    }
    
    public HeightmapSection getSection(int index, int x, int y)
    {
        return new HeightmapSection(index, x, y, image.getSubimage(x * SECTION_WIDTH, y * SECTION_HEIGHT, SECTION_WIDTH, SECTION_HEIGHT), SECTION_ITEM_WIDTH, SECTION_ITEM_DIVISOR);
    }
}
