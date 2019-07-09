package fr.irit.smac.learningdata.requests;

public class Response {

	private String agentName;
	
	private boolean accepted;

	public Response(String agentName, boolean accepted) {
		this.agentName = agentName;
		this.accepted = accepted;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}
	
	
}
