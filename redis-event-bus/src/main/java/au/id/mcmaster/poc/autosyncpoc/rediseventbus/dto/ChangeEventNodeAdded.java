package au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto;

public class ChangeEventNodeAdded extends ChangeEvent {
	public ChangeEventNodeAdded() {
		this(0);
	}

	public ChangeEventNodeAdded(long id) {
		super(Type.NODE_ADDED, id);
	}
	
	public ChangeEventNodeAdded(Type type, long id) {
		super(type,id);
	}
}
