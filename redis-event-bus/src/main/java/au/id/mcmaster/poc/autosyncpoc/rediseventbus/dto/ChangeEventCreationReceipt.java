package au.id.mcmaster.poc.autosyncpoc.rediseventbus.dto;


/**
 * MATCH (u:User)
WHERE u.name = 'daniel'
CREATE (m:Message {text:'hello there'})-[:Send]->(u);

MATCH (u) WHERE id(u) = 58 CREATE (m:Message {text:'hello there'})<-[:Send]-(u);

 * @author tim
 *
 */
public class ChangeEventCreationReceipt extends ChangeEvent {
	public ChangeEventCreationReceipt() {
		this(0);
	}

	protected ChangeEventCreationReceipt(long id) {
		super(Type.NODE_CREATION_RECEIPT, id);
	}
}
