package messages;

import fr.irit.smac.amak.messaging.IAmakMessage;

public class MessageParameter implements IAmakMessage{

	private String name;
	
	private double value;
	
	public MessageParameter(String name, double value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public double getValue() {
		return value;
	}
	
	
}
