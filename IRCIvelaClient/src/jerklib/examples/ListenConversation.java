package jerklib.examples;

import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.ConnectionCompleteEvent;
import jerklib.events.JoinCompleteEvent;
import jerklib.events.MessageEvent;
import jerklib.listeners.DefaultIRCEventListener;

import org.jdamico.ircivelaclient.view.StaticData;

public class ListenConversation extends DefaultIRCEventListener implements Runnable {

	/**
	 * @param args
	 */
	
	
	private MessageEvent jce = null;
	
	public Session session;
    
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	//TODO: change this as to not spam our channel
    static final String CHANNEL_TO_JOIN = StaticData.channel;
	
	public ListenConversation()
    {
		
		String nick = StaticData.nick;
		if(StaticData.nick.equals(StaticData.teacher)){
			StaticData.nick = "|"+nick;
		}
		
		
		ConnectionManager manager = new ConnectionManager(new Profile(StaticData.nick, StaticData.nick+"_", StaticData.nick+"__", StaticData.nick+"___"));

        session = manager.requestConnection(StaticData.server);

        session.addIRCEventListener(this);
        
        setSession(session);

    }

    

    public void run()
    {
        
    }
    
    protected void handleJoinCompleteEvent(JoinCompleteEvent event)
    {
        event.getChannel().say("Connected");
        //setJce(event);
    }
    
    

    @Override
    protected void handleConnectComplete(ConnectionCompleteEvent event)
    {
        event.getSession().join(CHANNEL_TO_JOIN);
    }

    @Override
    protected void handleChannelMessage(MessageEvent event)
    {
    	
        StaticData.chatMessage = event.getNick() + ": " + event.getMessage();
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

	public MessageEvent getJce() {
		return jce;
	}

	public void setJce(MessageEvent jce) {
		this.jce = jce;
	}
	
	

}
