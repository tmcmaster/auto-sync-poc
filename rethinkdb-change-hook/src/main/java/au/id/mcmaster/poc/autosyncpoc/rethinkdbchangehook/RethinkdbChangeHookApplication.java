package au.id.mcmaster.poc.autosyncpoc.rethinkdbchangehook;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeAdded;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.service.RedisService;

@SpringBootApplication
public class RethinkdbChangeHookApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(RethinkdbChangeHookApplication.class, args);
	}
	
	@Override
    public void run(String... args) throws Exception {
		RedisService redisService = new RedisService(RedisService.Topics.INCOMING);
		ChangeEventNodeAdded changeEventNodeAdded = new ChangeEventNodeAdded(2223);
		changeEventNodeAdded.addProperty("aaa", "AAA");
		redisService.sendChangeEvent(changeEventNodeAdded);
    }
}
