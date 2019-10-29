package fr.irit.smac.learningdata.Agents;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.irit.smac.learningdata.Agents.InputAgent.Operator;
import fr.irit.smac.learningdata.requests.Offer;
import fr.irit.smac.learningdata.requests.Request;
import fr.irit.smac.learningdata.requests.RequestForRow;
import fr.irit.smac.learningdata.requests.RequestForWeight;

public class WeightAgent extends AgentLearning{


	private String myData;

	private String myInput;

	private LearningFunction myFunction;

	private double weight;

	private RequestForWeight rowRequest;

	private RequestForWeight dataRequest;

	private RequestForWeight columnRequest;

	private List<RequestForWeight> mailBox;

	private static int SIZE_WINDOW = 3;

	private Map<Integer,Integer> past;

	private Integer[] memory = new Integer[SIZE_WINDOW];

	private int indMemory;


	public WeightAgent(LearningFunction function, String data, String input) {
		this.myData = data;
		this.myFunction = function;
		this.myInput = input;

		this.mailBox = new ArrayList<RequestForWeight>();
		this.past = new TreeMap<Integer,Integer>();
		this.weight = 0.5;
		this.indMemory = 0;
	}

	/**
	 * Add a request to the mailbox of the agent
	 * 
	 * @param request
	 * 			The request
	 */
	public void addRequest(RequestForWeight request) {
		this.mailBox.add(request);
	}

	/**
	 * Setter for the weight
	 * @param weight
	 */
	public void setWeigth(double weight) {
		this.weight = weight;
	}

	/**
	 * Getter for the weight
	 * @return weight
	 */
	public Double getWeight() {
		return this.weight;
	}

	public void onPerceive() {
		for(RequestForWeight request : this.mailBox) {
			if(request.getAgentType().equals("ROW")) {
				this.rowRequest = request;
			}
			else {
				if(request.getAgentType().equals("COLUMN")) {
					this.columnRequest = request;
				}
				else {
					this.dataRequest = request;
				}
			}
		}
		this.mailBox.clear();


	}

	public void onDecideAndAct(int step) {

		//this.myFunction.askRow(this.myInput,new RequestForRow(this.dataRequest.getCriticality(), this.dataRequest.getAgentName(), this.dataRequest.getId(), this.dataRequest.getDecision()));
		Operator myEnvy = this.computeEnvy();
		List<Operator> allDecisions = new ArrayList<Operator>();
		allDecisions.add(myEnvy);
		allDecisions.add(this.dataRequest.getDecision());
		allDecisions.add(this.rowRequest.getDecision());
		allDecisions.add(this.columnRequest.getDecision());

		if(step == 0) {
			if(this.isDecisionCoherent(allDecisions)) {
				this.applyDecision(this.dataRequest.getDecision());
			}
		}
		if(step == 1) {
			if(this.dataRequest.getDecision().equals(this.rowRequest.getDecision()) || this.dataRequest.getDecision().equals(this.columnRequest.getDecision())) {
				this.applyDecision(this.dataRequest.getDecision());
			}
		}
		if(step == 2) {
			double inputCrit = this.myFunction.getInputCriticalityAfterUpdate(this.myData,this.myInput,this.dataRequest.getDecision());
			double dataCrit = this.myFunction.getDataCriticalityAfterUpdate(this.myData,this.myInput,this.dataRequest.getDecision());
			if(inputCrit >dataCrit) {
				this.applyDecision(this.dataRequest.getDecision());
			}
			else {
				this.applyDecision(this.dataRequest.getDecision());
			}
		}
		


	}

	private void applyDecision(Operator decision) {
		switch(decision) {
		case MOINS:
			this.decreaseWeight();
			break;
		case NONE:
			break;
		case PLUS:
			this.increaseWeight();
			break;
		default:
			break;

		}
		
	}

	@SuppressWarnings("unused")
	private Operator computeEnvy() {
		Double res = null;
		for(int i = 0; i < SIZE_WINDOW; i++) {
			if(this.memory[i] != null) {
				res += this.memory[i];
			}
		}
		if(res != null) {
			if(res >0) {
				return Operator.PLUS;
			}
			if(res <0) {
				return Operator.MOINS;
			}
		}
		return Operator.NONE;
	}

	private boolean isDecisionCoherent(List<Operator> allDecisions) {
		boolean pos = false;
		boolean neg = false;
		for(Operator ope : allDecisions) {
			if(ope == Operator.PLUS) {
				pos = true;
			}
			if(ope == Operator.MOINS) {
				neg = true;
			}
		}
		return (neg == false && pos == false) || neg != pos;
	}

	/**
	 * @param allDecisions 
	 * 
	 */
	private void solveSNC() {
		double critInput = this.rowRequest.getCriticality();
		double critData = this.columnRequest.getCriticality();
		//System.out.println("DATA : "+this.dataRequest.getDecision()+"|Column :"+this.columnRequest.getDecision());
		if(this.dataRequest.getDecision() == this.columnRequest.getDecision() || this.columnRequest.getDecision() == Operator.NONE) {
			if(critData > critInput) {
				switch(this.dataRequest.getDecision()) {
				case MOINS:
					this.decreaseWeight();
					break;
				case NONE:
					break;
				case PLUS:
					this.increaseWeight();
					break;
				default:
					break;

				}
			}
			else {
				switch(this.rowRequest.getDecision()) {
				case MOINS:
					this.decreaseWeight();
					break;
				case NONE:
					break;
				case PLUS:
					this.increaseWeight();
					break;
				default:
					break;

				}
			}
		}
	}

	@Override
	public void requestAccepted(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void requestDenied(int id) {
		// TODO Auto-generated method stub

	}

	public Offer askWeight(Operator decision) {
		if(this.dataRequest != null && this.dataRequest.getDecision() != decision && this.weight != 0.0) {
			return null;
		}
		else {
			return new Offer(this.myData,0.0);
		}
	}

	public void decreaseWeight() {
		if(this.weight>0.0) {
			this.myFunction.modification();
		}
		this.weight = Math.max(0.0, (this.weight*100-5)/100);
		
	}

	public void increaseWeight() {
		/*if(this.weight < 1.0) {
			this.myFunction.modification();
		}*/
		//this.weight = Math.min(1.0, this.weight+0.05);
		this.myFunction.modification();
		this.weight = (this.weight*100+5)/100;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}


}
