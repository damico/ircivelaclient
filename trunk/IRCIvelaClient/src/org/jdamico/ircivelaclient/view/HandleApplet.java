package org.jdamico.ircivelaclient.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
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
import org.jdamico.ircivelaclient.util.IRCIvelaClientStringUtils;

public class HandleApplet extends JApplet implements Runnable {

	/**
	 * May the force be with you!
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
	private JButton sendMessageButton = new JButton(actionSend);
	private static JTextArea messageArea = new JTextArea();
	private static JComboBox nicksComboBox = new JComboBox();
	private Document doc = null;
	private ListenConversation chatter = null;
	/**
	 * http://forums.sun.com/thread.jspa?threadID=174214
	 * 
	 */

	// Executed when the applet is first created.
	public void init() {

		System.out.println("init(): begin");

		StaticData.server = getParameter(Constants.PARAM_SERVER);
		StaticData.teacher = getParameter(Constants.PARAM_TEACHER);
		StaticData.channel = getParameter(Constants.PARAM_CHANNEL);
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
				if (evt.getKeyCode() == KeyEvent.VK_ENTER) sendMessage();
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

		System.out.println("init(): end");
	}


	public void destroy() {
		System.out.println("destroy()");
	}

	public void start() {

		chatter = new ListenConversation();
		Thread tC = new Thread(chatter);
		tC.start();

		if (t == null) {
			System.out.println("start(): creating thread");
			t = new Thread(this);
			System.out.println("start(): starting thread");
			threadSuspended = false;
			t.start();
		}

		session = chatter.getSession();
		si = session.getServerInformation();

	}

	public void stop() {
		System.out.println("stop(): begin");
		threadSuspended = true;
	}

	public void run() {

		System.out.println("run(): begin");

		String oldMsg = Constants.BLANK_STRING;
		

		int c = 0;
		try {
			while (true) {
				i++;
				if (!oldMsg.equals(StaticData.chatMessage)) {

					mainContentArea.scrollRectToVisible(new Rectangle(0,mainContentArea.getBounds(null).height, 1, 1));

					appendText(mainContentArea, IRCIvelaClientStringUtils.singleton().setMessage(StaticData.chatMessage, row));
					row++;

					mainContentArea.setFocusable(true);
					mainContentArea.setVisible(true);
					mainContentArea.setEnabled(true);

				}

				oldMsg = StaticData.chatMessage;
				mainContentArea.setBackground(Color.WHITE);

				t.sleep(2000); 
				

				if (serverName.length() > 2 && !isConnected()) {
					messageArea.setEnabled(true);
					messageArea.setEditable(true);
					sendMessageButton.setEnabled(true);
					messageArea.setAutoscrolls(true);
					messageArea.setLineWrap(true);
					messageArea.setWrapStyleWord(false);
					setConnected(true);
					messageArea.setText(Constants.BLANK_STRING);
					
				} else if(serverName.length() < 2) {
					String connMessage = " .";
					String m = Constants.BLANK_STRING;
					int k = 0;
					while(k < c){
						m = m + connMessage;
						k++;
					}
					messageArea.setText("Connecting "+m+"["+k+"]");
				} else if(c > Constants.CONN_TIMEOUT && !isConnected()) {
					messageArea.setText(messageArea.getText()+"[TIME OUT]");
					t.interrupt();
				}
				
				if(isConnected()) setConnectedUsers();

				serverName = si.getServerName();
				si = session.getServerInformation();
				c++;
				
			}
		} catch (InterruptedException e) {
		}
		System.out.println("run(): end");
	}

	public void paint(Graphics g) {
		System.out.println("paint(Graphics g)");
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
			session.sayChannel(StaticData.clientMessage, channel);
		} else {
			session.sayPrivate(nicksComboBox.getSelectedItem().toString(), StaticData.clientMessage);
		}
		
		StaticData.chatMessage = IRCIvelaClientStringUtils.singleton().setMyMessage(StaticData.clientMessage);
		messageArea.setText(Constants.BLANK_STRING);
		messageArea.setFocusable(true);
	}
	
	public static void sendSystemMessage(String sysMsg) {

		Channel channel = session.getChannel(StaticData.channel);
		session.sayChannel(StaticData.clientMessage, channel);
		
	}

	public static Action actionSend = new AbstractAction() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			sendMessage();
		}
	};
	
	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}
	
	public static void setConnectedUsers(){
		Channel currentChannel = session.getChannel(StaticData.channel);
		List<String> connectedUsers = currentChannel.getNicks();
		List<String> activeUsers = new ArrayList<String>();
		for(int j = 0; j < nicksComboBox.getItemCount(); j++){
			activeUsers.add(nicksComboBox.getItemAt(j).toString());
		}
		for(int l = 0; l < connectedUsers.size(); l++){
			if(!activeUsers.contains(connectedUsers.get(l))) nicksComboBox.addItem(connectedUsers.get(l));
		}
	}
}
