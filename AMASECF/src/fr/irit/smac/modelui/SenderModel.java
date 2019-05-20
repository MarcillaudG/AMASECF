package fr.irit.smac.modelui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SenderModel implements PropertyChangeListener {
	private StringProperty param;
	
	private BooleanProperty isReceive;
	
	private List<String> who;
	
	private StringProperty all_who;

	public SenderModel(String param, boolean isReceive) {
		this.param = new SimpleStringProperty(param);
		this.isReceive = new SimpleBooleanProperty(isReceive);
		this.who = new ArrayList<String>();
		this.all_who = new SimpleStringProperty();
	}

	public StringProperty getParam() {
		return param;
	}

	public void setParam(StringProperty param) {
		this.param = param;
	}

	public BooleanProperty isReceive() {
		return isReceive;
	}

	public void setReceive(boolean isReceive) {
		this.isReceive = new SimpleBooleanProperty(isReceive);
	}

	public List<String> getWho() {
		return who;
	}

	public void setWho(List<String> who) {
		this.who = who;
	}

	private void calculAllWho() {
		String ens = "";
		for(String s : this.who) {
			ens += s + "\n";
		}
		this.all_who.set(ens);
	}
	
	public StringProperty getAllWho() {
		return this.all_who;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("SM REMOVE")) {
			this.who.remove((String) evt.getNewValue());
			this.calculAllWho();
		}
		if(evt.getPropertyName().equals("SM ADD")) {
			if(!this.who.contains(evt.getNewValue()) && evt.getOldValue().equals(this.param.get())){
				this.who.add((String) evt.getNewValue());
				this.calculAllWho();
			}
		}
		if(evt.getPropertyName().equals("SM RECEIVE")) {
			if(evt.getOldValue().equals(param.get())) {
				this.isReceive.set(true);
			}
		}
		if(evt.getPropertyName().equals("SM NOT RECEIVE")) {
			if(evt.getOldValue().equals(param.get())) {
				this.isReceive.set(false);
			}
		}
	}
	
	
	
}