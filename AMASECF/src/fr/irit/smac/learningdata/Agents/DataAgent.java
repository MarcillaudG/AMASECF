package fr.irit.smac.learningdata.Agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.irit.smac.learningdata.requests.Request;
import fr.irit.smac.learningdata.requests.RequestColumn;
import fr.irit.smac.learningdata.requests.RequestRow;

public class DataAgent extends AgentLearning{

	private Map<String,Double> trustValues;
	private String name;

	private double value;

	private LearningFunction function;
	private double feedback;

	private String will;


	private List<DataAgent> dataAgentToDiscuss;

	private List<String> namesOfConcurrent;

	private Map<String,Double> influences;

	private Set<String> inputsAvailable;

	private static double INIT_VALUE = 0.5;

	private List<Request> mailbox;
	
	private int id;
	private Set<String> inputChosen;
	private double criticality;


	public DataAgent(String name,LearningFunction function, int id) {
		this.name = name;
		this.function = function;
		this.id = id;
		init();
	}

	private void init() {
		this.value = 0.0;
		this.feedback = 0.0;
		this.trustValues = new TreeMap<String,Double>();
		this.dataAgentToDiscuss = new ArrayList<DataAgent>();
		this.namesOfConcurrent = new ArrayList<String>();
		this.influences = new TreeMap<String,Double>();
		this.inputsAvailable = new TreeSet<String>();
		this.inputChosen = new TreeSet<String>();
		this.mailbox = new ArrayList<Request>();
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
	public String getWill() {
		return this.will;
	}

	public void setFeedback(double feedback) {
		this.feedback= feedback;

		if(this.will != null) {
			this.updateTrustValues();
		}
	}

	/**
	 * Perception
	 */
	public void perceive() {
		this.dataAgentToDiscuss.clear();
		this.inputChosen.clear();
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

		// Cooperation
		/*this.cooperate();
		double max = 0.0;
		this.will = null;
		for(String inputs : this.inputsAvailable) {
			if(this.trustValues.get(inputs) > max) {
				this.will = inputs;
				max = this.trustValues.get(inputs);
			}
		}*/
		treatRequest();
		
		if(this.id == 1) {
			System.out.println(this.toString() + " Choice : "+this.inputsAvailable );
		}
		
		this.function.informDecision(this,this.inputsAvailable);

	}

	private void updateTrustValues() {
		int sizeHistory = this.function.getHistoryFeedback().size();
		if(sizeHistory > 1) {
			if(this.function.getHistoryFeedback().get(sizeHistory-1) != 0 ) {
				this.trustValues.put(this.will, this.trustValues.get(will)-0.01);
			}
			else {
				this.trustValues.put(this.will, this.trustValues.get(will)+0.05);
			}
		}
		if(sizeHistory > 2) {
			if(function.getHistoryFeedback().get(sizeHistory-1) > function.getHistoryFeedback().get(sizeHistory-2)) {
				this.trustValues.put(this.will, this.trustValues.get(will)-0.05);
			}
			else {
				if(!function.getHistoryFeedback().get(sizeHistory-1).equals(function.getHistoryFeedback().get(sizeHistory-2))) {
					this.trustValues.put(this.will, this.trustValues.get(will)+0.05);
				}
			}
		}

	}

	/**
	 * Remove from the list of input available  the will
	 * if someone has a higher priority
	 */
	private void cooperate() {
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
	}

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

	private void treatRequest() {
		double maxCrit = 0.0;
		Request chosen = null;
		for(Request request : this.mailbox) {
			if(request.getCriticality() > maxCrit) {
				chosen = request;
			}
		}
		
		if(chosen != null) {
			if(chosen instanceof RequestColumn) {
				this.treatRequestColumn((RequestColumn) chosen);
			}
			if(chosen instanceof RequestRow) {
				this.treatRequestRow((RequestRow) chosen);
			}
		}
		this.mailbox.clear();
	}
	
	private void treatRequestColumn(RequestColumn request) {
		if(request.getCriticality() > this.criticality) {
			double minTrust = 10.0;
			String inputToRemove = "";
			for(String s : this.trustValues.keySet()) {
				Double d = this.trustValues.get(s);
				if(d < minTrust) {
					minTrust = d;
					inputToRemove = s;
				}
			}
			this.inputsAvailable.remove(inputToRemove);
			this.function.acceptRequest(request.getAgentName(),request.getId());
		}
		else {
			this.function.rejectRequest(request.getAgentName(),request.getId());
		}
		
	}


	private void treatRequestRow(RequestRow request) {
		
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
	
	

}
