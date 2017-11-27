package au.id.mcmaster.poc.autosyncpoc.rethinkdbworker.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeAdded;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeChanged;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeDeleted;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.KeyValue;

@Service
public class RethinkDBService {
	private static final RethinkDB r = RethinkDB.r;
	private String instanceName = "poc";
	
	public RethinkDBService() {
	}
	
	public void nodeAdded(ChangeEventNodeAdded changeEvent)
	{
		Map<String,String> properties = getProperties(changeEvent.getPayload().getProperties());;
		insert("contact", properties);
	}

	public void nodeDeleted(ChangeEventNodeDeleted changeEvent)
	{
	}

	public void nodeChanged(ChangeEventNodeChanged changeEvent)
	{
		
	}

	private Map<String, String> getProperties(Collection<KeyValue> properties) {
		Map<String,String> propertyMap = new HashMap<String,String>();
		for (KeyValue keyValue : properties) {
			propertyMap.put(keyValue.getKey(), keyValue.getValue());
		}
		return propertyMap;
	}
	
	private void insert(String entity, Map<String,? extends Object> dto) {
		Connection connection = RethinkDB.r.connection().hostname("localhost").connect();
        HashMap<?, ?> result = r.db(instanceName).table(entity).insert(dto).run(connection);
        System.out.println(result);
        connection.close();

	}
}
