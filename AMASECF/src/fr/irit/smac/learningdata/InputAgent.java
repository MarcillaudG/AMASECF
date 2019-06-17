package fr.irit.smac.learningdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputAgent {

	private double influence;

	private String name;

	private DataAgent currentData;

	private double lastValue;

	private DataAgent lastData;

	private double lastFeedback;

	private LearningFunction function;

	private double feedback;

	private List<Double> historyValues;

	private enum Operator {PLUS,MOINS};

	private Map<Operator,Double> influences;

	public InputAgent(String name, LearningFunction function) {
		this.name = name;
		this.function = function;
		init();
	}

	private void init(){
		this.influence = 0.0;
		this.lastFeedback = 0.0;
		this.feedback = 0.0;
		this.historyValues = new ArrayList<Double>();

		// Init influences
		this.influences = new HashMap<Operator,Double>();
		for(Operator ope : Operator.values()) {
			this.influences.put(ope, 0.5);
		}

	}

	public String getName() {
		return this.name;
	}

	public DataAgent getCurrentData() {
		return this.currentData;
	}


	public void perceive() {
		if(this.currentData != null) {
			this.historyValues.add(currentData.getValue());
			this.lastData = currentData;
		}


	}

	/**
	 * Update the influence with the history
	 */
	private void computeInfluence() {
		// Si il y a moins d ecart
		if(Math.abs(this.feedback) < Math.abs(this.lastFeedback)) {
			// Si la nouvelle valeur est plus grande
			if(this.historyValues.get(historyValues.size()-1) > this.historyValues.get(historyValues.size()-2)) {
				this.increaseInfluence(Operator.PLUS);
			}
			// Si la nouvelle valeur est plus petite
			else {
				this.increaseInfluence(Operator.MOINS);
			}
		}
		// S'il y a plus d'ecart
		else {
			// Si la nouvelle valeur est plus grande
			if(this.historyValues.get(historyValues.size()-1) > this.historyValues.get(historyValues.size()-2)) {
				this.decreaseInfluence(Operator.PLUS);
			}
			// Si la nouvelle valeur est plus petite
			else {
				this.increaseInfluence(Operator.MOINS);
				this.decreaseInfluence(Operator.PLUS);
			}
		}

	}

	/**
	 * Increase the influence
	 * 
	 * @param ope
	 */
	private void increaseInfluence(Operator ope) {
		this.influences.put(ope, this.influences.get(ope)+0.05);
	}

	/**
	 * Decrease the influence
	 * 
	 * @param ope
	 */
	private void decreaseInfluence(Operator ope) {
		this.influences.put(ope, this.influences.get(ope)-0.05);
	}

	public void decideAndAct() {
		if(this.lastData != null) {
			computeInfluence();
		}


		this.lastValue = currentData.getValue();
	}

	/**
	 * Setter of feedback
	 * 
	 * @param feedback
	 */
	public void setFeedback(double feedback) {
		this.feedback = feedback;
	}

	public Double getInfluence() {
		return this.influence;
	}
}
