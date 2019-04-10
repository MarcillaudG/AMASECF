package fr.irit.smac.mas;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.tuple.Pair;

import fr.irit.smac.amak.Environment;
import fr.irit.smac.amak.Scheduling;

public class EnvironmentF extends Environment{

	public enum Expe {LPD, RANDOM};
	private static final int NB_VARIABLE_MAX = 1000;

	public static final int NB_AGENTS_MAX = 10;

	
	// Static variables for LPD
	private static final double MIN_SPEED = 30.0/3.6;

	private static final double MAX_SPEED = 130.0/3.6;

	private static final double MIN_LENGTH = 500.0;

	private static final double MAX_LENGTH = 8000.0;

	private static final double MIN_CAPACITY = 100.0;

	private static final double MAX_CAPACITY = 200.0;

	private static final double MIN_FLOW = 1.0;

	private static final double MAX_FLOW = 30.0;
	
	// Collections for LPD
	private Map<String,Double> speeds;

	private Map<String,Double> lengths;

	private Map<String,Double> capacities;

	private Map<String,Double> flows;
	
	// Statics variables for Random

	protected static final int NB_TYPES_VARIABLES = 50;

	protected static final int NB_VARIABLES_FIXES = 100;

	protected static final int NB_VARIABLES_VARIABLES = 20;

	/**
	 * Keys variables, Value value of variable
	 */
	private Map<String,Double> variables;

	/**
	 * Keys type variable, value list of all variable of the type
	 */
	private Map<String,Set<String>> all_variables;
	
	private Map<String,Pair<Double,Double>> variables_limits;

	private Random r;

	private Expe expe;

	public EnvironmentF(Scheduling _scheduling, Object[] params, Expe expe) {
		super(_scheduling, params);
		this.expe = expe;
		init();
	}



	private void init() {

		this.variables = new TreeMap<String,Double>();
		this.all_variables = new TreeMap<String,Set<String>>();
		this.variables_limits = new TreeMap<String,Pair<Double,Double>>();

		this.r = new Random();
		switch(this.expe) {
		case LPD:
			this.speeds = new TreeMap<String,Double>();
			this.lengths = new TreeMap<String,Double>();
			this.capacities = new TreeMap<String,Double>();
			this.flows = new TreeMap<String,Double>();

			for(int i = 0 ; i < NB_AGENTS_MAX;i++) {
				this.lengths.put("L"+i, 0.0);
				this.variables.put("L"+i,0.0);

				this.speeds.put("Speed"+i, 0.0);
				this.variables.put("Speed"+i,0.0);

				this.capacities.put("C"+i, 0.0);
				this.variables.put("C"+i,0.0);
			}

			for(int i = 0; i < NB_VARIABLE_MAX;i++) {
				this.flows.put("F"+i, 0.0);
				this.variables.put("F"+i,0.0);
			}
			break;
		case RANDOM:
			for(int i = 0 ; i < NB_TYPES_VARIABLES;i++) {
				String type = "Type"+i;
				this.all_variables.put(type,new TreeSet<String>());
				for(int j = 0; j < NB_VARIABLE_MAX/NB_TYPES_VARIABLES; j++) {
					this.variables.put(type+"v"+j,0.0);
					this.all_variables.get(type).add(type+"v"+j);
				}
				double min = r.nextInt(200);
				double max = r.nextInt(200) + min+ 20;
				this.variables_limits.put(type, Pair.of(min, max));
			}
			break;
		default:
			break;

		}
	}

	/**
	 * Update the value of all variable at each cycle
	 */
	public void update() {
		// Update Lengths
		switch(this.expe) {
		case LPD:
			for(String s : this.lengths.keySet()) {
				double value = r.nextDouble()*(MAX_LENGTH-MIN_LENGTH)+MIN_LENGTH;
				this.lengths.put(s, value);
			}

			// Update Speeds
			for(String s : this.speeds.keySet()) {
				double value = r.nextDouble()*(MAX_SPEED-MIN_SPEED)+MIN_SPEED;
				this.speeds.put(s, value);
			}

			// Update Capacities
			for(String s : this.capacities.keySet()) {
				double value = r.nextDouble()*(MAX_CAPACITY-MIN_CAPACITY)+MIN_CAPACITY;
				this.capacities.put(s, value);
			}

			// Update Flows
			for(String s : this.flows.keySet()) {
				double value = r.nextDouble()*(MAX_FLOW-MIN_FLOW)+MIN_FLOW;
				this.flows.put(s, value);
			}
			break;
		case RANDOM:
			for(String type : this.all_variables.keySet()) {
				for(String variable : this.all_variables.get(type)) {
					double min = this.variables_limits.get(type).getLeft();
					double max = this.variables_limits.get(type).getRight();
					double value = r.nextDouble() *(max-min)+min;
					this.variables.put(variable, value);
				}
			}
			break;
		default:
			break;
		
		}
		
	}



	public Set<String> getVariables() {
		return variables.keySet();
	}
	

	public Set<String> getAllVariables() {
		return this.all_variables.keySet();
	}



	public Map<String, Double> getSpeeds() {
		return speeds;
	}



	public Map<String, Double> getLengths() {
		return lengths;
	}



	public Map<String, Double> getCapacities() {
		return capacities;
	}



	public Map<String, Double> getFlows() {
		return flows;
	}

	public Double getValueOfVariable(String s) {
		return this.variables.get(s);
	}
	
	public Expe getExpe() {
		return this.expe;
	}
	
	public String getTypeFromVariable(String variable) {
		String res = "";
		boolean found = false;
		List<String> tmp = new ArrayList<String>(this.all_variables.keySet());
		int i = 0;
		while(!found) {
			if(this.all_variables.get(tmp.get(i)).contains(variable)) {
				found = true;
				res = tmp.get(i);
			}
			i++;
			// TODO change in throw exception
			if(i > NB_TYPES_VARIABLES+2)
				System.out.println("ERROR : BOUCLE INFINI");
		}
		
		return res;
	}



}
