package fr.irit.smac.functions;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class SumFunction extends Function{
	
	private Set<String> correctVariables;
	
	private Map<String,Double> allVariables;
	
	public SumFunction() {
		this.correctVariables = new TreeSet<String>();
		this.allVariables = new TreeMap<String, Double>();
	}

	@Override
	public double compute() {
		double res = 0.0;
		for(String s : this.allVariables.keySet()) {
			if(this.correctVariables.contains(s)) {
				res += this.allVariables.get(s);
			}
			else {
				res -= this.allVariables.get(s);
			}
		}
		return res;
	}
	
	public void addCorrectVariable(String variable) {
		this.correctVariables.add(variable);
	}
	
	public void initFunction() {
		this.allVariables = new TreeMap<String, Double>();
	}
	
	public void AddParameters(String name, Double value) {
		this.allVariables.put(name, value); 
	}

	public double compute(Map<String, Double> parameters) {
		double res = 0.0;
		for(String s : parameters.keySet()) {
			res += parameters.get(s);
		}
		return res;
		
	}

}
