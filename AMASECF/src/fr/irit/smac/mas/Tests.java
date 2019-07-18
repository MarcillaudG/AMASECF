package fr.irit.smac.mas;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import fr.irit.smac.amak.Scheduling;
import fr.irit.smac.mas.EnvironmentF.Expe;
public class Tests {

	public static void main(String[] args) {

		//AmasF amas = new AmasF(new EnvironmentF(Scheduling.DEFAULT, args, Expe.RANDOM), Scheduling.DEFAULT, args);
		
		List<String> test = new ArrayList<String>();
		int i = 0;
		Tests.testInt(i++);
		Tests.testInt(i);
		
	}
	
	public static void testInt(int i) {
		System.out.println(i);
	}
}
