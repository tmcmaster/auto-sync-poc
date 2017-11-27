package au.id.mcmaster.poc.autosyncpoc.rethinkdbworker.dto;

import java.util.HashSet;
import java.util.Set;

public class Account {
	String name;
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
