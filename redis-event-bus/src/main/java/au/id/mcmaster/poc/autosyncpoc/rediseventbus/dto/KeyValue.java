package au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto;

public class KeyValue {
	private String key;
	private String value;
	private String oldValue;
	
	public KeyValue() {
		
	}
	public KeyValue(String key, String value, String oldValue) {
		super();
		this.key = key;
		this.value = value;
		this.oldValue = oldValue;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String value) {
		this.oldValue = value;
	}
}
