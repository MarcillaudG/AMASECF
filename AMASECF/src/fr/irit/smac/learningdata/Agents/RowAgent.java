package fr.irit.smac.learningdata.Agents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import fr.irit.smac.learningdata.requests.Request;
import fr.irit.smac.learningdata.requests.RequestColumn;
import fr.irit.smac.learningdata.requests.RequestRow;

public class RowAgent extends AgentLearning{

	private static final Double CRITICALITY_EMPTY = 20.0;

	private Double criticality;

	private InputAgent input;

	private LearningFunction function;

	private String name;

	private Map<DataAgent,Integer> row;
	
	private Map<Integer,RequestRow> waitingRequest;

	private int idRequest;

	public RowAgent(String name,InputAgent input, LearningFunction function ) {
		this.criticality = 0.0;
		this.input = input;
		this.name = name;
		this.function = function;
		this.idRequest = 0;

		this.row = new HashMap<DataAgent,Integer>();
		this.waitingRequest = new TreeMap<Integer,RequestRow>();

	}

	public Double getCriticality() {
		return criticality;
	}

	public InputAgent getInput() {
		return input;
	}

	public Set<DataAgent> getDataAgents() {
		return row.keySet();
	}

	public String getName() {
		return name;
	}

	public void perceive() {
		for(DataAgent dataAgent: this.row.keySet()) {
			if(dataAgent.getWill().equals(this.input.getName())) {
				this.row.put(dataAgent, 1);
			}
			else {
				this.row.put(dataAgent,0);
			}
		}
	}

	public void decideAndAct() {
		int sum = 0;
		for(DataAgent dataAgent: this.row.keySet()) {
			if(this.row.get(dataAgent) == 1) {
				sum += 1;
			}
		}
		if(sum == 0 ) {
			this.criticality = RowAgent.CRITICALITY_EMPTY;
		}
		else {
			this.criticality = sum > 1 ? Math.pow(sum,2) : 0.0;
		}

		if(this.criticality > 0) {
			this.searchForService();
		}
	}

	public void onCycleBegin() {
		for(DataAgent dataAgent : this.row.keySet()) {
			this.row.put(dataAgent, 0);
		}
	}

	/**
	 * Request the data agent
	 */
	private void searchForService() {
		for(DataAgent dataAgent : this.row.keySet()) {
			if(this.row.get(dataAgent) == 1) {
				Request request = new RequestRow(this.criticality, this.name, this.idRequest++, this.input.getName());
				dataAgent.sendRequest(request);
			}
		}
	}

	/**
	 * Compute the criticality if a dataAgent apply
	 * 
	 * @return the criticality
	 */
	public double criticalityIfApplying() {
		return criticality + Math.pow(criticality, 2);
	}

	/*
	 * Add a new DataAgent
	 */
	public void addDataAgent(DataAgent dataAgent) {
		this.row.put(dataAgent, 0);
	}

	public void dataAgentApplying(DataAgent dataAgent) {
		this.row.put(dataAgent, 1);
	}

	@Override
	public void requestAccepted(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestDenied(int id) {
		// TODO Auto-generated method stub

	}
}
