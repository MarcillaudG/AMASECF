package fr.irit.smac.learningdata;

import java.util.ArrayDeque;
import java.util.ArrayList;
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
	
	public LearningFunction(AmasLearning amas, Object[] params, String name, SyntheticFunction function) {
		super(amas, params);
		this.name = name;
		this.function = function;
		init();
	}

	private Map<String,InputAgent> allInputAgent;
	
	private Map<String,DataAgent> allDataAgent;
	
	private String name;
	private double feedback;
	
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
		
		for(int i = 0; i < this.inputAgents.size();i++) {
			this.valueOfOperand.offer((this.inputAgents.get(i).getCurrentData().getValue()));
		}
	}
	
	private void startInputAgent() {
		boolean ended = false;
		Random rand = new Random();
		for(InputAgent inputAgent : this.allInputAgent.values()) {
			inputAgent.setFeedback(this.feedback);
		}
		
		List<InputAgent> inputAgentRemaining = new ArrayList<InputAgent>(this.allInputAgent.values());
		while(inputAgentRemaining.size() != 0) {
			inputAgentRemaining.remove(rand.nextInt(inputAgentRemaining.size())).perceive();
		}
		inputAgentRemaining.addAll(this.allInputAgent.values());
		while(inputAgentRemaining.size() != 0) {
			inputAgentRemaining.remove(rand.nextInt(inputAgentRemaining.size())).decideAndAct();
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
	
	private boolean createInputAgent(String name) {
		if(this.allInputAgent.containsKey(name)) {
			return false;
		}
		InputAgent inag = new InputAgent(name,this);
		this.allInputAgent.put(inag.getName(), inag);
		return true;
	}
	
	public void setFunction(SyntheticFunction fun) {
		this.function = fun;
	}
}
