package au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.data.util.Pair;

public abstract class ChangeEvent {
	private long id;
	private Type type;
	private List<KeyValue> properties = new ArrayList<KeyValue>();

	protected ChangeEvent(Type type, long id) {
		super();
		this.type = type;
		this.id = id;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public void addProperty(String key, String value) {
		this.properties.add(new KeyValue(key,value));
	}
	
	public void addProperties(Map<String,String> props) {
		for (String key : props.keySet()) {
			this.properties.add(new KeyValue(key,props.get(key)));
		}
	}
	
	public Iterator<KeyValue> getProperties() {
		return this.properties.iterator();
	}
	
	public static enum Type {
		NODE_ADDED,NODE_DELETED,NODE_CHANGED,
		RELATIONSHIP_ADDED,RELATIOINSHIP_DELETED,RELATIONSHIP_CHANGED,
		NODE_PROPERTY_ADDED,NODE_PROPERTY_DELETED,NODE_PROPERTY_CHANGED,
		RELATIONSHIP_PROPERTY_ADDED,RELATIONSHIP_PROPERTY_DELETED,RELATIONSHIP_PROPERTY_CHANGED
	}

	public boolean isType(String typeString) {
		// TODO Auto-generated method stub
		return this.type == Type.valueOf(typeString);
	}
}
