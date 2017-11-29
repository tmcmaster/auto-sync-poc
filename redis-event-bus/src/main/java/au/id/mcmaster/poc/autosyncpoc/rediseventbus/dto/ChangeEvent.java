package au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
	    use = JsonTypeInfo.Id.NAME,
	    include = JsonTypeInfo.As.EXISTING_PROPERTY,
	    property = "type")
	@JsonSubTypes({
	    @Type(value = ChangeEventNodeAdded.class, name = "NODE_ADDED"),
	    @Type(value = ChangeEventCreationReceipt.class, name = "NODE_CREATION_RECEIPT"),
	    @Type(value = ChangeEventNodeChanged.class, name = "NODE_CHANGED"),
	    @Type(value = ChangeEventNodeDeleted.class, name = "NODE_DELETED"),
	    @Type(value = ChangeEventRelationshipAdded.class, name = "RELATIONSHIP_ADDED"),
	    @Type(value = ChangeEventRelationshipChanged.class, name = "RELATIONSHIP_CHANGED"),
	    @Type(value = ChangeEventRelationshipDeleted.class, name = "RELATIOINSHIP_DELETED")
	})
public abstract class ChangeEvent {
	private Type type;
	private long id;
	private ChangeMetadata metadata;
	private ChangePayload payload;
		
	protected ChangeEvent(Type type, long id) {
		super();
		this.type = type;
		this.id = id;
		this.metadata = new ChangeMetadata();
		this.payload = new ChangePayload();
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
	
	public ChangeMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(ChangeMetadata metadata) {
		this.metadata = metadata;
	}

	public ChangePayload getPayload() {
		return payload;
	}

	public void setPayload(ChangePayload payload) {
		this.payload = payload;
	}

	public static enum Type {
		NODE_ADDED,NODE_DELETED,NODE_CHANGED,
		RELATIONSHIP_ADDED,RELATIOINSHIP_DELETED,RELATIONSHIP_CHANGED,
		//NODE_PROPERTY_ADDED,NODE_PROPERTY_DELETED,NODE_PROPERTY_CHANGED,
		//RELATIONSHIP_PROPERTY_ADDED,RELATIONSHIP_PROPERTY_DELETED,RELATIONSHIP_PROPERTY_CHANGED,
		NODE_CREATION_RECEIPT,RELATIONSHIP_CREATION_RECEIPT
	}

	public boolean isType(String typeString) {
		return this.type == Type.valueOf(typeString);
	}
}
