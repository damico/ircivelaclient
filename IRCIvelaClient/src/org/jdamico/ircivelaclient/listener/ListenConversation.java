package org.jdamico.ircivelaclient.listener;

import java.util.ArrayList;
import java.util.Observer;

import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.ConnectionCompleteEvent;
import jerklib.events.JoinCompleteEvent;
import jerklib.events.JoinEvent;
import jerklib.events.MessageEvent;
import jerklib.events.QuitEvent;
import jerklib.listeners.DefaultIRCEventListener;

import org.jdamico.ircivelaclient.observer.IRCObservable;
import org.jdamico.ircivelaclient.view.HandleApplet;
import org.jdamico.ircivelaclient.view.StaticData;

public class ListenConversation extends DefaultIRCEventListener implements Runnable,IRCObservable {

	/**
	 * @param args
	 */
	
	
	private MessageEvent jce = null;
	private HandleApplet parent = null;
	private ArrayList<Observer> observerList; 
	
	public Session session;
    
	//TODO: change this as to not spam our channel
    static final String CHANNEL_TO_JOIN = StaticData.channel;
	
	public ListenConversation(HandleApplet parent){
		
		this.parent = parent;
		this.observerList = new ArrayList<Observer>();

    }

    public void run()
    {
    	
    	
		/*if(StaticData.nick.equals(StaticData.teacher)){
			System.out.println("teste");
			StaticData.nick = "|"+nick;
		}*/
		
		
		ConnectionManager manager = new ConnectionManager(new Profile(StaticData.nick, StaticData.nick, StaticData.nick, StaticData.nick));

        session = manager.requestConnection(StaticData.server);

        session.addIRCEventListener(this);

        setSession(session);
        System.out.println("session: " + session);
        
        
    }
    
    //handlers
    
    @Override
    protected void handleConnectComplete(ConnectionCompleteEvent event)
    {
        event.getSession().join(CHANNEL_TO_JOIN);
        System.out.println("conectado");
        
        /*parent.getChatPanel().enableControls();
        
        parent.setLoadingFlag(false);
        */
        this.notifyObservers(event);
        
    }
    
  
    protected void handleJoinCompleteEvent(JoinCompleteEvent event)
    {
        event.getChannel().say("Connected");
        StaticData.nick = session.getNick();
        this.parent.getChatPanel().setSession(session);
        
        /*Channel currentChannel = event.getChannel();
        if(currentChannel!=null)
        	parent.getChatPanel().populateConnectedUsers(currentChannel);
        */
        notifyObservers(event);
    }

    @Override
    protected void handleChannelMessage(MessageEvent event)
    {
    	 
    	/*String tempStr = event.getNick() + ": " + event.getMessage();
        StaticData.chatMessage = tempStr;
        //System.out.println("teste--->"+event.getMessage());
        String color = parent.getChatPanel().getColor(event.getNick().trim());
        parent.getChatPanel().updateMainContentArea(tempStr,color);
        */
    	notifyObservers(event);
    	this.setJce(event);
        
    	
       
            try
            {
                Thread.sleep(2000);
                
            }
            catch (InterruptedException e)
            {
                // *nothing*
            }
            
        
    }
    
    protected void handlePrivateMessage(MessageEvent event)
    {
    	this.notifyObservers(event);
    	//StaticData.chatMessage = event.getNick() + ": [private] " + event.getMessage();
    	this.setJce(event);
        
    }
    
   
    @Override
    protected void handleJoinEvent(JoinEvent event) {
    	 this.notifyObservers(event);
    	 //this.parent.getChatPanel().addUserTable(event.getNick());
    }
    
    @Override
    protected void handleQuitEvent(QuitEvent event) {
    	//System.out.println(event.getNick()+" has left");
    	this.notifyObservers(event);
    	//this.parent.getChatPanel().removeUserTable(event.getNick());
    	
    }

	
	
	 
	//Observer pattern methods
	
	@Override
	public void addObserver(Observer observer) {
		 
		this.observerList.add(observer);
	}

	@Override
	public void notifyObservers(Object arg) {
		
		for(Observer observer : this.observerList){
			observer.update(null, arg);
		}
	}
	
	//set & gets
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	//message
	public MessageEvent getJce() {
		return jce;
	}

	public void setJce(MessageEvent jce) {
		this.jce = jce;
	}
}

 