package au.id.mcmaster.poc.autosyncpoc.neo4jchangehook;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.event.LabelEntry;
import org.neo4j.graphdb.event.PropertyEntry;
import org.neo4j.graphdb.event.TransactionData;

import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEvent;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeAdded;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeChanged;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeDeleted;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventRelationshipAdded;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventRelationshipChanged;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventRelationshipDeleted;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeMetadata;

public class TransactionDataTransformer {

	public Collection<ChangeEvent> process(TransactionData transactionData, GraphDatabaseService gds) {
		Map<Long,ChangeEvent> changeEvents = new HashMap<Long,ChangeEvent>();

		// added nodes
		System.out.println("------**-- processing added nodes.");
		for (Node node : transactionData.createdNodes()) {
			System.out.println("------**-- Node has been created: " + node.getId());
//			if (!nodeIsSourceNode(node)) {
//				System.out.println("------------ Node has no Source label: " + node.getId());
				ChangeEvent changeEvent = new ChangeEventNodeAdded(node.getId());
				changeEvents.put(node.getId(), changeEvent);
//			}
//			else
//			{
//				System.out.println("------------ Node has Source label: " + node.getId());
//			}
	    }
		
		// deleted nodes
		System.out.println("------**-- processing deleted nodes.");
		for (Node node : transactionData.deletedNodes()) {
			ChangeEvent changeEvent = new ChangeEventNodeDeleted(node.getId());
			changeEvents.put(node.getId(), changeEvent);
	    }
		
		// relationship added
		for (Relationship relationship : transactionData.createdRelationships()) {
			ChangeEvent changeEvent = new ChangeEventRelationshipAdded(relationship.getId());
			String relationshipType = relationship.getType().name();
			changeEvent.getPayload().addLabel(relationshipType);
			addMetadataToStartNode(relationship, changeEvents, gds);
			changeEvents.put(relationship.getId(), changeEvent);
	    }
		
		// deleted relationship
		for (Relationship relationship : transactionData.deletedRelationships()) {
			ChangeEvent changeEvent = new ChangeEventRelationshipDeleted(relationship.getId());
			changeEvents.put(relationship.getId(), changeEvent);
	    }

		// node property changes
		for (PropertyEntry<Node> propertyEntry : transactionData.assignedNodeProperties()) {
			Node node = propertyEntry.entity();
//			if (!nodeIsSourceNode(node)) {
				String key = propertyEntry.key();
				Object value = propertyEntry.value();
				Object oldValue = propertyEntry.previouslyCommitedValue();
				String valueString = (value == null ? null : value.toString());
				String oldValueString = (oldValue == null ? null : oldValue.toString());
				ChangeEvent changeEvent = changeEvents.get(node.getId());
				if (changeEvent == null) {
					changeEvent = new ChangeEventNodeChanged(node.getId());
					changeEvents.put(node.getId(), changeEvent);
				}
				changeEvent.getPayload().addProperty(key, valueString, oldValueString);
//			}
		}
		
		// relationship property changes
		for (PropertyEntry<Relationship> propertyEntry : transactionData.assignedRelationshipProperties()) {
			Relationship relationship = propertyEntry.entity();
			String key = propertyEntry.key();
			Object value = propertyEntry.value();
			Object oldValue = propertyEntry.previouslyCommitedValue();
			String valueString = (value == null ? null : value.toString());
			String oldValueString = (oldValue == null ? null : oldValue.toString());
			ChangeEvent changeEvent = changeEvents.get(relationship.getId());
			if (changeEvent == null) {
				changeEvent = new ChangeEventRelationshipChanged(relationship.getId());
				changeEvents.put(relationship.getId(), changeEvent);
			}
			changeEvent.getPayload().addProperty(key, valueString, oldValueString);
		}
		
		for (LabelEntry labelEntry : transactionData.assignedLabels())
		{
			Node node = labelEntry.node();
			String label = labelEntry.label().name();
			ChangeEvent changeEvent = changeEvents.get(node.getId());
			if (changeEvent == null) {
				changeEvent = new ChangeEventNodeChanged(node.getId());
				changeEvents.put(node.getId(), changeEvent);
			}
			changeEvent.getPayload().addLabel(label);
		}
		
		return changeEvents.values();
	}

	private void addMetadataToStartNode(Relationship relationship, Map<Long, ChangeEvent> changeEvents, GraphDatabaseService gds) {
		Node endNode = relationship.getEndNode();
		Node startNode = relationship.getEndNode();
		ChangeEvent addedNodeChangeEvent = changeEvents.get(startNode.getId());
		if (addedNodeChangeEvent != null) {
			long endNodeId = endNode.getId();
			ChangeMetadata changeMetadata = getMetadata(endNodeId, gds);
			addedNodeChangeEvent.getMetadata().setSourceId(changeMetadata.getSourceId());
			addedNodeChangeEvent.getMetadata().setSourceEntity(changeMetadata.getSourceEntity());
			addedNodeChangeEvent.getMetadata().setSourceSystem(changeMetadata.getSourceSystem());
		}
	}

	private ChangeMetadata getMetadata(long sourceNodeId, GraphDatabaseService gds) {
		ChangeMetadata changeMetadata = new ChangeMetadata();
		try
		{
			Transaction txn = gds.beginTx();
			String cypher = String.format("MATCH (node)-[:SOURCE]-(source:Source) WHERE id(node) = %s return source", sourceNodeId);
			Result result = gds.execute(cypher);
			if (result.hasNext()) {
				Map<String,Object> metadataMap = result.next();
				String sourceId = metadataMap.get("sourceId").toString();
				String sourceEntity = metadataMap.get("sourceEntity").toString();
				String sourceSystem = metadataMap.get("sourceSystem").toString();
				changeMetadata.setSourceId(sourceId);
				changeMetadata.setSourceEntity(sourceEntity);
				changeMetadata.setSourceSystem(sourceSystem);
			}
			txn.success();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return changeMetadata;
	}

//	private Iterable<ChangeEvent> getNodeChangedEvents(TransactionData transactionData) {
//		Map<Long,ChangeEvent> changeEvents = new HashMap<Long,ChangeEvent>();
//		for (PropertyEntry<Node> propertyEntry : transactionData.assignedNodeProperties()) {
//			Node node = propertyEntry.entity();
//			String key = propertyEntry.key();
//			Object value = propertyEntry.value();
//			Object oldValue = propertyEntry.previouslyCommitedValue();
//			String valueString = (value == null ? null : value.toString());
//			String oldValueString = (oldValue == null ? null : oldValue.toString());
//			ChangeEvent changeEvent = changeEvents.get(node.getId());
//			if (changeEvent == null) {
//				changeEvent = new ChangeEventNodeChanged(node.getId());
//				changeEvents.put(node.getId(), changeEvent);
//			}
//			changeEvent.getPayload().addProperty(key, valueString, oldValueString);
//		}
//		
//		return changeEvents.values();
//	}
	
//	private Iterable<ChangeEvent> getRelationshipChangedEvents(TransactionData transactionData) {
//		Map<Long,ChangeEvent> changeEvents = new HashMap<Long,ChangeEvent>();
//		for (PropertyEntry<Relationship> propertyEntry : transactionData.assignedRelationshipProperties()) {
//			Relationship relationship = propertyEntry.entity();
//			String key = propertyEntry.key();
//			Object value = propertyEntry.value();
//			Object oldValue = propertyEntry.previouslyCommitedValue();
//			String valueString = (value == null ? null : value.toString());
//			String oldValueString = (oldValue == null ? null : oldValue.toString());
//			ChangeEvent changeEvent = changeEvents.get(relationship.getId());
//			if (changeEvent == null) {
//				changeEvent = new ChangeEventRelationshipChanged(relationship.getId());
//				changeEvents.put(relationship.getId(), changeEvent);
//			}
//			changeEvent.getPayload().addProperty(key, valueString, oldValueString);
//		}
//		return changeEvents.values();
//	}
	
    private boolean nodeIsSourceNode(Node node) {
    		Iterator<Label> labels = node.getLabels().iterator();

		while (labels.hasNext()) {
			 String label = labels.next().name();
			 System.out.println("------------ Node labels: " + label);
			 if ("Source".equals(label))
			 {
				 return true;
			 }
		}
	
		return false;
	}
}
