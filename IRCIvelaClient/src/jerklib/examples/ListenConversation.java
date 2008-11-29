package jerklib.examples;

import jerklib.ConnectionManager;
import jerklib.Profile;
import jerklib.Session;
import jerklib.events.ConnectionCompleteEvent;
import jerklib.events.JoinCompleteEvent;
import jerklib.events.MessageEvent;
import jerklib.listeners.DefaultIRCEventListener;

import org.jdamico.ircivelaclient.view.StaticMessages;

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
    static final String CHANNEL_TO_JOIN = StaticMessages.channel;
	
	public ListenConversation()
    {
		
		String nick = StaticMessages.nick;
		if(StaticMessages.nick.equals(StaticMessages.teacher)){
			StaticMessages.nick = "|"+nick;
		}
		
		
		ConnectionManager manager = new ConnectionManager(new Profile(StaticMessages.nick, StaticMessages.nick+"_", StaticMessages.nick+"__", StaticMessages.nick+"___"));

        session = manager.requestConnection(StaticMessages.server);

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
    	
        StaticMessages.chatMessage = event.getNick() + ": " + event.getMessage();
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
   
    	StaticMessages.chatMessage = event.getNick() + ": [private] " + event.getMessage();
        setJce(event);
    }

	public MessageEvent getJce() {
		return jce;
	}

	public void setJce(MessageEvent jce) {
		this.jce = jce;
	}
	
	

}
