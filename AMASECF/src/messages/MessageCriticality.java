package messages;

import fr.irit.smac.amak.messaging.IAmakMessage;

/**
 * 
 * @author gmarcill
 *
 * Message used to share the criticality between agent
 *
 */
public class MessageCriticality implements IAmakMessage{

	
	private double criticality;
	
	public MessageCriticality(double value) {
		this.criticality = value;
	}
	
	public double getCriticality() {
		return this.criticality;
	}
}
