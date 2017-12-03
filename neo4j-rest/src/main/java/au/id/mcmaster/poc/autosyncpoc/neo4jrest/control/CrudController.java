package au.id.mcmaster.poc.autosyncpoc.neo4jrest.control;

import javax.ws.rs.Consumes;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import au.id.mcmaster.poc.autosyncpoc.neo4jrest.dto.BasePOJO;

@Deprecated
@RestController
public abstract class CrudController<T extends BasePOJO, R extends Neo4jRepository<T,Long>> {
	@Autowired
	private R repository;
	
	@RequestMapping(method = RequestMethod.GET)
	public Iterable<T> getObject() {
		return this.repository.findAll();
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public T createObject(@RequestBody T object) {
		return this.repository.save(object);
	}
	
	@Consumes({"application/xml","text/plain"})
	@RequestMapping(method = RequestMethod.GET, value="/{objectId}")
	public ResponseEntity<T> getObject(@PathVariable long objectId) {
		T object = this.repository.findOne(objectId,1);
		if (object != null) {
			return new ResponseEntity<T>(object, HttpStatus.OK);
		}
		else
		{
			return new ResponseEntity<T>(HttpStatus.NOT_FOUND);
		}
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value="/{objectId}")
	public void deleteObject(@PathVariable long objectId) {
		this.repository.delete(objectId);
	}
}
