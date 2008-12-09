package org.jdamico.ircivelaclient.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class DrawPanel extends JPanel{
	
	public DrawPanel() {
		
		this.setSize(new Dimension(820,500));
		this.setBackground(Color.white);
	}

	@Override
	public void paint(Graphics g) {
		
		super.paint(g);
		
		g.drawString("Vc vai desenhar aqui.", 50, 50);
	}
}
