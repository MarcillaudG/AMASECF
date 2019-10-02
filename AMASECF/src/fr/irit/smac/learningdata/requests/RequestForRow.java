package fr.irit.smac.learningdata.requests;

import fr.irit.smac.learningdata.Agents.InputAgent.Operator;

public class RequestForRow extends Request{

	private Operator decision;
	public RequestForRow(double criticality, String agentName, int id, Operator decision) {
		super(criticality, agentName, id);
		this.decision = decision;
	}
	public Operator getDecision() {
		return decision;
	}

	public void setDecision(Operator decision) {
		this.decision = decision;
	}
}
