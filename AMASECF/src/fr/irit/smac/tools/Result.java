package fr.irit.smac.tools;

import org.apache.commons.lang3.tuple.Pair;

import fr.irit.smac.mas.Decisions;

public class Result {

	private Pair<Decisions, Double> result;
	
	public Result(Decisions decision, Double value) {
		this.result = Pair.of(decision, value);
	}
	
	public Decisions getDecision() {
		return this.result.getLeft();
	}
	
	public Double getValue() {
		return this.result.getRight();
	}
}
