
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

import java.awt.Color;
import java.awt.image.BufferedImage;
public class Brush
{
	private String name;
    private int size;
	private int hardness;
	private Color color;
	public Brush(String name, int size, int hardness, Color color)
	{
		this.name = name;
		this.size = size;
		this.hardness = hardness;
		this.color = color;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public int getSize()
	{
		return this.size;
	}
	
	public int getHardness()
	{
		return this.hardness;
	}
	
	public Color getColor()
	{
		return this.color;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setSize(int size)
	{
		this.size = size;
	}
	
	public void setHardness(int hardness)
	{
		this.hardness = hardness;
	}
	
	public void setColor(Color color)
	{
		this.color = color;
	}
	
	public BufferedImage applyBrushToImage(BufferedImage im, int x, int y)
	{
		return im;
	}
	
	@Override
	public String toString()
	{
		if(name == null)
		{
			return "Brush";
		}
		return name;
	}
}
