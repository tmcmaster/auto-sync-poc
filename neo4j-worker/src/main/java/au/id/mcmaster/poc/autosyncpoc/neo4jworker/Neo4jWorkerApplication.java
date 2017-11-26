package au.id.mcmaster.poc.autosyncpoc.neo4jworker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEvent;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeAdded;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeChanged;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeDeleted;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.service.RedisService;

@SpringBootApplication
public class Neo4jWorkerApplication implements CommandLineRunner {
	@Autowired
	private MessageConsumer messageConsumer;
	
	@Autowired
	Neo4jService neo4jService;

	public static void main(String[] args) {
		SpringApplication.run(Neo4jWorkerApplication.class, args);
	}
	
	@Override
    public void run(String... args) throws Exception {
		RedisService redisService = new RedisService(RedisService.Topics.INCOMING);
		redisService.registerListener(messageConsumer);
		//neo4jService.nodeChanged(new ChangeEventNodeChanged(1052) {{addProperty("bbb", "BBBBBB", "AAA");}});
    }
}

@Component
class MessageConsumer implements RedisService.ChangeEventListener
{
	@Autowired
	private ApplicationEventPublisher publisher;

	@Override
	public void process(ChangeEvent changeEvent) {
		publisher.publishEvent(changeEvent);
	}	
}

@Component
class Worker {	
	public static final Logger log = LoggerFactory.getLogger(Worker.class);

	@Autowired
	private Neo4jService neo4jService;
	
	@EventListener(condition="{#changeEvent.isType('NODE_ADDED')}")
	public void handleNodeCreatedEvent(ChangeEventNodeAdded changeEvent) {
		log.debug("-- Node Created " + changeEvent);
		neo4jService.nodeAdded(changeEvent);
	}
	
	@EventListener(condition="{#changeEvent.isType('NODE_CHANGED')}")
	public void handleNodeCreatedEvent(ChangeEventNodeChanged changeEvent) {
		log.debug("-- Node Created " + changeEvent);
		neo4jService.nodeChanged(changeEvent);
	}
	
	@EventListener(condition="{#changeEvent.isType('NODE_DELETED')}")
	public void handleNodeDeletedEvent(ChangeEventNodeDeleted changeEvent) {
		log.debug("-- Node Deleted " + changeEvent.isType("NODE_DELETED"));
		neo4jService.nodeDeleted(changeEvent);
	}
}