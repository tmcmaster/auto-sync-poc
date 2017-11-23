package au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto;

public class ChangeEventNodeDeleted extends ChangeEvent {
	public ChangeEventNodeDeleted() {
		this(0);
	}
	public ChangeEventNodeDeleted(long id) {
		super(Type.NODE_DELETED,id);
	}
}
