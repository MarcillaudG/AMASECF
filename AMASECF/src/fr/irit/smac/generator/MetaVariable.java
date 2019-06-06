package fr.irit.smac.generator;

import java.util.Set;
import java.util.TreeSet;

public class MetaVariable {

	private Set<String> variables;
	
	private double value;
	
	private String name;
	
	public MetaVariable() {
		this.variables = new TreeSet<String>();
		this.value = 0.0;
	}
	
	public void addVariable(String var) {
		this.variables.add(var);
	}
	
	public void removeVariable(String var) {
		this.variables.remove(var);
	}
	
	public Set<String> getVariables(){
		return new TreeSet<String>(this.variables);
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	public double getValue() {
		return this.value;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	
}
