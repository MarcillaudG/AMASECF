package fr.irit.smac.learningdata.requests;

public class Offer {

	private String nameOfAgent;
	
	private double offer;

	public String getNameOfAgent() {
		return nameOfAgent;
	}

	public void setNameOfAgent(String nameOfAgent) {
		this.nameOfAgent = nameOfAgent;
	}

	public double getOffer() {
		return offer;
	}

	public void setOffer(double offer) {
		this.offer = offer;
	}

	public Offer(String nameOfAgent, double offer) {
		super();
		this.nameOfAgent = nameOfAgent;
		this.offer = offer;
	}
	
	
}
