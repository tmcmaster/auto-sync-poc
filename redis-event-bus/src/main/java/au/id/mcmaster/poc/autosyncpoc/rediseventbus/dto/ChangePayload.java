package au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChangePayload {
	private List<KeyValue> properties = new ArrayList<KeyValue>();
	private List<String> labels = new ArrayList<String>();

	public Collection<KeyValue> getProperties() {
		return new ArrayList<KeyValue>(this.properties);
	}
	
	public void addProperty(String key, String value, String oldValue) {
		addProperty(new KeyValue(key,value,oldValue));
	}
	
	public void addProperty(KeyValue keyValue) {
		this.properties.add(keyValue);
	}

	public Collection<String> getLabels() {
		return new ArrayList<String>(this.labels);
	}

	public void addLabel(String label) {
		this.labels.add(label);
	}

}
