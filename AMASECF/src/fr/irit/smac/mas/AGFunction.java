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

public class AGFunction extends Agent<AmasF,EnvironmentF>{

	private final static double SEUIL = 1.0;
	
	private final static int NB_CYCLE_MIN = 5;
	
	private final static int NB_CYCLE_MAX = 100;
	
	private int cycle_since_last_information;
	
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
	
	private Set<String> dataPerceived;
	
	private Set<String> dataCommunicated;
	
	
	
	private LPDFunction myFunction;
	
	private double feedback;
	public AGFunction(AmasF amas, Object[] params, String name) {
		super(amas, params);
		
		this.name = name;
		parameters = new TreeMap<String,Double>();
		coeffs = new TreeMap<String,Double>();
		this.neighbours = new ArrayList<AGFunction>();
		this.dataPerceived = new TreeSet<String>();
		this.dataCommunicated = new TreeSet<String>();
		
		this.flow = new ArrayList<String>();
		feedback = 0.0;
		initHistory();
	}
	
	private void initHistory() {
		//this.history = new ArrayList<Map<String,Double>>();
		//this.resultHistory = new ArrayList<Double>();
		this.cycle_since_last_information = 0;
	}
	
	@Override
	protected void onInitialization() {
		
	}
	
	@Override
	protected void onPerceive() {
		// Recuperation du voisinage
		this.neighbours = this.getAmas().askNeighbourgs(this);
		
		//Renitialisation de la fonction
		this.myFunction = new LPDFunction(this.getAmas().getLength(this.length), this.getAmas().getSpeed(this.speed));
		
		
		//if(this.capacity != null && this.dataPerceived.contains(this.capacity)) {
			this.myFunction.setC(this.getAmas().getCapacity(this.capacity));
		//}
		

		// Recuperation de tous les parametres percus
		for(String s : this.flow) {
			if(this.dataPerceived.contains(s)) {
				this.myFunction.addFlow(this.getAmas().getFlow(s));
			}
		}
	}
	
	@Override
	protected void onDecide() {
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
				this.myFunction.addFlow(datas.get(s));
			}
			
		}
	}
	
	/*private void updateCoeff() {
		double[][] experiments = new double[this.history.size()][this.parameters.size()];
		double[] resultsOracle = new double[this.history.size()];
		
		// CHANGER EN TREEMAP !!!!!!!!!!
		//Map<Integer,String> heresy = new HashMap<Integer,String>();
		
		int ind = 0;
		for(int i = 0; i < this.history.size(); i++) {
			Map<String,Double> tmp = this.history.get(i);
			for(String s : this.parameters.keySet()) {
				experiments[i][ind] = tmp.get(s);
				ind++;
			}
			resultsOracle[i] = this.resultHistory.get(i);
		}
		

        MultipleLinearRegression regression = new MultipleLinearRegression(experiments, resultsOracle);
        ind = 0;
		for(String s : this.parameters.keySet()) {
			this.coeffs.put(s, regression.beta(ind));
			ind++;
		}
	}*/

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
			if(this.dataPerceived.contains(s)) {
				ret.put(s, this.getAmas().getFlow(s));
			}
		}
		return ret;
	}

	@Override
	protected void onAct() {
		double res = this.myFunction.compute();
		System.out.println(this.name + " RESULTAT : "+res);
		double oracle = this.getAmas().getValueOracle(this.name);
		feedback = Math.abs(res - oracle);
		System.out.println(this.name + " : ORACLE : "+oracle);
		
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
		this.dataPerceived.add(data);
	}

	public void setFunction(LPDFunction fun) {
		this.myFunction = fun;
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

	public double compute() {
		return this.myFunction.compute();
	}

}
