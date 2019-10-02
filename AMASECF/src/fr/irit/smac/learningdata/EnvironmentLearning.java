package fr.irit.smac.learningdata;

import java.util.Set;

import fr.irit.smac.amak.Environment;
import fr.irit.smac.amak.Scheduling;
import fr.irit.smac.generator.ShieldUser;
import fr.irit.smac.shield.c2av.SyntheticFunction;

public class EnvironmentLearning extends Environment{

	private ShieldUser shieldUser;

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
		
		this.shieldUser.initSetOfTypedVariableWithRange(50, 0, 200, "Type 1");
		this.shieldUser.generateAllFunctionsOfVariable();
		
		this.shieldUser.initGeneratorOfFunction();
		
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

	public void generateNewValues() {
		this.shieldUser.nextCycle();
		
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
