package fr.irit.smac.messages;

import fr.irit.smac.amak.messaging.IAmakMessage;

public class MessageNeighbour implements IAmakMessage{

	private String name;
	
	public MessageNeighbour(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
}
