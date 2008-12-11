package org.jdamico.ircivelaclient.view;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
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
import javax.swing.JButton;

public class FrDrw2FS extends Applet implements MouseListener, MouseMotionListener {
	
	private static final long serialVersionUID = 327232587795767474L;
	private BufferedImage bufferedImage = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
    private Point start, end;
    private Graphics2D g2d, g2dFS = null;
    private JButton saveImg = new JButton();
    
    public FrDrw2FS() {
        this.setBackground(Color.BLACK);
        this.setForeground(Color.WHITE);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }
    
    public void init(){
    	saveImg.setText("Save, Jeff!");
    	saveImg.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				write2FS(getImage());
			}
		});
    	add(saveImg);
    	g2dFS = bufferedImage.createGraphics();
    }
    
    public void mousePressed(MouseEvent e) {
        start = new Point(e.getX(), e.getY());
    }

    public void mouseDragged(MouseEvent e) {
        g2d = (Graphics2D) this.getGraphics();        
        end = new Point(e.getX(), e.getY());
        g2d.drawLine(start.x, start.y, end.x, end.y);
        g2dFS.drawLine(start.x, start.y, end.x, end.y); /* Olha a gambi aqui! */
        start = end;
    }

    public RenderedImage getImage() {
        g2dFS.dispose();   
        return bufferedImage;
    }
    
    public void write2FS(RenderedImage rendImage){
        try {
            File file = new File("/tmp/damico.png");
            ImageIO.write(rendImage, "png", file);
        } catch (IOException ioe) {
        	ioe.printStackTrace();
        }
    }
    
    public void mouseMoved(MouseEvent e) 	{}
    public void mouseClicked(MouseEvent e) 	{}
    public void mouseEntered(MouseEvent e) 	{}
    public void mouseExited(MouseEvent e)	{}
    public void mouseReleased(MouseEvent e) {}
}