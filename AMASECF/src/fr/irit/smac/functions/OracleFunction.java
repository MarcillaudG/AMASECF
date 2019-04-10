package fr.irit.smac.functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.irit.smac.mas.EnvironmentF;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class OracleFunction {

	
	private Map<String,Double> coeffs;
	
	private String name;
	
	private Map<String, Double> parameters;
	
	private List<String> keysParameters;
	
	private Set<String> parametersFixes;
	
	private Set<String> parametersVariables;
	
	private SumFunction myFunctionSum;
	
	private LPDFunction myFunctionLPD;
	
	public OracleFunction(String name) {
		this.name = name;
		this.coeffs = new TreeMap<String,Double>();
		this.parameters = new TreeMap<String,Double>();
		this.keysParameters = new ArrayList<String>();
		this.parametersFixes = new TreeSet<String>();
		this.parametersVariables = new TreeSet<String>();
		this.myFunctionSum = new SumFunction();
	}
	
	/**
	 * Return the result of the function
	 * @return the result
	 */
	public double computeSum() {
		return this.myFunctionSum.compute();
	}
	public double computeLPD() {
		return this.myFunctionLPD.compute();
	}
	
	public void addCoeff(String s, double coeff) {
		this.coeffs.put(s, coeff);
	}
	
	public void setCoeffs(Map<String, Double> coeffs) {
		this.coeffs = coeffs;
	}
	
	public void addParameter(String s, Double parameter) {
		this.parameters.put(s, parameter);
		this.keysParameters.add(s);
	}
	
	public String getParametersI(int i) {
		return this.keysParameters.get(i);
	}
	
	public String getname() {
		return this.name;
	}
	
	public int getNbParameters() {
		return this.keysParameters.size();
	}
	
	public double getCoeff(String s) {
		return this.coeffs.get(s);
	}

	@Override
	public String toString() {
		return "OracleFunction [coeffs=" + coeffs + ", name=" + name + ", parameters=" + parameters
				+ ", keysParameters=" + keysParameters + "]";
	}

	public List<String> getParameters() {
		return this.keysParameters;
	}
	
	public Set<String> getAllParameters(){
		return this.parameters.keySet();
	}
	
	/**
	 * Put a paramater in the map of parameters
	 * @param s
	 * @param value
	 */
	public void setParameter(String s, double value) {
		this.parameters.put(s, value);
	}
	
	/**
	 * Set the calcul function
	 * @param fun
	 */
	public void setFunction(LPDFunction fun) {
		this.myFunctionLPD = fun;
	}
	
	public void setLength(Double length) {
		this.myFunctionLPD.setL(length);
	}
	
	public void setSpeed(Double speed) {
		this.myFunctionLPD.setV(speed);
	}
	
	public void setC(Double c) {
		this.myFunctionLPD.setC(c);
	}
	
	public void addFlow(Double flow) {
		this.myFunctionLPD.addFlow(flow);
	}

	public LPDFunction getFunction() {
		return this.myFunctionLPD;
	}

	public void addParameters(String string) {
		this.keysParameters.add(string);
		
	}

	public void reinit() {
		this.myFunctionLPD.initFlows();
		
	}

	public void addParametersFixe(String variable) {
		this.parametersFixes.add(variable);
		this.myFunctionSum.addCorrectVariable(variable);
		this.parameters.put(variable, 0.0);
		
	}

	public Set<String> getParametersFixes() {
		return this.parametersFixes;
	}

	public void addParametersVariable(String variable) {
		this.parametersVariables.add(variable);
		this.myFunctionSum.addCorrectVariable(variable);
		this.parameters.put(variable, 0.0);
	}
	

	public Set<String> getParametersVariables() {
		return this.parametersVariables;
	}
	
	public void setValueOfVariable(String variable,Double value) {
		this.parameters.put(variable, value);
		this.myFunctionSum.AddParameters(variable, value);
	}

	public Map<String,Double> getParameterAndValue() {
		return this.parameters;
		
	}


	
}
