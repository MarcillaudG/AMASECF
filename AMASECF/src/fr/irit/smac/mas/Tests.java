package fr.irit.smac.mas;

import fr.irit.smac.amak.Scheduling;
import fr.irit.smac.mas.EnvironmentF.Expe;
public class Tests {

	public static void main(String[] args) {

		AmasF amas = new AmasF(new EnvironmentF(Scheduling.DEFAULT, args, Expe.RANDOM), Scheduling.DEFAULT, args);
		
	}

}
