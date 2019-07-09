package fr.irit.smac.modelui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class InputLearningModel implements PropertyChangeListener{

	
	private StringProperty name;
	
	private DoubleProperty influenceAdd;
	
	private DoubleProperty influenceMinus;
	
	
	public InputLearningModel(String name) {
		this.name = new SimpleStringProperty(name);
		this.influenceAdd = new SimpleDoubleProperty(0.5);
		this.influenceMinus = new SimpleDoubleProperty(0.5);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("NAME")) {
			this.setName((String) evt.getNewValue());
		}
		if(evt.getPropertyName().equals("INFLUENCE ADD")) {
			this.influenceAdd.set((double) evt.getNewValue());
		}
		if(evt.getPropertyName().equals("INFLUENCE MINUS")) {
			this.influenceMinus.set((double) evt.getNewValue());
		}
		
	}

	
	public void setName(String name) {
		this.name.set(name);
	}
	
	public StringProperty getName() {
		return this.name;
	}
	
	public DoubleProperty getInfluenceAdd() {
		return this.influenceAdd;
	}
	

	public DoubleProperty getInfluenceMinus() {
		return this.influenceMinus;
	}
}
