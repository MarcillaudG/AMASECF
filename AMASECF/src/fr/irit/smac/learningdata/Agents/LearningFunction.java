package fr.irit.smac.learningdata.Agents;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.irit.smac.amak.Agent;
import fr.irit.smac.learningdata.AmasLearning;
import fr.irit.smac.learningdata.EnvironmentLearning;
import fr.irit.smac.modelui.InputLearningModel;
import fr.irit.smac.shield.c2av.SyntheticFunction;

public class LearningFunction extends Agent<AmasLearning, EnvironmentLearning>{

	private SyntheticFunction function;
	private Deque<Double> valueOfOperand;


	private Set<String> variableInEnvironment;

	private List<Double> historyFeedback;

	private Map<String,InputAgent> allInputAgent;

	private Map<String,DataAgent> allDataAgent;

	private Map<InputAgent, RowAgent> allRowAgent;

	private Map<DataAgent, ColumnAgent> allColumnAgent;

	private Map<String,AgentLearning> allAgents;

	private String name;
	private double feedback;

	private int nbDataAgent = 0;


	public LearningFunction(AmasLearning amas, Object[] params, String name, SyntheticFunction function) {
		super(amas, params);
		this.name = name;
		this.function = function;
		init();
	}

	private void init() {
		this.allDataAgent = new TreeMap<String,DataAgent>();
		this.allInputAgent = new TreeMap<String,InputAgent>();
		this.allColumnAgent = new TreeMap<DataAgent, ColumnAgent>();
		this.allRowAgent = new TreeMap<InputAgent, RowAgent>();
		this.historyFeedback = new ArrayList<Double>();
		this.allAgents = new TreeMap<String,AgentLearning>();
		this.feedback = 0.0;

		for(Integer i : this.function.getInputIDRemoved()) {
			this.createInputAgent("Input:"+i, i);
			this.createRowAgent("Row"+i,this.allInputAgent.get("Input"+i));
		}
	}

	public String getName() {
		return this.name;
	}

	@Override
	protected void onAgentCycleBegin() {

		System.out.println("Cycle : "+this.getAmas().getCycle());
		System.out.println("NB DATA : "+this.nbDataAgent);
		this.valueOfOperand = new ArrayDeque<Double>();
		this.variableInEnvironment = new TreeSet<String>();
		
		// Update the influence and the trust
		

	}

	@Override
	protected void onPerceive() {
		this.historyFeedback.add(this.feedback);
		Iterator<String> iter = this.function.getOperands().iterator();
		while(iter.hasNext()) {
			this.valueOfOperand.offer(this.getAmas().getValueOfVariable(iter.next()));
		}

		// Getting the variables in the environment
		this.variableInEnvironment.addAll(this.getAmas().getVariableInEnvironment());
		this.variableInEnvironment.removeAll(this.function.getOperands());

		// Creation of the data agent
		List<String> dataAgentToCreate = new ArrayList<String>(this.variableInEnvironment);
		dataAgentToCreate.removeAll(this.allDataAgent.keySet());

		for(String s : dataAgentToCreate) {
			this.createDataAgent(s);
		}
		

		// Give the feedback to the input agent
		for(InputAgent inputAgent : this.allInputAgent.values()) {
			inputAgent.updateInfluence(this.feedback);
		}
		

		// Give the feedback of the function to all dataAgent
		for(DataAgent dataAgent : this.allDataAgent.values()) {
			dataAgent.setInputAvailable(this.allInputAgent.keySet());
			dataAgent.updateTrust(this.feedback);
		}

	}

	/**
	 * Active the different agent in the system
	 */
	@Override
	protected void onDecide() {

		//Row Agent
		this.startRowAgent();

		//ColumnAgent
		this.startColumnAgent();

		// InputAgent
		this.startInputAgent();

		// DataAgent
		this.startDataAgent();

		for(DataAgent dataAgent : this.allDataAgent.values()) {
			if(dataAgent.getWill() != null) {
				this.allInputAgent.get(dataAgent.getWill()).setDataAgent(dataAgent);
			}
		}

		for(InputAgent inputAgent : this.allInputAgent.values()) {
			this.function.setOperandOfInput(inputAgent.getId(),inputAgent.getCurrentData().getName());
		}
	}


	/**
	 * Start the cycle for all row agent
	 */
	private void startRowAgent() {
		// Random order
		List<RowAgent> rowAgentRemaining = new ArrayList<RowAgent>(this.allRowAgent.values());
		Collections.shuffle(rowAgentRemaining);

		for(RowAgent rowAgent : rowAgentRemaining) {
			rowAgent.perceive();
			rowAgent.decideAndAct();
		}
	}

	/**
	 * Start the cycle for all ColumnAgent
	 */
	private void startColumnAgent() {
		// Random order
		List<ColumnAgent> columnAgentRemaining = new ArrayList<ColumnAgent>(this.allColumnAgent.values());
		Collections.shuffle(columnAgentRemaining);

		for(ColumnAgent columnAgent : columnAgentRemaining) {
			columnAgent.perceive();
			columnAgent.decideAndAct();
		}
	}

	/**
	 * Start the cycle for all the InputAgent
	 */
	private void startInputAgent() {


		// All inputAgent perceives in random order
		List<InputAgent> inputAgentRemaining = new ArrayList<InputAgent>(this.allInputAgent.values());
		Collections.shuffle(inputAgentRemaining);
		for(InputAgent inputAgent : inputAgentRemaining) {
			inputAgent.perceive();
		}

		// All inputAgent decide and act in random order
		Collections.shuffle(inputAgentRemaining);
		for(InputAgent inputAgent : inputAgentRemaining) {
			inputAgent.decideAndAct();
		}



	}

	/**
	 * Start the cycle for all the dataAgent
	 */
	private void startDataAgent() {

		// Initialize the plan
		Map<String, List<String>> acquisition = new TreeMap<String,List<String>>();
		for(String nameOfInput: this.allInputAgent.keySet()) {
			acquisition.put(nameOfInput, new ArrayList<String>());
		}
		
		for(List<String> list :acquisition.values()) {
			list.clear();
		}
		
		List<DataAgent> dataAgentRemaining = new ArrayList<DataAgent>(this.allDataAgent.values());
		Collections.shuffle(dataAgentRemaining);

		// All dataAgent perceives
		for(DataAgent dataAgent : dataAgentRemaining) {
			dataAgent.perceive();
		}

		Collections.shuffle(dataAgentRemaining);
		// All DataAgent decide and act
		for(DataAgent dataAgent : dataAgentRemaining) {
			dataAgent.decideAndAct();
			if(dataAgent.getWill() != null) {
				acquisition.get(dataAgent.getWill()).add(dataAgent.getName());
			}

		}
		for(String nameOfInput : acquisition.keySet()) {
			if(acquisition.get(nameOfInput).size() > 1) {
				for(String nameOfData : acquisition.get(nameOfInput)) {
					this.allDataAgent.get(nameOfData).addConccurent(acquisition.get(nameOfInput));
				}
			}
		}
		System.out.println(acquisition);

	}

	@Override
	protected void onAct() {
		double result = this.function.computeInput();
		this.feedback = result - this.getAmas().getResultOracle(this.name);
		/*for(DataAgent dataAgent : this.allDataAgent.values()) {
			System.out.println(dataAgent);
			System.out.println(dataAgent.getTrustValues());
		}*/
		System.out.println(feedback);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Create a data agent
	 * 
	 * @param name
	 * 
	 * @return true if the agent does not already exist
	 */
	private boolean createDataAgent(String name) {
		if(this.allDataAgent.keySet().contains(name)) {
			return false;
		}
		DataAgent dag = new DataAgent(name,this, nbDataAgent);
		nbDataAgent++;
		for(InputAgent inputAgent : this.allInputAgent.values()) {
			dag.addNewInputAgent(inputAgent.getName());
		}
		this.allDataAgent.put(dag.getName(), dag);
		this.allAgents.put(name, dag);

		for(RowAgent rowAgent: this.allRowAgent.values()) {
			rowAgent.addDataAgent(dag);
		}
		return true;
	}

	/**
	 * Create an input Agent 
	 * @param name
	 * @return true if the agent does not already exist
	 */
	private boolean createInputAgent(String name, int id) {
		if(this.allInputAgent.containsKey(name)) {
			return false;
		}
		InputAgent inag = new InputAgent(name,this, id);
		this.allInputAgent.put(inag.getName(), inag);
		this.allAgents.put(inag.getName(), inag);
		return true;
	}

	private boolean createRowAgent(String name, InputAgent input) {
		if(this.allRowAgent.containsKey(input)) {
			return false;
		}
		RowAgent rowAgent = new RowAgent( name,input,this);
		this.allRowAgent.put(input, rowAgent);
		this.allAgents.put(name, rowAgent);
		return true;
	}

	/**
	 * Set the function
	 * 
	 * @param fun
	 */
	public void setFunction(SyntheticFunction fun) {
		this.function = fun;
	}

	public List<Double> getHistoryFeedback(){
		return this.historyFeedback;
	}

	public DataAgent getDataAgentWithName(String nameOfData) {
		return this.allDataAgent.get(nameOfData);
	}

	public Map<String, Double> getInfluences() {
		Map<String, Double> influences = new TreeMap<String,Double>();
		for(String nameOfInput : this.allInputAgent.keySet()) {
			influences.put(nameOfInput, this.allInputAgent.get(nameOfInput).getInfluence());
		}
		return influences;
	}

	public int getCycle() {
		return this.getAmas().getCycle();
	}

	public double getDataValue(String name2) {
		return this.getAmas().getValueOfVariable(name2);
	}

	public Set<String> getInputsName() {
		return this.allInputAgent.keySet();
	}

	public void addListenerToInput(String input, InputLearningModel model) {
		this.allInputAgent.get(input).addPropertyChangeListener(model);

	}



	/**
	 * Return the column agent with the name
	 * 
	 * @param name
	 * 
	 * @return the column agent
	 */
	public ColumnAgent getColumnAgentWithName(String name) {
		return this.allColumnAgent.get(name);
	}

	/**
	 * Return the row agent with the name
	 * 
	 * @param name
	 * 
	 * @return the row agent
	 */
	public RowAgent getRowAgentWithName(String name) {
		return (RowAgent) this.allAgents.get(name);
	}

	public void informDecision(DataAgent dataAgent,Set<String> inputsChosen) {
		for(String input : inputsChosen) {
			this.allRowAgent.get(this.allInputAgent.get(input)).dataAgentApplying(dataAgent);
		}

	}

	public void acceptRequest(String agentName, int idRequest) {
		this.allAgents.get(agentName).requestAccepted(idRequest);
	}

	public void rejectRequest(String agentName, int id) {
		this.allAgents.get(agentName).requestDenied(id);

	}
}
