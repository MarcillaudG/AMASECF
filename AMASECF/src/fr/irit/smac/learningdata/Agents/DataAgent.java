package fr.irit.smac.learningdata.Agents;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.irit.smac.learningdata.Agents.InputAgent.Operator;
import fr.irit.smac.learningdata.requests.Offer;
import fr.irit.smac.learningdata.requests.Request;
import fr.irit.smac.learningdata.requests.RequestColumn;
import fr.irit.smac.learningdata.requests.RequestRow;
import fr.irit.smac.modelui.learning.DataLearningModel;

public class DataAgent extends AgentLearning{

	private Map<String,Double> trustValues;
	private String name;

	private double value;

	private LearningFunction function;
	private double feedback;

	//private String will;


	private List<DataAgent> dataAgentToDiscuss;

	private List<String> namesOfConcurrent;

	private Map<String,Double> influences;

	private Set<String> inputsAvailable;

	private Set<String> inputRefused;

	private static double INIT_VALUE = 0.5;

	private List<Request> mailbox;

	private int id;
	private Set<String> inputChosen;
	private double criticality;
	private PropertyChangeSupport support;


	public DataAgent(String name,LearningFunction function, int id) {
		this.name = name;
		this.function = function;
		this.id = id;
		init();
	}

	private void init() {
		this.value = 0.0;
		this.feedback = 0.0;
		this.criticality = 0.0;
		this.trustValues = new TreeMap<String,Double>();
		this.dataAgentToDiscuss = new ArrayList<DataAgent>();
		this.namesOfConcurrent = new ArrayList<String>();
		this.influences = new TreeMap<String,Double>();
		this.inputsAvailable = new TreeSet<String>();
		this.inputChosen = new TreeSet<String>();
		this.mailbox = new ArrayList<Request>();
		this.inputRefused = new TreeSet<String>();
		this.support = new PropertyChangeSupport(this);
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
	/*public String getWill() {
		return this.will;
	}*/

	public void setFeedback(double feedback) {
		this.feedback= feedback;

		/*if(this.will != null) {
			this.updateTrustValues();
		}*/
	}

	/**
	 * Clear the choice
	 */
	public void clearInput() {
		this.inputsAvailable.clear();
		this.inputChosen.clear();
		this.inputRefused.clear();
	}

	/**
	 * Perception
	 */
	public void perceive() {
		this.dataAgentToDiscuss.clear();
		for(String nameOfData : this.namesOfConcurrent) {
			this.dataAgentToDiscuss.add(this.function.getDataAgentWithName(nameOfData));
		}

		this.value = this.function.getDataValue(this.name);
		this.influences = this.function.getInfluences();

	}

	/**
	 * Decision
	 */
	public void decideAndAct() {

		treatRequest();



		//this.function.informDecision(this,this.inputChosen);

	}

	private void updateTrustValues() {
		for(String will : this.inputsAvailable) {
			int sizeHistory = this.function.getHistoryFeedback().size();
			if(sizeHistory > 1) {
				if(this.function.getHistoryFeedback().get(sizeHistory-1) != 0 ) {
					this.trustValues.put(will, this.trustValues.get(will)-0.01);
				}
				else {
					this.trustValues.put(will, this.trustValues.get(will)+0.05);
				}
			}
			if(sizeHistory > 2) {
				if(function.getHistoryFeedback().get(sizeHistory-1) > function.getHistoryFeedback().get(sizeHistory-2)) {
					this.trustValues.put(will, this.trustValues.get(will)-0.05);
				}
				else {
					if(!function.getHistoryFeedback().get(sizeHistory-1).equals(function.getHistoryFeedback().get(sizeHistory-2))) {
						this.trustValues.put(will, this.trustValues.get(will)+0.05);
					}
				}
			}
		}

	}

	/**
	 * Remove from the list of input available  the will
	 * if someone has a higher priority
	 */
	/*private void cooperate() {
		if(this.will != null) {
			for(DataAgent others : this.dataAgentToDiscuss) {
				if(others.will != null) {
					if(others.influences.get(this.will) > this.influences.get(this.will)) {
						this.inputsAvailable.remove(this.will);
					}
					else {
						String myname = this.name;
						if(others.influences.get(this.will).equals(this.influences.get(this.will))) {
							if(others.name.compareTo(myname) < 0) {
								this.inputsAvailable.remove(this.will);
							}
						}
					}
				}
			}
		}
	}*/

	/**
	 * Clear the old concurrent and add all the concurrent in param
	 * 
	 * @param list
	 * 			The list of concurrent
	 */
	public void addConccurent(List<String> list) {
		this.namesOfConcurrent.clear();
		this.namesOfConcurrent.addAll(list);
		this.namesOfConcurrent.remove(this.name);
	}

	public void setInputAvailable(Set<String> inputs) {
		this.inputsAvailable.clear();
		this.inputsAvailable.addAll(inputs);

	}

	@Override
	public String toString() {
		return "DataAgent [id="+id +"name=" + name + "]";
	}

	public Map<String, Double> getTrustValues() {
		return this.trustValues;
	}


	public Set<String> getInputChosen() {
		return this.inputChosen;
	}

	public void sendRequest(Request request) {
		this.mailbox.add(request);
	}

	/**
	 * Treat only one request
	 * 
	 * Choose which request has priority
	 * 
	 * And then treat it
	 */
	private void treatRequest() {
		double maxCrit = 0.0;
		Collections.shuffle(mailbox);
		for(Request request : this.mailbox) {
			if(request.getCriticality() > maxCrit) {
				if(request instanceof RequestColumn) {
					this.treatRequestColumn((RequestColumn) request);
				}
				if(request instanceof RequestRow) {
					this.treatRequestRow((RequestRow) request);
				}
				/*if(chosen != null) {
					this.function.rejectRequest(chosen.getAgentName(), chosen.getId());
				}
				chosen = request;
				maxCrit = chosen.getCriticality();*/
			}
			else {
				this.function.rejectRequest(request.getAgentName(), request.getId());
			}
		}

		/*if(chosen != null) {
			if(chosen instanceof RequestColumn) {
				this.treatRequestColumn((RequestColumn) chosen);
			}
			if(chosen instanceof RequestRow) {
				this.treatRequestRow((RequestRow) chosen);
			}
		}*/
		this.mailbox.clear();
	}

	private void treatRequestColumn(RequestColumn request) {
		if(request.getCriticality() > this.criticality) {
			double minTrust = 10.0;
			String inputToRemove = "";
			for(String s : this.inputChosen) {
				Double d = this.trustValues.get(s);
				if(d < minTrust) {
					minTrust = d;
					inputToRemove = s;
				}
			}
			this.inputChosen.remove(inputToRemove);
			this.function.acceptRequest(request.getAgentName(),request.getId());
			this.inputRefused.add(inputToRemove);
		}
		else {
			this.function.rejectRequest(request.getAgentName(),request.getId());
		}

	}


	private void treatRequestRow(RequestRow request) {
		if(request.getCriticality() > this.criticality) {
			switch(request.getReason()) {
			case OVERCHARGED:
				//this.inputChosen.remove(request.getInputName());
				//this.function.acceptRequest(request.getAgentName(), request.getId());
				this.function.applyForRequest(request, new Offer(this.name,1-this.trustValues.get(request.getInputName())));
				break;
			case UNDERCHARGED:
				if(!this.inputRefused.contains(request.getInputName())){
					/*double maxTrust = 0.0;
					for(String s : this.inputChosen) {
						if(this.trustValues.get(s)>maxTrust) {
							maxTrust = this.trustValues.get(s);
						}
					}
					if(maxTrust < this.trustValues.get(request.getInputName())){
						//this.inputChosen.add(request.getInputName());
						this.function.applyForRequest(request, new Offer(this.name,this.trustValues.get(request.getInputName())));
					}*/
					this.function.applyForRequest(request, new Offer(this.name,this.trustValues.get(request.getInputName())));
				}
				break;
			default:
				break;
			}
		}
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
		DataAgent other = (DataAgent) obj;
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
	public void requestAccepted(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestDenied(int id) {
		// TODO Auto-generated method stub

	}

	public void updateTrust(double feedback) {
		for(String will : this.inputChosen) {
			int sizeHistory = this.function.getHistoryFeedback().size();
			if(sizeHistory > 1 && sizeHistory < 3) {
				if(this.function.getHistoryFeedback().get(sizeHistory-1) != 0 ) {
					this.trustValues.put(will, this.trustValues.get(will)-0.05);
				}
				else {
					this.trustValues.put(will, this.trustValues.get(will)+0.05);
				}
			}
			if(sizeHistory > 2) {
				if(function.getHistoryFeedback().get(sizeHistory-1) > function.getHistoryFeedback().get(sizeHistory-2)) {
					this.trustValues.put(will, this.trustValues.get(will)-0.05);
				}
				else {
					if(!function.getHistoryFeedback().get(sizeHistory-1).equals(function.getHistoryFeedback().get(sizeHistory-2))) {
						this.trustValues.put(will, this.trustValues.get(will)+0.05);
					}
				}
			}
		}

	}

	public Set<String> getWhatInputIAplied(){
		return this.inputsAvailable;
	}

	public void applyWinRequest(Request request) {
		if(request instanceof RequestRow) {
			switch(((RequestRow) request).getReason()) {
			case OVERCHARGED:
				this.inputChosen.remove(((RequestRow) request).getInputName());
				this.inputRefused.add(((RequestRow) request).getInputName());
				this.function.acceptRequest(request.getAgentName(), request.getId());
				break;
			case UNDERCHARGED:
				this.inputChosen.add(((RequestRow) request).getInputName());
				this.function.acceptRequest(request.getAgentName(), request.getId());
				break;
			default:
				break;

			}
		}else {

		}

	}

	public void addPropertyChangeListener(DataLearningModel model) {
		this.support.addPropertyChangeListener(model);
		
	}

	public void printTrustValues() {
		String res = "";
		for(String s : this.trustValues.keySet()) {
			res += "|"+s+"->"+this.trustValues.get(s);
		}
		System.out.println(res);
	}

	public void fireTrustValues() {
		this.support.firePropertyChange("TRUSTVALUES", null, this.trustValues);
		
	}

	

}
