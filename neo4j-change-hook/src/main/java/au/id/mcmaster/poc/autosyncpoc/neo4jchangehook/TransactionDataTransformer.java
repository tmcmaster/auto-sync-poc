package au.id.mcmaster.poc.autosyncpoc.neo4jchangehook;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.event.PropertyEntry;
import org.neo4j.graphdb.event.TransactionData;

import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEvent;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeAdded;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeChanged;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeDeleted;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventRelationshipAdded;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventRelationshipChanged;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventRelationshipDeleted;

public class TransactionDataTransformer {

	public Iterable<ChangeEvent> process(TransactionData transactionData) {
		Map<Long,ChangeEvent> changeEvents = new HashMap<Long,ChangeEvent>();

		// added nodes
		for (Node node : transactionData.createdNodes()) {
			ChangeEvent changeEvent = new ChangeEventNodeAdded(node.getId());
			changeEvents.put(node.getId(), changeEvent);
	    }
		
		// deleted nodes
		for (Node node : transactionData.deletedNodes()) {
			ChangeEvent changeEvent = new ChangeEventNodeDeleted(node.getId());
			changeEvents.put(node.getId(), changeEvent);
	    }
		
		// relationship added
		for (Relationship relationship : transactionData.createdRelationships()) {
			ChangeEvent changeEvent = new ChangeEventRelationshipAdded(relationship.getId());
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
		return changeEvents.values();
	}

	public Iterable<ChangeEvent> getNodeChangedEvents(TransactionData transactionData) {
		Map<Long,ChangeEvent> changeEvents = new HashMap<Long,ChangeEvent>();
		for (PropertyEntry<Node> propertyEntry : transactionData.assignedNodeProperties()) {
			Node node = propertyEntry.entity();
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
		}
		
		return changeEvents.values();
	}
	
	public Iterable<ChangeEvent> getRelationshipChangedEvents(TransactionData transactionData) {
		Map<Long,ChangeEvent> changeEvents = new HashMap<Long,ChangeEvent>();
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
		return changeEvents.values();
	}
}
