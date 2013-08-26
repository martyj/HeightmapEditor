
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
import java.awt.image.*;
import java.util.ArrayList;
public class HeightmapSection
{
	public int index;
    public int x;
    public int y;
	public float width;
	public float divisor;
	public BufferedImage image;
    public ArrayList<Triangle> triangles = new ArrayList<Triangle>();
    
    public HeightmapSection(int index, int x, int y, BufferedImage im, float width, float divisor)
    {
		this.index = index;
        this.x = x;
        this.y = y;
		this.image = im;
        this.width = width;
        this.divisor = divisor;
		updateData();
    }
	
	public void setPixel(int x, int y, int rgb)
	{
		if(image != null)
		{
			image.setRGB(x, y, rgb);
			updateData();
		}
	}
	
	public void updateData()
	{
		triangles.clear();
		
        int hmWidth = image.getWidth();
        int hmHeight = image.getHeight();
        int xm1 = image.getWidth() - 1;
        int zm1 = image.getHeight() - 1;
        
		for(int i = 0; i < zm1; i++)
		{
            for(int j = 0; j < xm1; j++)
            {
				int iwidth = i * hmWidth;
				int ij = iwidth + j;
				int i1j1 = (i + 1) * hmWidth + j + 1;
				int i1j = (i + 1) * hmWidth + j;
				int ij1 = iwidth + j + 1;

				int iw = (int)(i * width);
				int jw = (int)(j * width);
				int i1w = (int)((i + 1) * width);
				int j1w = (int)((j + 1) * width);
                
                int i1 = HeightmapSection.colorToInt(new Color(image.getRGB(i, j)));
                int i2 = HeightmapSection.colorToInt(new Color(image.getRGB(i, j + 1)));
                int i3 = HeightmapSection.colorToInt(new Color(image.getRGB(i + 1, j)));
                int i4 = HeightmapSection.colorToInt(new Color(image.getRGB(i + 1, j + 1)));
                
				double h_ij = ((double)i1)/divisor;
				double h_ij1 = ((double)i2)/divisor;
				double h_i1j = ((double)i3)/divisor;
				double h_i1j1 = ((double)i4)/divisor;

				Vector3D vec = new Vector3D(iw, h_ij1, j1w);
				Vector3D vec2 = new Vector3D(i1w, h_i1j, jw);
				Vector3D vec3 = new Vector3D(iw, h_ij, jw);

				Vector3D vec4 = new Vector3D(iw, h_ij1, j1w);
				Vector3D vec5 = new Vector3D(i1w, h_i1j1, j1w);
				Vector3D vec6 = new Vector3D(i1w, h_i1j, jw);

				Vector3D n1 = Vector3D.cross(Vector3D.subtract(vec2, vec), Vector3D.subtract(vec3, vec));
				Vector3D n2 = Vector3D.cross(Vector3D.subtract(vec5, vec4), Vector3D.subtract(vec6, vec4));
                
                Vector3D c1 = colorToVector3D(new Color(140, 140, 140));
                Vector3D c2 = colorToVector3D(new Color(140, 140, 140));
                
                triangles.add(new Triangle(vec, vec2, vec3, n1, c1));
                triangles.add(new Triangle(vec4, vec5, vec6, n2, c2));
            }
		}
	}
    
	public static Vector3D colorToVector3D(Color c)
	{
		return new Vector3D(c.getRed()/255.0f, c.getGreen()/255.0f, c.getBlue()/255.0f);
	}
	
    public static int colorToInt(Color c)
    {
        int g = c.getGreen() << 8;
        int b = c.getBlue();
        
        return g | b;
    }
	
	public String toString()
	{
		return "i: " + index + "    x: " + x + "  y: " + y;
	}
}
