package fr.irit.smac.learningdata.requests;

import fr.irit.smac.learningdata.Agents.InputAgent.Operator;

public class RequestForWeight extends Request{

	private Operator decision;
	
	private String agentType;
	
	public RequestForWeight(double criticality, String agentName, int id, Operator decision, String agentType) {
		super(criticality, agentName, id);
		this.decision = decision;
		this.agentType = agentType;
	}

	public Operator getDecision() {
		return decision;
	}

	public void setDecision(Operator decision) {
		this.decision = decision;
	}

	public String getAgentType() {
		return agentType;
	}

	public void setAgentType(String agentType) {
		this.agentType = agentType;
	}
	
	
	
	

}
