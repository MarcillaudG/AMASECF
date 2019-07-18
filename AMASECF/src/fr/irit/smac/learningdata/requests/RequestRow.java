package fr.irit.smac.learningdata.requests;

public class RequestRow extends Request{

	private String inputName;
	
	public RequestRow(double criticality, String agentName, int id, String inputName) {
		super(criticality, agentName, id);
		this.inputName = inputName;
	}

	public String getInputName() {
		return inputName;
	}

	public void setInputName(String inputName) {
		this.inputName = inputName;
	}


}
