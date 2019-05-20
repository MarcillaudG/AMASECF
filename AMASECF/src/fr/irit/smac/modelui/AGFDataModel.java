package fr.irit.smac.modelui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.StringProperty;

public class AGFDataModel implements PropertyChangeListener{

	private String name;
	
	private ListProperty<StringProperty> neighbours;
	
	private ListProperty<Receiver> receivers;
	
	private ListProperty<Sender> sender;
	
	public AGFDataModel(String name) {
		this.name = name;
		
		this.neighbours = new SimpleListProperty<StringProperty>();
		this.receivers = new SimpleListProperty<Receiver>();
		this.sender = new SimpleListProperty<Sender>();
		
		init(name);
	}
	
	





	public ListProperty<StringProperty> getNeighbours() {
		return neighbours;
	}


	public void setNeighbours(ListProperty<StringProperty> neighbours) {
		this.neighbours = neighbours;
	}



	public ListProperty<Receiver> getReceivers() {
		return receivers;
	}

	public void setReceivers(ListProperty<Receiver> receivers) {
		this.receivers = receivers;
	}



	public ListProperty<Sender> getSender() {
		return sender;
	}



	public void setSender(ListProperty<Sender> sender) {
		this.sender = sender;
	}


	private void init(String name) {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Receiver createReceiver(StringProperty param, StringProperty name) {
		return new Receiver(param,name);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}
	

	public Sender createSender(StringProperty param, boolean b) {
		return new Sender(param,b);
	}
	

	
	public class Sender {
		private StringProperty param;
		
		private boolean isReceive;
		
		private List<StringProperty> who;

		public Sender(StringProperty param, boolean isReceive) {
			this.param = param;
			this.isReceive = isReceive;
			this.who = new ArrayList<StringProperty>();
		}

		public StringProperty getParam() {
			return param;
		}

		public void setParam(StringProperty param) {
			this.param = param;
		}

		public boolean isReceive() {
			return isReceive;
		}

		public void setReceive(boolean isReceive) {
			this.isReceive = isReceive;
		}

		public List<StringProperty> getWho() {
			return who;
		}

		public void setWho(List<StringProperty> who) {
			this.who = who;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + (isReceive ? 1231 : 1237);
			result = prime * result + ((param == null) ? 0 : param.hashCode());
			result = prime * result + ((who == null) ? 0 : who.hashCode());
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
			Sender other = (Sender) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (isReceive != other.isReceive)
				return false;
			if (param == null) {
				if (other.param != null)
					return false;
			} else if (!param.equals(other.param))
				return false;
			if (who == null) {
				if (other.who != null)
					return false;
			} else if (!who.equals(other.who))
				return false;
			return true;
		}

		private AGFDataModel getOuterType() {
			return AGFDataModel.this;
		}
		
		
		
	}
	
	public class Receiver{
		
		private StringProperty param;
		
		private StringProperty agfName;
		
		public Receiver(StringProperty param, StringProperty agfName) {
			this.param = param;
			this.agfName = agfName;
		}

		public StringProperty getParam() {
			return param;
		}

		public void setParam(StringProperty param) {
			this.param = param;
		}

		public StringProperty getAgfName() {
			return agfName;
		}

		public void setAgfName(StringProperty agfName) {
			this.agfName = agfName;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((agfName == null) ? 0 : agfName.hashCode());
			result = prime * result + ((param == null) ? 0 : param.hashCode());
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
			Receiver other = (Receiver) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (agfName == null) {
				if (other.agfName != null)
					return false;
			} else if (!agfName.equals(other.agfName))
				return false;
			if (param == null) {
				if (other.param != null)
					return false;
			} else if (!param.equals(other.param))
				return false;
			return true;
		}

		private AGFDataModel getOuterType() {
			return AGFDataModel.this;
		}
		
		
	}

	public void setAll(ListProperty<StringProperty> neighbours2, ListProperty<Receiver> receivers2,
		ListProperty<Sender> senders) {
		this.neighbours = neighbours2;
		this.receivers = receivers2;
		this.sender = senders;
		
	}

}
