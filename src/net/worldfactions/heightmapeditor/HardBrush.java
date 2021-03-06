
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
* Created by WorldFactions.net
* For feature implementation suggestions please visit https://www.facebook.com/WorldFactions
**/

import java.awt.Color;
import java.awt.image.BufferedImage;
public class HardBrush extends Brush 
{
	public HardBrush(int size, int hardness, Color color)
	{
		super("Hard Brush", size, hardness, color);
	}
	
	@Override
	public BufferedImage applyBrushToImage(BufferedImage im, int x, int y)
	{
		if(x < 0 || y < 0 || im == null || x >= im.getWidth() || y >= im.getHeight())
		{
			return im;
		}
		
		int half_size = getSize()/2;
		
		int x1 = WFMath.clamp(x - half_size, 0, im.getWidth() - 1);
		int x2 = WFMath.clamp(x + half_size, 0, im.getWidth() - 1);
		int y1 = WFMath.clamp(y - half_size, 0, im.getHeight() - 1);
		int y2 = WFMath.clamp(y + half_size, 0, im.getHeight() - 1);
		
		int rgb = getColor().getRGB();
		
		// Brute force way of circle.
		for(int i = x1; i <= x2; i++)
		{
			for(int j = y1; j <= y2; j++)
			{
				if(WFMath.distance(i, j, x, y) <= half_size)
				{
					im.setRGB(i, j, rgb);
				}
			}
		}
		
		return im;
	}
}
