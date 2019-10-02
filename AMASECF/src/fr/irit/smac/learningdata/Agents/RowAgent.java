package fr.irit.smac.learningdata.Agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.irit.smac.learningdata.Agents.InputAgent.Operator;
import fr.irit.smac.learningdata.requests.Offer;
import fr.irit.smac.learningdata.requests.Request;
import fr.irit.smac.learningdata.requests.RequestColumn;
import fr.irit.smac.learningdata.requests.RequestForRow;
import fr.irit.smac.learningdata.requests.RequestForWeight;
import fr.irit.smac.learningdata.requests.RequestRow;
import fr.irit.smac.learningdata.requests.RequestRow.Reason;

public class RowAgent extends AgentLearning{

	private static final Double CRITICALITY_EMPTY = 20.0;

	private Double criticality;

	private InputAgent input;

	private LearningFunction function;

	private String name;

	private Reason reason;

	private Map<String,Double> row;

	private Map<Integer,RequestRow> waitingRequest;

	private List<Offer> offersData; 

	private List<RequestForRow> mailBox;

	private int idRequest;

	private Configuration currentConfig;

	public RowAgent(String name,InputAgent input, LearningFunction function ) {
		this.criticality = 0.0;
		this.input = input;
		this.name = name;
		this.function = function;
		this.idRequest = 0;

		this.row = new HashMap<String,Double>();
		this.waitingRequest = new TreeMap<Integer,RequestRow>();
		this.mailBox = new ArrayList<RequestForRow>();
		this.offersData = new ArrayList<Offer>();

	}

	public Double getCriticality() {
		return criticality;
	}

	public InputAgent getInput() {
		return input;
	}

	public Set<String> getDataAgents() {
		return row.keySet();
	}

	public Map<String,Double> getRow(){
		return this.row ;
	}

	public Set<String> getDataApplying(){
		Set<String> ret = new TreeSet<String>();
		for(String name : this.row.keySet()) {
			if(this.row.get(name) == 1) {
				ret.add(name);
			}
		}
		return ret;
	}

	public String getName() {
		return name;
	}

	public void perceive() {

		this.currentConfig = this.function.getCurrentConfig();
		if(this.currentConfig != null) {
			for(String name: this.row.keySet()) {
				this.row.put(name, this.currentConfig.getDataValueForInput(this.input.getName(), name));
			}
		}
	}

	public void decideAndAct() {
		/*Double sum = 0.0;
		for(String name: this.row.keySet()) {
			sum += this.row.get(name);
		}
		if(sum <1.0) {
			this.criticality = RowAgent.CRITICALITY_EMPTY;
			this.reason = Reason.UNDERCHARGED;
		}
		else {
			if(sum > 1.0) {
				this.criticality = Math.pow(sum,2);
				this.reason = Reason.OVERCHARGED;
			}
			else {
				this.criticality = 0.0;
			}

		}

		if(this.criticality > 0) {
			//System.out.println("ROWAGENT : "+this.getName()+" "+this.criticality);
			this.searchForService();
			this.function.constraintNotRespected();
		}

		this.treatRequests();*/

		this.decideRequests();

	}

	/**
	 * Decision of which request will be send to the weight
	 */
	private void decideRequests() {
		double max = 0.0;
		int countNbMax = 0;
		double sum = 0.0;
		// Identification of the value of the maximum weight
		for(Double value : this.row.values()) {
			if(value == max) {
				countNbMax++;
				sum += -value;
			}else {
				if (value >max) {
					sum += (1-value) +(max-1)*2;
					max = value;
					countNbMax = 1;
				}
				else {
					sum += -value;
				}
			}
		}
		// In case of need of global criticality
		List<RequestForWeight> toSend = new ArrayList<RequestForWeight>();
		boolean maxDone = false;
		for(String data : this.row.keySet()) {
			RequestForWeight requestToSend = new RequestForWeight(0, this.name, 0, null, "ROW");
			if(this.row.get(data)==max) {
				if(countNbMax >1) {
					requestToSend.setDecision(Operator.NONE);
				}else {
					requestToSend.setDecision(Operator.PLUS);
					requestToSend.setCriticality(1-this.row.get(data));
				}
			}
			else {
				requestToSend.setDecision(Operator.MOINS);
				requestToSend.setCriticality(this.row.get(data));
			}
			this.function.sendRequestForWeight(this.input.getName(),data,requestToSend);
		}

	}

	private void treatRequests() {
		for(RequestForRow request : this.mailBox) {
			switch(request.getDecision()) {
			case MOINS:
				break;
			case PLUS:
				Double bestOffer = null;
				String dataWinner = null;
				for(String data : this.row.keySet()) {
					this.offersData.add(this.function.askData(data,Operator.MOINS,this.input.getName()));
				}
				for(Offer offer : this.offersData) {
					if(offer != null) {
						if(bestOffer == null) {
							bestOffer = offer.getOffer();
							dataWinner = offer.getNameOfAgent();
						}
						else {
							switch(this.input.getDecision()) {
							case MOINS:
								if(this.function.getFeedback() > 0 ) {
									if(offer.getOffer() < bestOffer) {
										bestOffer = offer.getOffer();
										dataWinner = offer.getNameOfAgent();
									}
								}
								else {
									if(offer.getOffer() > bestOffer) {
										bestOffer = offer.getOffer();
										dataWinner = offer.getNameOfAgent();
									}

								}
								break;
							case PLUS:
								if(this.function.getFeedback() > 0 ) {
									if(offer.getOffer() > bestOffer) {
										bestOffer = offer.getOffer();
										dataWinner = offer.getNameOfAgent();
									}
								}
								else {
									if(offer.getOffer() < bestOffer) {
										bestOffer = offer.getOffer();
										dataWinner = offer.getNameOfAgent();
									}

								}
								break;
							default:
								break;

							}
						}
					}
				}
				if(dataWinner != null) {
					// Decrease the weight of a data agent for this input
					this.function.getDataAgentWithName(dataWinner).decreaseWeight(this.input.getName());
					// Increase the weight of the requested data agent for this input
					this.function.getDataAgentWithName(dataWinner).increaseWeight(this.input.getName());
				}
				else {

				}
				break;
			default:
				break;

			}
		}

	}

	public void onCycleBegin() {
		for(String name: this.row.keySet()) {
			this.row.put(name, 0.0);
		}
	}

	/**
	 * Request the data agent
	 */
	private void searchForService() {
		switch(this.reason) {
		case OVERCHARGED:
			/*for(String name: this.row.keySet()) {
				if(this.row.get(name) == 1) {
					Request request = new RequestRow(this.criticality, this.name, this.idRequest++, this.input.getName(), Reason.OVERCHARGED);
					//this.function.getDataAgentWithName(name).sendRequest(request);
					this.function.proposeRequest(request);
				}
			}*/
			Request request = new RequestRow(this.criticality, this.name, this.idRequest++, this.input.getName(), Reason.OVERCHARGED);
			this.function.proposeRequest(request);
			break;
		case UNDERCHARGED:
			/*for(String name: this.row.keySet()) {
				Request request = new RequestRow(this.criticality, this.name, this.idRequest++, this.input.getName(), Reason.UNDERCHARGED);
				//this.function.getDataAgentWithName(name).sendRequest(request);
				this.function.proposeRequest(request);

			}*/
			Request request2 = new RequestRow(this.criticality, this.name, this.idRequest++, this.input.getName(), Reason.UNDERCHARGED);
			this.function.proposeRequest(request2);
			break;
		default:
			break;

		}
	}

	/**
	 * Compute the criticality if a dataAgent apply
	 * 
	 * @return the criticality
	 */
	public double criticalityIfApplying() {
		return criticality + Math.pow(criticality, 2);
	}

	/*
	 * Add a new DataAgent
	 */
	public void addDataAgent(DataAgent dataAgent) {
		this.row.put(dataAgent.getName(), 0.0);
	}

	public void dataAgentApplying(DataAgent dataAgent) {
		this.row.put(dataAgent.getName(), 1.0);
	}

	@Override
	public void requestAccepted(int id) {

	}

	@Override
	public void requestDenied(int id) {
		// TODO Auto-generated method stub

	}

	public void addRequest(RequestForRow requestForRow) {
		this.mailBox.add(requestForRow);
	}
}
