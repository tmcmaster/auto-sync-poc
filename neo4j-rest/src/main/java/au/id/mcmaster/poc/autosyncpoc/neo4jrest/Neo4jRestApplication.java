package au.id.mcmaster.poc.autosyncpoc.neo4jrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import au.id.mcmaster.poc.autosyncpoc.neo4jrest.dto.Account;
import au.id.mcmaster.poc.autosyncpoc.neo4jrest.dto.Contact;

@SpringBootApplication
@EnableNeo4jRepositories
public class Neo4jRestApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(Neo4jRestApplication.class, args);
	}
}

@RepositoryRestResource(collectionResourceRel = "contact", path = "contact")
interface ContactRepository extends Neo4jRepository<Contact,Long> {}

@RepositoryRestResource(collectionResourceRel = "account", path = "account")
interface AccountRepository extends Neo4jRepository<Account,Long> {}
