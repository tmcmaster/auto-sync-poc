package au.id.mcmaster.poc.autosyncpoc.neo4jworker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEvent;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventCreationReceipt;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeAdded;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeChanged;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeDeleted;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.service.RedisService;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages={"au.id.mcmaster.poc.autosyncpoc"})
public class Neo4jWorkerApplication implements CommandLineRunner {
	@Autowired
	private RedisService redisService;

	@Autowired
	private MessageConsumer messageConsumer;
	
	public static void main(String[] args) {
		SpringApplication.run(Neo4jWorkerApplication.class, args);
	}
	
	@Override
    public void run(String... args) throws Exception {
		redisService.registerListener(messageConsumer);
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
	public void handleNodeChangedEvent(ChangeEventNodeChanged changeEvent) {
		log.debug("-- Node Created " + changeEvent);
		neo4jService.nodeChanged(changeEvent);
	}
	
	@EventListener(condition="{#changeEvent.isType('NODE_DELETED')}")
	public void handleNodeDeletedEvent(ChangeEventNodeDeleted changeEvent) {
		log.debug("-- Node Deleted " + changeEvent.isType("NODE_DELETED"));
		neo4jService.nodeDeleted(changeEvent);
	}

	@EventListener(condition="{#changeEvent.isType('NODE_CREATION_RECEIPT')}")
	public void handleCreationReceiptEvent(ChangeEventCreationReceipt changeEvent) {
		log.debug("-- Node Deleted " + changeEvent.isType("NODE_CREATION_RECEIPT"));
		neo4jService.nodeCreationReceipt(changeEvent);
	}
}