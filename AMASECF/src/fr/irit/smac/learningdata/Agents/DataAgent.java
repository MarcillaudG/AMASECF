package fr.irit.smac.learningdata.Agents;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.irit.smac.learningdata.Agents.InputAgent.Operator;
import fr.irit.smac.learningdata.requests.Offer;
import fr.irit.smac.learningdata.requests.Request;
import fr.irit.smac.learningdata.requests.RequestColumn;
import fr.irit.smac.learningdata.requests.RequestForWeight;
import fr.irit.smac.learningdata.requests.RequestRow;
import fr.irit.smac.modelui.learning.DataLearningModel;

public class DataAgent extends AgentLearning{

	private Map<String,Double> trustValues;
	private String name;

	private double value;
	private double old_value;

	private LearningFunction function;
	private double feedback;

	//private String will;


	private List<DataAgent> dataAgentToDiscuss;

	private List<String> namesOfConcurrent;

	private Map<String,Double> influences;

	private Map<String,WeightAgent> weightAgents;

	private Set<String> inputsAvailable;

	private Set<String> inputRefused;

	private static double INIT_VALUE = 0.5;

	private List<Request> mailbox;

	private int id;
	private Set<String> inputChosen;
	private double criticality;
	private PropertyChangeSupport support;

	private Configuration lastConfig;

	private Map<String,Operator> inputDecision;


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
		this.weightAgents = new TreeMap<String,WeightAgent>();
		this.inputDecision = new TreeMap<String,Operator>();
		this.support = new PropertyChangeSupport(this);
	}

	public void addNewInputAgent(String name) {
		this.trustValues.put(name, DataAgent.INIT_VALUE);
		
		WeightAgent weight = new WeightAgent(this.function, this.name, name);
		this.weightAgents.put(name, weight);
		this.function.addNewWeightAgent(weight,this.name,name);
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
		
		this.old_value = this.value;
		this.value = this.function.getDataValue(this.name);
		this.influences = this.function.getInfluences();

		this.lastConfig = this.function.getCurrentConfig();
		this.feedback = this.function.getFeedback();
		this.inputDecision.clear();
		for(String s : this.function.getInputsDecisions().keySet()) {
			this.inputDecision.put(s, this.function.getInputsDecisions().get(s));
		}
		//this.inputDecision = this.function.getInputsDecisions();
	}

	/**
	 * Decision
	 */
	public void decideAndAct() {
		for(String input : this.inputDecision.keySet()) {
			RequestForWeight request = new RequestForWeight(0, this.name, 0, null, "DATA");
			switch(this.inputDecision.get(input)) {
			case MOINS:
				if(this.old_value > 0 ) {
					if(this.feedback >= 0) {
						request.setDecision(Operator.PLUS);
					}
					else {
						request.setDecision(Operator.MOINS);
					}
				}
				else {
					if(this.feedback > 0) {
						request.setDecision(Operator.MOINS);
					}
					else {
						request.setDecision(Operator.PLUS);
					}
				}
				break;
			case NONE:
				request.setDecision(Operator.NONE);
				break;
			case PLUS:
				if(this.old_value > 0 ) {
					if(this.feedback > 0) {
						request.setDecision(Operator.MOINS);
					}
					else {
						request.setDecision(Operator.PLUS);
					}
				}
				else {
					if(this.feedback >= 0) {
						request.setDecision(Operator.PLUS);
					}
					else {
						request.setDecision(Operator.MOINS);
					}
				}
				break;
			default:
				break;

			}
			//System.out.println("OLD:"+this.old_value+"|feedback:"+this.feedback+"|Decin:"+this.inputDecision.get(input)+"|req"+request.getDecision());
			this.sendRequestForWeight(input, request);
		}
		/*String inputToIncrease = null;
		Double max = 0.0;
		for(String input : this.trustValues.keySet()) {
			if(this.trustValues.get(input) > max) {
				max = this.trustValues.get(input);
				inputToIncrease = input;
			}
		}
		if(inputToIncrease != null) {
			Operator decisionInput = this.function.getInputDecision(inputToIncrease);
			switch(decisionInput) {
			case MOINS:
					this.weightAgents.get(inputToIncrease).addRequest(new RequestForWeight(this.old_value, this.name, 0, Operator.PLUS, "DATA"));
				break;
			case PLUS:
				this.weightAgents.get(inputToIncrease).addRequest(new RequestForWeight(this.old_value, this.name, 0, Operator.PLUS, "DATA"));
				break;
			default:
				break;

			}
		}
		treatRequest();*/



		//this.function.informDecision(this,this.inputChosen);

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

	public void updateTrust(Configuration config) {
		if(config != null) {
			for(String input : this.trustValues.keySet()) {
				this.trustValues.put(input, config.getDataValueForInput(input, this.name));
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

	public void manageConfig(Configuration config) {
		Collections.shuffle(mailbox);
		for(Request request : this.mailbox) {
			if(request instanceof RequestRow) {
				treatRequestRow((RequestRow) request,config);	
			}
			if(request instanceof RequestColumn) {
				treatRequestColumn((RequestColumn) request,config);	
			}
		}	
	}

	private void treatRequestColumn(RequestColumn request, Configuration config) {
		// TODO Auto-generated method stub

	}

	private void treatRequestRow(RequestRow request, Configuration config) {
		Double value = this.trustValues.get(request.getInputName());
		Random r = new Random();
		Double configValue = config.getDataValueForInput(request.getInputName(), this.name);
		Double prob = Math.abs(value - configValue);
		prob = 0.5-prob;
		if(r.nextDouble() < prob) {
			switch(request.getReason()) {
			case OVERCHARGED:
				this.inputChosen.remove(((RequestRow) request).getInputName());
				this.inputRefused.add(((RequestRow) request).getInputName());
				break;
			case UNDERCHARGED:
				this.inputChosen.add(((RequestRow) request).getInputName());
				break;
			default:
				break;
			}
			this.function.acceptRequest(request.getAgentName(), request.getId());
		}
	}

	public Offer askData(Operator decision, String input) {
		Offer res = this.weightAgents.get(input).askWeight(decision);
		if(res != null) {
			res.setOffer(this.old_value);
		}
		return res;
	}

	public void decreaseWeight(String input) {
		this.weightAgents.get(input).decreaseWeight();

	}

	/**
	 * Ask the weight to increase of the corresponding input
	 * @param input
	 */
	public void increaseWeight(String input) {
		this.weightAgents.get(input).increaseWeight();		
	}

	/**
	 * Send a request to the weight agent of the corresponding input
	 * @param input
	 * 			The input
	 * @param requestToSend
	 * 			The request
	 */
	public void sendRequestForWeight(String input, RequestForWeight requestToSend) {
		this.weightAgents.get(input).addRequest(requestToSend);
	}

	/**
	 * Return the weight of an input
	 * 
	 * @param input
	 * 		The input
	 * @return the weight
	 */
	public Double getWeightOfInput(String input) {
		return this.weightAgents.get(input).getWeight();
	}

	public void startWeights() {
		List<WeightAgent> tmp = new ArrayList<WeightAgent>(this.weightAgents.values());
		Collections.shuffle(tmp);
		for(WeightAgent weight: tmp) {
			weight.onPerceive();
		}

		Collections.shuffle(tmp);
		for(WeightAgent weight: tmp) {
			weight.onDecideAndAct(0);
		}

	}



}
