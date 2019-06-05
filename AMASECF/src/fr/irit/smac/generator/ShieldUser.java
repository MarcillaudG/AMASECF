package fr.irit.smac.generator;

import java.util.Set;

import fr.irit.smac.shield.c2av.GeneratorOfFunction;
import fr.irit.smac.shield.c2av.GeneratorOfTypedVariable;
import fr.irit.smac.shield.c2av.SyntheticFunction;
import fr.irit.smac.shield.model.Generator;

public class ShieldUser {

	private GeneratorOfTypedVariable generator;
	
	private GeneratorOfFunction funGen;
	
	public ShieldUser() {
		this.generator = new GeneratorOfTypedVariable();
	}
	
	
	public void initSetOfVariableWithRange(int nbVar,double min, double max) {
		this.generator.initSetOfVariableWithRange(nbVar, min, max);
		this.generator.generateAllValues();
	}
	
	public void initSetOfTypedVariableWithRange(int nbVar, double min, double max, String type) {
		this.generator.initSetOfTypedVariable(nbVar, min, max, type);
	}
	
	public void generateAllFunctionsOfVariable() {
		this.generator.generateAllFunctions();
	}
	
	/**
	 * Init the generator of function
	 */
	public void initGeneratorOfFunction() {
		this.funGen = new GeneratorOfFunction(this.generator);
	}
	
	/**
	 * Return the synthetic function with the matching name
	 * 
	 * @param name
	 * 			The name of the function
	 * @return a synthetic function
	 */
	public SyntheticFunction getSyntheticFunctionWithName(String name) {
		return this.funGen.getSyntheticFunctionWithName(name);
	}
	
	/**
	 * Generate a new synthetic function
	 * 
	 * @param name
	 * 			The name of the function
	 * @param nbVarMax
	 * 			The max number of variable for the compute of the function
	 */
	public void generateSyntheticFunction(String name, int nbVarMax) {
		this.funGen.generateFunction(name,nbVarMax);
	}
	
	/**
	 * Return the value of a variable
	 * 
	 * @param name
	 * 
	 * @return the value
	 */
	public double getValueOfVariable(String name) {
		return this.generator.getValueOfVariable(name);
	}
	
	public void nextCycle() {
		this.generator.generateAllValues();
	}
	
	public Set<String> getAllVariables(){
		return this.generator.getAllVariables();
	}
}
