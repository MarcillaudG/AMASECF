package fr.irit.smac.mas;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.tuple.Pair;

import Jama.Matrix;
import fr.irit.smac.amak.Agent;
import fr.irit.smac.amak.CommunicatingAgent;
import fr.irit.smac.amak.aid.AID;
import fr.irit.smac.amak.messaging.IAmakEnvelope;
import fr.irit.smac.functions.LPDFunction;
import fr.irit.smac.functions.SumFunction;
import messages.MessageCriticality;
import messages.MessageNotify;
import messages.MessageParameter;
import messages.SimpleEnvelope;

public class AGFunction extends CommunicatingAgent<AmasF,EnvironmentF>{


	private final static int NB_COMMUNICATION_MAX = 20;

	private Map<String,Double>  parameters;

	private String name;

	private List<AGFunction> neighbours;

	private String length;

	private String speed;

	private String capacity;

	private int criticality = 0;

	private List<String> flow;


	private List<String> parametersFixes;

	private Set<String> parametersVariables;

	private Set<String> parametersUseful;
	private Set<String> parametersNotUseful;
	private Set<String> parametersCommunicated;
	private Set<String> parametersUsefulButShared;

	private Set<MessageParameter> parametersToCommunicate;

	private Map<String,AID> agentsToThanks;
	private Map<String,AID> agentsToDiscuss;

	private LPDFunction myFunctionLPD;
	private SumFunction myFunctionSum;

	private double feedback;
	public AGFunction(AmasF amas, Object[] params, String name) {
		super(amas, params);

		this.name = name;
		parameters = new TreeMap<String,Double>();
		this.neighbours = new ArrayList<AGFunction>();
		this.parametersFixes = new ArrayList<String>();
		this.parametersVariables = new TreeSet<String>();
		this.parametersUseful = new TreeSet<String>();
		this.parametersNotUseful = new TreeSet<String>();
		this.parametersCommunicated = new TreeSet<String>();
		this.parametersUsefulButShared = new TreeSet<String>();



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

			// Initialization of the function
			this.myFunctionSum = new SumFunction();

			// Initialization of the collection for communication
			this.parametersToCommunicate = new HashSet<MessageParameter>();
			this.agentsToThanks = new TreeMap<String,AID>();
			this.agentsToDiscuss = new TreeMap<String,AID>();


			// Perception of the fixed parameters
			for(String s : this.parametersFixes) {
				this.parameters.put(s, this.getAmas().getValueOfParameters(s,this));
			}
			int i = 0;
			// Reading of all messages
			for(IAmakEnvelope env : this.getAllMessages()) {

				// Case of a parameters is sent
				if(env.getMessage() instanceof MessageParameter) {
					MessageParameter param = (MessageParameter)env.getMessage();
					// Getting the data if it is useful
					if(this.parametersVariables.contains(param.getName())) {
						this.parameters.put(param.getName(), param.getValue());
						this.agentsToThanks.put(param.getName(), env.getMessageSenderAID());
					}
					// If someone else share the same parameters
					if(this.parametersFixes.contains(param.getName())) {
						this.agentsToDiscuss.put(param.getName(), env.getMessageSenderAID());
					}
				}
				else {
					// Case of a message about criticality
					if(env.getMessage() instanceof MessageCriticality) {
						MessageCriticality param = (MessageCriticality)env.getMessage();
						if(param.getCriticality() < this.criticality) {
							this.parametersUsefulButShared.add(param.getParam());
							this.parametersUseful.remove(param.getParam());
						}
					}

				// Case of a notify is sent to notify that a parameter is useful to send
				else {
					MessageNotify notif = (MessageNotify)env.getMessage();
					this.parametersUseful.add(notif.getName());
				}
			}
		}

		// Ask for the type of the variable
		for(String s : this.parametersVariables) {
			getAmas().CommunicateNeedOfVariableType(s);
		}

		//this.parametersUseful.addAll(getAmas().isParametersUseful(this.parametersFixes));

		// Remove the parameters communicated but useless
		this.parametersCommunicated.removeAll(this.parametersUseful);
		this.parametersNotUseful.addAll(this.parametersCommunicated);
		this.parametersCommunicated = new TreeSet<String>();


		criticality = this.parametersUseful.size();



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
						//this.dataCommunicated.add(s);
					}
				}
			}
		}
		// Recuperation des donnes par communication
		/*Set<String> dataMisses = new TreeSet<String>(this.dataCommunicated);
			int i = 0;
			while(dataMisses.size() > 0 && i < this.neighbours.size()) {
				AGFunction agf = this.neighbours.get(i);
				Map<String,Double> datas = agf.exchangeInformation(dataMisses);

				for(String s : datas.keySet()) {
					dataMisses.remove(s);
					this.myFunctionLPD.addFlow(datas.get(s));
				}

			}*/
		break;
	case RANDOM:
		// The agent decide which parameters to communicate
		int nbCom = 0;
		// Communication of the parameters useful
		for(String variableUseful : this.parametersUseful) {
			nbCom++;
			MessageParameter param = new MessageParameter(variableUseful, this.parameters.get(variableUseful));
			this.parametersToCommunicate.add(param);
			// this.getAmas().communicateValueOfVariable(variableUseful, this.parameters.get(variableUseful),this);
			this.parametersCommunicated.add(variableUseful);
		}

		// Communication of the parameters remaining
		List<String> variablesRemaining = new ArrayList<String>(this.parametersFixes);
		variablesRemaining.removeAll(this.parametersUseful);
		variablesRemaining.removeAll(parametersNotUseful);
		for(int j = 0; j < variablesRemaining.size() && nbCom < NB_COMMUNICATION_MAX;j++) {
			String s = variablesRemaining.get(j);
			MessageParameter param = new MessageParameter(s, this.parameters.get(s));
			//this.getAmas().communicateValueOfVariable(s,this.parameters.get(s),this);
			this.parametersToCommunicate.add(param);
			nbCom++;
			this.parametersCommunicated.add(s);
		}


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

		// Compute the calcul
		double resSum = this.myFunctionSum.compute(this.parameters);

		double oracleSum = this.getAmas().getValueOracle(this.name);

		feedback = Math.abs(resSum - oracleSum);


		// Send the messages for the next cycle
		this.getAmas().CommunicateMessageLinks(this.parametersToCommunicate,this.name,this);

		// Send the message to share the value of a parameter for the next cycle
		for(AGFunction agf : this.getAmas().getNeighborhood(this)) {
			for(MessageParameter mess : this.parametersToCommunicate) {
				this.sendMessage(mess, agf.getAID());
			}
		}

		// Send the message to notify the usefulness of a parameter
		for(String param : this.agentsToThanks.keySet()) {
			this.sendMessage(new MessageNotify(param), this.agentsToThanks.get(param));
			this.getAmas().notifyLinksVariableUseful(param,this.name);
		}

		// Send the message to discuss of whom share the parameters
		for(String param : this.agentsToDiscuss.keySet()) {
			this.sendMessage(new MessageCriticality(param,this.criticality),this.agentsToDiscuss.get(param));
		}
		break;
	default:
		break;

	}
}

/**
 * Calcul the criticality of the agent
 */
protected void computeMyCriticality() {
	this.criticality = this.parametersUseful.size();
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
	return "AGFunction [parameters=" + parameters +  ", name=" + name + ", feedback="
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

@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	return result;
}

@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	AGFunction other = (AGFunction) obj;
	if (name == null) {
		if (other.name != null)
			return false;
	} else if (!name.equals(other.name))
		return false;
	return true;
}
public List<String> getFixes(){
	return this.parametersFixes;
}



}
