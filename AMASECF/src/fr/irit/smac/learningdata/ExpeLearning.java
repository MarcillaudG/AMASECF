package fr.irit.smac.learningdata;

import fr.irit.smac.amak.Scheduling;

public class ExpeLearning {

	public static void main(String[] args){
		
		AmasLearning amas = new AmasLearning(new EnvironmentLearning(Scheduling.DEFAULT, args), Scheduling.DEFAULT, args);
		
	}
}
