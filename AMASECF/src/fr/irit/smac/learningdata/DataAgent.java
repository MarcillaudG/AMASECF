package fr.irit.smac.learningdata;

import java.util.Map;
import java.util.TreeMap;

public class DataAgent {

	private Map<String,Double> trustValues;
	private String name;
	
	public DataAgent(String name) {
		this.name = name;
		
		init();
	}

	private void init() {
		this.trustValues = new TreeMap<String,Double>();
	}
	
	public void addNewInputAgent(String name) {
		this.trustValues.put(name, 0.5);
	}
}
