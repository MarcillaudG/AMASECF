package fr.irit.smac.learningdata;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import fr.irit.smac.amak.Amas;
import fr.irit.smac.amak.Scheduling;
import fr.irit.smac.learningdata.Agents.LearningFunction;
import fr.irit.smac.modelui.learning.DataLearningModel;
import fr.irit.smac.modelui.learning.InputLearningModel;
import fr.irit.smac.shield.c2av.Input;
import fr.irit.smac.shield.c2av.SyntheticFunction;
import fr.irit.smac.shield.exceptions.TooMuchVariableToRemoveException;

public class AmasLearning extends Amas<EnvironmentLearning>{

	private Map<String, LearningFunction> allFunctions;
	private Map<String,SyntheticFunction> oracles;
	
	public AmasLearning(EnvironmentLearning environment, Scheduling scheduling, Object[] params) {
		super(environment, scheduling, params);
		
		init(params);
	}
	

	public AmasLearning(EnvironmentLearning environment,Object[] params) {
		super(environment, Scheduling.DEFAULT, params);
		
		init(params);
	}

	private void init(Object[] params) {
		this.allFunctions = new TreeMap<String,LearningFunction>();
		this.oracles = new TreeMap<String,SyntheticFunction>();
		
		
		String name = "Function1";
		this.oracles.put(name, this.environment.generateFunction(name, 5));
		this.environment.printAllVariables();
		LearningFunction lfun = new LearningFunction(this, params,name,this.degradeFunction(name,3));
		for(String input : lfun.getInputsName()) {
			
		}
		this.allFunctions.put(lfun.getName(), lfun);
	}
	
	private SyntheticFunction degradeFunction(String name, int nbVar) {
		try {
			return this.oracles.get(name).degradeFunctionInput(nbVar);
		} catch (TooMuchVariableToRemoveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public double getValueOfVariable(String variable) {
		return this.environment.getValueOfVariableWithName(variable);
		
	}

	public Set<String> getVariableInEnvironment() {
		System.out.println(this.environment.getAllVariable().size());
		return this.environment.getAllVariable();
	}

	public double getResultOracle(String name) {
		for(int i = 0; i < this.oracles.get(name).getNbInput();i++) {
			this.oracles.get(name).setValueOfOperand(i, this.getValueOfVariable(this.oracles.get(name).getInput(i).getOperand()));
		}
		return this.oracles.get(name).computeCustomOracle();
	}
	
	public double getResultOracle(String name,List<Integer> inputs) {
		return this.oracles.get(name).computeInput(inputs);
	}


	public Set<String> getInputsName(String name) {
		return this.allFunctions.get(name).getInputsName();
	}


	public void addListenerToInput(String function,String input, InputLearningModel model) {
		this.allFunctions.get(function).addListenerToInput(input,model);
		
	}


	public void addListenerToData(String function, String data, DataLearningModel model) {
		this.allFunctions.get(function).addListenerToData(data,model);		
	}


	public Set<String> getDatasNames(String function) {
		return this.allFunctions.get(function).getDatasNames();
	}


	public String getNameOfCorrectDataForInput(int idInput, String nameOfFunction) {
		return this.oracles.get(nameOfFunction).getInput(idInput).getOperand();
		
	}


	public void generateNewValues() {
		this.environment.generateNewValues();
	}

	@Override
	protected void onSystemCycleBegin() {
		super.onSystemCycleBegin();
		for(LearningFunction lf : this.allFunctions.values()) {
			this.setValueOfVariableNonDegraded(lf);
		}
	}


	public void setValueOfVariableNonDegraded(LearningFunction lf) {
		for(int i = 0; i < this.oracles.get(lf.getName()).getNbInput();i++) {
			if(!lf.getFunction().getInputIDRemoved().contains(i)) {
				lf.getFunction().setValueOfOperand(i,this.getValueOfVariable(lf.getFunction().getInput(i).getOperand()));
			}
		}
	}


}
