package fr.irit.smac.functions;

import java.util.ArrayList;
import java.util.List;

public class LPDFunction extends Function {

	private double L;
	
	private double V;
	
	private List<Double> flow;
	
	private double C;
	
	
	
	public LPDFunction(double l, double v) {
		super();
		L = l;
		V = v;
		this.flow = new ArrayList<Double>();
	}

	

	public double getL() {
		return L;
	}



	public void setL(double l) {
		L = l;
	}



	public double getV() {
		return V;
	}



	public void setV(double v) {
		V = v;
	}



	public double getC() {
		return C;
	}



	public void setC(double c) {
		C = c;
	}

	
	
	public List<Double> getFlow() {
		return flow;
	}



	public void addFlow(double vec) {
		this.flow.add(vec);
	}


	@Override
	public double compute() {
		double sumFlow = 0.0;
		
		for(double d : this.flow) {
			sumFlow += d;
		}
		if(this.flow.size() == 0) {
			return L/V*(1+0.15/Math.pow(C, 4));
		}
		return L/V*(1.0+0.15*(Math.pow(sumFlow,4)/Math.pow(C, 4)));
	}



	public void initFlows() {
		this.flow = new ArrayList<Double>();
		
	}

}
