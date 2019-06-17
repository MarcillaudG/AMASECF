package fr.irit.smac.learningdata;

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
import fr.irit.smac.shield.c2av.SyntheticFunction;

public class LearningFunction extends Agent<AmasLearning, EnvironmentLearning>{

	private SyntheticFunction function;
	private Deque<Double> valueOfOperand;

	private List<InputAgent> inputAgents;

	private Set<String> variableInEnvironment;

	private List<Double> historyFeedback;

	private Map<String,InputAgent> allInputAgent;

	private Map<String,DataAgent> allDataAgent;

	private String name;
	private double feedback;


	public LearningFunction(AmasLearning amas, Object[] params, String name, SyntheticFunction function) {
		super(amas, params);
		this.name = name;
		this.function = function;
		init();
	}

	private void init() {
		this.allDataAgent = new TreeMap<String,DataAgent>();
		this.allInputAgent = new TreeMap<String,InputAgent>();
		this.inputAgents = new ArrayList<InputAgent>();
		this.feedback = 0.0;

		Iterator<String> iter = this.function.getOperands().iterator();
		while(iter.hasNext()) {
			this.createInputAgent(iter.next());
		}
	}

	public String getName() {
		return this.name;
	}

	@Override
	protected void onAgentCycleBegin() {
		this.valueOfOperand = new ArrayDeque<Double>();
		this.variableInEnvironment = new TreeSet<String>();
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

	}

	@Override
	protected void onDecide() {

		this.startInputAgent();

		this.startDataAgent();

		for(int i = 0; i < this.inputAgents.size();i++) {
			this.valueOfOperand.offer((this.inputAgents.get(i).getCurrentData().getValue()));
		}
	}


	/**
	 * Start the cycle for all the InputAgent
	 */
	private void startInputAgent() {
		boolean ended = false;
		for(InputAgent inputAgent : this.allInputAgent.values()) {
			inputAgent.setFeedback(this.feedback);
		}

		ended = true;
		// All inputAgent perceives in random order
		List<InputAgent> inputAgentRemaining = new ArrayList<InputAgent>(this.allInputAgent.values());
		Collections.shuffle(inputAgentRemaining);
		for(InputAgent inputAgent : inputAgentRemaining) {
			inputAgent.perceive();
		}
		/*while(inputAgentRemaining.size() != 0) {
				inputAgentRemaining.get(rand.nextInt(inputAgentRemaining.size())).perceive();
			}*/

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
		boolean ended = false;
		
		// Initialize the plan
		Map<String, List<String>> acquisition = new TreeMap<String,List<String>>();
		for(String nameOfInput: this.allInputAgent.keySet()) {
			acquisition.put(nameOfInput, new ArrayList<String>());
		}
		// Give the feedback of the function to all dataAgent
		for(DataAgent dataAgent : this.allDataAgent.values()) {
			dataAgent.setFeedback(this.feedback);
			dataAgent.setInputAvailable(this.allInputAgent.keySet());
		}

		// while the plan is not finalize
		while(!ended) {
			ended = true;
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
				acquisition.get(dataAgent.getWill()).add(dataAgent.getName());
			}
			for(String nameOfInput : acquisition.keySet()) {
				if(acquisition.get(nameOfInput).size() > 1) {
					ended = false;
					for(String nameOfData : acquisition.get(nameOfInput)) {
						this.allDataAgent.get(nameOfData).addConccurent(acquisition.get(nameOfInput));
					}
				}
			}
		}

	}

	@Override
	protected void onAct() {
		double result = this.function.compute();
		this.feedback = result - this.getAmas().getResultOracle(this.name);
	}

	private boolean createDataAgent(String name) {
		if(this.allDataAgent.keySet().contains(name)) {
			return false;
		}
		DataAgent dag = new DataAgent(name,this);
		this.allDataAgent.put(dag.getName(), dag);
		return true;
	}

	/**
	 * Create an input Agent 
	 * @param name
	 * @return true if the agent does not already exist
	 */
	private boolean createInputAgent(String name) {
		if(this.allInputAgent.containsKey(name)) {
			return false;
		}
		InputAgent inag = new InputAgent(name,this);
		this.allInputAgent.put(inag.getName(), inag);
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
}
