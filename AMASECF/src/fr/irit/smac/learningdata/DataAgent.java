package fr.irit.smac.learningdata;

import java.util.Map;
import java.util.TreeMap;

public class DataAgent {

	private Map<String,Double> trustValues;
	private String name;
	
	private double value;
	
	private LearningFunction function;
	
	private static double INIT_VALUE = 0.5;
	
	
	public DataAgent(String name,LearningFunction function) {
		this.name = name;
		this.function = function;
		init();
	}

	private void init() {
		this.value = 0.0;
		this.trustValues = new TreeMap<String,Double>();
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
		String res = "";
		double max = 0.0;
		for(String s : this.trustValues.keySet()) {
			if(this.trustValues.get(s) > max) {
				res = s;
				max = this.trustValues.get(s);
			}
		}
		return res;
	}
}
