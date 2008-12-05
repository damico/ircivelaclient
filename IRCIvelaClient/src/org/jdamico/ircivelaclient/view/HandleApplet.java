package org.jdamico.ircivelaclient.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import jerklib.Channel;
import jerklib.ServerInformation;
import jerklib.Session;
import jerklib.examples.ListenConversation;

import org.jdamico.ircivelaclient.config.Constants;
import org.jdamico.ircivelaclient.util.ChatPrinter;
import org.jdamico.ircivelaclient.util.IRCIvelaClientStringUtils;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

public class HandleApplet extends JApplet  {

	/**
	 * 
	 */

	private static int row = 0;
	private boolean connected = false;
	private static final long serialVersionUID = -97950894125721726L;
	private int i = 0;
	private static Session session;
	private static ServerInformation si = null;
	private static String serverName = "*";
	private Thread t = null;
	boolean threadSuspended;
	private JScrollPane mainContentScrollPane = null;
	private JEditorPane mainContentArea = new JEditorPane();
	private JButton sendMessageButton = new JButton();
	private static JTextArea messageArea = new JTextArea();
	private static JComboBox nicksComboBox = new JComboBox();
	private Document doc = null;
	private ListenConversation chatter = null;
	
	
	private boolean loadingFlag = true;
	private Image loadingGif ; 
	private boolean showWaitMsg = true ; // will be set to false after image downloads
	
	private Hashtable userColorTable = new Hashtable();
	String[] colors = {"red","black","green","orange","pink","gray"};
	/**
	 * http://forums.sun.com/thread.jspa?threadID=174214
	 * 
	 */

	// Executed when the applet is first created.
	public void init() {

		//System.out.println("--"+getCodeBase());
		
		ChatPrinter.print("init(): begin");

		StaticData.server = getParameter(Constants.PARAM_SERVER);
		StaticData.teacher = getParameter(Constants.PARAM_TEACHER);
		StaticData.channel = getParameter(Constants.PARAM_CHANNEL);
		System.out.println(StaticData.channel);
		StaticData.nick = getParameter(Constants.PARAM_NICK);
		setLayout(null);
		
		
		String hexColor = getParameter(Constants.PARAM_BGCOLOR);
		Color bColor = IRCIvelaClientStringUtils.singleton().getColorParameter(hexColor);
		this.setBackground(bColor);
		this.setSize(820,480);
		
		nicksComboBox.addItem(Constants.NICKSCOMBOBOX_FIRST_ELEMENT);
		mainContentArea.setEditable(false);
		mainContentArea.setContentType(Constants.MAINCONTENT_CONTENT_TYPE);
		mainContentArea.setEditorKit(new HTMLEditorKit());
		setVisible(true);
		
		
		mainContentArea.setBackground(Color.WHITE);
		sendMessageButton.setText(Constants.SEND_BUTTON_NAME);
		mainContentArea.setSize(800, 390);
		mainContentScrollPane = new JScrollPane(mainContentArea);

		/**
		 * set text msg and send button disabled till complete connection
		 */
		messageArea.setEnabled(false);
		messageArea.setAutoscrolls(true);
		messageArea.setLineWrap(true);
		messageArea.setWrapStyleWord(false);
		sendMessageButton.setEnabled(false);
		
		
		messageArea.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				msgKeyPressed(evt);
			}

			private void msgKeyPressed(KeyEvent evt) {
				if (evt.getKeyCode() == KeyEvent.VK_ENTER){
					
					//update my MsgContentArea
					String tempMsg = messageArea.getText().replaceAll("Constants.TEACHER_IDENTIFIER", Constants.BLANK_STRING);
					tempMsg = messageArea.getText().replaceAll(Constants.LINE_BREAK, Constants.BLANK_STRING);
					updateMainContentArea(tempMsg,"blue");
					//send remote msg to IRC
					sendMessage();
				}
					
			}

		});
		
		sendMessageButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//update my MsgContentArea
				String tempMsg = messageArea.getText().replaceAll("Constants.TEACHER_IDENTIFIER", Constants.BLANK_STRING);
				tempMsg = messageArea.getText().replaceAll(Constants.LINE_BREAK, Constants.BLANK_STRING);
				updateMainContentArea(tempMsg,"blue");
				//send remote msg to IRC
				sendMessage();
				
			}
		});

		add(mainContentScrollPane);
		add(messageArea);
		add(sendMessageButton);
		add(nicksComboBox);

		mainContentScrollPane.setBounds(5, 5, 800, 390);
		mainContentArea.setBackground(Color.WHITE);
		messageArea.setBounds(5, 400, 728, 70);
		sendMessageButton.setBounds(738, 435, 68, 33);
		nicksComboBox.setBounds(738, 400, 68, 33);

		appendText(mainContentArea, IRCIvelaClientStringUtils.singleton().showVersion());

		ChatPrinter.print("init(): end");
		
		 
	}


	public void destroy() {
		ChatPrinter.print("destroy()");
	}

	public void start() {

		chatter = new ListenConversation(this);
		Thread tC = new Thread(chatter);
		tC.start();
		try {
			tC.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/*if (t == null) {
			ChatPrinter.print("start(): creating thread");
			t = new Thread(this);
			ChatPrinter.print("start(): starting thread");
			threadSuspended = false;
			t.start();
		}*/
		 
		session = chatter.getSession();
		si = session.getServerInformation();
		

	}

	public void stop() {
		ChatPrinter.print("stop(): begin");
		threadSuspended = true;
	}

	 

	public void paint(Graphics g) {
		super.paint(g);
		//ChatPrinter.print("paint(Graphics g)");
		if(loadingFlag){
			 // test a boolean to if the "loading" message should be displayed
	        if ( showWaitMsg )
	        {
	            g.drawString( "Please wait... loading..." , 20 , 20 );
	            loadGraphics(); // call the method that actually loads the graphics
	        } 
	        else
	        {
	            g.drawImage( loadingGif , 240 , 25 , this );
	            
	        }
		}
	}

	private JEditorPane appendText(JEditorPane tA, String text) {
		doc = (Document) tA.getDocument();
		try {

			((HTMLEditorKit) tA.getEditorKit()).read(new java.io.StringReader(
					text), tA.getDocument(), tA.getDocument().getLength());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		tA.setCaretPosition(doc.getLength());

		return tA;
	}

	

	public static void sendMessage() {
		
		String tempMsg = messageArea.getText().replaceAll("Constants.TEACHER_IDENTIFIER", Constants.BLANK_STRING);
		tempMsg = messageArea.getText().replaceAll(Constants.LINE_BREAK, Constants.BLANK_STRING);
		
		StaticData.clientMessage = tempMsg;
		
		Channel channel = session.getChannel(StaticData.channel);
		if(nicksComboBox.getSelectedItem().equals("All")){
			//session.sayChannel(StaticData.clientMessage, channel);
			channel.say(StaticData.clientMessage);
		} else {
			//session.sayPrivate(nicksComboBox.getSelectedItem().toString(), StaticData.clientMessage);
			channel.say(StaticData.clientMessage);
		}
		
		StaticData.chatMessage = IRCIvelaClientStringUtils.singleton().setMyMessage(StaticData.clientMessage);
		messageArea.setText(Constants.BLANK_STRING);
		messageArea.setFocusable(true);
	}
	
	 
	
	public static void sendSystemMessage(String sysMsg) {

		Channel channel = session.getChannel(StaticData.channel);
		channel.say(StaticData.clientMessage);
		
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	public static void setConnectedUsers(){
		
		Channel currentChannel = session.getChannel(StaticData.channel);
		System.out.println("1:" + currentChannel); 
		List<String> connectedUsers = currentChannel.getNicks();
		System.out.println("2:" + connectedUsers); 
		List<String> activeUsers = new ArrayList<String>();
		 
		for(int j = 0; j < nicksComboBox.getItemCount(); j++){
			activeUsers.add(nicksComboBox.getItemAt(j).toString());
		}
		
		System.out.println("3:" + activeUsers); 
		
		for(int l = 0; l < connectedUsers.size(); l++){
			if(!activeUsers.contains(connectedUsers.get(l))) nicksComboBox.addItem(connectedUsers.get(l));
		}
	}
	
	
	public void populateConnectedUsers(Channel currentChannel){
		 
		 
		List<String> connectedUsers = currentChannel.getNicks();
		 
		List<String> activeUsers = new ArrayList<String>();
		 
		for(int j = 0; j < nicksComboBox.getItemCount(); j++){
			activeUsers.add(nicksComboBox.getItemAt(j).toString());
		}
	
		for(int l = 0; l < connectedUsers.size(); l++){
			
			if(!activeUsers.contains(connectedUsers.get(l))) 
				nicksComboBox.addItem(connectedUsers.get(l));
			
			int pos = IRCIvelaClientStringUtils.generateRandomNumber(colors.length-1);
			userColorTable.put(connectedUsers.get(l).trim(), colors[pos]);
		
		}
	}
	
	public void enableControls(){
		
		//enabling gui;
		messageArea.setEnabled(true);
		messageArea.setEditable(true);
		sendMessageButton.setEnabled(true);
		messageArea.setAutoscrolls(true);
		messageArea.setLineWrap(true);
		messageArea.setWrapStyleWord(false);
		setConnected(true);
		messageArea.setText(Constants.BLANK_STRING);
		
		//get conected users
		//HandleApplet.setConnectedUsers();
	}
	
	public void updateMainContentArea(String msg, String color){
		mainContentArea.scrollRectToVisible(new Rectangle(0,mainContentArea.getBounds(null).height, 1, 1));
		System.out.println("color: " +color);
		appendText(mainContentArea, IRCIvelaClientStringUtils.singleton().setMessage(msg, row,color));
		row++;

		mainContentArea.setFocusable(true);
		mainContentArea.setVisible(true);
		mainContentArea.setEnabled(true);
	}


	public void setLoadingFlag(boolean loadingFlag) {
		this.loadingFlag = loadingFlag;
	}
	
	public void loadGraphics() 
    {
        // now load the graphics - this is like your "real" init
        loadingGif = getImage( getCodeBase() , "loading.gif" );
        
        MediaTracker mt = new MediaTracker( this );
        mt.addImage( loadingGif , 0 );
    
        try 
        {
            mt.waitForAll();  // block here until images are downloaded
        }
        catch ( InterruptedException e ) 
        {
        }
    
        showWaitMsg = false ; // it is safe for paint to draw the image now
        repaint();
    } // close loadGraphics 
	
	public String getColor(String nick){
		return (String)this.userColorTable.get(nick);
	}
	
	public void addUserTable(String nick){
		
		if(userColorTable.contains(nick.trim()))
			return;
		int pos = IRCIvelaClientStringUtils.generateRandomNumber(colors.length-1);
		
		userColorTable.put(nick.trim(), colors[pos]);
		nicksComboBox.addItem(nick.trim());
	}

	public void removeUserTable(String nick){
		userColorTable.remove(nick.trim());
		nicksComboBox.removeItem(nick.trim());
		nicksComboBox.repaint();
	}
	
	
}