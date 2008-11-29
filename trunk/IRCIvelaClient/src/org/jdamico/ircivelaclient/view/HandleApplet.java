package org.jdamico.ircivelaclient.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

public class HandleApplet extends JApplet implements Runnable {

	/**
	 * 
	 */

	public static int row = 0;
	private boolean connected = false;
	

	private static final long serialVersionUID = -97950894125721726L;
	int width = 300, height = 300;
	int i = 0;

	// public static MessageEvent jce = null;
	public static Session session;
	public static ServerInformation si = null;
	public static String serverName = "*";

	// Frame f = null;
	Thread t = null;
	boolean threadSuspended;

	private JScrollPane jS = null;
	private JEditorPane tA = new JEditorPane();
	private JButton b1 = new JButton(actionSend);
	public static JTextArea msg = new JTextArea();
	public static JComboBox nicks = new JComboBox();
	private Document doc = null;
	private ListenConversation chatter = null;

	// TextArea tA = new TextArea(400,400);

	/**
	 * http://forums.sun.com/thread.jspa?threadID=174214
	 * 
	 */

	// Executed when the applet is first created.
	public void init() {

		System.out.println("init(): begin");

		StaticMessages.server = getParameter("server");
		StaticMessages.teacher = getParameter("teacher");
		StaticMessages.channel = getParameter("channel");
		StaticMessages.nick = getParameter("nick");

		setLayout(null);

		nicks.addItem("All");
		
		tA.setEditable(false);
		tA.setContentType("html/text");
		tA.setEditorKit(new HTMLEditorKit());

		setVisible(true);

		setBackground(Color.DARK_GRAY);
		tA.setBackground(Color.WHITE);

		b1.setText("Send");

		tA.setSize(800, 390);
		jS = new JScrollPane(tA);

		/**
		 * set text msg and send button disabled till complete connection
		 */
		msg.setEnabled(false);
		b1.setEnabled(false);
		
		
		msg.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				msgKeyPressed(evt);
			}

			private void msgKeyPressed(KeyEvent evt) {

				if (evt.getKeyCode() == KeyEvent.VK_ENTER) {

					sendMessage();

				}
			}

		});

		add(jS);
		add(msg);
		add(b1);
		add(nicks);

		jS.setBounds(5, 5, 800, 390);
		tA.setBackground(Color.WHITE);
		msg.setBounds(5, 400, 728, 70);
		b1.setBounds(738, 435, 68, 33);
		nicks.setBounds(738, 400, 68, 33);

		appendText(tA, "<font color='green'>IRCIvelaClient 0.0.25a</font><br>");

		System.out.println("init(): end");
	}

	// Executed when the applet is destroyed.
	public void destroy() {
		System.out.println("destroy()");
	}

	// Executed after the applet is created; and also whenever
	// the browser returns to the page containing the applet.
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

	// Executed whenever the browser leaves the page containing the applet.
	public void stop() {
		System.out.println("stop(): begin");
		threadSuspended = true;
	}

	// Executed within the thread that this applet created.
	public void run() {

		System.out.println("run(): begin");

		String oldMsg = "";
		String fontColor = "'blue'";
		String bgColor = "'#FFFFFF'";
		String msgTB_a = "";
		String msgTB_b = "";
		String msgTB_c = "";
		int c = 0;
		try {
			while (true) {
				i++;
				if (!oldMsg.equals(StaticMessages.chatMessage)) {

					tA.scrollRectToVisible(new Rectangle(0,
							tA.getBounds(null).height, 1, 1));

					if (StaticMessages.chatMessage.contains("|"
							+ StaticMessages.teacher)) {
						fontColor = "'red'";
					} else {
						fontColor = "'blue'";
					}

					if (row % 2 == 0)
						bgColor = "#EFEFEF";
					else
						bgColor = "#FFFFFF";

					msgTB_a = "<table width=\"100%\" bgcolor='"
						+ bgColor
						+ "' border ='0' cellpadding='4'><tr valign='top'><td width='90'>";
					msgTB_b = "</td><td>";
					msgTB_c = "</td></tr></table>";

					appendText(tA, msgTB_a + getTime() + msgTB_b
							+ "<font color=" + fontColor + ">"
							+ StaticMessages.chatMessage.replaceAll("|", "")
							+ "</font>" + msgTB_c);
					row++;

					// jce = chatter.getJce();
					tA.setFocusable(true);
					tA.setVisible(true);
					tA.setEnabled(true);

				}

				oldMsg = StaticMessages.chatMessage;
				tA.setBackground(Color.WHITE);

				t.sleep(2000); 
				

				if (serverName.length() > 2 && !isConnected()) {
					msg.setEnabled(true);
					msg.setEditable(true);
					b1.setEnabled(true);
					msg.setAutoscrolls(true);
					//msg.setWrapStyleWord(true);
					setConnected(true);
					msg.setText("");
					
				} else if(serverName.length() < 2) {
					String connMessage = ".";
					String m = "";
					int k = 0;
					while(k < c){
						m = m + connMessage;
						k++;
					}
					msg.setText("Connecting "+m);
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

	public static String getTime() {
		Format formatter;
		Date date = new Date();
		formatter = new SimpleDateFormat("HH:mm:ss");
		String stime = formatter.format(date);
		return "<font color'#efefef'>[" + stime + "]&nbsp</font>";
	}

	public static void sendMessage() {
		StaticMessages.clientMessage = msg.getText().replaceAll("|", "");
		// jce.getChannel().say(StaticMessages.clientMessage);
		Channel channel = session.getChannel(StaticMessages.channel);
		if(nicks.getSelectedItem().equals("All")){
			session.sayChannel(StaticMessages.clientMessage, channel);
		} else {
			session.sayPrivate(nicks.getSelectedItem().toString(), StaticMessages.clientMessage);
		}
		
		
		StaticMessages.chatMessage = "<font color'#666666'>Me: "
			+ StaticMessages.clientMessage + "</font>";
		msg.setText("");
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
		Channel currentChannel = session.getChannel(StaticMessages.channel);
		List<String> connectedUsers = currentChannel.getNicks();
		List<String> activeUsers = new ArrayList<String>();
		for(int j = 0; j < nicks.getItemCount(); j++){
			activeUsers.add(nicks.getItemAt(j).toString());
		}
		for(int l = 0; l < connectedUsers.size(); l++){
			if(!activeUsers.contains(connectedUsers.get(l))) nicks.addItem(connectedUsers.get(l));
		}
	}
}
