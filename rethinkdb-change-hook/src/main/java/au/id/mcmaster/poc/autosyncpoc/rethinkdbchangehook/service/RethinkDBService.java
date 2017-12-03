package au.id.mcmaster.poc.autosyncpoc.rethinkdbchangehook.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;

import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEvent;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeAdded;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeChanged;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeDeleted;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.KeyValue;

@Service
public class RethinkDBService {
	
	public RethinkDBService()
	{
		
	}
	
	public void registerListener(final ChangeEventListener changeEventListener) {
		Connection connection = RethinkDB.r.connection().hostname("localhost").connect();
		Cursor<HashMap<String,Object>> cursor = RethinkDB.r.db("poc").table("contact").changes().run(connection);
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (HashMap<String,Object> change : cursor) {
					ChangeEvent changeEvent = createChangeEvent(change, "contact", "RethinkDB");
					changeEventListener.process(changeEvent);
				}
			}

		}).start();
	}

	@SuppressWarnings("unchecked")
	private ChangeEvent createChangeEvent(HashMap<String, Object> change, String sourceEntity, String sourceSystem) {
		ChangeEvent changeEvent;
		HashMap<String,Object> newValue = (HashMap<String,Object>)change.get("new_val");
		HashMap<String,Object> oldValue = (HashMap<String,Object>)change.get("old_val");
		
		String sourceId = (newValue == null ? getSourceId(oldValue) : getSourceId(newValue));
		
		if (oldValue == null) {
			changeEvent = new ChangeEventNodeAdded();
		} else if (newValue == null) {
			changeEvent = new ChangeEventNodeDeleted();			
		} else {
			changeEvent = new ChangeEventNodeChanged();						
		}
		
		changeEvent.getMetadata().setSourceId(sourceId);
		changeEvent.getMetadata().setSourceEntity(sourceEntity);
		changeEvent.getMetadata().setSourceSystem(sourceSystem);
		
		if (newValue != null) {
			List<KeyValue> properties = getProperties(newValue, oldValue);
			for (KeyValue keyValue : properties) {
				changeEvent.getPayload().addProperty(keyValue);
			}
		}
		return changeEvent;
	}
	
	private List<KeyValue> getProperties(HashMap<String, Object> newValue, HashMap<String, Object> oldValue) {
		Map<String, Object> map = (newValue == null ? oldValue : newValue);
		
		return newValue.keySet().stream()
				.filter(key -> (!"id".equals(key) && (map.get(key) == null || map.get(key) instanceof String)))
				.map(key -> new KeyValue(key,(newValue == null ? null : (String)newValue.get(key)), (oldValue == null ? null : (String)oldValue.get(key))))
				.collect(Collectors.toList());
	}

	private String getSourceId(HashMap<String, Object> map) {
		return (String)map.get("id");
	}


	public static interface ChangeEventListener
	{
		public void process(ChangeEvent changeEvent);
	}
}
