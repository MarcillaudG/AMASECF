package fr.irit.smac.learningdata.Agents;

import java.util.Map;
import java.util.TreeMap;

public class FeedbackAgent extends AgentLearning{
	
	private Map<Integer,Double> feedbacks;
	
	private Map<Integer, Map<String,String>> history;
	
	private Map<Integer, Double> decisions;
	
	private LearningFunction function;
	
	public FeedbackAgent(LearningFunction function) {
		this.feedbacks = new TreeMap<Integer,Double>();
		this.history = new TreeMap<Integer, Map<String,String>>();
		this.decisions = new TreeMap<Integer,Double>();
		this.function = function;
	}
	
	/**
	 * 
	 * @param cycle
	 * 		the number of the cycle
	 * @param feedback
	 * 		the feedback
	 * @param configuration
	 * 			The configuration of the data agent during the corresponding cycle
	 */
	public void addFeedback(int cycle, Double feedback, Map<String,String> configuration) {
		this.feedbacks.put(cycle, feedback);
		this.history.put(cycle,configuration);
	}
	
	public void perceive() {
		this.feedbacks.put(this.function.getCycle(),this.function.getFeedback());
		this.history.put(this.function.getCycle(),this.function.getPreviousConfiguration());
	}
	
	public void decide() {
		Double best = null;
		Integer bestCycle = null;
		for(Integer cycle : this.decisions.keySet()) {
			double resCycle = this.decisions.get(cycle);
			if(best == null ) {
				best = resCycle;
				bestCycle = cycle;
			}
			else {
				if(Math.abs(best) > Math.abs(resCycle)) {
					best = resCycle;
					bestCycle = cycle;
				}
			}
		}
		
		Map<Double,Integer> cycleOrdered = new TreeMap<Double,Integer>();
		for(Integer cy : this.decisions.keySet()) {
			cycleOrdered.put(this.decisions.get(cy), cy);
		}
		
		for(Double resOrder : cycleOrdered.keySet()) {
			Integer cycleNumber = cycleOrdered.get(resOrder);
			Map<String,String> pair = this.history.get(cycleNumber);
			for(String input : pair.keySet()) {
				String data = pair.get(input);
			}			
		}
	}

	@Override
	public void requestAccepted(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestDenied(int id) {
		// TODO Auto-generated method stub
		
	}

}
