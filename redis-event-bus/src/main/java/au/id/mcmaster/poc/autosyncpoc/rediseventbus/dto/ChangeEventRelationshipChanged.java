package au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto;

public class ChangeEventRelationshipChanged extends ChangeEventRelationshipAdded {
	public ChangeEventRelationshipChanged() {
		this(0);
	}
	public ChangeEventRelationshipChanged(long id) {
		super(Type.RELATIONSHIP_CHANGED, id);
	}
}