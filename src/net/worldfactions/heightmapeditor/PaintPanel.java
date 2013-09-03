
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

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.*;
public class PaintPanel extends JPanel implements MouseListener, MouseMotionListener
{
	private int updateCount = 0;
	private HeightmapSection section;
	private HeightmapEditListener listener;
	private Brush brush;
	private Point mouse;
	public PaintPanel()
	{
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponents(g);
		if(section != null && section.image != null)
		{
			g.drawImage(section.image, 0, 0, this.getWidth(), this.getHeight(), 0, 0, section.image.getWidth(), section.image.getHeight(), this);
		}
		
		if(mouse != null)
		{
			int size = brush.getSize();
			int half_size = (int)Math.ceil(size/2.0f);
			g.setColor(Color.red);
			g.drawOval(mouse.x - half_size, mouse.y - half_size, brush.getSize(), brush.getSize());
		}
	}
	
	public Brush getBrush()
	{
		return brush;
	}
	
	public void setHeightmapEditListener(HeightmapEditListener l)
	{
		listener = l;
	}
	
	public void setSection(HeightmapSection sect)
	{
		section = sect;
	}
	
	public void setBrush(Brush b)
	{
		brush = b;
	}
	
	public void updateImage(MouseEvent e, boolean repaint)
	{
		if(section == null || section.image == null)
		{
			return;
		}
		
		int x = e.getX();
		int y = e.getY();
		x = (int)(x*(((float)section.image.getWidth())/((float)this.getWidth())));
		y = (int)(y*(((float)section.image.getHeight())/((float)this.getHeight())));
		
		if(x >= section.image.getWidth() || y >= section.image.getHeight() || x < 0 || y < 0)
		{
			return;
		}
		
		section.image = brush.applyBrushToImage(section.image, x, y);
		section.updateData();
		if(repaint)
		{
			if(listener != null)
			{
				listener.heightmapChanged(section);
			}
		
			repaint();
		}
	}
	
	public void mouseClicked(MouseEvent e)
	{
		updateImage(e, true);
	}

	public void mousePressed(MouseEvent e)
	{
		
	}

	public void mouseReleased(MouseEvent e)
	{
		updateCount = 0;
		updateImage(e, true);
	}

	public void mouseEntered(MouseEvent e)
	{
		mouse = new Point(e.getX(), e.getY());
		repaint();
	}

	public void mouseExited(MouseEvent e)
	{
		mouse = null;
		repaint();
	}

	public void mouseDragged(MouseEvent e)
	{
		updateCount++;
		updateImage(e, updateCount % 4 == 0);
	}

	public void mouseMoved(MouseEvent e)
	{
		mouse = new Point(e.getX(), e.getY());
		repaint();
	}
}
