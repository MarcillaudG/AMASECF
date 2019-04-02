package fr.irit.smac.mas;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import fr.irit.smac.amak.Agent;
import fr.irit.smac.amak.Amas;
import fr.irit.smac.amak.Scheduling;
import fr.irit.smac.functions.OracleFunction;
import fr.irit.smac.lxplot.LxPlot;
import fr.irit.smac.lxplot.commons.ChartType;
import fr.irit.smac.lxplot.server.LxPlotChart;
import fr.irit.smac.mas.EnvironmentF.Expe;

public class AmasF extends Amas<EnvironmentF>{


	private static final int NB_PARAMETER_MAX = 17;

	private static final int NB_PARAMETER_MIN = 5;

	private Map<String,OracleFunction> oracle;

	//private List<String> parameters;

	private Map<String,Double> parameters;

	private List<AGFunction> allAGFunctions;

	private Random r = new Random();

	public AmasF(EnvironmentF environment, Scheduling scheduling, Object[] params) {
		super(environment, scheduling, params);

		init();
	}


	private void init() {
		this.oracle = new TreeMap<String,OracleFunction>();
		//this.parameters = new ArrayList<String>();
		this.allAGFunctions = new ArrayList<AGFunction>();
		this.parameters = new TreeMap<String, Double>();

		// Initialization of parameter
		for(int i = 0; i < NB_PARAMETER_MAX; i++) {
			//this.parameters.add("param"+i);
			this.parameters.put("param"+i, 0.0);
		}
		for(int i = 0; i < EnvironmentF.NB_AGENTS_MAX; i++) {
			String name = "function "+i;
			//Creation of the oracle function and the corresponding agent
			this.allAGFunctions.add(new AGFunction(this, params, name));



			this.oracle.put(name,new OracleFunction(name));

			/*this.oracle.get(name).setLength(this.environment.getLengths().get("L"+i));
			this.oracle.get(name).setSpeed(this.environment.getSpeeds().get("Speed"+i));
			this.oracle.get(name).setC(this.environment.getCapacities().get("C"+i));*/

			this.oracle.get(name).addParameters("L"+i);
			this.oracle.get(name).addParameters("Speed"+i);
			this.oracle.get(name).addParameters("C"+i);


			int nbParameters = r.nextInt(NB_PARAMETER_MAX-NB_PARAMETER_MIN)+NB_PARAMETER_MIN;

			// Initialization of parameters of oracle function




			List<String> parameterTmp = new ArrayList<String>(this.environment.getFlows().keySet());
			for(int j = 0; j < nbParameters; j ++) {
				String tmp = parameterTmp.remove(r.nextInt(parameterTmp.size()));
				this.oracle.get(name).addFlow(this.environment.getFlows().get(tmp));
				this.oracle.get(name).getParameters().add(tmp);
			}
		}



		// Initialization of the AGFunction parameter
		for(AGFunction ag : this.allAGFunctions) {
			OracleFunction of = this.oracle.get(ag.getName());
			ag.setLength(of.getParametersI(0));
			ag.setSpeed(of.getParametersI(1));
			ag.setC(of.getParametersI(2));


			int nbParameters = 1+r.nextInt(of.getParameters().size()-NB_PARAMETER_MIN-2);
			for(int i = 3; i < nbParameters+3; i++) {
				ag.addParameter(of.getParametersI(i));
				ag.addDataPerceived(of.getParametersI(i));
			}
		}
		System.out.println("END INIT");
		/*for(int i = 0; i < this.allAGFunctions.size(); i++) {
			System.out.println(this.allAGFunctions.get(i));
			System.out.println(this.oracle.get(this.allAGFunctions.get(i).getName()));
		}*/
	}

	@Override
	protected void onSystemCycleBegin() {
		// Values of the parameters for this cycle
		this.environment.update();

		// Give the parameters to the oracle
		for(OracleFunction of : this.oracle.values()) {
			of.reinit();
			of.setLength(this.environment.getLengths().get(of.getParameters().get(0)));
			of.setSpeed(this.environment.getSpeeds().get(of.getParameters().get(1)));
			of.setC(this.environment.getCapacities().get(of.getParameters().get(2)));
			for(int i = 3; i < of.getParameters().size(); i++) {
				String s = of.getParameters().get(i);
				of.addFlow(this.environment.getFlows().get(s));
			}
		}
	}


	@Override
	protected void onSystemCycleEnd() {
		if(this.getCycle() > 1) {
			for(AGFunction agf : this.allAGFunctions) {
				OracleFunction of = this.oracle.get(agf.getName());
				//LxPlot.getChart(agf.getName()).add("Agent",this.getCycle(), of.compute());
				//LxPlot.getChart(agf.getName()).add("Oracle",this.getCycle(),agf.compute());
				LxPlot.getChart(agf.getName(), ChartType.LINE, 20).add("Oracle",this.getCycle(), of.compute());
				LxPlot.getChart(agf.getName(), ChartType.LINE, 20).add("Agent",this.getCycle(), agf.compute());
			}
		}
	}

	public Map<String, Double> getData(Set<String> inparameters) {
		Map<String, Double> ret = new TreeMap<String,Double>();
		for(String s : inparameters) {
			ret.put(s, this.parameters.get(s));
		}
		return ret;
	}


	public static void main(String args[]) {

		AmasF amas = new AmasF(new EnvironmentF(Scheduling.DEFAULT, args, Expe.RANDOM), Scheduling.DEFAULT, args);
		amas.start();
	}




	public List<AGFunction> askNeighbourgs(AGFunction func) {
		List<AGFunction> ret = new ArrayList<AGFunction>(this.allAGFunctions);
		int nbNeighbours = r.nextInt(this.allAGFunctions.size());
		int ind = r.nextInt(this.allAGFunctions.size());
		while(nbNeighbours >0) {
			ret.remove(ind);
			nbNeighbours--;
			ind = (ind+1)% ret.size();
		}
		return ret;
	}


	public double getValueOracle(String name) {
		return this.oracle.get(name).compute();
	}


	/*private void receiveParameter(String nameParameter, AGFunction ag) {
		OracleFunction of = this.oracle.get(ag.getName());
		// ag.addParameter(nameParameter, of.getCoeff(ag.getName()));

	}*/


	public double getLength(String length) {
		return this.environment.getLengths().get(length);
	}


	public double getSpeed(String speed) {
		return this.environment.getSpeeds().get(speed);
	}


	public double getCapacity(String capacity) {
		return this.environment.getCapacities().get(capacity);
	}


	public Double getFlow(String s) {
		return this.environment.getFlows().get(s);
	}


	public boolean isParameterUseful(String s, AGFunction agFunction) {
		OracleFunction of = this.oracle.get(agFunction.getName());
		return of.getParameters().contains(s);
	}
}
