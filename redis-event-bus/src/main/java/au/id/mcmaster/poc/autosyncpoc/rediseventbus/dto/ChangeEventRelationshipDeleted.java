package au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto;

public class ChangeEventRelationshipDeleted extends ChangeEvent {
	public ChangeEventRelationshipDeleted() {
		this(0);
	}
	public ChangeEventRelationshipDeleted(long id) {
		super(Type.RELATIOINSHIP_DELETED,id);
	}
}
