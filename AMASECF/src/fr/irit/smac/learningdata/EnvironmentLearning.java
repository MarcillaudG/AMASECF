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

	private void init() {
		this.shieldUser = new ShieldUser();
		
		this.shieldUser.initSetOfTypedVariableWithRange(150, 0, 100, "Type 1");
		
		this.shieldUser.initGeneratorOfFunction();
		
	}
	
	public SyntheticFunction generateFunction(String name, int nbVar) {
		this.shieldUser.generateSyntheticFunction(name,nbVar);
		
		return this.shieldUser.getSyntheticFunctionWithName(name);
	}
	
	public double getValueOfVariableWithName(String name) {
		return this.shieldUser.getValueOfVariable(name);
	}

	public Set<String> getAllVariable() {
		return this.shieldUser.getAllVariables();
	}

	
	

}
