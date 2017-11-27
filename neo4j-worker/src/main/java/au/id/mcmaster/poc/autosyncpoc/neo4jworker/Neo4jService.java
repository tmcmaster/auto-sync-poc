package au.id.mcmaster.poc.autosyncpoc.neo4jworker;

import java.util.Iterator;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;

import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.ogm.model.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEvent;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventCreationReceipt;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeAdded;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeChanged;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeDeleted;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeMetadata;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.KeyValue;

@Service
public class Neo4jService {
	private Driver driver;
	
	public Neo4jService() {
		this.driver = GraphDatabase.driver("bolt://localhost:7687");
	}
	
	private String getPropertyString(ChangeEvent changeEvent) {
		return "{" + changeEvent.getPayload().getProperties().stream()
					.map(entry -> entry.getKey() + ":'" + entry.getValue() + "'")
					.collect(Collectors.joining(", ")) + "}";
	}
	
	private void executeCypher(String cyperString) {
		System.out.println("--- Cypher String: " + cyperString);
		
		Session session = driver.session();
		StatementResult result = session.run(cyperString);
		if (result.hasNext()) {
			Record record = result.next();
			System.out.println("--- Result: " + record.asMap());			
		}
		session.close();
	}
	
	@PreDestroy
	public void cleanUp() throws Exception {
		driver.close();
	}

	public void nodeAdded(ChangeEventNodeAdded changeEvent)
	{
		String propertyString = getPropertyString(changeEvent);
		String cypher = String.format("CREATE (node %s)",propertyString);
		executeCypher(cypher);
	}

	public void nodeDeleted(ChangeEventNodeDeleted changeEvent)
	{
		long id = changeEvent.getId();
		String cypher = String.format("MATCH (node) WHERE id(node)=%s DELETE node", id);
		executeCypher(cypher);
	}

	public void nodeChanged(ChangeEventNodeChanged changeEvent)
	{
		long id = changeEvent.getId();
		String propertyString = getPropertyString(changeEvent);
		String cypher = String.format("MATCH (node) WHERE id(node)=%s SET node += %s return node", id,propertyString);
		executeCypher(cypher);
	}

	// This need to create a new node and a relationship
	public void nodeCreationReceipt(ChangeEventCreationReceipt changeEvent) {
		long id = changeEvent.getId();
		String propertyString = getCreationReceiptPropertyString(changeEvent);
		String cypher = String.format("MATCH (node) WHERE id(node) = %s CREATE (source:Source %s)<-[:SYNC]-(node);", id,propertyString);
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