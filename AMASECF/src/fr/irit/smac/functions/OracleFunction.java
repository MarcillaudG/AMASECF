package fr.irit.smac.functions;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OracleFunction {

	
	private Map<String,Double> coeffs;
	
	private String name;
	
	private Map<String, Double> parameters;
	
	private List<String> keysParameters;
	
	private LPDFunction myFunction;
	
	public OracleFunction(String name) {
		this.name = name;
		this.coeffs = new TreeMap<String,Double>();
		this.parameters = new TreeMap<String,Double>();
		this.keysParameters = new ArrayList<String>();
		this.myFunction = new LPDFunction(0.0, 0.0);
	}
	
	/**
	 * Return the result of the function
	 * @return the result
	 */
	public double compute() {
		return this.myFunction.compute();
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
		this.myFunction = fun;
	}
	
	public void setLength(Double length) {
		this.myFunction.setL(length);
	}
	
	public void setSpeed(Double speed) {
		this.myFunction.setV(speed);
	}
	
	public void setC(Double c) {
		this.myFunction.setC(c);
	}
	
	public void addFlow(Double flow) {
		this.myFunction.addFlow(flow);
	}

	public LPDFunction getFunction() {
		return this.myFunction;
	}

	public void addParameters(String string) {
		this.keysParameters.add(string);
		
	}

	public void reinit() {
		this.myFunction.initFlows();
		
	}
	
	
}
