package fr.irit.smac.mas;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import fr.irit.smac.messages.MessageCriticality;
import fr.irit.smac.messages.MessageNeighbour;
import fr.irit.smac.messages.MessageNotify;
import fr.irit.smac.messages.MessageParameter;
import fr.irit.smac.messages.SimpleEnvelope;
import fr.irit.smac.modelui.NeighbourModel;
import fr.irit.smac.modelui.ReceiverModel;
import fr.irit.smac.modelui.SenderModel;

public class AGFunction extends CommunicatingAgent<AmasF,EnvironmentF>{


	private final static int NB_COMMUNICATION_MAX = 20;

	private Map<String,Double>  parameters;

	private String name;

	private Map<String,AID> neighborsAID;
	private Set<String> old_neighborsAID;

	private List<AGFunction> neighbours;
	private List<AGFunction> old_neighbours;

	private String length;

	private String speed;

	private String capacity;

	private int criticality = 0;

	private int idZone;

	private int nbCom;


	// Variable to send to the ui
	private int nb_sended = 0;
	private int old_nb_sended = 0;
	private int nb_receive = 0;
	private int old_nb_receive = 0;

	private List<String> flow;


	/**
	 * All the parameters that the function can obtain by itself
	 */
	private List<String> parametersFixes;

	/**
	 * All the parameters that the function can't obtain by itself
	 */
	private Set<String> parametersVariables;

	/**
	 * All the parameters the function know they are useful to communicate
	 *  It will communicate those in priority
	 */
	private Set<String> parametersUseful;
	/**
	 * All the parameters the function knows that they are not useful to communicate
	 */
	private Set<String> parametersNotUseful;

	/**
	 * Collection used to know which parameter were communicated
	 */
	private Set<String> parametersCommunicated;

	/**
	 * Collection use for the UI and the listener
	 */
	private Set<String> old_parametersCommunicated;

	/**
	 * All the parameters which are useful but another function can communicate it too
	 */
	private Set<String> parametersUsefulButShared;

	/**
	 * All the parameters that 
	 */
	private Set<String> parametersShared;

	/**
	 * The set with all the parameters that the function decide to communicate
	 */
	private Set<MessageParameter> parametersToCommunicate;

	/**
	 * Map use to send the messages to notify the usefulness of a parameter
	 */
	private Map<String,AID> agentsToThanks;

	/**
	 * Map use to the cooperation between function
	 */
	private Map<String,AID> agentsToDiscuss;

	/**
	 * Map use to store the parameters that can be obtain but not useful
	 */
	private Map<String,MessageParameter> parametersObtainFromNeighboursToSend; 

	private Map<String,AID>  parametersObtainFromNeighbours;

	private Set<String> parametersUsefulFromOther;

	/**
	 * Map used to know to which aid the matching parameter is useful to send
	 */
	private Map<String,Set<AID>> parametersToSendTo;

	private Map<AID,Set<MessageParameter>> messageToCommunicate;

	private LPDFunction myFunctionLPD;
	private SumFunction myFunctionSum;

	private PropertyChangeSupport support;

	private double feedback;

	private boolean nmodel;
	private boolean smodel;

	private Object old_criticality;

	public AGFunction(AmasF amas, Object[] params, String name) {
		super(amas, params);

		this.name = name;
		parameters = new TreeMap<String,Double>();

		this.idZone = 0;
		this.nmodel = false;
		this.smodel = false;

		// Collections for UI
		this.neighbours = new ArrayList<AGFunction>();
		this.old_neighbours = new ArrayList<AGFunction>();
		this.neighborsAID = new TreeMap<String,AID>();
		this.old_neighborsAID = new TreeSet<String>();

		// Collections for the agent
		this.parametersFixes = new ArrayList<String>();
		this.parametersVariables = new TreeSet<String>();

		//Collections for the learning
		this.parametersUseful = new TreeSet<String>();
		this.parametersNotUseful = new TreeSet<String>();
		this.parametersCommunicated = new TreeSet<String>();
		this.parametersObtainFromNeighboursToSend = new TreeMap<String,MessageParameter>();
		this.parametersObtainFromNeighbours = new TreeMap<String,AID>();
		this.parametersUsefulFromOther = new TreeSet<String>();
		this.parametersToSendTo = new TreeMap<String,Set<AID>>();

		//Collection for the UI
		this.old_parametersCommunicated = new TreeSet<String>();

		//Collection for the cooperation
		this.parametersUsefulButShared = new TreeSet<String>();
		this.parametersShared = new TreeSet<String>();



		this.support = new PropertyChangeSupport(this);


		this.flow = new ArrayList<String>();
		feedback = 0.0;
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

		this.old_nb_receive = this.nb_receive;
		this.nb_receive = 0;
		this.old_nb_sended = this.nb_sended;
		this.nb_sended = 0;

		this.old_criticality = this.criticality;

		this.old_neighbours = this.neighbours;
		this.neighbours = new ArrayList<AGFunction>();

		switch(getAmas().getEnvironment().getExpe()) {
		case LPD:
			// Recuperation du voisinage
			this.neighbours = this.getAmas().askNeighbourgs(this);

			//Renitialisation de la fonction
			this.myFunctionLPD = new LPDFunction(this.getAmas().getLength(this.length), this.getAmas().getSpeed(this.speed));


			//if(this.capacity != null && this.dataPerceived.contains(this.capacity)) {
			this.myFunctionLPD.setC(this.getAmas().getCapacity(this.capacity));
			//}

			break;
		case RANDOM:

			// Initialization of the function
			this.myFunctionSum = new SumFunction();

			// Initialization of the collection for communication
			this.parametersToCommunicate = new HashSet<MessageParameter>();
			this.agentsToThanks = new TreeMap<String,AID>();
			this.agentsToDiscuss = new TreeMap<String,AID>();
			this.messageToCommunicate = new HashMap<AID,Set<MessageParameter>>();


			// Perception of the fixed parameters
			for(String s : this.parametersFixes) {
				this.parameters.put(s, this.getAmas().getValueOfParameters(s,this));
			}
			readAllMessage();
			for(String s : this.parametersVariables) {
				if(!this.agentsToThanks.keySet().contains(s)) {
					this.support.firePropertyChange("SM NOT RECEIVE", s, 0);
				}
			}

			// Ask for the type of the variable
			for(String s : this.parametersVariables) {
				getAmas().CommunicateNeedOfVariableType(s);
			}

			//this.parametersUseful.addAll(getAmas().isParametersUseful(this.parametersFixes));

			// Remove the parameters communicated but useless
			this.old_parametersCommunicated.clear();
			this.old_parametersCommunicated.addAll(parametersCommunicated);
			this.parametersCommunicated.removeAll(this.parametersUseful);
			this.parametersNotUseful.removeAll(this.parametersUseful);
			this.parametersNotUseful.addAll(this.parametersCommunicated);
			this.parametersCommunicated = new TreeSet<String>();





			break;
		default:
			break;

		}
	}

	/**
	 * Read all the message received
	 */
	private void readAllMessage() {
		for(IAmakEnvelope env : this.getAllMessages()) {

			// Case of a parameters is sent
			if(env.getMessage() instanceof MessageParameter) {
				MessageParameter param = (MessageParameter)env.getMessage();
				// Getting the data if it is useful
				if(this.parametersVariables.contains(param.getName())) {
					if(!this.agentsToThanks.keySet().contains(param.getName())) {
						this.parameters.put(param.getName(), param.getValue());
						this.support.firePropertyChange("SM RECEIVE", param.getName(), param.getValue());
						this.nb_receive++;
					}
					this.agentsToThanks.put(param.getName(), env.getMessageSenderAID());
				}

				// If someone else share the same parameters
				if(this.parametersFixes.contains(param.getName()) && param.getNbJump() <= 1) {
					if(this.parametersUseful.contains(param.getName())) {
						this.agentsToDiscuss.put(param.getName(), env.getMessageSenderAID());
					}
				}
				else {
					if(this.parametersObtainFromNeighboursToSend.keySet().contains(param.getName())) {
						if(this.parametersObtainFromNeighboursToSend.get(param.getName()).getNbJump() >= param.getNbJump()) {
							this.parametersObtainFromNeighboursToSend.put(param.getName(),param);
							this.parametersObtainFromNeighbours.put(param.getName(), env.getMessageSenderAID());
						}
					}
					else {
						this.parametersObtainFromNeighboursToSend.put(param.getName(),param);
						this.parametersObtainFromNeighbours.put(param.getName(), env.getMessageSenderAID());
					}
				}
			}
			else {
				// Case of a message about criticality
				if(env.getMessage() instanceof MessageCriticality) {
					MessageCriticality param = (MessageCriticality)env.getMessage();
					if(param.getCriticality() < this.criticality) {
						this.parametersUsefulButShared.add(param.getParam());
						this.parametersUseful.remove(param.getParam());
						this.parametersToSendTo.remove(param.getParam());
					}
					else {
						if(this.parametersFixes.contains(param.getParam())) {
							this.parametersUseful.add(param.getParam());
						}
					}
				}
				else {
					if(env.getMessage() instanceof MessageNeighbour) {
						MessageNeighbour neighb = (MessageNeighbour)env.getMessage();
						this.neighborsAID.put(neighb.getName(), env.getMessageSenderAID());
					}
					// Case of a message is sent to notify that a parameter is useful to send
					else {
						MessageNotify notif = (MessageNotify)env.getMessage();
						if(this.parametersFixes.contains(notif.getName())) {
							if(!this.parametersUsefulButShared.contains(notif.getName())) {
								this.parametersUseful.add(notif.getName());
							}
							if(this.parametersToSendTo.containsKey(notif.getName()) && !this.parametersUsefulButShared.contains(notif.getName())){
								this.parametersToSendTo.get(notif.getName()).add(env.getMessageSenderAID());
							}
							else {
								Set<AID> lis = new HashSet<AID>();
								lis.add(env.getMessageSenderAID());
								this.parametersToSendTo.put(notif.getName(), lis);
							}
						}
						else {
							this.agentsToThanks.put(notif.getName(), this.parametersObtainFromNeighbours.get(notif.getName()));
							this.parametersUsefulFromOther.add(notif.getName());
						}
					}
				}


			}
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
			break;
		case RANDOM:

			if(!this.neighborsAID.isEmpty()) {
				// The agent decide which parameters to communicate
				nbCom = 0;
				// Communication of the parameters useful
				for(String param : this.parametersToSendTo.keySet()) {
					if(nbCom < NB_COMMUNICATION_MAX) {
						for(AID aid : this.parametersToSendTo.get(param)) {
							if(nbCom < NB_COMMUNICATION_MAX) {
								MessageParameter mess = new MessageParameter(param, this.parameters.get(param));
								//this.parametersToCommunicate.add(mess);
								this.parametersCommunicated.add(param);

								//this.parametersCommunicated.add(param+" to "+this.getAmas().getAGFNameWithAID(aid));
								Set<MessageParameter> lis = new HashSet<MessageParameter>();
								if(this.messageToCommunicate.containsKey(aid)) {
									lis.addAll(this.messageToCommunicate.get(aid));
								}
								lis.add(mess);
								this.messageToCommunicate.put(aid, lis);
							}

						}

						nbCom++;
					}
				}

				// Communication of the parameters remaining
				List<String> variablesRemaining = new ArrayList<String>(this.parametersFixes);
				variablesRemaining.removeAll(this.parametersToSendTo.keySet());
				variablesRemaining.removeAll(parametersNotUseful);
				variablesRemaining.removeAll(this.parametersUsefulButShared);
				for(int j = 0; j < variablesRemaining.size() && nbCom < NB_COMMUNICATION_MAX;j++) {
					String s = variablesRemaining.get(j);
					MessageParameter param = new MessageParameter(s, this.parameters.get(s));
					this.parametersToCommunicate.add(param);
					nbCom++;
					this.parametersCommunicated.add(s);
				}

				// Communication of the parameters useful from other function 
				List<String> fromOthers = new ArrayList<String>(this.parametersUsefulFromOther);
				for(int j = 0; j < fromOthers.size() && nbCom < NB_COMMUNICATION_MAX;j++) {
					if(this.parametersObtainFromNeighboursToSend.containsKey(fromOthers.get(j))) {
						String s = fromOthers.get(j);
						MessageParameter param = this.parametersObtainFromNeighboursToSend.get(s);
						param.increaseJump();
						this.parametersToCommunicate.add(param);
						nbCom++;
						this.parametersCommunicated.add(s);
					}
				}

				// Communication of the parameters from other
				if(nbCom < NB_COMMUNICATION_MAX) {
					List<MessageParameter> redistribute = new ArrayList<MessageParameter>(this.parametersObtainFromNeighboursToSend.values());
					for(int j = 0; j < redistribute.size() && nbCom <NB_COMMUNICATION_MAX;j++) {
						MessageParameter mess = redistribute.get(j);
						mess.increaseJump();
						this.parametersToCommunicate.add(mess);
						nbCom++;
						this.parametersCommunicated.add(mess.getName());
						this.parametersObtainFromNeighboursToSend.remove(mess.getName());
					}
				}
				List<String> calcCrit = new ArrayList<String>(this.parametersUseful);
				calcCrit.removeAll(this.parametersCommunicated);
				criticality = calcCrit.size();
			}
			break;
		default:
			break;

		}
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
			for(AID aid : this.neighborsAID.values()) {
				if(this.messageToCommunicate.keySet().contains(aid)) {
					this.getAmas().CommunicateMessageLinks(this.messageToCommunicate.get(aid),this.name,this);
					for(MessageParameter mess : this.messageToCommunicate.get(aid)) {
						this.sendMessage(mess, aid);
					}
				}
				for(MessageParameter mess : this.parametersToCommunicate) {
					this.sendMessage(mess, aid);
				}
			}
			this.nb_sended = nbCom;


			// Send the message to notify the usefulness of a parameter
			for(String param : this.agentsToThanks.keySet()) {
				if(this.neighborsAID.values().contains(this.agentsToThanks.get(param))) {
					this.sendMessage(new MessageNotify(param), this.agentsToThanks.get(param));
					this.getAmas().notifyLinksVariableUseful(param,this.name);
				}
			}

			// Send the message to discuss of whom share the parameters
			for(String param : this.agentsToDiscuss.keySet()) {
				this.sendMessage(new MessageCriticality(param,this.criticality),this.agentsToDiscuss.get(param));
			}

			this.support.firePropertyChange("SENT", this.old_nb_sended, this.nb_sended);

			this.support.firePropertyChange("RECEIVE", this.old_nb_receive, this.nb_receive);

			// Neighbors
			this.notifyNeighborsUI();
			for(AGFunction agf :getAmas().getNeighborhood(this.idZone)) {
				MessageNeighbour mess =new MessageNeighbour(this.getName());
				this.sendMessage(mess, agf.getAID());
			}
			// Receiver
			this.support.firePropertyChange("REC SENT", this.old_parametersCommunicated, this.parametersCommunicated);

			this.getAmas().setCriticityLinks(this.name, this.criticality);
			this.support.firePropertyChange("CRITICALITY", this.old_criticality, this.criticality);

			break;
		default:
			break;

		}
	}

	private void notifyNeighborsUI() {
		if(this.nmodel) {
			this.neighbours = getAmas().getNeighborhood(this.idZone);
			List<AGFunction> tmp = new ArrayList<AGFunction>(this.neighbours);
			tmp.removeAll(this.old_neighbours);
			for(AGFunction agf : tmp) {
				this.support.firePropertyChange("N ADD", "a", agf.getName());
			}
			this.old_neighbours.removeAll(this.neighbours);
			for(AGFunction agf : this.old_neighbours) {
				this.support.firePropertyChange("N REMOVE", null, agf.getName());
			}
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

	/**
	 * Add a listener
	 * @param pcl
	 */
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		support.addPropertyChangeListener(pcl);
		if(pcl instanceof NeighbourModel) {
			this.nmodel = true;
			this.notifyNeighborsUI();
		}
		if(pcl instanceof SenderModel) {
			this.smodel = true;
		}
		if(pcl instanceof ReceiverModel) {

			this.support.firePropertyChange("REC SENT", null, this.parametersCommunicated);
		}
	}

	/**
	 * Remove a listener
	 * @param pcl
	 */
	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		support.removePropertyChangeListener(pcl);
	}

	/**
	 * Return the set of all parameters communicated
	 * 
	 * @return a set with the name of all parameters communicated
	 */
	public Set<String> getParametersToCommunicate(){
		Set<String> res = new TreeSet<String>();
		for(MessageParameter mess : this.parametersToCommunicate) {
			res.add(mess.getName());
		}
		return res;
	}

	/**
	 * Setter for the idZone
	 * 
	 * @param id
	 */
	public void SetIdZone(int id) {
		this.idZone = id;
	}

	public int getIdZone() {
		return this.idZone;
	}
}
