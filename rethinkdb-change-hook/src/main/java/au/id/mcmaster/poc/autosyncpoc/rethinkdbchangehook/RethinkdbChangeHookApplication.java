package au.id.mcmaster.poc.autosyncpoc.rethinkdbchangehook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeAdded;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeChanged;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.service.RedisService;

@SpringBootApplication
public class RethinkdbChangeHookApplication implements CommandLineRunner {

	@Autowired
	private RedisService redisService;
	
	public static void main(String[] args) {
		SpringApplication.run(RethinkdbChangeHookApplication.class, args);
	}
	
	@Override
    public void run(String... args) throws Exception {
		ChangeEventNodeChanged changeEventNodeChanged = new ChangeEventNodeChanged(89);
		changeEventNodeChanged.getPayload().addProperty("firstname", "Jayne",null);
		changeEventNodeChanged.getPayload().addProperty("lastname", "Cobb",null);
		changeEventNodeChanged.getPayload().addProperty("phone", "0404 404 404",null);
		redisService.sendChangeEvent(changeEventNodeChanged);
		
//		ChangeEventNodeAdded changeEventNodeAdded = new ChangeEventNodeAdded();
//		changeEventNodeAdded.getPayload().addProperty("firstname", "Jayne",null);
//		changeEventNodeAdded.getPayload().addProperty("lastname", "Cobb",null);
//		changeEventNodeAdded.getMetadata().setSourceEntity("Contact");
//		changeEventNodeAdded.getMetadata().setSourceEntity("RethinkDB");
//		redisService.sendChangeEvent(changeEventNodeAdded);

//		ChangeEventNodeDeleted changeEventNodeDeleted = new ChangeEventNodeDeleted(91);
//		redisService.sendChangeEvent(changeEventNodeDeleted);

    }
}
