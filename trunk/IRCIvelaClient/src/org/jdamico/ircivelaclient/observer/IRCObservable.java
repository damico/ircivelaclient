package org.jdamico.ircivelaclient.observer;

import java.util.Observer;

public interface IRCObservable {

	public void addObserver(Observer observer);
	public void notifyObservers(Object arg);
}
