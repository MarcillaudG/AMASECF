package fr.irit.smac.learningdata.requests;

public class RequestRow extends Request{

	public enum Reason{OVERCHARGED,UNDERCHARGED};
	
	private String inputName;
	
	private Reason reason;
	
	public RequestRow(double criticality, String agentName, int id, String inputName, Reason reason) {
		super(criticality, agentName, id);
		this.inputName = inputName;
		this.reason = reason;
	}

	public String getInputName() {
		return inputName;
	}

	public void setInputName(String inputName) {
		this.inputName = inputName;
	}

	public Reason getReason() {
		return reason;
	}

	public void setReason(Reason reason) {
		this.reason = reason;
	}

	@Override
	public String toString() {
		return "RequestRow [inputName=" + inputName + ", reason=" + reason + "]";
	}

	

}
