package fr.irit.smac.mas;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.tuple.Pair;

import Jama.Matrix;
import fr.irit.smac.amak.Agent;
import fr.irit.smac.functions.LPDFunction;
import fr.irit.smac.functions.SumFunction;

public class AGFunction extends Agent<AmasF,EnvironmentF>{

	
	private final static int NB_COMMUNICATION_MAX = 20;
	
	private Map<String,Double>  parameters;

	//private List<Map<String,Double>> history;
	
	//private List<Double> resultHistory;
	
	private Map<String,Double> coeffs;
	
	private String name;
	
	private List<AGFunction> neighbours;
	
	private String length;
	
	private String speed;
	
	private String capacity;
	
	private List<String> flow;
	
	
	private Set<String> dataCommunicated;
	
	private List<String> parametersFixes;
	
	private Set<String> parametersVariables;
	
	private Set<String> parametersUseful;
	private Set<String> parametersNotUseful;
	private Set<String> parametersCommunicated;
	
	private LPDFunction myFunctionLPD;
	private SumFunction myFunctionSum;
	
	private double feedback;
	public AGFunction(AmasF amas, Object[] params, String name) {
		super(amas, params);
		
		this.name = name;
		parameters = new TreeMap<String,Double>();
		coeffs = new TreeMap<String,Double>();
		this.neighbours = new ArrayList<AGFunction>();
		this.dataCommunicated = new TreeSet<String>();
		this.parametersFixes = new ArrayList<String>();
		this.parametersVariables = new TreeSet<String>();
		this.parametersUseful = new TreeSet<String>();
		this.parametersNotUseful = new TreeSet<String>();
		this.parametersCommunicated = new TreeSet<String>();
		
		this.flow = new ArrayList<String>();
		feedback = 0.0;
		initHistory();
	}
	
	private void initHistory() {
		//this.history = new ArrayList<Map<String,Double>>();
		//this.resultHistory = new ArrayList<Double>();
	}
	
	@Override
	protected void onInitialization() {
		
	}
	
	@Override
	protected void onAgentCycleBegin() {
		for(String s : this.parameters.keySet()) {
			this.parameters.put(s, 0.0);
		}
	}
	
	
	@Override
	protected void onPerceive() {
		switch(getAmas().getEnvironment().getExpe()) {
		case LPD:
			// Recuperation du voisinage
			this.neighbours = this.getAmas().askNeighbourgs(this);
			
			//Renitialisation de la fonction
			this.myFunctionLPD = new LPDFunction(this.getAmas().getLength(this.length), this.getAmas().getSpeed(this.speed));
			
			
			//if(this.capacity != null && this.dataPerceived.contains(this.capacity)) {
				this.myFunctionLPD.setC(this.getAmas().getCapacity(this.capacity));
			//}
			

			// Recuperation de tous les parametres percus
			/*for(String s : this.flow) {
				if(this.dataPerceived.contains(s)) {
					this.myFunctionLPD.addFlow(this.getAmas().getFlow(s));
				}
			}*/
			break;
		case RANDOM:
			this.myFunctionSum = new SumFunction();
			for(String s : this.parametersFixes) {
				this.parameters.put(s, this.getAmas().getValueOfParameters(s,this));
			}
			for(String s : this.parametersVariables) {
				getAmas().CommunicateNeedOfVariableType(s);
			}
			this.parametersUseful.addAll(getAmas().isParametersUseful(this.parametersFixes));
			this.parametersCommunicated.removeAll(this.parametersUseful);
			this.parametersNotUseful.addAll(this.parametersCommunicated);
			this.parametersCommunicated = new TreeSet<String>();
			
			

			int nbCom = 0;
			for(String variableUseful : this.parametersUseful) {
				nbCom++;
				this.getAmas().communicateValueOfVariable(variableUseful, this.parameters.get(variableUseful),this);
				this.parametersCommunicated.add(variableUseful);
			}
			
			List<String> variablesRemaining = new ArrayList<String>(this.parametersFixes);
			variablesRemaining.removeAll(this.parametersUseful);
			variablesRemaining.removeAll(parametersNotUseful);
			for(int j = 0; j < variablesRemaining.size() && nbCom < NB_COMMUNICATION_MAX;j++) {
				String s = variablesRemaining.get(j);
					this.getAmas().communicateValueOfVariable(s,this.parameters.get(s),this);
					nbCom++;
					this.parametersCommunicated.add(s);
			}
			
			break;
		default:
			break;
		
		}
	}
	
	@Override
	protected void onDecide() {
		switch(getAmas().getEnvironment().getExpe()) {
		case LPD:
			// Si le resultat precedent est faux on cherche de nouvelles informations
			if(this.feedback > 1.0) {
				for(AGFunction ag : this.neighbours) {
					for(String s : ag.flow) {
						if(!this.flow.contains(s) && this.getAmas().isParameterUseful(s, this)) {
							this.flow.add(s);
							this.dataCommunicated.add(s);
						}
					}
				}
			}
			// Recuperation des donnes par communication
			Set<String> dataMisses = new TreeSet<String>(this.dataCommunicated);
			int i = 0;
			while(dataMisses.size() > 0 && i < this.neighbours.size()) {
				AGFunction agf = this.neighbours.get(i);
				Map<String,Double> datas = agf.exchangeInformation(dataMisses);
				
				for(String s : datas.keySet()) {
					dataMisses.remove(s);
					this.myFunctionLPD.addFlow(datas.get(s));
				}
				
			}
			break;
		case RANDOM:
			/*int nbCom = 0;
			for(String variableUseful : this.parametersUseful) {
				nbCom++;
				this.getAmas().communicateValueOfVariable(variableUseful, this.parameters.get(variableUseful));
				this.parametersCommunicated.add(variableUseful);
			}
			//System.out.println("NBCOM BEFORE : "+nbCom);
			List<String> variablesRemaining = new ArrayList<String>(this.parametersFixes);
			variablesRemaining.removeAll(this.parametersUseful);
			variablesRemaining.removeAll(parametersNotUseful);
			for(int j = 0; j < variablesRemaining.size() && nbCom < NB_COMMUNICATION_MAX;j++) {
				String s = variablesRemaining.get(j);
					this.getAmas().communicateValueOfVariable(s,this.parameters.get(s));
					nbCom++;
					this.parametersCommunicated.add(s);
			}*/
			
			
			
			
			break;
		default:
			break;
		
		}
	}

	/**
	 * Ask a neighbour the informations
	 */
	private void searchForInformation() {
		int i = 0;
		boolean end = false;
		for(AGFunction agf : this.neighbours) {
			this.exchangeInformation(agf.parameters.keySet());
		}
		this.initHistory();
	}

	/**
	 * Partage d'information a une autre fonction
	 * @param parameters2
	 * @return Map avec les couples parametres et valeurs
	 * TODO Communication des donnees communiquees 
	 */
	private Map<String, Double> exchangeInformation(Set<String> parameters2) {
		Map<String, Double> ret = new TreeMap<String,Double>();
		for(String s : parameters2) {
			/*if(this.dataPerceived.contains(s)) {
				ret.put(s, this.getAmas().getFlow(s));
			}*/
		}
		return ret;
	}

	@Override
	protected void onAct() {
		switch(getAmas().getEnvironment().getExpe()) {
		case LPD:
			double res = this.myFunctionLPD.compute();
			System.out.println(this.name + " RESULTAT : "+res);
			double oracle = this.getAmas().getValueOracle(this.name);
			feedback = Math.abs(res - oracle);
			System.out.println(this.name + " : ORACLE : "+oracle);
			break;
		case RANDOM:
			// Get the parameters communicated
			for(String s : this.parametersVariables) {
				this.parameters.put(s, this.getAmas().getValueFromNetwork(s,this));
			}
			
			double resSum = this.myFunctionSum.compute(this.parameters);
			
			double oracleSum = this.getAmas().getValueOracle(this.name);
			
			feedback = Math.abs(resSum - oracleSum);
			
			break;
		default:
			break;
		
		}
		
		//this.resultHistory.add(oracle);
		//this.history.add(new TreeMap<String,Double>(this.parameters));
	}
	
	/**
	 * getter on name
	 * @return name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Add a new parameter
	 * @param s
	 * 	the name of the parameter
	 * @param coeff
	 * 	the coefficient
	 */
	protected void addParameter(String s) {
		this.parameters.put(s, 1.0);
	}

	@Override
	public String toString() {
		return "AGFunction [parameters=" + parameters + ", coeffs=" + coeffs + ", name=" + name + ", feedback="
				+ feedback + "]";
	}
	
	
	public void addDataPerceived(String data) {
		//this.dataPerceived.add(data);
	}

	public void setFunction(LPDFunction fun) {
		this.myFunctionLPD = fun;
	}
	
	public void setLength(String length) {

		this.addParameter(length);
		this.addDataPerceived(length);
		this.length = length;
	}
	
	public void setSpeed(String speed) {
		this.addParameter(speed);
		this.addDataPerceived(speed);
		this.speed =speed;
	}
	
	public void setC(String c) {
		this.addParameter(c);
		this.addDataPerceived(c);
		this.capacity = c;
	}
	
	public void addFlow(String flow) {
		this.flow.add(flow);
	}

	public double computeLPD() {
		return this.myFunctionLPD.compute();
	}

	public void addParameterFixe(String variable) {
		this.parametersFixes.add(variable);
	}
	

	public void addParameterVariable(String variable) {
		this.parametersVariables.add(variable);
	}

	public double computeSum() {
		return this.myFunctionSum.compute(this.parameters);
	}

	public Map<String,Double> getParameterAndValue() {
		return this.parameters;
	}

	public Set<String> getParametersVariables() {
		return this.parametersVariables;
	}

}
