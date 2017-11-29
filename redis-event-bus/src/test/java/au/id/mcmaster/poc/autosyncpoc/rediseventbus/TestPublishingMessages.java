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
	
	@Override
    public void run(String... args) throws Exception {
		//testIncomingNodeAdded();
		//testIncomingNodeChanged();
		testIncomingNodeDeleted();
		//testIncomingNodeCreationReceipt();
		
		//testOutgoingNodeAdded();
		//testOutgoingNodeChanged();
		//testOutgoingNodeDeleted();
	}

	private void testIncomingNodeAdded() {
		new RedisService(RedisService.Topics.INCOMING, "localhost").sendChangeEvent(new ChangeEventNodeAdded() {{
			setId(10);
			setPayload(new ChangePayload() {{
				addProperty("firstname", "Malcolm",null);
				addProperty("lastname", "Reynolds",null);
				addProperty("phone", "0404 404 403",null);
			}});
			setMetadata(new ChangeMetadata() {{
				setSourceEntity("Contact");
				setSourceEntity("RethinkDB");
			}});
		}});
	}
	
	private void testIncomingNodeChanged() {
		new RedisService(RedisService.Topics.INCOMING, "localhost").sendChangeEvent(new ChangeEventNodeChanged() {{
			setId(120);
			setPayload(new ChangePayload() {{
				addProperty("phone", "0404 404 404",null);
			}});
			setMetadata(new ChangeMetadata() {{
				setSourceEntity("Contact");
				setSourceEntity("RethinkDB");
			}});
		}});
	}
	
	private void testIncomingNodeDeleted() {
		new RedisService(RedisService.Topics.INCOMING, "localhost").sendChangeEvent(new ChangeEventNodeDeleted() {{
			setId(50);
			setMetadata(new ChangeMetadata() {{
				setSourceEntity("Contact");
				setSourceEntity("RethinkDB");
			}});
		}});
	}
	
	private void testIncomingNodeCreationReceipt() {
		new RedisService(RedisService.Topics.INCOMING, "localhost").sendChangeEvent(new ChangeEventCreationReceipt() {{
			setId(58);
			setMetadata(new ChangeMetadata() {{
				setSourceId("f77a368b-cf71-4a1c-a3ef-503711335e2c");
				setSourceEntity("Contact");
				setSourceSystem("RethinkDB");
			}});
		}});
	}

	private void testOutgoingNodeAdded() {
		new RedisService(RedisService.Topics.OUTGOING, "localhost").sendChangeEvent(new ChangeEventNodeAdded() {{
			setId(10);
			setPayload(new ChangePayload() {{
				addProperty("firstname", "Malcolm",null);
				addProperty("lastname", "Reynolds",null);
			}});
			setMetadata(new ChangeMetadata() {{
				setSourceEntity("Contact");
				setSourceEntity("RethinkDB");
			}});
		}});
	}
	
	private void testOutgoingNodeChanged() {
		new RedisService(RedisService.Topics.OUTGOING, "localhost").sendChangeEvent(new ChangeEventNodeChanged() {{
			setId(10);
			setPayload(new ChangePayload() {{
				addProperty("phone", "0404 404 404",null);
			}});
			setMetadata(new ChangeMetadata() {{
				setSourceEntity("Contact");
				setSourceEntity("RethinkDB");
			}});
		}});
	}
	
	private void testOutgoingNodeDeleted() {
		new RedisService(RedisService.Topics.OUTGOING, "localhost").sendChangeEvent(new ChangeEventNodeDeleted() {{
			setId(10);
			setMetadata(new ChangeMetadata() {{
				setSourceEntity("Contact");
				setSourceEntity("RethinkDB");
			}});
		}});
	}
}
