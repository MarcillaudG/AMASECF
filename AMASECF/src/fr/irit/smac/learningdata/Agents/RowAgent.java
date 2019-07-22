package fr.irit.smac.learningdata.Agents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.irit.smac.learningdata.requests.Request;
import fr.irit.smac.learningdata.requests.RequestColumn;
import fr.irit.smac.learningdata.requests.RequestRow;
import fr.irit.smac.learningdata.requests.RequestRow.Reason;

public class RowAgent extends AgentLearning{

	private static final Double CRITICALITY_EMPTY = 20.0;

	private Double criticality;

	private InputAgent input;

	private LearningFunction function;

	private String name;

	private Reason reason;

	private Map<String,Integer> row;

	private Map<Integer,RequestRow> waitingRequest;

	private int idRequest;

	public RowAgent(String name,InputAgent input, LearningFunction function ) {
		this.criticality = 0.0;
		this.input = input;
		this.name = name;
		this.function = function;
		this.idRequest = 0;

		this.row = new HashMap<String,Integer>();
		this.waitingRequest = new TreeMap<Integer,RequestRow>();

	}

	public Double getCriticality() {
		return criticality;
	}

	public InputAgent getInput() {
		return input;
	}

	public Set<String> getDataAgents() {
		return row.keySet();
	}

	public Map<String,Integer> getRow(){
		return this.row ;
	}
	
	public Set<String> getDataApplying(){
		Set<String> ret = new TreeSet<String>();
		for(String name : this.row.keySet()) {
			if(this.row.get(name) == 1) {
				ret.add(name);
			}
		}
		return ret;
	}
	
	public String getName() {
		return name;
	}

	public void perceive() {
		
		for(String name: this.row.keySet()) {
			this.row.put(name, 0);
			DataAgent dataAgent = this.function.getDataAgentWithName(name);
			for(String will : dataAgent.getInputChosen()) {
				if(will.equals(this.input.getName())) {
					this.row.put(name, 1);
				}
			}
		}
	}

	public void decideAndAct() {
		int sum = 0;
		for(String name: this.row.keySet()) {
			if(this.row.get(name) == 1) {
				sum += 1;
			}
		}
		if(sum == 0 ) {
			this.criticality = RowAgent.CRITICALITY_EMPTY;
			this.reason = Reason.UNDERCHARGED;
		}
		else {
			if(sum > 1) {
				this.criticality = Math.pow(sum,2);
				this.reason = Reason.OVERCHARGED;
			}
			else {
				this.criticality = 0.0;
			}

		}

		if(this.criticality > 0) {
			this.searchForService();
			this.function.constraintNotRespected();
		}
	}

	public void onCycleBegin() {
		for(String name: this.row.keySet()) {
			this.row.put(name, 0);
		}
	}

	/**
	 * Request the data agent
	 */
	private void searchForService() {
		switch(this.reason) {
		case OVERCHARGED:
			for(String name: this.row.keySet()) {
				if(this.row.get(name) == 1) {
					Request request = new RequestRow(this.criticality, this.name, this.idRequest++, this.input.getName(), Reason.OVERCHARGED);
					//this.function.getDataAgentWithName(name).sendRequest(request);
					this.function.proposeRequest(request);
				}
			}
			break;
		case UNDERCHARGED:
			for(String name: this.row.keySet()) {
				Request request = new RequestRow(this.criticality, this.name, this.idRequest++, this.input.getName(), Reason.UNDERCHARGED);
				//this.function.getDataAgentWithName(name).sendRequest(request);
				this.function.proposeRequest(request);

			}
			break;
		default:
			break;

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
		this.row.put(dataAgent.getName(), 0);
	}

	public void dataAgentApplying(DataAgent dataAgent) {
		this.row.put(dataAgent.getName(), 1);
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
