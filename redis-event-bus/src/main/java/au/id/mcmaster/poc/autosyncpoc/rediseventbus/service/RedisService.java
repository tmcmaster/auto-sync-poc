package au.id.mcmaster.poc.autosyncpoc.rediseventbus.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEvent;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeChanged;
import redis.clients.jedis.BinaryJedisPubSub;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

@Service
public class RedisService {
	public static final Logger LOG = LoggerFactory.getLogger(RedisService.class);

	public static interface Topics {
		public static final String OUTGOING = "outgoing-events";
		public static final String INCOMING = "incoming-events";
	}
	
	private ObjectMapper objectMapper;
	private RedisConnectionFactory connectionFactory;
	private String topic;
	
	public RedisService(@Value("${app.redis.topic:default_topic}") String topic, @Value("${app.redis.host:localhost}") String host)
	{
		JedisShardInfo config = new JedisShardInfo(host);
		//RedisStandaloneConfiguration config = new RedisStandaloneConfiguration("localhost",6379);
		this.connectionFactory = new JedisConnectionFactory(config);
		this.topic = topic;
		this.objectMapper = new ObjectMapper();
		this.objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}
	
	public void sendChangeEvent(ChangeEvent changeEvent) {
		try
		{
			String messageString = marshalChangeEvent(changeEvent);
			
			sendMessage(messageString);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Could not send the ChangeEvent", e);
		}
	}
	
	protected ChangeEvent unmarshalChangeEvent(String message) 
		throws JsonParseException, JsonMappingException, IOException
	{
		ChangeEvent object = objectMapper.readValue(message.getBytes(), ChangeEvent.class);
		LOG.debug("ChangeEventNodeChanged: " + object.getClass().getName());
		return object;
	}
	protected String marshalChangeEvent(ChangeEvent changeEvent) 
			throws JsonGenerationException, JsonMappingException, IOException {
		boolean canSerialize = objectMapper.canSerialize(changeEvent.getClass());
		LOG.debug("Can Serialize: " + canSerialize);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		objectMapper.writeValue(baos, changeEvent);
		return baos.toString();
	}

	private void sendMessage(String messageString) {
		StringRedisTemplate template = new StringRedisTemplate(connectionFactory);
		LOG.debug("Sending message to Redis topic: " + messageString);
		template.convertAndSend(this.topic, messageString);
	}

	public void registerListener(ChangeEventListener listener) {
		final ChangeEventAdapter changeEventAdapter = new ChangeEventAdapter(listener);
		final BinaryJedisPubSub binaryJedisPubSub = new BinaryJedisPubSub() {
	    		public void onMessage(byte[] channel, byte[] message) {
	    			LOG.debug("Received <" + new String(message) + ">");
	    			changeEventAdapter.receiveMessage(new String(message));
	    		}
	    	};
	    	final String topicName = this.topic;
		new Thread(new Runnable() {
	    		public void run() {
	        		Jedis jedis = new Jedis("localhost");
	        		jedis.subscribe(binaryJedisPubSub, topicName.getBytes());
	        		jedis.close();
	        	}
	    	}).start();
	}
	
	public static interface ChangeEventListener
	{
		public void process(ChangeEvent changeEvent);
	}
	
	private class ChangeEventAdapter {
	    RedisService.ChangeEventListener changeEventListener;
	    
	    @Autowired
	    public ChangeEventAdapter(RedisService.ChangeEventListener changeEventListener) {
	    		this.changeEventListener = changeEventListener;
	    }

	    @SuppressWarnings("unused")
		public void receiveMessage(String message) {
	    		LOG.debug("Received <" + message + ">");
			try {
				ChangeEvent changeEvent = RedisService.this.unmarshalChangeEvent(message);
				changeEventListener.process(changeEvent);
			} catch (IOException e) {
				throw new RuntimeException("Could not process message: " + message, e);
			}
	        
	    }
	}
}


