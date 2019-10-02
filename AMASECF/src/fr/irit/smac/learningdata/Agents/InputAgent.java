package fr.irit.smac.learningdata.Agents;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.irit.smac.modelui.learning.InputLearningModel;

public class InputAgent extends AgentLearning{

	private double influence;

	private String name;

	private int id;

	private DataAgent currentData;

	private DataAgent lastData;

	private List<String> dataAgentApplying;

	private double lastFeedback;

	private LearningFunction function;

	private double feedback;

	private List<Double> historyValues;

	public enum Operator {PLUS,MOINS,NONE};

	private Map<Operator,Double> influences;

	private PropertyChangeSupport support;

	private Operator decision;

	private Double lastValue;


	public InputAgent(String name, LearningFunction function, int id) {
		this.name = name;
		this.function = function;
		this.id = id;
		init();
	}

	private void init(){
		this.influence = 0.0;
		this.lastFeedback = 0.0;
		this.feedback = 0.0;
		this.historyValues = new ArrayList<Double>();
		this.support = new PropertyChangeSupport(this);
		this.dataAgentApplying = new ArrayList<String>();

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

	public List<String> getDataAgentApplying(){
		return new ArrayList<String>(this.dataAgentApplying);
	}

	public void addDataAgent(String dataAgent) {
		this.dataAgentApplying.add(dataAgent);
	}

	public void clearApplying() {
		this.dataAgentApplying.clear();
	}

	public void perceive() {
		if(this.currentData != null) {
			this.historyValues.add(currentData.getValue());
			this.lastData = currentData;
		}
		this.feedback = this.function.getFeedback();
		this.lastValue = this.function.getLastValueOfinput(this.name);
	}

	/**
	 * Update the influence with the history
	 */
	private void computeInfluence() {
		if(this.function.getCycle()> 3) {
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
				//System.out.println(this.historyValues);
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
		if(this.lastValue != null) {
			Double resultIncrease = this.function.computeResult(this.id,this.lastValue +5.0,this.lastValue);
			Double resultDecrease = this.function.computeResult(this.id,this.lastValue -5.0,this.lastValue);
			if(resultDecrease < resultIncrease) {
				this.decision = Operator.PLUS;
			}
			else {
				this.decision = Operator.MOINS;
			}
		}
		else {
			this.decision = Operator.NONE;
		}

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

	public Map<Operator, Double> getInfluences() {
		return this.influences;
	}

	public void setDataAgent(DataAgent dataAgent) {
		this.currentData = dataAgent;

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void addPropertyChangeListener(InputLearningModel model) {
		this.support.addPropertyChangeListener(model);

	}

	public void removePropertyChangeListener(InputLearningModel model) {
		this.support.removePropertyChangeListener(model);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InputAgent other = (InputAgent) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "InputAgent [name=" + name + ", id=" + id + ", currentData=" + currentData + ", influences=" + influences
				+ "]";
	}

	@Override
	public void requestAccepted(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestDenied(int id) {
		// TODO Auto-generated method stub

	}

	public void updateInfluence(double feedback) {
		// TODO Auto-generated method stub

	}

	public void addDataAgent(DataAgent dataAgent) {
		this.currentData = dataAgent;
	}

	public void fireAllProperty() {
		this.support.firePropertyChange("INFLUENCE ADD", null, this.influences.get(Operator.PLUS));
		this.support.firePropertyChange("INFLUENCE MINUS", null, this.influences.get(Operator.MOINS));
		List<String> dataApplying = new ArrayList<String>(this.function.getAllDataAgentApplyingForInput(this.name));
		this.support.firePropertyChange("DATA", "", dataApplying);
		this.support.firePropertyChange("CORRECT", "", this.function.getCorrectData(this.id));
	}

	public Operator getDecision() {
		return this.decision;
	}


}
