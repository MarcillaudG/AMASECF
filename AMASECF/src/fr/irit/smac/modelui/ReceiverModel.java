package fr.irit.smac.modelui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Set;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

public class ReceiverModel implements PropertyChangeListener {

	private ListProperty<String> params;

	public ReceiverModel() {
		this.params = new SimpleListProperty<String>(FXCollections.observableArrayList());
	}

	public ListProperty<String> getparams() {
		return params;
	}

	public void setParams(ListProperty<String> agfName) {
		this.params.set(agfName);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		Platform.runLater(() -> {

			if (evt.getPropertyName().equals("REC ADD")) {
				this.params.add((String) evt.getNewValue());
			}
			if (evt.getPropertyName().equals("REC REMOVE")) {
				this.params.remove(evt.getNewValue());
			}
			if (evt.getPropertyName().equals("REC SENT")) {
				Set<String> tmp = (Set<String>) evt.getNewValue();
				ListProperty<String> res = new SimpleListProperty<String>(FXCollections.observableArrayList());
				res.addAll(tmp);
				this.setParams(res);
			}
		});
	}
}
