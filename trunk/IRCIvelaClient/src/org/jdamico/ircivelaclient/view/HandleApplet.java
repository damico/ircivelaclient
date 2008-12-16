package org.jdamico.ircivelaclient.view;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JApplet;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdamico.ircivelaclient.config.Constants;
import org.jdamico.ircivelaclient.listener.ListenConversation;
import org.jdamico.ircivelaclient.observer.IRCResponseHandler;
import org.jdamico.ircivelaclient.util.ChatPrinter;
import org.jdamico.ircivelaclient.util.IRCIvelaClientStringUtils;

public class HandleApplet extends JApplet  {

	 
	private static final long serialVersionUID = -97950894125721726L;

	private JTabbedPane mainTabbedPane;
	private ListenConversation chatter = null;
	private ChatPanel chatPanel;
	private FrDrw2FS drawPanel;
	
	
	
	//Executed when the applet is first created.
	public void init() {

		//System.out.println("--"+getCodeBase());
		
		ChatPrinter.print("init(): begin");

		StaticData.server = getParameter(Constants.PARAM_SERVER);
		StaticData.teacher = getParameter(Constants.PARAM_TEACHER);
		StaticData.channel = getParameter(Constants.PARAM_CHANNEL);
		//System.out.println(StaticData.channel);
		StaticData.nick = getParameter(Constants.PARAM_NICK);
		setLayout(new BorderLayout());
		
		//size and colour configuration
		String hexColor = getParameter(Constants.PARAM_BGCOLOR);
		Color bColor = IRCIvelaClientStringUtils.singleton().getColorParameter(hexColor);
		this.setBackground(bColor);
		this.setSize(820,500);
		
		//starting IRC facade
		this.chatter = new ListenConversation(this);
		Thread tC = new Thread(chatter);
		
		tC.start();
		 
		//creating panels
		this.mainTabbedPane = new JTabbedPane();
		this.chatPanel = new ChatPanel();
		this.drawPanel = new FrDrw2FS(this.chatPanel);
		
		//adding to tab
		this.mainTabbedPane.addTab("Chat", this.chatPanel);
		this.mainTabbedPane.addTab("BlackBoard", this.drawPanel);
		
		//adding panels
		this.add(this.mainTabbedPane,BorderLayout.CENTER);
		
		//adding observers
		IRCResponseHandler irResponseHandler = new IRCResponseHandler();
		irResponseHandler.setChatPanel(chatPanel);
		irResponseHandler.setHandleApplet(this);
		this.chatter.addObserver(irResponseHandler);
		
		//add tab listener
		/*mainTabbedPane.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				int index = ((JTabbedPane)e.getSource()).getSelectedIndex();
				
				if(index==1){
					
					drawPanel.transfer();
				}
				
				
			}
			
		});*/
		
		//end
		ChatPrinter.print("init(): end");
		
		 
	}


	public void destroy() {
		ChatPrinter.print("destroy()");
	}
 
	public void stop() {
		ChatPrinter.print("stop(): begin");
		
	}

	

	public ChatPanel getChatPanel() {
		return chatPanel;
	}


	public void setChatPanel(ChatPanel chatPanel) {
		this.chatPanel = chatPanel;
	}


	public void setLoadingFlag(boolean loadingFlag) {
		if(!loadingFlag){
			this.chatPanel.stopLoadingPanel();
			this.repaint();
		}
	}
		
}