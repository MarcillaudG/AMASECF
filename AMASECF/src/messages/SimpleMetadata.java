package messages;

import fr.irit.smac.amak.messaging.IAmakMessageMetaData;

public class SimpleMetadata implements IAmakMessageMetaData{
	
	private double timeOfSend;
	

	public SimpleMetadata(double timeOfSend) {
		this.timeOfSend = timeOfSend;
	}

	public double getTimeOfSend() {
		return timeOfSend;
	}

	
}