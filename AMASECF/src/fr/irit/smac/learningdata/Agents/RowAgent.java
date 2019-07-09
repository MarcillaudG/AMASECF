package fr.irit.smac.learningdata.Agents;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RowAgent extends AgentLearning{

	private Double criticality;
	
	private InputAgent input;
	
	
	private String name;
	
	private Map<DataAgent,Integer> row;

	public RowAgent(String name,InputAgent input ) {
		this.criticality = 0.0;
		this.input = input;
		this.name = name;
		
		this.row = new HashMap<DataAgent,Integer>();
		
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
	}
	
	public void onCycleBegin() {
		for(DataAgent dataAgent : this.row.keySet()) {
			this.row.put(dataAgent, 0);
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
