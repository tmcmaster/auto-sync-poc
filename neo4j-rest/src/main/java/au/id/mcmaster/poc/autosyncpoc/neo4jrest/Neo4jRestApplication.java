package au.id.mcmaster.poc.autosyncpoc.neo4jrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import au.id.mcmaster.poc.autosyncpoc.neo4jrest.control.CrudController;
import au.id.mcmaster.poc.autosyncpoc.neo4jrest.dto.Contact;

@SpringBootApplication
@EnableNeo4jRepositories
public class Neo4jRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(Neo4jRestApplication.class, args);
	}
}

interface ContactRepository extends Neo4jRepository<Contact,Long> {
    //Contact findByFirstName(String name);
}

@RestController
@RequestMapping("/contact")
class ContactController extends CrudController<Contact, ContactRepository> {}