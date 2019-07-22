package fr.irit.smac.learningdata.Agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.irit.smac.learningdata.requests.RequestColumn;

public class ColumnAgent extends AgentLearning{

	private double criticality;
	
	private DataAgent dataAgent;
	
	private List<InputAgent> column;
	
	private String name;
	
	private int idRequest;
	
	private Map<Integer,RequestColumn> waitingRequest;
	
	private boolean requestDenied;
	
	private LearningFunction function;
	
	public ColumnAgent(String name, DataAgent dataAgent, Collection<InputAgent> column,LearningFunction function) {
		this.name = name;
		this.dataAgent = dataAgent;
		this.column = new ArrayList<InputAgent>(column);
		this.idRequest = 0;
		this.criticality = 0.0;
		this.requestDenied = false;
		this.waitingRequest = new TreeMap<Integer,RequestColumn>();
		this.function = function;
	}
	
	public void perceive() {
		int somme = 0;
		for(InputAgent inputAgent : this.column) {
			if(this.dataAgent.getInputChosen().contains(inputAgent.getName())) {
				somme += 1;
			}
		}
		this.criticality = somme > 1 ? Math.pow(somme,2) : 0.0;
	}
	
	public void decideAndAct() {
		if(this.requestDenied) {
			this.criticality = Math.pow(this.criticality, 2);
		}
		if(this.criticality > 0 ) {
			System.out.println("COLONNE");
			searchForService();
			this.function.constraintNotRespected();
		}
	}

	/**
	 * Request the data agent
	 */
	private void searchForService() {
		this.waitingRequest.put(idRequest, new RequestColumn(this.criticality, this.name,idRequest));
		this.dataAgent.sendRequest(this.waitingRequest.get(idRequest));
		idRequest++;
		this.requestDenied = false;
	}

	public double getCriticality() {
		return criticality;
	}

	public DataAgent getDataAgent() {
		return dataAgent;
	}

	public List<InputAgent> getRow() {
		return column;
	}

	public String getName() {
		return name;
	}

	@Override
	public void requestAccepted(int id) {
		this.waitingRequest.remove(id);
		
	}

	@Override
	public void requestDenied(int id) {
		this.waitingRequest.remove(id);
		this.requestDenied = true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataAgent == null) ? 0 : dataAgent.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		ColumnAgent other = (ColumnAgent) obj;
		if (dataAgent == null) {
			if (other.dataAgent != null)
				return false;
		} else if (!dataAgent.equals(other.dataAgent))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	
	
}
