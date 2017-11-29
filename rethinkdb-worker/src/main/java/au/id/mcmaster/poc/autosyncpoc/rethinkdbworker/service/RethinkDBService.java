package au.id.mcmaster.poc.autosyncpoc.rethinkdbworker.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEvent;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventCreationReceipt;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeAdded;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeChanged;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeDeleted;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.KeyValue;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.service.RedisService;

@Service
public class RethinkDBService {
	private static final RethinkDB r = RethinkDB.r;
	private String instanceName = "poc";
	
	private RedisService redisService;
	
	public RethinkDBService() {
		this.redisService = new RedisService(RedisService.Topics.INCOMING, "localhost");
	}
	
	public void nodeAdded(ChangeEventNodeAdded changeEvent)
	{
		Map<String,String> properties = getProperties(changeEvent.getPayload().getProperties());;
		String sourceId = insert("contact", properties);
		sendCreationReceipt(changeEvent.getId(), "contact", sourceId);
	}

	public void nodeDeleted(ChangeEventNodeDeleted changeEvent)
	{
	}

	public void nodeChanged(ChangeEventNodeChanged changeEvent)
	{
		
	}
	
	private void sendCreationReceipt(long id, String entity, String sourceId) {
		ChangeEventCreationReceipt changeEvent = new ChangeEventCreationReceipt();
		changeEvent.setId(id);
		changeEvent.getMetadata().setSourceId(sourceId);
		changeEvent.getMetadata().setSourceEntity(entity);
		changeEvent.getMetadata().setSourceSystem("RethinkDB");
		redisService.sendChangeEvent(changeEvent);
	}

	private Map<String, String> getProperties(Collection<KeyValue> properties) {
		Map<String,String> propertyMap = new HashMap<String,String>();
		for (KeyValue keyValue : properties) {
			propertyMap.put(keyValue.getKey(), keyValue.getValue());
		}
		return propertyMap;
	}
	
	private String insert(String entity, Map<String,? extends Object> dto) {
		Connection connection = RethinkDB.r.connection().hostname("localhost").connect();
        HashMap<?, ?> result = r.db(instanceName).table(entity).insert(dto).run(connection);
        Object idObject = result.get("generated_keys");
        String id = ((List<String>) idObject).get(0);
        System.out.println(result);
        connection.close();
        return id;
	}
	
	public static interface ChangeEventListener
	{
		public void process(ChangeEvent changeEvent);
	}
}
