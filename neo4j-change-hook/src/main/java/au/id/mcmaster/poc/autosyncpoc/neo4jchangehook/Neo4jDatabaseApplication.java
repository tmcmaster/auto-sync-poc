package au.id.mcmaster.poc.autosyncpoc.neo4jchangehook;

import java.io.File;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.config.Setting;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

import au.id.mcmaster.poc.autosyncpoc.rediseventbus.service.RedisService;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages={"au.id.mcmaster.poc.autosyncpoc"})
public class Neo4jDatabaseApplication implements CommandLineRunner {
	
	public static void main(String[] args) {
		SpringApplication.run(Neo4jDatabaseApplication.class, args);
	}
	
	@Override
    public void run(String... args) throws Exception {
		try
		{
			//
			// THIS IS NOT CURRENTLY WORKING
			//
			
			//graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( "data );
			//registerShutdownHook( graphDb );
			
			GraphDatabaseService graphDb = new GraphDatabaseFactory()
				    .newEmbeddedDatabaseBuilder( new File("data") )
				    .loadPropertiesFromFile( "conf/neo4j.conf" )
				    .newGraphDatabase();
			
			Thread.sleep(100000);
			
			//GraphDatabaseFactory graphDbFactory = new GraphDatabaseFactory();
			//graphDbFactory.newEmbeddedDatabaseBuilder(new File("data")).newGraphDatabase();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
    }
	
}

