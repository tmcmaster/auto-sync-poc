package au.id.mcmaster.poc.autosyncpoc.neo4jworker;

import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Statement;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.springframework.stereotype.Service;

import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEvent;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventCreationReceipt;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeAdded;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeChanged;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeDeleted;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeMetadata;

@Service
public class Neo4jService {
	private Driver driver;
	Session session;
	
	public Neo4jService() {
		this.driver = GraphDatabase.driver("bolt://localhost:7687");
		session = driver.session();
	}
	
	private String getPropertyString(ChangeEvent changeEvent) {
		return "{" + changeEvent.getPayload().getProperties().stream()
					.map(entry -> entry.getKey() + ":'" + entry.getValue() + "'")
					.collect(Collectors.joining(", ")) + "}";
	}

	private String getSourceProperties(ChangeEventNodeAdded changeEvent) {
		return String.format("{sourceId:'%s',sourceEntity:'%s',sourceSystem:'%s'}", 
				changeEvent.getMetadata().getSourceId(),
				changeEvent.getMetadata().getSourceEntity(),
				changeEvent.getMetadata().getSourceSystem());
	}
	
	private void executeCypher(String cyperString) {
		System.out.println("--- Cypher String: " + cyperString);		

		Transaction transaction = session.beginTransaction();
		Statement statement = new Statement(cyperString);
		StatementResult result = transaction.run(statement);
		if (result.hasNext()) {
			Record record = result.next();
			System.out.println("--- Result: " + record.asMap());			
		}
		transaction.success();
		transaction.close();
	}
	
	@PreDestroy
	public void cleanUp() throws Exception {
		driver.close();
	}

	public void nodeAdded(ChangeEventNodeAdded changeEvent)
	{
		//String propertyString = getPropertyString(changeEvent);
		//String cypher = String.format("MERGE (node %s)",propertyString);
		String nodeProperties = getPropertyString(changeEvent);
		String sourceProperties = getSourceProperties(changeEvent);
		String cypher = String.format("MERGE (node %s)-[:SYNC]->(source:Source %s)", nodeProperties, sourceProperties);
		executeCypher(cypher);
	}

	public void nodeDeleted(ChangeEventNodeDeleted changeEvent)
	{
		long id = changeEvent.getId();
		String cypher = String.format("MATCH (node)-[r]->(source) WHERE id(node)=%s DELETE r,node,source", id);
		executeCypher(cypher);
	}

	public void nodeChanged(ChangeEventNodeChanged changeEvent)
	{
		long id = changeEvent.getId();
		String propertyString = getPropertyString(changeEvent);
		String cypher = String.format("MERGE (node) WHERE id(node)=%s SET node += %s return node", id,propertyString);
		executeCypher(cypher);
	}

	// This need to create a new node and a relationship
	public void nodeCreationReceipt(ChangeEventCreationReceipt changeEvent) {
		long id = changeEvent.getId();
		String propertyString = getCreationReceiptPropertyString(changeEvent);
		String cypher = String.format("MATCH (node) WHERE id(node) = %s MERGE (source:Source %s)<-[:SYNC]-(node);", id,propertyString);
		executeCypher(cypher);
	}

	private String getCreationReceiptPropertyString(ChangeEventCreationReceipt changeEvent) {
		ChangeMetadata metadata = changeEvent.getMetadata();
		String sourceId = metadata.getSourceId();
		String sourceEntity = metadata.getSourceEntity();
		String sourceSystem = metadata.getSourceSystem();
		
		return String.format("{uuid:'%s',entity:'%s',system:'%s'}", sourceId, sourceEntity, sourceSystem);
	}
}