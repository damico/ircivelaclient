package org.jdamico.ircivelaclient.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import jerklib.Channel;
import jerklib.Session;

import org.jdamico.ircivelaclient.config.Constants;
import org.jdamico.ircivelaclient.util.IRCIvelaClientStringUtils;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

public class ChatPanel extends JPanel{
	
	private static int row = 0;
	
	private static final long serialVersionUID = -97950894125721726L;

	private Session session;

	boolean threadSuspended;
	private JScrollPane mainContentScrollPane = null;
	private JEditorPane mainContentArea = new JEditorPane();
	//private JButton sendMessageButton = new JButton();
	private static JTextArea messageArea = new JTextArea();
	//private static JComboBox nicksComboBox = new JComboBox();
	private LoadingPanel loadingPanel;
	private UsersPanel usersPanel;
	private Document doc = null;
	
	private Hashtable userColorTable = new Hashtable();
	private ArrayList<String> usersHost = new ArrayList<String>();
	String[] colors = {"purple","black","green","pink","gray"};
	
	public ChatPanel(HandleApplet parent) {
		
		this.setLayout(null);
		
		this.usersPanel = new UsersPanel(parent);
		this.usersPanel.setSize(100,100);
		
		//this.setSize(new Dimension(600,480));
		
		this.loadingPanel = new LoadingPanel();
		this.loadingPanel.setSize(300,300);
		this.loadingPanel.setLocation(250, 50);
		
		//nicksComboBox.addItem(Constants.NICKSCOMBOBOX_FIRST_ELEMENT);
		mainContentArea.setEditable(false);
		mainContentArea.setContentType(Constants.MAINCONTENT_CONTENT_TYPE);
		mainContentArea.setEditorKit(new HTMLEditorKit());
		setVisible(true);
		
		mainContentArea.setBackground(Color.WHITE);
		//sendMessageButton.setText(Constants.SEND_BUTTON_NAME);
		//mainContentArea.setSize(700, 390);
		mainContentScrollPane = new JScrollPane(mainContentArea);

		/**
		 * set text msg and send button disabled till complete connection
		 */
		messageArea.setEnabled(false);
		messageArea.setAutoscrolls(true);
		messageArea.setLineWrap(true);
		messageArea.setWrapStyleWord(false);
		//sendMessageButton.setEnabled(false);
		
		
		messageArea.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				msgKeyPressed(evt);
			}

			private void msgKeyPressed(KeyEvent evt) {
				if (evt.getKeyCode() == KeyEvent.VK_ENTER){
					
					//update my MsgContentArea
					String tempMsg = messageArea.getText().replaceAll("Constants.TEACHER_IDENTIFIER", Constants.BLANK_STRING);
					tempMsg = messageArea.getText().replaceAll(Constants.LINE_BREAK, Constants.BLANK_STRING);
					updateMainContentArea("Me: "+tempMsg,"blue");
					//send remote msg to IRC
					sendMessage();
				}
					
			}

		});
		
		/*sendMessageButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//update my MsgContentArea
				String tempMsg = messageArea.getText().replaceAll("Constants.TEACHER_IDENTIFIER", Constants.BLANK_STRING);
				tempMsg = messageArea.getText().replaceAll(Constants.LINE_BREAK, Constants.BLANK_STRING);
				updateMainContentArea("Me: "+tempMsg,"blue");
				//send remote msg to IRC
				sendMessage();
				
			}
		});*/

		JScrollPane scrollPane = new JScrollPane(this.usersPanel);
		 
		
		add(mainContentScrollPane);
		add(messageArea);
		//add(sendMessageButton);
		//add(nicksComboBox);
		add(loadingPanel);
		add(scrollPane);
		
		scrollPane.setBounds(810,5,100,390);
		mainContentScrollPane.setBounds(5, 5, 800, 390);
		mainContentArea.setBackground(Color.WHITE);
		messageArea.setBounds(5, 400, 904, 70);
		messageArea.setBorder(BorderFactory.createLineBorder(Color.black));
		//sendMessageButton.setBounds(738, 435, 68, 33);
		//nicksComboBox.setBounds(738, 400, 68, 33);

		appendText(mainContentArea, IRCIvelaClientStringUtils.singleton().showVersion());
		appendText(mainContentArea, "<b>USER: " + StaticData.nick+"</b>");
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

	

	public void sendMessage() {
		
		String tempMsg = messageArea.getText().replaceAll("Constants.TEACHER_IDENTIFIER", Constants.BLANK_STRING);
		tempMsg = messageArea.getText().replaceAll(Constants.LINE_BREAK, Constants.BLANK_STRING);
		
		StaticData.clientMessage = tempMsg;
		
		Channel channel = session.getChannel(StaticData.channel);
		
		channel.say(StaticData.clientMessage);
		 
		
		StaticData.chatMessage = IRCIvelaClientStringUtils.singleton().setMyMessage(StaticData.clientMessage);
		messageArea.setText(Constants.BLANK_STRING);
		messageArea.setFocusable(true);
	}
	
	public void sendBroadcastPrivateMessage(String msg){
		Enumeration<String> nicks = this.userColorTable.keys();
		while(nicks.hasMoreElements()){
			String nickTemp = nicks.nextElement();
			session.sayPrivate(nickTemp, msg);
		}
	}
	 
	
	public void sendSystemMessage(String sysMsg) {

		Channel channel = session.getChannel(StaticData.channel);
		channel.say(StaticData.clientMessage);
		
	}
	
	public void setConnectedUsers(){
		
		Channel currentChannel = session.getChannel(StaticData.channel);
		//System.out.println("1:" + currentChannel); 
		List<String> connectedUsers = currentChannel.getNicks();
		//System.out.println("2:" + connectedUsers); 
		List<String> activeUsers = new ArrayList<String>();
		 
		/*for(int j = 0; j < nicksComboBox.getItemCount(); j++){
			activeUsers.add(nicksComboBox.getItemAt(j).toString());
		}*/
		
		//System.out.println("3:" + activeUsers); 
		
		/*for(int l = 0; l < connectedUsers.size(); l++){
			if(!activeUsers.contains(connectedUsers.get(l))) nicksComboBox.addItem(connectedUsers.get(l));
		}*/
	}
	
	
	public void populateConnectedUsers(Channel currentChannel){
		 
		 
		List<String> connectedUsers = currentChannel.getNicks();
		 
		List<String> activeUsers = new ArrayList<String>();
		
		 
		/*for(int j = 0; j < nicksComboBox.getItemCount(); j++){
			activeUsers.add(nicksComboBox.getItemAt(j).toString());
		}*/
	
		for(int l = 0; l < connectedUsers.size(); l++){
			 
			addUserTable(connectedUsers.get(l).trim());
			 
		}
	}
	
	public void enableControls(){
		
		//enabling gui;
		messageArea.setEnabled(true);
		messageArea.setEditable(true);
		//sendMessageButton.setEnabled(true);
		messageArea.setAutoscrolls(true);
		messageArea.setLineWrap(true);
		messageArea.setWrapStyleWord(false);
		messageArea.setText(Constants.BLANK_STRING);
		
		//get conected users
		//HandleApplet.setConnectedUsers();
	}
	
	public void updateMainContentArea(String msg, String color){
		mainContentArea.scrollRectToVisible(new Rectangle(0,mainContentArea.getBounds(null).height, 1, 1));
		//System.out.println("color: " +color);
		appendText(mainContentArea, IRCIvelaClientStringUtils.singleton().setMessage(msg, row,color));
		row++;

		mainContentArea.setFocusable(true);
		mainContentArea.setVisible(true);
		mainContentArea.setEnabled(true);
	}	
	
	public String getColor(String nick){
		return (String)this.userColorTable.get(nick);
	}
	
	public void addUserTable(String nick){
		
		if(userColorTable.contains(nick.trim()))
			return;
		
		if(StaticData.teacher.equalsIgnoreCase(nick.trim())){
			System.out.println("louco");
			userColorTable.put(nick.trim(), "red");
			//nicksComboBox.addItem(nick.trim());
			return;
		}
			
		int pos = IRCIvelaClientStringUtils.generateRandomNumber(colors.length-1);
		
		userColorTable.put(nick.trim(), colors[pos]);
		//nicksComboBox.addItem(nick.trim());
		this.usersPanel.addUser(nick.trim());
	}

	public void removeUserTable(String nick){
		userColorTable.remove(nick.trim());
		//nicksComboBox.removeItem(nick.trim());
		this.usersPanel.removeUser(nick.trim());
		//nicksComboBox.repaint();
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public void stopLoadingPanel(){
		this.loadingPanel.setLoadingFlag(false);
		this.remove(this.loadingPanel);
	}
	
	public void addUserHost(String ip){
		this.usersHost.add(ip.trim());
	}
	
	public void removeUserHost(String ip){
		this.usersHost.remove(ip.trim());
	}
	
	public ArrayList<String> getUserHost(){
		return this.usersHost;
	}
}
