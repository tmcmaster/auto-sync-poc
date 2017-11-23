package au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto;

public class ChangeEventRelationshipAdded extends ChangeEvent {
	public ChangeEventRelationshipAdded() {
		this(0);
	}
	public ChangeEventRelationshipAdded(long id) {
		super(Type.RELATIONSHIP_ADDED,id);
	}
	public ChangeEventRelationshipAdded(Type type, long id) {
		super(type,id);
	}
}
