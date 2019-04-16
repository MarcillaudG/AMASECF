package messages;

import fr.irit.smac.amak.messaging.IAmakMessage;

public class MessageNotify implements IAmakMessage{

	private String name;

	public MessageNotify(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	
}
