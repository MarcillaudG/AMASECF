package fr.irit.smac.learningdata.Agents;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.tuple.Pair;

import fr.irit.smac.amak.Agent;
import fr.irit.smac.learningdata.AmasLearning;
import fr.irit.smac.learningdata.EnvironmentLearning;
import fr.irit.smac.learningdata.Agents.InputAgent.Operator;
import fr.irit.smac.learningdata.requests.Offer;
import fr.irit.smac.learningdata.requests.Request;
import fr.irit.smac.learningdata.requests.RequestForRow;
import fr.irit.smac.learningdata.requests.RequestForWeight;
import fr.irit.smac.learningdata.requests.RequestRow;
import fr.irit.smac.learningdata.ui.Matrix;
import fr.irit.smac.lxplot.LxPlot;
import fr.irit.smac.modelui.learning.DataLearningModel;
import fr.irit.smac.modelui.learning.InputLearningModel;
import fr.irit.smac.shield.c2av.SyntheticFunction;
import links2.driver.connection.LinksConnection;
import links2.driver.connection.LocalLinksConnection;
import links2.driver.marshaler.Link2DriverMarshaler;
import links2.driver.marshaler.MarshallingMode;
import links2.driver.model.Entity;
import links2.driver.model.Experiment;
import links2.driver.model.Relation;
import links2.driver.model.Snapshot;

public class LearningFunction extends Agent<AmasLearning, EnvironmentLearning>{

	private SyntheticFunction function;
	private Deque<Double> valueOfOperand;


	private Set<String> variableInEnvironment;

	private List<Double> historyFeedback;

	private Map<String,InputAgent> allInputAgent;

	private Map<String,DataAgent> allDataAgent;

	private Map<InputAgent, RowAgent> allRowAgent;

	private Map<DataAgent, ColumnAgent> allColumnAgent;

	private Map<String,AgentLearning> allAgents;

	private Map<Pair<String,String>, WeightAgent> allWeightAgent;

	private Map<String,Double> inputsValues;

	private Map<Request,List<Offer>> auctions;

	private Map<Integer, List<Configuration>> configurations;

	private Configuration lastBestConfig;

	private Snapshot snapshot;

	private Experiment experiment;

	private FileWriter file;

	private Matrix matrix;

	private String name;
	private double feedback;

	private int nbDataAgent = 0;

	private boolean modificationHappened;

	private int idConfig;

	private Configuration currentConfig;
	private Map<String, Operator> inputsDecisions;


	public LearningFunction(AmasLearning amas, Object[] params, String name, SyntheticFunction function) {
		super(amas, params);
		this.name = name;
		this.function = function;
		init();
	}

	private void init() {
		this.allDataAgent = new TreeMap<String,DataAgent>();
		this.allInputAgent = new TreeMap<String,InputAgent>();
		this.allColumnAgent = new HashMap<DataAgent, ColumnAgent>();
		this.allRowAgent = new HashMap<InputAgent, RowAgent>();

		this.historyFeedback = new ArrayList<Double>();
		this.allAgents = new TreeMap<String,AgentLearning>();
		this.auctions = new HashMap<Request,List<Offer>>();
		this.configurations = new TreeMap<Integer,List<Configuration>>();
		this.inputsDecisions = new TreeMap<String,Operator>();
		this.inputsValues = new TreeMap<String,Double>();
		this.allWeightAgent = new TreeMap<Pair<String,String>,WeightAgent>();
		this.feedback = 0.0;
		try {
			this.file = new FileWriter(new File("C:\\\\Users\\\\gmarcill\\\\Desktop\\\\matrix.csv"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		experiment = new Experiment("Variable Selection ID : TEST");

		for(Integer i : this.function.getInputIDRemoved()) {
			this.createInputAgent("Input:"+i, i);
		}
		//this.amas.setValueOfVariableNonDegraded(this);
		for(InputAgent input : this.allInputAgent.values()) {
			String nameOfCorrect = this.amas.getNameOfCorrectDataForInput(input.getId(), this.name);
			try {
				this.file.write(input.getName()+";");
				this.file.write(nameOfCorrect+"\n");
				//this.function.setValueOfOperand(input.getId(), this.amas.getValueOfVariable(nameOfCorrect));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public String getName() {
		return this.name;
	}

	@Override
	protected void onAgentCycleBegin() {

		System.out.println("Cycle : "+this.getAmas().getCycle());
		System.out.println("NB DATA : "+this.nbDataAgent);
		this.amas.generateNewValues();
		try {
			this.file.write("Cycle : " + this.getAmas().getCycle()+ "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.valueOfOperand = new ArrayDeque<Double>();
		this.variableInEnvironment = new TreeSet<String>();

		snapshot = new Snapshot();
		snapshot.setSnapshotNumber(this.getCycle());

		for(String input : this.allInputAgent.keySet()) {
			Entity ent = new Entity(input, "Input");
			ent.setAttribute(Entity.ATTRIBUTE_NAME, input);
			snapshot.addEntity(ent);
		}
		// Update the influence and the trust

		this.idConfig = 0;

	}

	@Override
	protected void onPerceive() {
		this.historyFeedback.add(this.feedback);
		Iterator<String> iter = this.function.getOperands().iterator();
		while(iter.hasNext()) {
			this.valueOfOperand.offer(this.getAmas().getValueOfVariable(iter.next()));
		}

		// Getting the variables in the environment
		this.variableInEnvironment.addAll(this.getAmas().getVariableInEnvironment());
		this.variableInEnvironment.removeAll(this.function.getOperandNotRemoved());

		// Creation of the data agent
		List<String> dataAgentToCreate = new ArrayList<String>(this.variableInEnvironment);
		dataAgentToCreate.removeAll(this.allDataAgent.keySet());

		for(String s : dataAgentToCreate) {
			this.createDataAgent(s);
		}


		// Give the feedback to the input agent
		for(InputAgent inputAgent : this.allInputAgent.values()) {
			inputAgent.updateInfluence(this.feedback);
			inputAgent.clearApplying();
		}



		// Give the feedback of the function to all dataAgent
		for(DataAgent dataAgent : this.allDataAgent.values()) {
			dataAgent.setInputAvailable(this.allInputAgent.keySet());
			//dataAgent.updateTrust(this.feedback);
			dataAgent.updateTrust(this.lastBestConfig);
			dataAgent.clearInput();
			dataAgent.fireTrustValues();
			String data = dataAgent.getName();
			Entity ent = new Entity(data, "Data");
			ent.setAttribute(Entity.ATTRIBUTE_NAME, data);
			for(String trust : dataAgent.getTrustValues().keySet()) {
				ent.setAttribute(trust, dataAgent.getTrustValues().get(trust));
			}
			this.snapshot.addEntity(ent);
		}

		this.modificationHappened = false;

	}

	/**
	 * Active the different agent in the system
	 */
	@Override
	protected void onDecide() {

		this.startInputAgent();

		for(InputAgent input : this.allInputAgent.values()) {
			this.inputsDecisions.put(input.getName(), input.getDecision());
		}
		if(this.feedback !=0) {
			this.startDataAgent();

			this.startRowAgent();

			this.startColumnAgent();
			try {
				this.startWeightsAgent();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		this.currentConfig = new Configuration(this.amas.getCycle(), 0);




		for(String input : this.allInputAgent.keySet()) {
			this.currentConfig.addInput(input);
			for(String data : this.allDataAgent.keySet()) {
				this.currentConfig.addDataValueToInput(input, data, this.allDataAgent.get(data).getWeightOfInput(input));
			}
		}


	}


	private void startWeightsAgent() throws Exception {
		/*for(DataAgent data : this.allDataAgent.values()) {
			data.startWeights();
		}*/

		List<WeightAgent> tmp = new ArrayList<WeightAgent>(this.allWeightAgent.values());
		Collections.shuffle(tmp);
		for(WeightAgent weight : tmp) {
			weight.onPerceive();
		}
		int  step = 0;
		while(!this.modificationHappened) {
			Collections.shuffle(tmp);
			for(WeightAgent weight : tmp) {
				weight.onDecideAndAct(step);
			}
			step++;
			if(step >3) {
				throw new Exception("No one want to change");
			}
		}

	}

	/**
	 * Start the cycle for all row agent
	 */
	private void startRowAgent() {
		// Random order
		List<RowAgent> rowAgentRemaining = new ArrayList<RowAgent>(this.allRowAgent.values());
		Collections.shuffle(rowAgentRemaining);

		for(RowAgent rowAgent : rowAgentRemaining) {
			rowAgent.perceive();
		}
		Collections.shuffle(rowAgentRemaining);
		for(RowAgent rowAgent : rowAgentRemaining) {
			rowAgent.decideAndAct();
		}
	}

	/**
	 * Start the cycle for all ColumnAgent
	 */
	private void startColumnAgent() {
		// Random order
		List<ColumnAgent> columnAgentRemaining = new ArrayList<ColumnAgent>(this.allColumnAgent.values());
		Collections.shuffle(columnAgentRemaining);

		for(ColumnAgent columnAgent : columnAgentRemaining) {
			columnAgent.perceive();
		}
		Collections.shuffle(columnAgentRemaining);
		for(ColumnAgent columnAgent : columnAgentRemaining) {
			columnAgent.decideAndAct();
		}
	}

	/**
	 * Start the cycle for all the InputAgent
	 */
	private void startInputAgent() {


		// All inputAgent perceives in random order
		List<InputAgent> inputAgentRemaining = new ArrayList<InputAgent>(this.allInputAgent.values());
		Collections.shuffle(inputAgentRemaining);
		for(InputAgent inputAgent : inputAgentRemaining) {
			inputAgent.perceive();
		}

		// All inputAgent decide and act in random order
		Collections.shuffle(inputAgentRemaining);
		for(InputAgent inputAgent : inputAgentRemaining) {
			inputAgent.decideAndAct();
		}



	}

	/**
	 * Start the cycle for all the dataAgent
	 */
	private void startDataAgent() {

		List<DataAgent> dataAgentRemaining = new ArrayList<DataAgent>(this.allDataAgent.values());
		Collections.shuffle(dataAgentRemaining);

		// All dataAgent perceives
		for(DataAgent dataAgent : dataAgentRemaining) {
			dataAgent.perceive();
		}

		Collections.shuffle(dataAgentRemaining);
		// All DataAgent decide and act
		for(DataAgent dataAgent : dataAgentRemaining) {
			dataAgent.decideAndAct();
			/*for(String will : dataAgent.getInputChosen()) {
				acquisition.get(will).add(dataAgent.getName());
			}*/

		}

	}

	@Override
	protected void onAct() {
		
		
		
		this.amas.setValueOfVariableNonDegraded(this);
		List<Integer> inputs = new ArrayList<Integer>();
		Double res =0.0;
		for(InputAgent input : this.allInputAgent.values()) {
			inputs.add(input.getId());
		}
		try {
			this.feedback = this.calculResConfig(this.currentConfig);
			res = this.feedback;
			if(this.feedback > this.amas.getResultOracle(this.name)) {
				this.feedback = 1.0;
			}
			else {
				if(this.feedback < this.amas.getResultOracle(this.name)) {
					this.feedback = -1.0;
				}
				else {
					this.feedback = 0.0;
				}
			}
			this.file.write("\n"+this.feedback+";"+res+";"+this.amas.getResultOracle(this.name)+"\n");
			//this.feedback = this.feedback - this.amas.getResultOracle(this.name);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		updateMatrix();
		
		//TEST
		/*for(InputAgent input : this.allInputAgent.values()) {
			String nameOfCorrect = this.amas.getNameOfCorrectDataForInput(input.getId(), this.name);
			this.function.setValueOfOperand(input.getId(), this.amas.getValueOfVariable(nameOfCorrect));
		}
		System.out.println("RESULT :   "+this.function.computeCustom()+"          "+this.amas.getResultOracle(this.name));*/
		System.out.println("Feedback : "+ this.feedback);
		LxPlot.getChart("Feedback").add("Difference",this.getCycle(), Math.abs(res-this.amas.getResultOracle(this.name)));
		/*for(InputAgent input : this.allInputAgent.values()) {
			for(DataAgent data : this.allDataAgent.values()) {
				double critInput = this.allRowAgent.get(input).getCriticalityAfterUpdate(data.getName(), Operator.NONE);
				double critData = this.allColumnAgent.get(data).getCriticalityAfterUpdate(input.getName(),Operator.NONE);
				LxPlot.getChart(input.getName() + " and "+data.getName()).add("CritInput", this.getCycle(), critInput);
				LxPlot.getChart(input.getName() + " and "+data.getName()).add("CritData", this.getCycle(), critData);
			}
		}*/


		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.experiment.addSnapshot(this.snapshot);

		try {
			this.writeMatrixInFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if(this.getCycle() == 10000) {
			try {
				this.file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.amas.getScheduler().stop();
			//Helper to get connection with default parameters
			LinksConnection connection = LocalLinksConnection.getLocalConnexion();

			//Save the experiment 
			Link2DriverMarshaler.marshalling(connection, experiment, MarshallingMode.OVERRIDE_EXP_IF_EXISTING);

			//Don't forget to close the DB connection
			connection.close();

		}
	}

	private void updateMatrix() {
		System.out.println("UPDATE");
		String[] head = new String[this.allDataAgent.keySet().size()+1];
		head[0] = "";
		int i = 1;
		for(String s : this.allDataAgent.keySet()) {
			head[i] = s;
			i++;
		}
		Object[][] donnees = new Object[this.allInputAgent.keySet().size()+1][this.allDataAgent.keySet().size()+1];
		i = 0;
		for(String inp : this.allInputAgent.keySet()) {
			donnees[i][0] = inp +":"+ this.amas.getNameOfInputFormula(this.allInputAgent.get(inp).getId(), this.name);
			int j = 1;
			for(String dat : this.allDataAgent.keySet()) {
				donnees[i][j] = this.currentConfig.getDataValueForInput(inp, dat);
				j++;
			}
			i++;
		}
		if(this.matrix == null) {
			this.matrix = new Matrix(head, donnees);
			this.matrix.setVisible(true);
		}
		else {
			this.matrix.updateTable(head, donnees);
		}
	}

	private void writeMatrixInFile() throws IOException {
		this.file.write("\t");
		List<String> datasTmp = new ArrayList<String>(this.allDataAgent.keySet());
		List<String> inputTmp = new ArrayList<String>(this.allInputAgent.keySet());
		for(String data : datasTmp) {
			this.file.write(";"+data+" : "+this.allDataAgent.get(data).getValue());
		}
		for(String input : inputTmp) {
			String sensy ="";
			switch(this.inputsDecisions.get(input)) {
			case MOINS:
				sensy = "Decrois";
				break;
			case NONE:
				sensy = "None";
				break;
			case PLUS:
				sensy = "Crois";
				break;
			default:
				sensy = "Default";
				break;

			}
			this.file.write("\n"+input + ":"+sensy);
			for(String data : datasTmp) {
				this.file.write(";"+this.currentConfig.getDataValueForInput(input, data));
			}
		}
		this.file.write("\n"+"\n");
	}

	private Double calculResConfig(Configuration config) throws IOException {
		Double res = 0.0;
		this.file.write("\n values;");
		for(String input : this.allInputAgent.keySet()) {
			Double valueForInput = 0.0;
			for(String data : this.allDataAgent.keySet()) {
				valueForInput += config.getDataValueForInput(input, data) * this.allDataAgent.get(data).getValue();
			}
			this.function.setValueOfOperand(this.allInputAgent.get(input).getId(),valueForInput);
			this.inputsValues.put(input, valueForInput);
			this.file.write(""+valueForInput+";");
		}
		res = this.function.computeCustom();
		return res;
	}

	/**
	 * Create a data agent
	 * 
	 * @param name
	 * 
	 * @return true if the agent does not already exist
	 */
	private boolean createDataAgent(String name) {
		if(this.allDataAgent.keySet().contains(name)) {
			return false;
		}
		DataAgent dag = new DataAgent(name,this, nbDataAgent);
		nbDataAgent++;
		for(InputAgent inputAgent : this.allInputAgent.values()) {
			dag.addNewInputAgent(inputAgent.getName());
		}
		this.allDataAgent.put(dag.getName(), dag);
		this.allAgents.put(name, dag);

		for(RowAgent rowAgent: this.allRowAgent.values()) {
			rowAgent.addDataAgent(dag);
		}

		this.createColumnAgent("Column"+name,dag,this.allInputAgent.values());
		return true;
	}
	/**
	 * Create an input Agent 
	 * @param name
	 * @return true if the agent does not already exist
	 */
	private boolean createInputAgent(String name, int id) {
		if(this.allInputAgent.containsKey(name)) {
			return false;
		}
		InputAgent inag = new InputAgent(name,this, id);
		this.allInputAgent.put(inag.getName(), inag);
		this.allAgents.put(inag.getName(), inag);

		this.createRowAgent(name, inag);
		return true;
	}

	private boolean createRowAgent(String name, InputAgent input) {
		if(this.allRowAgent.containsKey(input)) {
			return false;
		}
		RowAgent rowAgent = new RowAgent( name,input,this);
		this.allRowAgent.put(input, rowAgent);
		this.allAgents.put(name, rowAgent);
		return true;
	}

	private void createColumnAgent(String name, DataAgent dag, Collection<InputAgent> inputs) {
		ColumnAgent columnAgent = new ColumnAgent(name,dag,this);
		this.allColumnAgent.put(dag, columnAgent);
		this.allAgents.put(name, columnAgent);
	}


	/**
	 * Set the function
	 * 
	 * @param fun
	 */
	public void setFunction(SyntheticFunction fun) {
		this.function = fun;
	}

	public List<Double> getHistoryFeedback(){
		return this.historyFeedback;
	}

	public DataAgent getDataAgentWithName(String nameOfData) {
		return this.allDataAgent.get(nameOfData);
	}

	public Map<String, Double> getInfluences() {
		Map<String, Double> influences = new TreeMap<String,Double>();
		for(String nameOfInput : this.allInputAgent.keySet()) {
			influences.put(nameOfInput, this.allInputAgent.get(nameOfInput).getInfluence());
		}
		return influences;
	}

	public int getCycle() {
		return this.getAmas().getCycle();
	}

	public double getDataValue(String name2) {
		return this.getAmas().getValueOfVariable(name2);
	}

	public Set<String> getInputsName() {
		return this.allInputAgent.keySet();
	}



	/**
	 * Return the column agent with the name
	 * 
	 * @param name
	 * 
	 * @return the column agent
	 */
	public ColumnAgent getColumnAgentWithName(String name) {
		return this.allColumnAgent.get(name);
	}

	/**
	 * Return the row agent with the name
	 * 
	 * @param name
	 * 
	 * @return the row agent
	 */
	public RowAgent getRowAgentWithName(String name) {
		return (RowAgent) this.allAgents.get(name);
	}

	public void informDecision(DataAgent dataAgent,Set<String> inputsChosen) {
		for(String input : inputsChosen) {
			this.allRowAgent.get(this.allInputAgent.get(input)).dataAgentApplying(dataAgent);
		}

	}

	public void acceptRequest(String agentName, int idRequest) {
		this.allAgents.get(agentName).requestAccepted(idRequest);
	}

	public void rejectRequest(String agentName, int id) {
		this.allAgents.get(agentName).requestDenied(id);

	}


	/**
	 * Manage the auction to give one to an agent
	 */
	private void manageAuctions() {
		for(Request request : this.auctions.keySet()) {
			try {
				this.file.write("Request : "+request + "\n");
				double maxOffer = 0.0;
				DataAgent agent = null;
				Collections.shuffle(this.auctions.get(request));
				for(Offer offer : this.auctions.get(request)) {
					this.file.write(offer+ "----");
					if(offer.getOffer() > maxOffer) {
						maxOffer = offer.getOffer();
						agent = this.getDataAgentWithName(offer.getNameOfAgent());
					}
				}
				if(agent != null) {
					agent.applyWinRequest(request);
					this.getRowAgentWithName(request.getAgentName()).requestAccepted(request.getId());
					this.file.write("\nWINNER : "+agent.getName()+ "\n");
				}
				else {
					this.getRowAgentWithName(request.getAgentName()).requestDenied(request.getId());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.auctions.clear();
	}

	/**
	 * 
	 * @param request
	 * @param offer
	 */
	public void applyForRequest(Request request, Offer offer) {
		this.auctions.get(request).add(offer);
	}

	public void proposeRequest(Request request) {
		this.auctions.put(request, new ArrayList<Offer>());
		for(DataAgent dataAgent : this.allDataAgent.values()) {
			dataAgent.sendRequest(request);
		}

	}

	public List<String> getAllDataAgentApplyingForInput(String name2) {
		List<String> res = new ArrayList<String>();
		for(DataAgent dataAgent : this.allDataAgent.values()) {
			if(dataAgent.getInputChosen().contains(name2)) {
				res.add(dataAgent.getName());
			}
		}
		return res;
	}

	public void addListenerToData(String data, DataLearningModel model) {
		this.allDataAgent.get(data).addPropertyChangeListener(model);
	}

	public void addListenerToInput(String input, InputLearningModel model) {
		this.allInputAgent.get(input).addPropertyChangeListener(model);

	}

	public Set<String> getDatasNames() {
		return this.allDataAgent.keySet();
	}

	public String getCorrectData(int idInput) {
		return this.amas.getNameOfCorrectDataForInput(idInput, this.name);
	}

	public double getFeedback() {
		return this.feedback;
	}

	/**
	 * Return the previous configuration
	 * 
	 * @return Map<String,String> res
	 * 			First, the name of the input, then the name of the data
	 */
	public Map<String,String> getPreviousConfiguration() {
		Map<String,String> res = new TreeMap<String,String>();
		for(InputAgent input : this.allInputAgent.values()) {
			res.put(input.getName(), input.getCurrentData().getName());
		}
		return res;
	}

	public List<DataAgent> getAllDataAgent() {
		return new ArrayList<DataAgent>(this.allDataAgent.values());
	}

	public Operator getBestInfluenceFromInput(String name) {
		Operator res = null;
		Double value = -1.0;
		Map<Operator,Double> influences = this.allInputAgent.get(name).getInfluences();
		for(Operator ope : influences.keySet()) {
			if(value < influences.get(ope)) {
				value = influences.get(ope);
				res = ope;
			}
		}
		return res;
	}

	/**
	 * Create new configurations util the timer is ended
	 */
	private void createConfiguration() {
		Configuration config = new Configuration(this.getAmas().getCycle(),this.idConfig);
		for(String input : this.allInputAgent.keySet()) {
			config.addInput(input);
		}
		do {
			this.startRowAgent();

			this.startColumnAgent();

			List<DataAgent> dataTmp = new ArrayList<DataAgent>(this.allDataAgent.values());
			Collections.shuffle(dataTmp);
			for(DataAgent data : dataTmp) {
				data.manageConfig(config);
			}

			this.manageAuctionsConfig();
		}while(!config.isConfigurationValid());
		this.configurations.get(this.getCycle()).add(config);
	}

	private void manageAuctionsConfig() {
		for(Request request : this.auctions.keySet()) {
			Collections.shuffle(this.auctions.get(request));
			for(Offer offer : this.auctions.get(request)) {
				this.currentConfig.addDataValueToInput(((RequestRow)request).getInputName(), offer.getNameOfAgent(), offer.getOffer());
			}
		}
	}

	public Configuration getCurrentConfig() {
		return this.currentConfig;
	}

	public Operator getInputDecision(String input) {
		return this.allInputAgent.get(input).getDecision();
	}

	public Double computeResult(int id,double value, double initValue) {
		this.function.setValueOfOperand(id, value);
		double res = this.function.computeCustom();
		this.function.setValueOfOperand(id, initValue);
		return res;
	}

	public void askRow(String input, RequestForRow requestForRow) {
		this.allRowAgent.get(input).addRequest(requestForRow);

	}

	public Offer askData(String data,Operator decision, String input) {
		return this.allDataAgent.get(data).askData(decision,input);
	}

	/**
	 * Transfer a request to a weight agent by the data agent
	 * 
	 * @param input
	 * 		The input (row) which send the request
	 * @param data
	 * 		The data holding the weight agent	 * 
	 * @param requestToSend
	 * 		The request
	 */
	public void sendRequestForWeight(String input, String data, RequestForWeight requestToSend) {
		this.allDataAgent.get(data).sendRequestForWeight(input,requestToSend);

	}

	public Map<String, Operator> getInputsDecisions() {
		return this.inputsDecisions;
	}

	public Double getLastValueOfinput(String input) {
		if(!this.inputsValues.containsKey(input))
			return null;
		return this.inputsValues.get(input);
	}

	public void modification() {
		this.modificationHappened = true;
	}

	public void addNewWeightAgent(WeightAgent weight,String data,String input) {
		if(this.amas.getNameOfCorrectDataForInput(this.allInputAgent.get(input).getId(), this.name).equals(data)) {
			weight.setWeight(0.6);
		}
		else {
			Random rand = new Random();
			if(rand.nextBoolean())
				weight.setWeight(0.1);
			else
				weight.setWeight(0.6);
		}
		this.allWeightAgent.put(Pair.of(input, data), weight);

	}

	public double getInputCriticalityAfterUpdate(String data,String input, Operator decision) {
		return this.allRowAgent.get(this.allInputAgent.get(input)).getCriticalityAfterUpdate(data,decision);
	}

	public double getDataCriticalityAfterUpdate(String data, String input, Operator decision) {
		return this.allColumnAgent.get(this.allDataAgent.get(data)).getCriticalityAfterUpdate(input,decision);
	}

	public SyntheticFunction getFunction() {
		return this.function;
	}
}
