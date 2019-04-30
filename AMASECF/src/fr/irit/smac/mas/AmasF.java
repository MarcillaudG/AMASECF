package fr.irit.smac.mas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import fr.irit.smac.amak.Agent;
import fr.irit.smac.amak.Amas;
import fr.irit.smac.amak.Configuration;
import fr.irit.smac.amak.Scheduling;
import fr.irit.smac.functions.OracleFunction;
import fr.irit.smac.lxplot.LxPlot;
import fr.irit.smac.lxplot.commons.ChartType;
import fr.irit.smac.lxplot.server.LxPlotChart;
import fr.irit.smac.mas.EnvironmentF.Expe;
import fr.irit.smac.visu.LinksFileReader;
import links2.driver.connection.LinksConnection;
import links2.driver.connection.LocalLinksConnection;
import links2.driver.marshaler.Link2DriverMarshaler;
import links2.driver.marshaler.MarshallingMode;
import links2.driver.model.Entity;
import links2.driver.model.Experiment;
import links2.driver.model.Relation;
import links2.driver.model.Snapshot;
import messages.MessageParameter;

public class AmasF extends Amas<EnvironmentF>{


	private static final int NB_PARAMETER_MAX = 17;

	private static final int NB_PARAMETER_MIN = 5;

	private Map<String,OracleFunction> oracle;

	//private List<String> parameters;

	private Map<String,Double> parameters;

	private Map<String,Double> network;

	private Map<String,Double> parameterTypeNeeded;

	private Map<String,AGFunction> allAGFunctions;

	private Set<String> parametersUseful;

	private Set<String> parametersUsefulLastCycle;

	private Random r = new Random();

	private Set<String> typesOfParametersNeeded;

	private File fileLinks;

	private FileWriter writer;

	private Snapshot snapshot;

	private Experiment experiment;

	private List<Relation> relationsToLinks;


	/**
	 * Constructor used by the test
	 * @param environment
	 * @param scheduling
	 * @param params
	 */
	public AmasF(EnvironmentF environment, Scheduling scheduling, Object[] params) {
		super(environment, scheduling, params);


		//Create a new experiment
		experiment = new Experiment("My experimentName");
		this.fileLinks = new File("linksexpe.txt");
		try {
			this.writer = new FileWriter(this.fileLinks);
		} catch (IOException e) {
			System.err.println("ERROR WRITER");
		}
		switch(environment.getExpe()) {
		case LPD:
			initLPD();
			break;
		case RANDOM:
			try {
				initRandom();
			} catch (IOException e) {
				System.err.println("ERROR INIT");
			}
			break;
		default:
			break;

		}
	}

	/**
	 * Constructor used by the UI
	 * @param environment
	 * @param params
	 */
	public AmasF(EnvironmentF environment, Object[] params) {
		super(environment, Scheduling.DEFAULT, params);
		this.fileLinks = new File("linksexpe.txt");
		try {
			this.writer = new FileWriter(this.fileLinks);
		} catch (IOException e) {
			System.err.println("ERROR WRITER");
		}
		switch(environment.getExpe()) {
		case LPD:
			initLPD();
			break;
		case RANDOM:
			try {
				initRandom();
			} catch (IOException e) {
				System.err.println("ERROR INIT");
			}
			break;
		default:
			break;

		}
	}

	@Override
	protected void onInitialConfiguration() {
		super.onInitialConfiguration();
		Configuration.executionPolicy = ExecutionPolicy.TWO_PHASES;
	}

	/**
	 * Initialisation when using the LPD function
	 */
	private void initLPD() {
		this.oracle = new TreeMap<String,OracleFunction>();
		//this.parameters = new ArrayList<String>();
		this.allAGFunctions = new TreeMap<String,AGFunction>();
		this.parameters = new TreeMap<String, Double>();

		// Initialization of parameter
		for(int i = 0; i < NB_PARAMETER_MAX; i++) {
			//this.parameters.add("param"+i);
			this.parameters.put("param"+i, 0.0);
		}
		for(int i = 0; i < EnvironmentF.NB_AGENTS_MAX; i++) {
			String name = "function "+i;
			//Creation of the oracle function and the corresponding agent
			this.allAGFunctions.put(name,new AGFunction(this, params, name));



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
		for(AGFunction ag : this.allAGFunctions.values()) {
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
	}

	/**
	 * Initialisation when using the sum function
	 * @throws IOException 
	 */
	private void initRandom() throws IOException {
		// init of collections
		this.oracle = new TreeMap<String,OracleFunction>();
		this.allAGFunctions = new TreeMap<String,AGFunction>();
		this.parameterTypeNeeded = new TreeMap<String,Double>();
		this.parametersUsefulLastCycle = new TreeSet<String>();


		List<String> variablestmp = new ArrayList<String>(this.environment.getVariables());

		System.out.println("SIZE : "+ variablestmp.size());
		this.writer.write("Entities : \n");

		// Creation of the firsts agents
		for(int i = 0; i < EnvironmentF.NB_AGENTS_MAX; i++) {
			String name = "function"+i;
			//Creation of the oracle function and the corresponding agent
			AGFunction agf = new AGFunction(this, params, name);

			this.writer.write("AGFunction," + name + "\n");

			OracleFunction of = new OracleFunction(name);

			// Initialization of parameters of function

			//Parameters fixes
			this.writer.write("Fixes : {\n");
			for(int j = 0; j < EnvironmentF.NB_VARIABLES_FIXES; j++) {
				String variable = variablestmp.remove(r.nextInt(variablestmp.size()));
				of.addParametersFixe(variable);
				agf.addParameterFixe(variable);

				this.writer.write(this.environment.getTypeFromVariable(variable) + ","+ variable + ","+ this.environment.getValueOfVariable(variable) + "\n");

			}
			this.writer.write("}\n");

			// Write the variable for Links
			this.writer.write("Variables : {\n");
			//Parameters variables
			List<String> variablesRemaining = new ArrayList<String>(this.environment.getVariables());
			variablesRemaining.removeAll(of.getParametersFixes());
			for(int j = 0; j < EnvironmentF.NB_VARIABLES_VARIABLES; j++) {
				String variable = variablesRemaining.remove(r.nextInt(variablesRemaining.size()));
				of.addParametersVariable(variable);
				agf.addParameterVariable(variable);
				this.writer.write(this.environment.getTypeFromVariable(variable) + ","+ variable + ","+ this.environment.getValueOfVariable(variable) + "\n");
			}
			this.writer.write("}\n");

			this.oracle.put(name,of);
			this.allAGFunctions.put(name,agf);
		}

		// Creation of the seconds agents
		//initSecond();

		this.writer.write("EXPE\n");
	}

	/**
	 * Second phase of initialisation of agents, can be optional
	 * @throws IOException
	 */
	private void initSecond() throws IOException {
		List<OracleFunction> oraclestmp = new ArrayList<OracleFunction>(this.oracle.values());
		for(OracleFunction old : oraclestmp) {

			// Variables of the old function
			List<String> variableOfOld = new ArrayList<String>(old.getParametersFixes());

			// Variables of the environment without the ones from the old functions
			List<String> environmentVariable = new ArrayList<String>(this.environment.getVariables());
			environmentVariable.removeAll(variableOfOld);

			//Construction of the functions
			String name = old.getname()+"Other";
			AGFunction agf = new AGFunction(this, params, name);
			OracleFunction of = new OracleFunction(name);

			this.writer.write("AGFunction," + name + "\n");


			// Initialization of parameters of function

			this.writer.write("Fixes : {\n");

			// Parameters fixes
			for(int j = 0; j < EnvironmentF.NB_VARIABLES_FIXES; j=j+2) {

				// Variable of the matching function
				String variable = variableOfOld.remove(r.nextInt(variableOfOld.size()));
				environmentVariable.remove(variable);
				of.addParametersFixe(variable);
				agf.addParameterFixe(variable);
				this.writer.write(this.environment.getTypeFromVariable(variable) + ","+ variable + ","+ this.environment.getValueOfVariable(variable) + "\n");

				// Other variables
				String variable2 = environmentVariable.remove(r.nextInt(environmentVariable.size()));
				of.addParametersFixe(variable2);
				agf.addParameterFixe(variable2);

				this.writer.write(this.environment.getTypeFromVariable(variable2) + ","+ variable2 + ","+ this.environment.getValueOfVariable(variable2) + "\n");

			}

			this.writer.write("}\n");

			// Write the variable for Links
			this.writer.write("Variables : {\n");
			//Parameters variables
			List<String> variablesRemaining = new ArrayList<String>(this.environment.getVariables());
			variablesRemaining.removeAll(of.getParametersFixes());
			for(int j = 0; j < EnvironmentF.NB_VARIABLES_VARIABLES; j++) {
				String variable = variablesRemaining.remove(r.nextInt(variablesRemaining.size()));
				of.addParametersVariable(variable);
				agf.addParameterVariable(variable);
				this.writer.write(this.environment.getTypeFromVariable(variable) + ","+ variable + ","+ this.environment.getValueOfVariable(variable) + "\n");
			}

			this.writer.write("}\n");

			//Add to the collections
			this.oracle.put(name,of);
			this.allAGFunctions.put(name,agf);

		}
	}

	@Override
	protected void onSystemCycleBegin() {
		// Values of the parameters for this cycle
		this.environment.update();
		this.typesOfParametersNeeded = new TreeSet<String>();
		this.network = new TreeMap<String,Double>();
		this.parametersUseful = new TreeSet<String>();


		// Give the parameters to the oracle
		switch(this.environment.getExpe()) {
		case LPD:
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
			break;
		case RANDOM:
			this.relationsToLinks = new ArrayList<Relation>();
			//Create a new snapshot
			snapshot = new Snapshot();
			snapshot.setSnapshotNumber((long) this.getCycle());
			for(AGFunction agf : this.allAGFunctions.values()) {
				Entity ent = new Entity(agf.getName(),"AGFunction");
				Map<String,Object> map = new TreeMap<String,Object>();
				map.put("Name", agf.getName());
				ent.setAttributeMap(map);
				this.snapshot.addEntity(ent);
			}
			try {
				this.writer.write("Cycle : "+this.getCycle()+"\n");
			} catch (IOException e) {
				System.err.println("ERROR CYCLE\n");
			}
			for(OracleFunction of : this.oracle.values()) {
				for(String s : of.getAllParameters()) {
					of.setValueOfVariable(s, this.environment.getValueOfVariable(s));
				}
			}
			break;
		default:
			break;

		}
	}


	@Override
	protected void onSystemCycleEnd() {
		if(this.getCycle() > 1) {
			switch(this.environment.getExpe()) {
			case LPD:
				for(AGFunction agf : this.allAGFunctions.values()) {
					OracleFunction of = this.oracle.get(agf.getName());
					LxPlot.getChart(agf.getName()).add("Agent",this.getCycle(), of.computeLPD());
					LxPlot.getChart(agf.getName()).add("Oracle",this.getCycle(),agf.computeLPD());
				}
				break;
			case RANDOM:
				for(AGFunction agf : this.allAGFunctions.values()) {
					OracleFunction of = this.oracle.get(agf.getName());
					LxPlot.getChart(agf.getName(), ChartType.LINE).add("Oracle",this.getCycle(), of.computeSum());
					LxPlot.getChart(agf.getName(), ChartType.LINE).add("Agent",this.getCycle(), agf.computeSum());
					LxPlot.getChart(agf.getName()+" Differences", ChartType.LINE).add("Agent",this.getCycle(), of.computeSum()-agf.computeSum());
				}
				this.parametersUsefulLastCycle = new TreeSet<String>();
				this.parametersUsefulLastCycle.addAll(this.parametersUseful);

				for(Relation r : this.relationsToLinks) {
					this.snapshot.addRelation(r);
					try {
						this.writer.write("ADD REL,"+ r.getNodeAid()+","+ r.getNodeBid()+",Receive,"+ r.getNodeAid() +",Reception \n");
					} catch (IOException e) {
						System.err.println("ERROR ADD REL");
					}
				}
				// Add the snapshot into the experiment
				experiment.addSnapshot(this.snapshot);
				try {
					this.writer.write("END Cycle\n");
				} catch (IOException e) {
					System.err.println("ERROR END CYCLE");
				}

				//System.out.println(this.parametersUsefulLastCycle);
				/*System.out.println("CYCLE : "+this.cycle);
				for(String s : this.allAGFunctions.get("function 0").getParameterAndValue().keySet()) {
					if(this.allAGFunctions.get("function 0").getParameterAndValue().get(s) == 0.0) {
						System.out.println("MISSING VARIABLE : "+s);
					}
				}*/
				break;
			default:
				break;

			}
		}
		if(this.getCycle() == 100) {

			//Helper to get connection with default parameters
			LinksConnection connection = LocalLinksConnection.getLocalConnexion();

			//Save the experiment 
			Link2DriverMarshaler.marshalling(connection, experiment, MarshallingMode.OVERRIDE_EXP_IF_EXISTING);

			//Don't forget to close the DB connection
			connection.close();

			try {
				this.writer.close();
				this.getScheduler().stop();
				new LinksFileReader("linksexpe.txt");
			} catch (IOException e) {
				System.err.println("ERROR Close");
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





	public List<AGFunction> askNeighbourgs(AGFunction func) {
		List<AGFunction> ret = new ArrayList<AGFunction>(this.allAGFunctions.values());
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
		switch(this.getEnvironment().getExpe()) {
		case LPD:
			return this.oracle.get(name).computeLPD();
		case RANDOM:
			return this.oracle.get(name).computeSum();
		default:
			return 0.0;

		}
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

	public Double getValueOfParameters(String s, AGFunction agf) {
		double value = this.environment.getValueOfVariable(s);
		// TODO add attribute
		try {
			this.writer.write("ADD Fixe,"+agf.getName()+","+s+","+value+"\n");
		} catch (IOException e) {
			System.err.println("ERROR ADD ATTRIBUTE");
		}
		return value;

	}

	public void CommunicateNeedOfVariableType(String s) {
		this.typesOfParametersNeeded.add(s);
	}

	public Set<String> getTypesOfVariableNeeded(){
		Set<String> res = new TreeSet<String>();
		for(String s : this.typesOfParametersNeeded) {
			res.add(this.getTypeOfVariable(s));
		}
		return res;
	}

	/**
	 * Return the type of the variable
	 * @param variable
	 * @return String the type
	 */
	public String getTypeOfVariable(String variable) {
		return this.environment.getTypeFromVariable(variable);
	}

	public void communicateValueOfVariable(String s, Double double1, AGFunction agf) {
		this.network.put(s, double1);
		Entity var = new Entity(s,"Variable");
		this.snapshot.addEntity(var);
		this.snapshot.addRelation(new Relation(agf.getName() + " send the variable "+s, agf.getName(), s, true, "Send variable"));
		try {
			this.writer.write("ADD VAR,"+s+"\n");
			this.writer.write(agf.getName()+","+ s+  ",isCommunicated,"+ "Communication\n");
		} catch (IOException e) {
			System.err.println("ERROR ADD VAR");
		}
	}

	/**
	 * return the value of the parameter communicated if it exist, 0 else
	 * Give the information that the parameter is useful to communicate
	 * @param s the name of the variable
	 * @return the value of the parameter
	 */
	public Double getValueFromNetwork(String s, AGFunction agf) {
		Double res = 0.0;
		if(this.network.containsKey(s)) {
			res = network.get(s);
			this.parametersUseful.add(s);
			this.snapshot.addRelation(new Relation(agf.getName() + " receive the variable "+s, s, agf.getName(), true, "Receive variable"));
			try {
				this.writer.write("ADD REL,"+ s+","+ agf.getName()+",Receive,"+s +",Reception \n");
			} catch (IOException e) {
				System.err.println("ERROR ADD REL");
			}
		}
		return res;
	}

	/**
	 * Return the parameter useful
	 * @param parametersFixes
	 * @return
	 */
	public Set<String> isParametersUseful(List<String> parametersFixes) {
		Set<String> res = new TreeSet<String>();
		for(String s : parametersFixes) {
			if(this.parametersUsefulLastCycle.contains(s)) {
				res.add(s);
			}
		}
		return res;
	}

	/**
	 * Return all AGFunction near in the neighborhood of the agent in input
	 * @param agFunction
	 * @return
	 */
	public List<AGFunction> getNeighborhood(AGFunction agFunction) {
		List<AGFunction> neighbours = new ArrayList<AGFunction>(this.allAGFunctions.values());
		neighbours.remove(agFunction);
		return neighbours;
	}

	/**
	 * Notify links that an agent communicate all its messages
	 * @param parametersToCommunicate
	 * @param name
	 */
	public void CommunicateMessageLinks(Set<MessageParameter> parametersToCommunicate,String name, AGFunction agf) {
		for(MessageParameter mess : parametersToCommunicate) {
			String s = mess.getName();
			Entity var = new Entity(s,"Variable");
			this.snapshot.addEntity(var);
			this.snapshot.addRelation(new Relation(agf.getName() + " send the variable "+s, agf.getName(), s, true, "Send variable"));
			try {
				this.writer.write("ADD VAR,"+s+"\n");
				this.writer.write(name+","+ s+  ",isCommunicated,"+ "Communication\n");
			} catch (IOException e) {
				System.err.println("ERROR ADD VAR");
			}
		}
	}

	/**
	 * Method use to notify links the reception of a variable
	 * @param param
	 * 		the name of the parameter
	 * @param agName
	 * The name of the function
	 */
	public void notifyLinksVariableUseful(String param, String agName) {
		Relation r = new Relation(agName + " receive the variable "+param, param, agName, true, "Receive variable");
		this.relationsToLinks.add(r);
		/*this.snapshot.addRelation(new Relation(agName + " receive the variable "+param, param, agName, true, "Receive variable"));
		try {
			this.writer.write("ADD REL,"+ param+","+ agName+",Receive,"+param +",Reception \n");
		} catch (IOException e) {
			System.err.println("ERROR ADD REL");
		}*/
	}

}
