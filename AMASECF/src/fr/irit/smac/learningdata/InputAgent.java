package fr.irit.smac.learningdata;

public class InputAgent {

	private double influence;
	
	private String name;
	
	private DataAgent currentData;
	
	private double lastValue;
	
	private double lastFeedback;
	
	private LearningFunction function;
	
	public InputAgent(String name, LearningFunction function) {
		this.name = name;
		this.influence = 0.0;
		this.lastFeedback = 0.0;
		this.function = function;
	}
	
	public String getName() {
		return this.name;
	}
	
	public DataAgent getCurrentData() {
		return this.currentData;
	}
	
	public void updateInfluence(double influence) {
		
	}
}
