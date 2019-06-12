package fr.irit.smac.learningdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LearningFunction {

	
	private Map<String,InputAgent> allInputAgent;
	
	private Map<String,DataAgent> allDataAgent;
	
	private String name;
	
	public LearningFunction(String name) {
		this.name = name;
		init();
	}
	
	private void init() {
		this.allDataAgent = new TreeMap<String,DataAgent>();
		this.allInputAgent = new TreeMap<String,InputAgent>();
	}
	
	public String getName() {
		return this.name;
	}
	
	private boolean createDataAgent(String name) {
		if(this.allDataAgent.keySet().contains(name)) {
			return false;
		}
		DataAgent dag = new DataAgent(name,this);
		this.allDataAgent.put(dag.getName(), dag);
		return true;
	}
	
}
