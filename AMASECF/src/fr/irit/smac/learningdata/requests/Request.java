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

	@Override
	public String toString() {
		return "Request [criticality=" + criticality + ", agentName=" + agentName + ", id=" + id + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agentName == null) ? 0 : agentName.hashCode());
		result = prime * result + id;
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
		Request other = (Request) obj;
		if (agentName == null) {
			if (other.agentName != null)
				return false;
		} else if (!agentName.equals(other.agentName))
			return false;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
