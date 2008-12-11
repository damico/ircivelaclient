package org.jdamico.ircivelaclient.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class FrDrw2FS extends JPanel implements MouseListener, MouseMotionListener {
	
	private static final long serialVersionUID = 327232587795767474L;
	
	private int width = 814;
	private int height = 473;
	private BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    private Point start, end;
    private Graphics2D g2d, g2dFS, g = null;
    private JButton saveImg = new JButton();
    private JButton eraseImg = new JButton(); 
    private JButton rubberImg = new JButton();
    
    private boolean rubberToggle = false;
    
    
    private int movedY, movedX;
    
    public FrDrw2FS() {
        this.setBackground(Color.BLACK);
        this.setForeground(Color.WHITE);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.init();
    }
    
    public void init(){
    	saveImg.setText("Save");
    	saveImg.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				write2FS(getImage());
			}
		});
    	
    	
    	eraseImg.setText("Erase");
    	eraseImg.addActionListener(new ActionListener(){
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			if(g2d!=null && g2dFS!=null){
    				g2dFS.setBackground(Color.black);
        			g2d.setBackground(Color.black);
        			g2dFS.clearRect(0, 0, width, height);
        			g2d.clearRect(0, 0, width, height);	
        			repaint();
    			}
    			
    		}
    	});
    	
    	rubberImg.setText("Rubber");
    	rubberImg.addActionListener(new ActionListener(){
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			
    			rubberToggle=!rubberToggle;
    			if(rubberToggle){
    				 Toolkit toolkit = Toolkit.getDefaultToolkit();
    	    	     Image image = toolkit.getImage("images/rubber.gif");
    	    	     Cursor c = toolkit.createCustomCursor(image , new Point(0,0), "img");
    	    	     setCursor (c);
    			}else{
    				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    			}
    			
    		}
    	});
    	
    	add(rubberImg);
    	add(saveImg);
    	add(eraseImg);
    	g2dFS = bufferedImage.createGraphics();
    }
    
     
    
    public void mousePressed(MouseEvent e) {
        start = new Point(e.getX(), e.getY());
    }

    public void mouseDragged(MouseEvent e) {
        g2d = (Graphics2D) this.getGraphics();
        if(!rubberToggle){
        	end = new Point(e.getX(), e.getY());
            g2d.setColor(Color.white);
            g2dFS.setColor(Color.white);
            g2d.drawLine(start.x, start.y, end.x, end.y);
            g2dFS.drawLine(start.x, start.y, end.x, end.y); /* Olha a gambi aqui! */
            start = end;
        }else{
        	
        	g2dFS.setBackground(Color.black);
			g2d.setBackground(Color.black);
			g2dFS.clearRect(e.getX(), e.getY(), 20, 20);
			g2d.clearRect(e.getX(), e.getY(), 20, 20);
			
        }
        
    }

    public RenderedImage getImage() {
        g2dFS.dispose();   
        return bufferedImage;
    }
    
    public void write2FS(RenderedImage rendImage){
        try {
            File file = new File("/home/jefferson/temp.png");
            ImageIO.write(rendImage, "png", file);
        } catch (IOException ioe) {
        	ioe.printStackTrace();
        }
    }
    
    public void mouseMoved(MouseEvent e){
    	
    }
    public void mouseClicked(MouseEvent e) 	{}
    public void mouseEntered(MouseEvent e) 	{}
    public void mouseExited(MouseEvent e)	{}
    public void mouseReleased(MouseEvent e) {}
}