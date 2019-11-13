package fr.irit.smac.learningdata;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import fr.irit.smac.amak.Environment;
import fr.irit.smac.amak.Scheduling;
import fr.irit.smac.generator.ShieldUser;
import fr.irit.smac.shield.c2av.SyntheticFunction;

public class EnvironmentLearning extends Environment{

	private ShieldUser shieldUser;
	
	private Map<Integer,Map<String,Double>> historic;

	public EnvironmentLearning(Scheduling _scheduling, Object[] params) {
		super(_scheduling, params);
		
		init();
	}
	
	public EnvironmentLearning(Object[] params) {
		super(Scheduling.DEFAULT, params);
		
		init();
	}

	private void init() {
		this.shieldUser = new ShieldUser();
		
		this.shieldUser.initSetOfTypedVariableWithRange(15, 0, 200, "Type 1");
		this.shieldUser.generateAllFunctionsOfVariable();
		
		this.shieldUser.initGeneratorOfFunction();
		this.historic = new TreeMap<Integer,Map<String,Double>>();
		
	}
	
	public SyntheticFunction generateFunction(String name, int nbVar) {
		this.shieldUser.generateSyntheticFunction(name,nbVar);
		
		return this.shieldUser.getSyntheticFunctionWithName(name);
	}
	
	public double getValueOfVariableWithName(String name) {
		return this.shieldUser.getValueOfVariable(name)-100;
	}

	public Set<String> getAllVariable() {
		return this.shieldUser.getAllVariables();
	}

	public void generateNewValues(int cycle) {
		this.historic.put(cycle-1, new TreeMap<String,Double>());
		for(String s : this.shieldUser.getAllVariables()) {
			this.historic.get(cycle-1).put(s, this.shieldUser.getValueOfVariable(s)-100);
		}
		/*System.out.println("ENV");
		for(String str : this.shieldUser.getAllVariables()){
			System.out.println(str+":"+this.shieldUser.getValueOfVariable(str));
		}*/
		this.shieldUser.nextCycle();
		/*System.out.println("HISt");
		System.out.println(this.historic);*/
		
	}

	public double getValueOfVariable(String var, int cycle) {
		return this.historic.get(cycle-1).get(var);
	}
	
	public void printAllVariables() {
		// TODO Auto-generated method stub
		
	}

	
	/*public static void main(String args[]) {
		EnvironmentLearning env = new EnvironmentLearning(args);
		
		env.generateNewValues();
		
		for(String s : env.getAllVariable()) {
			System.out.println(s + " : "+env.getValueOfVariableWithName(s));
		}
		env.generateNewValues();
		
		for(String s : env.getAllVariable()) {
			System.out.println(s + " : "+env.getValueOfVariableWithName(s));
		}
	}*/

}
