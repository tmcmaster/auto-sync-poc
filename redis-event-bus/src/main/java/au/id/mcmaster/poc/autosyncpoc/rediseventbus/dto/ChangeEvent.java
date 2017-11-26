package au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
	    use = JsonTypeInfo.Id.NAME,
	    include = JsonTypeInfo.As.PROPERTY,
	    property = "type")
	@JsonSubTypes({
	    @Type(value = ChangeEventNodeAdded.class, name = "NODE_ADDED"),
	    @Type(value = ChangeEventNodeChanged.class, name = "NODE_CHANGED"),
	    @Type(value = ChangeEventNodeDeleted.class, name = "NODE_DELETED") })
public abstract class ChangeEvent {
	private long id;
	private Type type;
	private List<KeyValue> properties = new ArrayList<KeyValue>();
	private List<String> labels = new ArrayList<String>();
	
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
	
	public void addProperty(String key, String value, String oldValue) {
		this.properties.add(new KeyValue(key,value,oldValue));
	}
	
	public Collection<KeyValue> getProperties() {
		return new ArrayList<KeyValue>(this.properties);
	}
	
	public void addLabel(String label) {
		this.labels.add(label);
	}
	
	public void addLabels(Iterable<String> labels) {
		for (String label : labels) {
			this.labels.add(label);
		}
	}
	
	public Iterator<String> getLabels() {
		return this.labels.iterator();
	}

	public static enum Type {
		NODE_ADDED,NODE_DELETED,NODE_CHANGED,
		RELATIONSHIP_ADDED,RELATIOINSHIP_DELETED,RELATIONSHIP_CHANGED,
		NODE_PROPERTY_ADDED,NODE_PROPERTY_DELETED,NODE_PROPERTY_CHANGED,
		RELATIONSHIP_PROPERTY_ADDED,RELATIONSHIP_PROPERTY_DELETED,RELATIONSHIP_PROPERTY_CHANGED
	}

	public boolean isType(String typeString) {
		return this.type == Type.valueOf(typeString);
	}
}
