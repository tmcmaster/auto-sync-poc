package au.id.mcmaster.poc.autosyncpoc.neo4jworker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEvent;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeAdded;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeDeleted;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.service.RedisService;

@SpringBootApplication
public class Neo4jWorkerApplication implements CommandLineRunner {
	@Autowired
	private MessageConsumer messageConsumer;
	
	public static void main(String[] args) {
		SpringApplication.run(Neo4jWorkerApplication.class, args);
	}
	
	@Override
    public void run(String... args) throws Exception {
		RedisService redisService = new RedisService(RedisService.Topics.INCOMING);
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
	@EventListener(condition="{#changeEvent.isType('NODE_ADDED')}")
	public void handleNodeCreatedEvent(ChangeEventNodeAdded changeEvent) {
		System.out.println("-- Node Created " + changeEvent);
	}
	
	@EventListener(condition="{#changeEvent.isType('NODE_DELETED')}")
	public void handleNodeDeletedEvent(ChangeEventNodeDeleted changeEvent) {
		System.out.println("-- Node Deleted " + changeEvent.isType("NODE_DELETED"));
	}
}