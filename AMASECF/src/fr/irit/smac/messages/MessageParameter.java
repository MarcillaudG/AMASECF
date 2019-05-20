package fr.irit.smac.messages;

import fr.irit.smac.amak.messaging.IAmakMessage;

public class MessageParameter implements IAmakMessage{

	private String name;
	
	private double value;
	
	private int nbJump;
	
	public MessageParameter(String name, double value) {
		this.name = name;
		this.value = value;
		this.nbJump=0;
	}

	public String getName() {
		return name;
	}

	public double getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessageParameter other = (MessageParameter) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value))
			return false;
		return true;
	}
	
	public int getNbJump() {
		return this.nbJump;
	}
	
	public void increaseJump() {
		this.nbJump++;
	}
}
