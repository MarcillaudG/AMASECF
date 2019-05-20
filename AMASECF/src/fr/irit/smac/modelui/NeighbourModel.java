package fr.irit.smac.modelui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

public class NeighbourModel implements PropertyChangeListener {

	private ListProperty<String> names;
	
	public NeighbourModel() {
		this.names = new SimpleListProperty<String>(FXCollections.observableArrayList());
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		Platform.runLater(() -> {
		if(evt.getPropertyName().equals("N ADD")) {
			if(!this.names.contains((String) evt.getNewValue()))
				this.names.add(((String) evt.getNewValue()));
		}
		if(evt.getPropertyName().equals("N REMOVE")) {
			this.names.remove(evt.getNewValue());
		}
		});
	}


	public ListProperty<String> getNames() {
		return names;
	}
}
