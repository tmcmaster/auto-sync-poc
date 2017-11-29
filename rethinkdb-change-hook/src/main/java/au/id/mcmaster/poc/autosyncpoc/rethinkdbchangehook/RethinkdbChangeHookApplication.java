package au.id.mcmaster.poc.autosyncpoc.rethinkdbchangehook;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.gen.ast.Changes;
import com.rethinkdb.net.Connection;
import com.rethinkdb.net.Cursor;

import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEvent;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeAdded;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeChanged;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.service.RedisService;
import au.id.mcmaster.poc.autosyncpoc.rethinkdbchangehook.service.RethinkDBService;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages={"au.id.mcmaster.poc.autosyncpoc"})
public class RethinkdbChangeHookApplication implements CommandLineRunner {
	
	@Autowired
	private RethinkDBService rethinkDBService;
	
	@Autowired
	private EventProcessor eventProcessor;
	
	public static void main(String[] args) {
		SpringApplication.run(RethinkdbChangeHookApplication.class, args);
	}
	
	@Override
    public void run(String... args) throws Exception {
		rethinkDBService.registerListener(eventProcessor);
    }
}

@Component
class EventProcessor implements RethinkDBService.ChangeEventListener {

	@Autowired
	private RedisService redisService;

	@Override
	public void process(ChangeEvent changeEvent) {
		redisService.sendChangeEvent(changeEvent);
	}
	
}