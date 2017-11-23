package au.id.mcmaster.poc.autosyncpoc.neo4jrest.dto;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Account {
	String name;
	@Relationship(type = "CONTACT", direction = Relationship.UNDIRECTED)
	public Set<Contact> contacts = new HashSet<Contact>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void worksWith(Contact contact) {
		contacts.add(contact);
	}

	public Set<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(Set<Contact> contacts) {
		this.contacts = contacts;
	}
}
