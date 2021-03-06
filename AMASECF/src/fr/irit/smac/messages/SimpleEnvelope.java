package fr.irit.smac.messages;


import fr.irit.smac.amak.aid.AddressableAID;
import fr.irit.smac.amak.messaging.IAmakEnvelope;
import fr.irit.smac.amak.messaging.IAmakMessage;

public class SimpleEnvelope implements IAmakEnvelope {

	private final IAmakMessage message;
	private final SimpleMetadata metaData;
	private final AddressableAID senderAID;

	public SimpleEnvelope(IAmakMessage messageToSend, AddressableAID senderAID, SimpleMetadata metaData) {
		this.message = messageToSend;
		this.senderAID = senderAID;
		this.metaData = metaData;
	}

	@Override
	public IAmakMessage getMessage() {
		return message;
	}

	@Override
	public SimpleMetadata getMetadata() {
		return metaData;
	}


	@Override
	public AddressableAID getMessageSenderAID() {
		return senderAID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((metaData == null) ? 0 : metaData.hashCode());
		result = prime * result + ((senderAID == null) ? 0 : senderAID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleEnvelope other = (SimpleEnvelope) obj;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (metaData == null) {
			if (other.metaData != null)
				return false;
		} else if (!metaData.equals(other.metaData))
			return false;
		if (senderAID == null) {
			if (other.senderAID != null)
				return false;
		} else if (!senderAID.equals(other.senderAID))
			return false;
		return true;
	}

}
