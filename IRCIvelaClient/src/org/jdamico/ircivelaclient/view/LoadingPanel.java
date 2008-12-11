package org.jdamico.ircivelaclient.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class LoadingPanel extends JPanel{
	
	private static final long serialVersionUID = 1L;

	private boolean loadingFlag = true;
	
	private int counter = 0;
	private Font counterFont = new Font("arial",Font.BOLD,18);
	
	Icon loadingIcon = new ImageIcon(this.getClass().getResource("/org/jdamico/ircivelaclient/view/images/loading.gif"));
	
	public LoadingPanel() {
		
		this.setBackground(Color.white);
		
		//thread counter 
		Thread tCounter = new Thread(){
			
			@Override
			public void run() {
				while(loadingFlag){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					counter++;
				}
				
			}
		};
		tCounter.start();
	}
	
	 

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		//ChatPrinter.print("paint(Graphics g)");
		if(loadingFlag){
			 
	        this.loadingIcon.paintIcon(this, g2d, 0, 0) ;
	        
	        this.setFont(counterFont);
	        if(this.counter<10)
	        	g2d.drawString(""+this.counter, 145, 150);
	        else if(this.counter>=10 && this.counter<100)
	        	g2d.drawString(""+this.counter, 141, 150);
	        else
	        	g2d.drawString(""+this.counter, 137, 150);
	        g2d.drawString("Connecting, please wait.", 50, 175);
		}
	}



	public void setLoadingFlag(boolean loadingFlag) {
		this.loadingFlag = loadingFlag;
	}
 
}
