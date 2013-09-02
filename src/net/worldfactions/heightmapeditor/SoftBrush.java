
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
public class SoftBrush extends Brush 
{
	public SoftBrush(int size, int hardness, Color color)
	{
		super("Soft Brush", size, hardness, color);
	}
	
	@Override
	public BufferedImage applyBrushToImage(BufferedImage im, int x, int y)
	{
		if(x < 0 || y < 0 || im == null || x >= im.getWidth() || y >= im.getHeight())
		{
			return im;
		}
		
		int half_size = (int)Math.ceil(getSize()/2);
		
		int x1 = WFMath.clamp(x - half_size, 0, im.getWidth() - 1);
		int x2 = WFMath.clamp(x + half_size, 0, im.getWidth() - 1);
		int y1 = WFMath.clamp(y - half_size, 0, im.getHeight() - 1);
		int y2 = WFMath.clamp(y + half_size, 0, im.getHeight() - 1);
		
		Color desiredColor = getColor();
		
		System.out.printf("x1: " + x1 + "\tx2:" + x2 + "\ty1:" + y1 + "\ty2:" + y2);
		// Brute force way of circle.
		for(int i = x1; i <= x2; i++)
		{
			for(int j = y1; j <= y2; j++)
			{
				double distance = WFMath.distance(i, j, x, y);
				if(distance <= half_size)
				{
					float newTanX = ((float)x - i)/half_size;
					float newTanY = ((float)y - j)/half_size;
					double angle = Math.atan2(newTanY, newTanX);
										
					double newcos = Math.cos(angle);
					double newsin = Math.sin(angle);
					System.out.println("origX: " + x + "\torigY:" + y + "\tx: " + newTanX + "\ty: " + newTanY + "\tnx:" + ((float)newcos) + "\tny:" + ((float)newsin) + "\tangle:" + (angle * 57.2957795));
					
					int plus_one_size = half_size + 1;
					int newx = (int)WFMath.clamp(x + (plus_one_size*Math.cos(angle)), 0, im.getWidth() - 1);
					int newy = (int)WFMath.clamp(y + (plus_one_size*Math.sin(angle)), 0, im.getHeight() - 1);
					
					Color edgecolor = new Color(im.getRGB(newx, newy));
					double newPercent = distance/half_size;
					double oldPercent = 1 - newPercent;
					
					int r = (int)WFMath.clamp(desiredColor.getRed() * oldPercent + edgecolor.getRed() * newPercent, 0, 255);
					int g = (int)WFMath.clamp(desiredColor.getGreen() * oldPercent + edgecolor.getGreen() * newPercent, 0, 255);
					int b = (int)WFMath.clamp(desiredColor.getBlue() * oldPercent + edgecolor.getBlue() * newPercent, 0, 255);
					
					im.setRGB(i, j, new Color(r, g, b).getRGB());
				}
			}
		}
		
		return im;
	}
}
