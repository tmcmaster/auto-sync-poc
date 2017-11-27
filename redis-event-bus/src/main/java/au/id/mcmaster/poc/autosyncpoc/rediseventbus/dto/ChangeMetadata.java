package au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto;

public class ChangeMetadata {
	private String sourceId;
	private String sourceEntity;
	private String sourceSystem;
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public String getSourceEntity() {
		return sourceEntity;
	}
	public void setSourceEntity(String sourceEntity) {
		this.sourceEntity = sourceEntity;
	}
	public String getSourceSystem() {
		return sourceSystem;
	}
	public void setSourceSystem(String sourceName) {
		this.sourceSystem = sourceName;
	}
}
