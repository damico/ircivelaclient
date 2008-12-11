package org.jdamico.ircivelaclient.observer;

import java.util.Observable;
import java.util.Observer;

import org.jdamico.ircivelaclient.view.ChatPanel;
import org.jdamico.ircivelaclient.view.HandleApplet;
import org.jdamico.ircivelaclient.view.StaticData;

import jerklib.Channel;
import jerklib.events.IRCEvent;
import jerklib.events.JoinCompleteEvent;
import jerklib.events.JoinEvent;
import jerklib.events.MessageEvent;
import jerklib.events.QuitEvent;
import jerklib.events.IRCEvent.Type;

public class IRCResponseHandler implements Observer {

	private ChatPanel chatPanel;
	private HandleApplet handleApplet;
	
	public IRCResponseHandler(){
		
	}
	
	public IRCResponseHandler(HandleApplet handleApplet, ChatPanel chatPanel) {
		this.handleApplet = handleApplet;
		this.chatPanel = chatPanel;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		 
		if(arg instanceof IRCEvent){
			IRCEvent event = (IRCEvent)arg;
			if (event.getType() == Type.CONNECT_COMPLETE){
				System.out.println("CONNECT_COMPLETE");
				System.out.println("chat: " +chatPanel);
				chatPanel.enableControls();
				handleApplet.setLoadingFlag(false);
				
			}else if (event.getType() == Type.JOIN_COMPLETE){
				System.out.println("JOIN_COMPLETE");
				JoinCompleteEvent joinCompleteEvent = (JoinCompleteEvent)event;
				Channel currentChannel = joinCompleteEvent.getChannel();
				chatPanel.populateConnectedUsers(currentChannel);
			
			}else if (event.getType() == Type.CHANNEL_MESSAGE){
				System.out.println("CHANNEL_MESSAGE");
				MessageEvent messageEvent = (MessageEvent)event;
				String tempStr = messageEvent.getNick() + ": " + messageEvent.getMessage();
		        StaticData.chatMessage = tempStr;
		        //System.out.println("teste--->"+event.getMessage());
		        String color = chatPanel.getColor(messageEvent.getNick().trim());
		        chatPanel.updateMainContentArea(tempStr,color);
				
			}else if (event.getType() == Type.PRIVATE_MESSAGE){
				System.out.println("PRIVATE_MESSAGE");
				MessageEvent messageEvent = (MessageEvent)event;
				StaticData.chatMessage = messageEvent.getNick() + ": [private] " + messageEvent.getMessage();
				
			}else if (event.getType() == Type.JOIN){
				System.out.println("JOIN");
				JoinEvent joinEvent = (JoinEvent)event;
				chatPanel.addUserTable(joinEvent.getNick());
				
			}else if (event.getType() == Type.QUIT){
				System.out.println("QUIT");
				QuitEvent quitEvent = (QuitEvent)event;
				chatPanel.removeUserTable(quitEvent.getNick());
				
				String tempStr = quitEvent.getNick() + " has left (" + quitEvent.getQuitMessage()+")";
		        StaticData.chatMessage = tempStr;
		        //System.out.println("teste--->"+event.getMessage());
		        String color = chatPanel.getColor(quitEvent.getNick().trim());
		        chatPanel.updateMainContentArea(tempStr,color);
			}
				
		}
	}

	public ChatPanel getChatPanel() {
		return chatPanel;
	}

	public void setChatPanel(ChatPanel chatPanel) {
		this.chatPanel = chatPanel;
	}

	public HandleApplet getHandleApplet() {
		return handleApplet;
	}

	public void setHandleApplet(HandleApplet handleApplet) {
		this.handleApplet = handleApplet;
	}

	
}
