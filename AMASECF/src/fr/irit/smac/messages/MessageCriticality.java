package fr.irit.smac.messages;

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
	
	private String parameters;
	
	public MessageCriticality(String param,double value) {
		this.criticality = value;
		this.parameters = param;
	}
	
	public double getCriticality() {
		return this.criticality;
	}
	
	public String getParam() {
		return this.parameters;
	}
}
