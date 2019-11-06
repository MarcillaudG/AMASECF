package fr.irit.smac.learningdata;

import java.util.ArrayList;
import java.util.List;

public class EasyFunction {

	public enum MathFuns {ADD,OPP,SQUARE,CUBE,OPP_SQUARE,MULT,INV,OPP_INV,DIV,LOG,SIN,COS,CARD};
	
	private int nbElem;
	
	private String name;
	
	private List<EasyInput> decisionProcess;
	
	
	public EasyFunction(String name, int nbElem) {
		this.name = name;
		this.nbElem = Math.min(MathFuns.values().length, nbElem);
		this.decisionProcess = new ArrayList<EasyInput>();
		MathFuns enums[] = MathFuns.values();
		for(int i = 0; i < this.nbElem;i++) {
			this.decisionProcess.add(i,new EasyInput(enums[i]));
		}
	}
	
	
	public void setValueOfInput(Double value,int input) {
		this.decisionProcess.get(input).dataValue = value;
	}
	
	public double compute() {
		double res = 0.0;
		for(int i = 0; i < this.nbElem;i++) {
			res += this.decisionProcess.get(i).compute();
		}
		return res;
	}
	
	
	
	
	private class EasyInput{
		
		
		private Double dataValue;
		
		private MathFuns mathfuns;
		
		private int nbx;
		
		
		public EasyInput(MathFuns mf) {
			this.mathfuns = mf;
		}
		
		public double compute() {
			double res = 0.0;
			switch(this.mathfuns) {
			case ADD:
				res = this.dataValue;
				break;
			case CARD:
				res = Math.sin(this.dataValue)/this.dataValue;
				break;
			case COS:
				res = Math.cos(this.dataValue);
				break;
			case CUBE:
				res =Math.pow(this.dataValue, 3);
				break;
			case DIV:
				break;
			case INV:
				res = 1/this.dataValue;
				break;
			case LOG:
				res = Math.log(this.dataValue);
				break;
			case MULT:
				break;
			case OPP:
				res = -this.dataValue;
				break;
			case OPP_INV:
				res = -1/this.dataValue;
				break;
			case OPP_SQUARE:
				res = -Math.pow(this.dataValue,2);
				break;
			case SIN:
				res = Math.sin(this.dataValue);
				break;
			case SQUARE:
				res = Math.pow(this.dataValue, 2);
				break;
			default:
				break;
			
			}
			return res;
		}
		
	}
}
