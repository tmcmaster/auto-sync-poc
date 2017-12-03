package au.id.mcmaster.poc.autosyncpoc.rediseventbus;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;

import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventCreationReceipt;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeAdded;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeChanged;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeEventNodeDeleted;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangeMetadata;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto.ChangePayload;
import au.id.mcmaster.poc.autosyncpoc.rediseventbus.service.RedisService;

@SuppressWarnings("unused")
public class TestPublishingMessages implements CommandLineRunner
{
	public static void main(String[] args) {
		SpringApplication.run(TestPublishingMessages.class, args);
	}
	
	private static long TEST_ID = 72;
	private static String TEST_FIRSTNAME = "Jayne";
	private static String TEST_LASTNAME = "Cobb";
	private static String TEST_PHONE = "0404 404 404";
	private static String TEST_EMAIL = "jayne.cobb@firefly.com";
	private static String TEST_UUID = "f77a368b-cf71-4a1c-a3ef-503711335e2d";
	private static String TEST_ENTITY = "Contact";
	private static String TEST_SYSTEM = "TESTING";
	
	@Override
    public void run(String... args) throws Exception {
		testIncomingNodeAdded();
		//testIncomingNodeChanged();
		//testIncomingNodeDeleted();
		//testIncomingNodeCreationReceipt();
		
		//testOutgoingNodeAdded();
		//testOutgoingNodeChanged();
		//testOutgoingNodeDeleted();
	}

	private void testIncomingNodeAdded() {
		new RedisService(RedisService.Topics.INCOMING, "localhost").sendChangeEvent(new ChangeEventNodeAdded() {{
			//setId(TEST_ID);
			setPayload(new ChangePayload() {{
				addProperty("firstname", TEST_FIRSTNAME,null);
				addProperty("lastname", TEST_LASTNAME,null);
				addProperty("phone", TEST_PHONE,null);
			}});
			setMetadata(new ChangeMetadata() {{
				setSourceId(TEST_UUID);
				setSourceEntity(TEST_ENTITY);
				setSourceSystem(TEST_SYSTEM);
			}});
		}});
	}
	
	private void testIncomingNodeChanged() {
		new RedisService(RedisService.Topics.INCOMING, "localhost").sendChangeEvent(new ChangeEventNodeChanged() {{
			setId(TEST_ID);
			setPayload(new ChangePayload() {{
				addProperty("phone", TEST_PHONE,null);
			}});
			setMetadata(new ChangeMetadata() {{
				setSourceId(TEST_UUID);
				setSourceEntity(TEST_ENTITY);
				setSourceSystem(TEST_SYSTEM);
			}});
		}});
	}
	
	private void testIncomingNodeDeleted() {
		new RedisService(RedisService.Topics.INCOMING, "localhost").sendChangeEvent(new ChangeEventNodeDeleted() {{
			setId(TEST_ID);
			setMetadata(new ChangeMetadata() {{
				setSourceId(TEST_UUID);
				setSourceEntity(TEST_ENTITY);
				setSourceSystem(TEST_SYSTEM);
			}});
		}});
	}
	
	private void testIncomingNodeCreationReceipt() {
		new RedisService(RedisService.Topics.INCOMING, "localhost").sendChangeEvent(new ChangeEventCreationReceipt() {{
			setId(TEST_ID);
			setMetadata(new ChangeMetadata() {{
				setSourceId(TEST_UUID);
				setSourceEntity(TEST_ENTITY);
				setSourceSystem(TEST_SYSTEM);
			}});
		}});
	}

	private void testOutgoingNodeAdded() {
		new RedisService(RedisService.Topics.OUTGOING, "localhost").sendChangeEvent(new ChangeEventNodeAdded() {{
			setId(TEST_ID);
			setPayload(new ChangePayload() {{
				addProperty("firstname", TEST_FIRSTNAME,null);
				addProperty("lastname", TEST_LASTNAME,null);
			}});
			setMetadata(new ChangeMetadata() {{
				setSourceId(TEST_UUID);
				setSourceEntity(TEST_ENTITY);
				setSourceSystem(TEST_SYSTEM);
			}});
		}});
	}
	
	private void testOutgoingNodeChanged() {
		new RedisService(RedisService.Topics.OUTGOING, "localhost").sendChangeEvent(new ChangeEventNodeChanged() {{
			setId(TEST_ID);
			setPayload(new ChangePayload() {{
				addProperty("phone", TEST_PHONE,null);
			}});
			setMetadata(new ChangeMetadata() {{
				setSourceId(TEST_UUID);
				setSourceEntity(TEST_ENTITY);
				setSourceSystem(TEST_SYSTEM);
			}});
		}});
	}
	
	private void testOutgoingNodeDeleted() {
		new RedisService(RedisService.Topics.OUTGOING, "localhost").sendChangeEvent(new ChangeEventNodeDeleted() {{
			setId(TEST_ID);
			setMetadata(new ChangeMetadata() {{
				setSourceId(TEST_UUID);
				setSourceEntity(TEST_ENTITY);
				setSourceSystem(TEST_SYSTEM);
			}});
		}});
	}
}

/**

r.db('poc').table('contact').insert({
"firstname":  "Jayne" ,
"lastname":  "Cobb" ,
"phone":  "0404 404 401"
})

 *
 *
 */
