package fr.irit.smac.modelui.learning;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DataLearningModel implements PropertyChangeListener{

	private StringProperty name;
	

	private Map<String,Double> trustValues;
	
	private StringProperty trustValuesString;
	

	public DataLearningModel(String name) {
		this.name = new SimpleStringProperty();
		this.name.set(name);
		this.trustValuesString = new SimpleStringProperty();
	}

	public StringProperty getName() {
		return name;
	}

	public void setName(String name) {
		this.name.set(name);
	}


	public Map<String, Double> getTrustValues() {
		return trustValues;
	}

	public void setTrustValues(Map<String, Double> trustValues) {
		this.trustValues = trustValues;
	}

	public StringProperty getTrustValuesString() {
		return trustValuesString;
	}

	public void setTrustValuesString(StringProperty trustValuesString) {
		this.trustValuesString = trustValuesString;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("NAME")) {
			this.setName((String) evt.getNewValue());
		}
		if(evt.getPropertyName().equals("TRUSTVALUES")) {
			this.setTrustValues((Map<String, Double>) evt.getNewValue());
			Map<Double,List<String>> mapi = new TreeMap<Double,List<String>>();
			for(String s : this.trustValues.keySet()) {
				if(mapi.containsKey(this.trustValues.get(s))) {
					mapi.get(this.trustValues.get(s)).add(s);
				}
				else {
					mapi.put(this.trustValues.get(s), new ArrayList<String>());
					mapi.get(this.trustValues.get(s)).add(s);
				}
			}	
			String res = "";
			for(Double d : mapi.keySet()) {
				for(String s : mapi.get(d)) {
					res += "|"+s+"->"+d;
				}
			}
			/*for(String s : this.trustValues.keySet()) {
				res += "|"+s+"->"+this.trustValues.get(s);
			}*/
			this.trustValuesString.set(res);
		}
	}
	
	
	
	
}
