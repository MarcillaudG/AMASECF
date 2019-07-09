package fr.irit.smac.learningdata.requests;

public abstract class Request {

	private double criticality;
	
	private String agentName;
	
	private int id;
	
	public Request(double criticality,String agentName, int id) {
		this.criticality = criticality;
		this.agentName =agentName;
		this.id = id;
	}

	public double getCriticality() {
		return criticality;
	}

	public void setCriticality(double criticality) {
		this.criticality = criticality;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
}
