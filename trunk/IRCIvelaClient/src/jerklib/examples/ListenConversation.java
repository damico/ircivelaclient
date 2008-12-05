package jerklib.examples;

import jerklib.Channel;
import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.ConnectionCompleteEvent;
import jerklib.events.IRCEvent;
import jerklib.events.JoinCompleteEvent;
import jerklib.events.JoinEvent;
import jerklib.events.MessageEvent;
import jerklib.events.QuitEvent;
import jerklib.events.IRCEvent.Type;
import jerklib.listeners.DefaultIRCEventListener;
import jerklib.tasks.TaskImpl;

import org.jdamico.ircivelaclient.view.HandleApplet;
import org.jdamico.ircivelaclient.view.StaticData;

public class ListenConversation extends DefaultIRCEventListener implements Runnable {

	/**
	 * @param args
	 */
	
	
	private MessageEvent jce = null;
	private HandleApplet parent = null;
	
	public Session session;
    
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	//TODO: change this as to not spam our channel
    static final String CHANNEL_TO_JOIN = StaticData.channel;
	
	public ListenConversation(HandleApplet parent){
		
		this.parent = parent;

    }

    

    public void run()
    {
    	
    	String nick = StaticData.nick;
		if(StaticData.nick.equals(StaticData.teacher)){
			StaticData.nick = "|"+nick;
		}
		
		
		ConnectionManager manager = new ConnectionManager(new Profile(StaticData.nick, StaticData.nick, StaticData.nick, StaticData.nick));

        session = manager.requestConnection(StaticData.server);

        session.addIRCEventListener(this);

        setSession(session);
        
        
    }
    
    protected void handleJoinCompleteEvent(JoinCompleteEvent event)
    {
        event.getChannel().say("Connected");
        
        Channel currentChannel = session.getChannel(StaticData.channel);
        if(currentChannel!=null)
        	parent.populateConnectedUsers(currentChannel);
    }
    
    

    @Override
    protected void handleConnectComplete(ConnectionCompleteEvent event)
    {
        event.getSession().join(CHANNEL_TO_JOIN);
        //System.out.println("conectado");
        parent.enableControls();
        
        parent.setLoadingFlag(false);
        
        
    }

    @Override
    protected void handleChannelMessage(MessageEvent event)
    {
    	 
    	String tempStr = event.getNick() + ": " + event.getMessage();
        StaticData.chatMessage = tempStr;
        //System.out.println("teste--->"+event.getMessage());
        String color = parent.getColor(event.getNick().trim());
        parent.updateMainContentArea(tempStr,color);
        setJce(event);
       
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
    	
    	StaticData.chatMessage = event.getNick() + ": [private] " + event.getMessage();
        setJce(event);
    }
    
   
    @Override
    protected void handleJoinEvent(JoinEvent event) {
    	 
    	this.parent.addUserTable(event.getNick());
    }
    
    @Override
    protected void handleQuitEvent(QuitEvent event) {
    	//System.out.println(event.getNick()+" has left");
    	this.parent.removeUserTable(event.getNick());
    }

	public MessageEvent getJce() {
		return jce;
	}

	public void setJce(MessageEvent jce) {
		this.jce = jce;
	}
	 

}

 