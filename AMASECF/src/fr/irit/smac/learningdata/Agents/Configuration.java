package fr.irit.smac.learningdata.Agents;

import java.util.Map;
import java.util.TreeMap;

public class Configuration {

	private Map<String,Map<String, Double>> config;

	private int cycle;

	private int id;

	public Configuration(int cycle, int id) {
		this.id = id;
		this.cycle = cycle;
		this.config = new TreeMap<String,Map<String,Double>>();
	}

	/*
	 * Add a new Input in the configuration
	 */
	public boolean addInput(String input) {
		if(this.config.containsKey(input)) {
			return false;
		}
		else {
			this.config.put(input,new TreeMap<String,Double>());
			return true;
		}
	}

	/**
	 * Add the value of a data to the corresponding input
	 * 
	 * @param input
	 * 			The input name
	 * @param data
	 * 			The data name
	 * @param value
	 * 			The value of the data
	 */
	public void addDataValueToInput(String input,String data,Double value) {
		this.config.get(input).put(data, value);
	}
	
	/**
	 * Return if the configuration is valid
	 * 
	 * @return true when, for each input, the data do not exceed 1.0
	 */
	public boolean isConfigurationValid() {
		for(String input : this.config.keySet()) {
			double sum = 0.0;
			for(String data : this.config.get(input).keySet()) {
				sum += this.config.get(input).get(data);
			}
			if(sum >1.0) {
				return false;
			}
		}
		return true;
	}
	
	public int getCycle() {
		return this.cycle;
	}
	
	public int getId() {
		return this.id;
	}
	
	public Double getDataValueForInput(String input,String data) {
		return this.config.get(input).get(data);
	}

	@Override
	public String toString() {
		return "Configuration [config=" + config + ", cycle=" + cycle + ", id=" + id + "]";
	}
	
}
