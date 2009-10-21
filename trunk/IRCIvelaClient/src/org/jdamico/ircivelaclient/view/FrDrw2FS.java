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
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jdamico.ircivelaclient.comm.ServletConnection;
import org.jdamico.ircivelaclient.config.Constants;
import org.jdamico.ircivelaclient.util.IRCIvelaClientStringUtils;
import org.jdamico.ircivelaclient.util.P2P;

public class FrDrw2FS extends JPanel implements MouseListener, MouseMotionListener {
	
	private static final long serialVersionUID = 327232587795767474L;
	
	private int width = 814;
	private int height = 473;
	private BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    private Point start, end;
    private Graphics2D g2d, g2dFS;
    private JButton saveImg = new JButton();
    private JButton eraseImg = new JButton(); 
    private JButton rubberImg = new JButton();
    private JButton sendAllImg = new JButton();
    private JButton updateImg = new JButton();
    private JButton testButton = new JButton();
    private JFileChooser jfChooser = new JFileChooser();
    
    private boolean rubberToggle = false;
    private ArrayList<P2P> drawn = new ArrayList<P2P>();
    private ArrayList<Point> erased = new ArrayList<Point>();
    
    private ServletConnection servletConnection;
    private ChatPanel chatPanel;
    
    public FrDrw2FS(ChatPanel chatPanel) {
        this.setBackground(Color.BLACK);
        this.setForeground(Color.WHITE);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        //this.udpConnection = new UDPConnection(Constants.UDP_PORT+1);
        this.servletConnection = new ServletConnection(StaticData.remoteServlet);
        this.chatPanel = chatPanel;
        //listening
        /*Thread t = new Thread(){
        	@Override
        	public void run() {
        		while(true){
        			String rcv = udpConnection.receive();
            		process(rcv);
        		}
        		
        	}
        };*/
        //t.start();
        
        
        this.init();
    }
    
    public void init(){
    	
    	jfChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    	
    	
    	saveImg.setText("Save");
    	saveImg.setToolTipText("Save Blackboard image in your HD.");
    	saveImg.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int returnVal = jfChooser.showOpenDialog(chatPanel);
				
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = jfChooser.getSelectedFile();
		            //This is where a real application would open the file.
		            String fileName = JOptionPane.showInputDialog("Enter file name");
		            String where = file.getAbsolutePath()+File.separatorChar+fileName+".png";
		            write2FS(getImage(),where);
		        } else {
		            System.out.println("Open command cancelled by user.");
		        }
		        
			}
		});
    	
    	
    	eraseImg.setText("Erase");
    	eraseImg.setToolTipText("Erase all you Blackboard.");
    	eraseImg.addActionListener(new ActionListener(){

    		public void actionPerformed(ActionEvent e) {
    			int confirm = JOptionPane.showConfirmDialog(chatPanel.getParent(), "Are you sure?");
    			if(confirm == JOptionPane.NO_OPTION)
    				return;
    			if(g2d!=null && g2dFS!=null){
    				g2dFS.setBackground(Color.black);
        			g2d.setBackground(Color.black);
        			g2dFS.clearRect(0, 0, width, height);
        			g2d.clearRect(0, 0, width, height);
        			
        			drawn.clear();
        			erased.clear();
        			repaint();
    			}
    			
    		}
    	});
    	
    	rubberImg.setText("Rubber");
    	rubberImg.setToolTipText("Rubber feature. Erase only what you want to.");
    	rubberImg.addActionListener(new ActionListener(){

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
    	
    	sendAllImg.setText("Send");
    	sendAllImg.setToolTipText("Send your draw to the server.");
    	sendAllImg.addActionListener(new ActionListener(){

    		public void actionPerformed(ActionEvent e) {
    			String snd = IRCIvelaClientStringUtils.generateInfoString(drawn);
    			
    			//System.out.println("SEND");
    			//System.out.println(snd);
    			try {
    				
					servletConnection.send(StaticData.classFolder+"@"+snd);
					
					JOptionPane.showMessageDialog(chatPanel.getParent(), "Picture sent to server. \nPlease, notify your students to update their blackboards.");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    			//chatPanel.sendBroadcastPrivateMessage("DRAW_IV " +snd);
    			/*ArrayList<String> users = chatPanel.getUserHost();

    			for(String user:users){
    				System.out.println("-->"+user);
    				InetAddress address = udpConnection.connect(user);
        			udpConnection.send(snd, address,Constants.UDP_PORT);
    			}
    			*/
    			
    		}
    	});
    	
    	updateImg.setText("Update");
    	updateImg.setToolTipText("Update the BlackBoard with a new draw.");
    	updateImg.addActionListener(new ActionListener(){

    		public void actionPerformed(ActionEvent e) {
    			
    			try {
					String s = servletConnection.readRemoteFile();
					if(s!=null){
						process(s);
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    			
    		}
    	});
    	
    	testButton.setText("Test");
    	testButton.setToolTipText("Test button.");
    	testButton.addActionListener(new ActionListener(){

    		public void actionPerformed(ActionEvent e) {
    			 
    			chatPanel.getSession().close("Closed browser.");
    		}
    	});
    	
    	if(StaticData.isTeacher){
    		add(sendAllImg);	
    	}
    	add(rubberImg);
    	add(eraseImg);
    	add(updateImg);
    	add(saveImg);
    	//add(testButton);
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
            
            P2P p2p = new P2P();
            p2p.p1 = start;
            p2p.p2 = end;
            this.drawn.add(p2p);
            start = end;
        }else{
        	
        	g2dFS.setBackground(Color.black);
			g2d.setBackground(Color.black);
			g2dFS.clearRect(e.getX(), e.getY(), 20, 20);
			g2d.clearRect(e.getX(), e.getY(), 20, 20);
			Point p = new Point(e.getX(), e.getY());
			
			erased.add(p);
        }
        
    }    
    
    public void process(String rcv){
    	//System.out.println("RCCV-->"+rcv);
    	this.drawn = (ArrayList<P2P>)IRCIvelaClientStringUtils.degenerateInfoString(rcv);
    	repaint();
    }
    
    private RenderedImage getImage() {
        g2dFS.dispose();   
        return bufferedImage;
    }
    
    private void write2FS(RenderedImage rendImage,String path){
        try {
            File file = new File(path);
            ImageIO.write(rendImage, "png", file);
        } catch (IOException ioe) {
        	ioe.printStackTrace();
        }
    }
    
    public void mouseMoved(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) 	{}
    public void mouseEntered(MouseEvent e) 	{}
    public void mouseExited(MouseEvent e)	{}
    public void mouseReleased(MouseEvent e) {}
    
    @Override
    public void paint(Graphics g) {
    
    	super.paint(g);
    	
    	Graphics2D g2dTemp = (Graphics2D)g;
    	for(P2P p2p : drawn){
    		
    		g2dTemp.drawLine(p2p.p1.x, p2p.p1.y, p2p.p2.x, p2p.p2.y);
    		g2dFS.drawLine(p2p.p1.x, p2p.p1.y, p2p.p2.x, p2p.p2.y);
    	}
    	
    	for(Point p : erased){
    		g2dTemp.setBackground(Color.black);
    		g2dTemp.clearRect(p.x, p.y, 20, 20);
    		g2dFS.setBackground(Color.black);
    		g2dFS.setBackground(Color.black);
    	}
    	
    }
}

