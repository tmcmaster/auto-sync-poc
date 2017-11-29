package au.id.mcmaster.poc.autosyncpoc.neo4jchangehook;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.event.LabelEntry;
import org.neo4j.graphdb.event.PropertyEntry;
import org.neo4j.graphdb.event.TransactionData;
import org.neo4j.graphdb.event.TransactionEventHandler;
import org.neo4j.graphdb.schema.ConstraintDefinition;
import org.neo4j.graphdb.schema.ConstraintType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Lists;

import au.id.mcmaster.poc.autosyncpoc.rediseventbus.service.RedisService;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEvent;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeAdded;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeChanged;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeDeleted;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventRelationshipAdded;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventRelationshipChanged;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventRelationshipDeleted;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChangeListenerEventHandler implements TransactionEventHandler<String> {
    private final static Logger logger = Logger.getLogger(ChangeListenerEventHandler.class.getName());
    
    private GraphDatabaseService gds;
    private RedisService redisService;
    private TransactionDataTransformer transactionDataTransformer;
    
    public ChangeListenerEventHandler()
    {
        this(null);
        logger.log(Level.FINE, "--+-- Creating the ExampleEventHandler : Constructor -----");
    }
    
    public ChangeListenerEventHandler(GraphDatabaseService gds) {
        logger.log(Level.FINE, "--+-- Creating the ExampleEventHandler : Constructor -----");
        this.gds = gds;
        this.redisService = new RedisService(RedisService.Topics.OUTGOING, "localhost");
    }
    @Override
    public String beforeCommit(TransactionData transactionData) throws Exception {
    		System.out.println("--+-- ExampleEventHandler : beforeCommit -----");
    		try
    		{
    			Iterable<ChangeEvent> changeEvents = new TransactionDataTransformer().process(transactionData);
    			for (ChangeEvent changeEvent : changeEvents) {
        			redisService.sendChangeEvent(changeEvent);
        		}
    		}
    		catch (Exception e) 
    		{
    			e.printStackTrace();
    		}
//    		for (Node node : transactionData.createdNodes()) {
//	    		if (!nodeIsSourceNode(node)) {
//				System.out.println(String.format("------------ Node has no Source label: %s - %s", node.getId(), nodeIsSourceNode(node)));
//			}
//    		}
    		
    		return "Before transaction commit was successful.";
    }

    @Override
    public void afterCommit(TransactionData transactionData, String result) {
        System.out.println("--+-- ExampleEventHandler : afterCommit -----");
    }
    
    public void afterCommitHold(TransactionData transactionData, String result) {
        System.out.println("---- ExampleEventHandler : afterCommit -----");
        
        // may need to manage a list as well, if there is a need to specify an event order.
		Map<Long,ChangeEvent> changeEvents = new HashMap<Long,ChangeEvent>();

		// added nodes
		System.out.println("------**-- Processing created Nodes.");
		for (Node node : transactionData.createdNodes()) {
			System.out.println("------**-- Node has been created: " + node.getId());
			if (!nodeIsSourceNode(node)) {
				System.out.println("------------ Node has no Source label: " + node.getId());
				ChangeEvent changeEvent = new ChangeEventNodeAdded(node.getId());
				changeEvents.put(node.getId(), changeEvent);
			}
			else
			{
				System.out.println("------------ Node has Source label: " + node.getId());
			}
			System.out.flush();
	    }
		System.out.println("------**-- Created Nodes have been processed");
		
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

        for (ChangeEvent changeEvent : changeEvents.values()) {
    			redisService.sendChangeEvent(changeEvent);
    		}
    }
    
    private boolean nodeIsSourceNode(Node node) {
    		//Iterator<Label> labels = node.getLabels().iterator();
    	
//		while (labels.hasNext()) {
//			 String label = labels.next().name();
//			 System.out.println("------------ Node labels: " + label);
//			 if ("Source".equals(label))
//			 {
//				 return true;
//			 }
//		}
		
		return false;
	}

	@Override
    public void afterRollback(TransactionData transactionData, String result) {
    		logger.log(Level.FINE, "Something bad happend, Harry: " + result);
    }
}
