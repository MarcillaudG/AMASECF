package fr.irit.smac.learningdata;

public class InputAgent {

	private double influence;
	
	private String name;
	
	private DataAgent currentData;
	
	public InputAgent(String name) {
		this.name = name;
		this.influence = 0.0;
	}
	
	public String getName() {
		return this.name;
	}
	
	
}
