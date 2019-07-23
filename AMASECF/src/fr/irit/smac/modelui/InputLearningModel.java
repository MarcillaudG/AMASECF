package fr.irit.smac.modelui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

public class InputLearningModel implements PropertyChangeListener{

	
	private StringProperty name;
	
	private DoubleProperty influenceAdd;
	
	private DoubleProperty influenceMinus;
	
	private List<String> dataApplying;
	
	private StringProperty datas;
	
	
	public InputLearningModel(String name) {
		this.name = new SimpleStringProperty(name);
		this.influenceAdd = new SimpleDoubleProperty(0.5);
		this.influenceMinus = new SimpleDoubleProperty(0.5);
		this.dataApplying = new ArrayList<String>();
		datas =  new SimpleStringProperty();
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
		if(evt.getPropertyName().equals("DATA")) {
			this.dataApplying.clear();
			this.dataApplying.addAll((List<String>) evt.getNewValue());
			String res = "";
			for(String s : this.dataApplying) {
				res += "|"+s;
			}
			this.datas.set(res);
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
	
	public StringProperty getDataApplying(){
		return this.datas;
	}
}
