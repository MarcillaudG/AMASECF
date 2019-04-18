package messages;
/**
 * 
 * @author gmarcill
 *
 * Message used to share the criticality between agent
 *
 */
public class MessageCriticality {

	
	private double criticality;
	
	public MessageCriticality(double value) {
		this.criticality = value;
	}
	
	public double getCriticality() {
		return this.criticality;
	}
}
