package au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto;


public class ChangeEventNodeChanged extends ChangeEventNodeAdded {
	public ChangeEventNodeChanged() {
		this(0);
	}
	public ChangeEventNodeChanged(long id) {
		super(Type.NODE_CHANGED ,id);
	}
}
