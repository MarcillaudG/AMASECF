package fr.irit.smac.modelui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AGFModel implements PropertyChangeListener{

	
	private final StringProperty name;
	
	private final IntegerProperty nbParamsSent;
	
	private final IntegerProperty nbParamsReceive;
	
	
	public AGFModel(String name) {
		this.name = new SimpleStringProperty(name);
		
		this.nbParamsSent = new SimpleIntegerProperty(0);

		this.nbParamsReceive = new SimpleIntegerProperty(0);
	}


	public StringProperty getName() {
		return name;
	}


	public IntegerProperty getNbParamsSent() {
		return nbParamsSent;
	}


	public IntegerProperty getNbParamsReceive() {
		return nbParamsReceive;
	}
	
	public void setNbParamsSent(int nb) {
		this.nbParamsSent.set(nb);
	}
	
	public void setNbParamsReceive(int nb) {
		this.nbParamsReceive.set(nb);
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("RECEIVE")) {
			this.nbParamsReceive.set((Integer)evt.getNewValue());
		}
		if(evt.getPropertyName().equals("SENT")) {
			this.nbParamsSent.set((Integer)evt.getNewValue());
		}
		
	}
}
