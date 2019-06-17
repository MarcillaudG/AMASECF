package fr.irit.smac.learningdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class DataAgent {

	private Map<String,Double> trustValues;
	private String name;
	
	private double value;
	
	private LearningFunction function;
	private double feedback;
	
	private String will;
	
	private List<String> historyInput;
	
	private List<DataAgent> dataAgentToDiscuss;
	
	private List<String> namesOfConcurrent;
	
	private Map<String,Double> influences;
	
	private Set<String> inputsAvailable;
	
	private static double INIT_VALUE = 0.5;
	
	
	public DataAgent(String name,LearningFunction function) {
		this.name = name;
		this.function = function;
		init();
	}

	private void init() {
		this.value = 0.0;
		this.feedback = 0.0;
		this.trustValues = new TreeMap<String,Double>();
		this.historyInput = new ArrayList<String>();
		this.dataAgentToDiscuss = new ArrayList<DataAgent>();
		this.namesOfConcurrent = new ArrayList<String>();
		this.influences = new TreeMap<String,Double>();
		this.inputsAvailable = new TreeSet<String>();
	}
	
	public void addNewInputAgent(String name) {
		this.trustValues.put(name, DataAgent.INIT_VALUE);
	}
	
	public String getName() {
		return this.name;
	}
	
	public void restoreTrustValue(String name) {
		this.trustValues.put(name, DataAgent.INIT_VALUE);
	}
	
	public void removeInputAgent(String name) {
		this.trustValues.remove(name);
	}
	
	public void setValue(Double value) {
		this.value = value;
	}
	
	public double getValue() {
		return this.value;
	}
	
	/**
	 * Return the name of the input the agent want to be part of
	 * 
	 * @return the name of the most trustworthy input
	 */
	public String getWill() {
		return this.will;
	}

	public void setFeedback(double feedback) {
		this.feedback= feedback;
		
		this.updateTrustValues();
	}

	/**
	 * Perception
	 */
	public void perceive() {
		this.dataAgentToDiscuss.clear();
		for(String nameOfData : this.namesOfConcurrent) {
			this.dataAgentToDiscuss.add(this.function.getDataAgentWithName(nameOfData));
		}
		
		this.influences = this.function.getInfluences();
		
	}

	/**
	 * Decision
	 */
	public void decideAndAct() {
		
		// Cooperation
		this.cooperate();
		double max = 0.0;
		for(String inputs : this.inputsAvailable) {
			if(this.trustValues.get(inputs) > max) {
				this.will = inputs;
				max = this.trustValues.get(inputs);
			}
		}
		
	}

	private void updateTrustValues() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Remove from the list of input available  the will
	 * if someone has a higher priority
	 */
	private void cooperate() {
		for(DataAgent others : this.dataAgentToDiscuss) {
			if(others.influences.get(this.will) > this.influences.get(this.will)) {
				this.inputsAvailable.remove(this.will);
			}
			else {
				if(others.influences.get(this.will) == this.influences.get(this.will)) {
					if(others.name.compareTo(this.name) < 0) {
						this.inputsAvailable.remove(this.will);
					}
				}
			}
		}
	}

	/**
	 * Clear the old concurrent and add all the concurrent in param
	 * 
	 * @param list
	 * 			The list of concurrent
	 */
	public void addConccurent(List<String> list) {
		this.namesOfConcurrent.clear();
		this.namesOfConcurrent.addAll(list);
		this.namesOfConcurrent.remove(this.name);
	}

	public void setInputAvailable(Set<String> inputs) {
		this.inputsAvailable.clear();
		this.inputsAvailable.addAll(inputs);
		
	}
}
