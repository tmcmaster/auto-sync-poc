package au.id.mcmaster.poc.autosyncpoc.neo4jrest.dto;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

abstract public class BasePOJO {
	@GraphId
	private Long id;
	private String sourceSystem;
	private String sourceUUID;
	protected BasePOJO() {
		
	}
	public BasePOJO(long id, String sourceSystem, String sourceUUID) {
		this.id = id;
		this.sourceSystem = sourceSystem;
		this.sourceUUID = sourceUUID;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getSourceSystem() {
		return sourceSystem;
	}
	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}
	public String getSourceUUID() {
		return sourceUUID;
	}
	public void setSourceUUID(String sourceUUID) {
		this.sourceUUID = sourceUUID;
	}
}
