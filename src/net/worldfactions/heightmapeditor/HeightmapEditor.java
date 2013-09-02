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

import javax.swing.*;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.util.ArrayList;
import com.sun.opengl.util.*;
import java.awt.Color;
import java.io.File;
import javax.imageio.ImageIO;
import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class HeightmapEditor implements GLEventListener, HeightmapEditListener, MouseListener, MouseMotionListener, KeyListener, ActionListener
{
	private int WINDOW_WIDTH = 1024;
	private int WINDOW_HEIGHT = 768;
	private float FIELD_OF_VIEW = 45.0f;
	private float aspectRatio = 1.0f;
	private boolean updateMVP = true;
	
	private FPSAnimator animator;
	private float view_rotx = 20.0f, view_roty = 30.0f, view_rotz = 0.0f;
	private float location_x = 0.0f, location_y = 0.0f, location_z = 0.0f;
	private int drawList;
	private float angle = 0.0f;
	private float keyMovement = 4.0f;
	
	private int prevMouseX, prevMouseY;
	private boolean mouseRButtonDown = false;
	private boolean updateList = false;
	
	private ArrayList<HeightmapSection> sections = new ArrayList<HeightmapSection>();
	private Heightmap heightmap;
	private String heightmapFile;
	
	private MenuItem newMenuItem = new MenuItem("New");
	private MenuItem openMenuItem = new MenuItem("Open");
	private MenuItem saveMenuItem = new MenuItem("Save");
	
	private JTextField loadXBox;
	private JTextField loadYBox;
	
	private JTextField redTextField;
	private JTextField greenTextField;
	private JTextField blueTextField;
	
	private JLabel dimensionLabel;
	private JLabel brushLabel;
	private JLabel hardnessLabel;
	private JLabel sizeLabel;
	private JSlider hardnessSlider;
	private JSlider sizeSlider;
	private JComboBox brushSelectBox;
	private JTabbedPane activeSegments;
	
	public HeightmapEditor()
	{
		JFrame frame = new JFrame("Heightmap Editor");
		GLCanvas canvas = new GLCanvas();
		animator = new FPSAnimator(canvas, 30);
		GroupLayout layout = new GroupLayout(frame.getContentPane());
		
		JLabel dimensionLabelLabel = new JLabel("Size:");
		dimensionLabel = new JLabel();
		
		/** Add button **/
		JLabel loadXLabel = new JLabel("X:");
		loadXBox = new JTextField("1");
		
		JLabel loadYLabel = new JLabel("Y:");
		loadYBox = new JTextField("1");
		
		final HeightmapEditor listener = this;
		
		JButton addSegmentButton = new JButton("Add Section");
		addSegmentButton.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent ev)
			{
				String xStr = loadXBox.getText();
				String yStr = loadYBox.getText();
				
				try
				{
					int x = Integer.parseInt(xStr);
					int y = Integer.parseInt(yStr);
					
					HeightmapSection section = heightmap.getSection(sections.size(), x, y);
					sections.add(section);
					
					PaintPanel pp = new PaintPanel();
					pp.setSize(activeSegments.getSize());
					pp.setSection(section);
					
					setPaintpanelSettings(pp);
					
					pp.setHeightmapEditListener(listener);
					activeSegments.add(xStr.concat(":").concat(yStr), pp);
					updateList = true;
				}
				catch(Exception ex)
				{
					System.out.println(ex.getMessage());
				}
			}
		});
		/** End Add button **/
		
		/** Brushes **/
		HardBrush hardBrush = new HardBrush(4, 1, Color.black);
		SoftBrush softBrush = new SoftBrush(4, 1, Color.black);
		RisingBrush risingBrush = new RisingBrush(4, 4);
		LoweringBrush loweringBrush = new LoweringBrush(4, 4);
		
		JLabel brushLabelLabel = new JLabel("Brush:");
		brushLabel = new JLabel(hardBrush.toString());
		brushSelectBox = new JComboBox();
		
		brushSelectBox.addItem(hardBrush);
		brushSelectBox.addItem(softBrush);
		brushSelectBox.addItem(risingBrush);
		brushSelectBox.addItem(loweringBrush);
		
		brushSelectBox.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				setActiveTabBrushSettings();
			}
		});
		/** End Brushes **/
		
		/** Brush Options **/
		JLabel hardnessLabelLabel = new JLabel("hardness:");
		JLabel sizeLabelLabel = new JLabel("size:");
		hardnessLabel = new JLabel();
		sizeLabel = new JLabel();
		hardnessSlider = new JSlider();
		sizeSlider = new JSlider();
		
		hardnessSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				hardnessLabel.setText(hardnessSlider.getValue() + "");
				setActiveTabBrushSettings();
			}
		});
		
		sizeSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				sizeLabel.setText(sizeSlider.getValue() + "");
				setActiveTabBrushSettings();
			}
		});
		
		hardnessSlider.setMaximum(255);
		hardnessSlider.setMinimum(1);
		
		sizeSlider.setMaximum(100);
		sizeSlider.setMinimum(1);
		/** End Brush Options **/
		
		/** Paint color set **/
		JLabel redLabel = new JLabel("R");
		redTextField = new JTextField("1");
		
		JLabel greenLabel = new JLabel("G");
		greenTextField = new JTextField("1");
		
		JLabel blueLabel = new JLabel("B");
		blueTextField = new JTextField("1");
		
		KeyAdapter colorChangeListener = new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				setActiveTabBrushSettings();
			}
		};
				
		redTextField.addKeyListener(colorChangeListener);
		greenTextField.addKeyListener(colorChangeListener);
		blueTextField.addKeyListener(colorChangeListener);
		
		activeSegments = new JTabbedPane();
		activeSegments.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				setActiveTabBrushSettings();
			}
		});
		/** End Paint color set **/
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addComponent(canvas)
			.addGap(5)
			.addGroup(
				layout.createParallelGroup()
				.addGroup(
					layout.createSequentialGroup()
					.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(dimensionLabelLabel)
						.addComponent(loadXLabel)
						.addComponent(loadYLabel)
					)
					.addGroup(
						layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(dimensionLabel)
						.addComponent(loadXBox)
						.addComponent(loadYBox)
					)
				)
				.addComponent(addSegmentButton)
				.addGroup(
					layout.createSequentialGroup()
					.addGroup(
						layout.createParallelGroup()
						.addGroup(
							layout.createSequentialGroup()
							.addComponent(hardnessLabelLabel)
							.addComponent(hardnessLabel)
						)
						.addComponent(hardnessSlider)
					)
					.addGroup(
						layout.createParallelGroup()
						.addGroup(
							layout.createSequentialGroup()
							.addComponent(sizeLabelLabel)
							.addComponent(sizeLabel)
						)
						.addComponent(sizeSlider)
					)
				)
				.addGroup(
					layout.createSequentialGroup()
					.addComponent(brushLabelLabel)
					.addComponent(brushLabel)
				)
				.addComponent(brushSelectBox)
				.addGroup(
					layout.createSequentialGroup()
					.addGroup(
						layout.createParallelGroup()
						.addComponent(redLabel)
						.addComponent(redTextField)
					)
					.addGroup(
						layout.createParallelGroup()
						.addComponent(greenLabel)
						.addComponent(greenTextField)
					)
					.addGroup(
						layout.createParallelGroup()
						.addComponent(blueLabel)
						.addComponent(blueTextField)
					)
				)
				.addComponent(activeSegments)
			)
		);
		
		layout.setVerticalGroup(layout.createParallelGroup()
			.addComponent(canvas)
			.addGroup(
				layout.createSequentialGroup()
				.addGroup(
					layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(dimensionLabelLabel)
					.addComponent(dimensionLabel)
				)
				.addGroup(
					layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(loadXLabel)
					.addComponent(loadXBox)
				)
				.addGroup(
					layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(loadYLabel)
					.addComponent(loadYBox)
				)
				.addComponent(addSegmentButton)
				.addGroup(
					layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(hardnessLabelLabel)
					.addComponent(hardnessLabel)
					.addComponent(sizeLabelLabel)
					.addComponent(sizeLabel)
				)
				.addGroup(
					layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(hardnessSlider)
					.addComponent(sizeSlider)
				)
				.addGroup(
					layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(brushLabelLabel)
					.addComponent(brushLabel)
				)
				.addComponent(brushSelectBox)
				.addGroup(
					layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(redLabel)
					.addComponent(greenLabel)
					.addComponent(blueLabel)
				)
				.addGroup(
					layout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(redTextField)
					.addComponent(greenTextField)
					.addComponent(blueTextField)
				)
				.addComponent(activeSegments)
			)
		);
		
		loadXBox.setSize(new Dimension(60, 25));
		loadXBox.setMaximumSize(new Dimension(60, 25));
		loadYBox.setSize(new Dimension(60, 25));
		loadYBox.setMaximumSize(new Dimension(60, 25));
		addSegmentButton.setSize(new Dimension(60, 25));
		
		brushSelectBox.setSize(new Dimension(100, 25));
		brushSelectBox.setMaximumSize(new Dimension(100, 25));
		
		redTextField.setSize(new Dimension(30, 25));
		redTextField.setMaximumSize(new Dimension(30, 25));
		greenTextField.setSize(new Dimension(30, 25));
		greenTextField.setMaximumSize(new Dimension(30, 25));
		blueTextField.setSize(new Dimension(30, 25));
		blueTextField.setMaximumSize(new Dimension(30, 25));
		
		activeSegments.setSize(new Dimension(300, 300));
		activeSegments.setMaximumSize(new Dimension(300, 300));
		
		canvas.setSize(new Dimension(700, 700));
		canvas.setMaximumSize(new Dimension(700, 700));
		
		canvas.addGLEventListener(this);
		frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		frame.setLayout(layout);
		frame.setMenuBar(getEditorMenuBar());
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		animator.start();
	}
		
	public MenuBar getEditorMenuBar()
	{
		MenuBar menu = new MenuBar();
		Menu fileMenu = new Menu("File");
		
		newMenuItem.addActionListener(this);
		saveMenuItem.addActionListener(this);
		openMenuItem.addActionListener(this);
		
		fileMenu.add(newMenuItem);
		fileMenu.add(openMenuItem);
		fileMenu.add(saveMenuItem);
		
		menu.add(fileMenu);
		
		return menu;
	}
	
	public void loadHeightmap(String file)
	{
		heightmap = new Heightmap(file);
		heightmapFile = file;
		sections.removeAll(sections);
		activeSegments.removeAll();
		dimensionLabel.setText(heightmap.getXSections() + "x" + heightmap.getYSections());
	}
	
	public void setActiveTabBrushSettings()
	{
		if(activeSegments == null)
		{
			return;
		}
		
		PaintPanel pp = (PaintPanel)activeSegments.getSelectedComponent();
		setPaintpanelSettings(pp);
	}
	
	public void setPaintpanelSettings(PaintPanel pp)
	{
		if(pp == null)
		{
			return;
		}
		
		try
		{
			Brush brush = (Brush)brushSelectBox.getSelectedItem();
			
			if(brush == null)
			{
				return;
			}
			
			brushLabel.setText(brush.toString());
			
			float r = Float.parseFloat(redTextField.getText())/255.0f;
			float g = Float.parseFloat(greenTextField.getText())/255.0f;
			float b = Float.parseFloat(blueTextField.getText())/255.0f;
			
			brush.setColor(new Color(r, g, b));
			brush.setHardness(hardnessSlider.getValue());
			brush.setSize(sizeSlider.getValue());
			
			pp.setBrush(brush);
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
	}
	
	public void init(GLAutoDrawable drawable)
	{
		// Use debug pipeline
		// drawable.setGL(new DebugGL(drawable.getGL()));
		GL gl = drawable.getGL();
		
		System.out.println("INIT GL IS: " + gl.getClass().getName());
		System.out.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
		
		gl.setSwapInterval(1);
		
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[]{ 5.0f, 5.0f, 10.0f, 0.0f }, 0);
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_LIGHT0);
		gl.glEnable(GL.GL_DEPTH_TEST);
		
		updateList(gl);
		
		gl.glEnable(GL.GL_NORMALIZE);

		drawable.addMouseListener(this);
		drawable.addKeyListener(this);
		drawable.addMouseMotionListener(this);
	}
	
	public void updateList(GL gl)
	{
		float  red[] = {0.2f, 0.9f, 0.1f, 1.0f};
		
		gl.glDeleteLists(drawList, 1);
		drawList = gl.glGenLists(1);
		gl.glNewList(drawList, GL.GL_COMPILE);
		gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT_AND_DIFFUSE, red, 0);
		for(int i = 0; i < sections.size(); i++)
		{
			gl.glPushMatrix();
			HeightmapSection section = sections.get(i);
			
			float widthOffset = (Heightmap.SECTION_WIDTH * Heightmap.SECTION_ITEM_WIDTH) - Heightmap.SECTION_ITEM_WIDTH;
			float heightOffset = (Heightmap.SECTION_HEIGHT * Heightmap.SECTION_ITEM_WIDTH) - Heightmap.SECTION_ITEM_WIDTH;
			float x = (section.x - 1) * widthOffset;
			float z = (section.y - 1) * heightOffset;
			gl.glTranslatef(x, -220.0f, z);
			
			gl.glBegin(GL.GL_TRIANGLES);
			for(int j = 0; j < section.triangles.size(); j++)
			{
				Triangle t = section.triangles.get(j);
				gl.glColor3d(t.color.x, t.color.y, t.color.z);
				gl.glNormal3d(t.normal.x, t.normal.y, t.normal.z);
				for(int q = 0; q < t.vectors.size(); q++)
				{
					Vector3D vec = t.vectors.get(q);
					gl.glVertex3d(vec.x, vec.y, vec.z);
				}
			}
			gl.glEnd();
			
			gl.glPopMatrix();
		}
		gl.glEndList();
	}
	
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		WINDOW_WIDTH = width;
		WINDOW_HEIGHT = height;
		aspectRatio = WINDOW_WIDTH/WINDOW_HEIGHT;
		updateMVP = true;
	}

	public void display(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		if(updateList)
		{
			updateList(gl);
			updateList = false;
		}
		
		if(updateMVP)
		{
			GLU glu = new GLU();
			gl.glMatrixMode(GL.GL_PROJECTION);

			gl.glLoadIdentity();

			glu.gluPerspective(45, aspectRatio, 1.0, -1000.0);
			gl.glMatrixMode(GL.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glRotatef(view_rotx, 1.0f, 0.0f, 0.0f);
			gl.glRotatef(view_roty + 90, 0.0f, 1.0f, 0.0f);
			gl.glRotatef(view_rotz, 0.0f, 0.0f, 1.0f);
			gl.glTranslatef(location_x, location_y, location_z);
			
			updateMVP = false;
		}
		
		gl.glClearColor(0.3f, 0.3f, 0.8f, 1.0f);
		
		if((drawable instanceof GLJPanel) && !((GLJPanel) drawable).isOpaque() && ((GLJPanel)drawable).shouldPreserveColorBufferIfTranslucent())
		{
			gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
		}
		else
		{
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		}
			
		gl.glPushMatrix();
		gl.glCallList(drawList);
		gl.glPopMatrix();
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
	{
		
	}
	
	public void mouseEntered(MouseEvent e)
	{
	
	}
	
	public void mouseExited(MouseEvent e)
	{
	
	}

	public void mousePressed(MouseEvent e)
	{
		prevMouseX = e.getX();
		prevMouseY = e.getY();

		if((e.getModifiers() & e.BUTTON3_MASK) != 0)
		{
			mouseRButtonDown = true;
		}
	}
	
	public void mouseReleased(MouseEvent e)
	{
		if((e.getModifiers() & e.BUTTON3_MASK) != 0)
		{
			mouseRButtonDown = false;
		}
	}
	
	public void mouseClicked(MouseEvent e)
	{
		
	}
	
	public void mouseDragged(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		Dimension size = e.getComponent().getSize();

		float thetaY = 360.0f * ( (float)(x-prevMouseX)/(float)size.width);
		float thetaX = 360.0f * ( (float)(prevMouseY-y)/(float)size.height);

		prevMouseX = x;
		prevMouseY = y;

		view_rotx += thetaX;
		view_roty += thetaY;
		updateMVP = true;
	}
	
	public void mouseMoved(MouseEvent e)
	{
		
	}
	
	public void keyTyped(KeyEvent e)
	{
		
	}

	public void keyPressed(KeyEvent e)
	{
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			animator.stop();
			System.exit(0);
		}
		
		if(e.getKeyCode() == KeyEvent.VK_A)
		{
			location_x -= keyMovement;
		}
		else if(e.getKeyCode() == KeyEvent.VK_D)
		{
			location_x += keyMovement;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_S)
		{
			location_z -= keyMovement;
		}
		else if(e.getKeyCode() == KeyEvent.VK_W)
		{
			location_z += keyMovement;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_E)
		{
			location_y -= keyMovement;
		}
		else if(e.getKeyCode() == KeyEvent.VK_R)
		{
			location_y += keyMovement;
		}
		
		updateMVP = true;
	}
	
	public void keyReleased(KeyEvent e)
	{
		
	}
	
	public void heightmapChanged(HeightmapSection section)
	{
		sections.set(section.index, section);
		updateList = true;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource().getClass().isInstance(MenuItem.class.getClass()))
		{
			MenuItem item = (MenuItem)e.getSource();
			if(item == openMenuItem)
			{
				menuOpenSelected();
			}
			else if(item == saveMenuItem)
			{
				menuSaveSelected();
			}
		}
	}
	
	public void menuNewSelected()
	{
		
	}
	
	public void menuOpenSelected()
	{
		JFileChooser fileChoser = new JFileChooser();
		
		if(fileChoser.showDialog(null, "Select a heightmap") == JFileChooser.APPROVE_OPTION)
		{
			loadHeightmap(fileChoser.getSelectedFile().getAbsolutePath());
		}
	}
	
	public void menuSaveSelected()
	{
		for(int i = 0; i < sections.size(); i++)
		{
			heightmap.applySection(sections.get(i));
		}
				
		String imageType = heightmapFile.substring(heightmapFile.lastIndexOf(".") + 1);
		try
		{
			ImageIO.write(heightmap.image, imageType, new File(heightmapFile));
		}
		catch(Exception ex)
		{
			JOptionPane.showMessageDialog(null, "Error saving image. Reason: ".concat(ex.getMessage()));
		}
	}
	
	public static void main(String[] args)
	{
		JFileChooser fileChoser = new JFileChooser();
		if(fileChoser.showDialog(null, "Select a heightmap") == JFileChooser.APPROVE_OPTION)
		{
			HeightmapEditor editor = new HeightmapEditor();
			editor.loadHeightmap(fileChoser.getSelectedFile().getAbsolutePath());
		}
		else
		{
			JOptionPane.showMessageDialog(null, "A file is needed to continue. Please try again.");
		}
	}
}