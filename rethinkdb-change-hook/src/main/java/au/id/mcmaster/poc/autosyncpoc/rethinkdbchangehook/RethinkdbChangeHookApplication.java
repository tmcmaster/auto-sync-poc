package au.id.mcmaster.poc.autosyncpoc.rethinkdbchangehook;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeAdded;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeChanged;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeDeleted;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.service.RedisService;

@SpringBootApplication
public class RethinkdbChangeHookApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(RethinkdbChangeHookApplication.class, args);
	}
	
	@Override
    public void run(String... args) throws Exception {
		RedisService redisService = new RedisService(RedisService.Topics.INCOMING);
		
//		ChangeEventNodeChanged changeEventNodeChanged = new ChangeEventNodeChanged(1052);
//		changeEventNodeChanged.addProperty("aaa", "AAAA",null);
//		changeEventNodeChanged.addProperty("bbb", "BBBB",null);
//		redisService.sendChangeEvent(changeEventNodeChanged);
		
//		ChangeEventNodeAdded changeEventNodeAdded = new ChangeEventNodeAdded();
//		changeEventNodeAdded.addProperty("a", "Aaaa",null);
//		changeEventNodeAdded.addProperty("b", "Bbbb",null);
//		redisService.sendChangeEvent(changeEventNodeAdded);

		ChangeEventNodeDeleted changeEventNodeDeleted = new ChangeEventNodeDeleted(91);
		redisService.sendChangeEvent(changeEventNodeDeleted);

    }
}
