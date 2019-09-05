package fr.irit.smac.modelui.learning;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
	
	private StringProperty dataCorrect;
	
	private BooleanProperty correct;
	
	
	public InputLearningModel(String name) {
		this.name = new SimpleStringProperty(name);
		this.influenceAdd = new SimpleDoubleProperty(0.5);
		this.influenceMinus = new SimpleDoubleProperty(0.5);
		this.dataApplying = new ArrayList<String>();
		datas =  new SimpleStringProperty();
		
		this.dataCorrect = new SimpleStringProperty();
		
		this.correct = new SimpleBooleanProperty(false);
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
				if(res.equals(""))
					res += s;
				else
					res += "|"+s;
			}
			this.datas.set(res);
			this.correct.set(this.dataApplying.size() == 1 && this.dataCorrect.isNotNull().get() &&  this.dataCorrect.get().equals(this.dataApplying.get(0)));
		}
		if(evt.getPropertyName().equals("CORRECT")) {
			this.dataCorrect.set((String) evt.getNewValue());
			this.correct.set(this.dataApplying.size() == 1 && this.dataCorrect.isNotNull().get() && this.dataCorrect.get().equals(this.dataApplying.get(0)));
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

	public StringProperty getDataCorrect() {
		return this.dataCorrect;
	}
	
	public BooleanProperty getCorrect() {
		return this.correct;
	}
}
