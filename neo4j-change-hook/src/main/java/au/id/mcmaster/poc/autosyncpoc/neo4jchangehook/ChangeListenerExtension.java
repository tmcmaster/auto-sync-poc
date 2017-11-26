package au.id.mcmaster.poc.autosyncpoc.neo4jchangehook;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.lifecycle.Lifecycle;

import java.util.*;
import java.util.logging.Logger;
import java.io.IOException;
import java.text.ParseException;


public class ChangeListenerExtension implements Lifecycle {
    private final GraphDatabaseService gds;
    private final static Logger logger = Logger.getLogger(ChangeListenerExtension.class.getName());
    private ChangeListenerEventHandler handler;

    public ChangeListenerExtension(GraphDatabaseService gds) {
        this.gds = gds;
        
		logger.warning("---------- ExampleExtension CONSTRUCTOR ---------");
        // creating our kernel handler that will be making all the work
        this.handler = new ChangeListenerEventHandler(gds);
        // registering our kernel event handler
        gds.registerTransactionEventHandler(handler);
    }

    @Override
    public void init() throws Throwable {
    		logger.warning("---------- ExampleExtension INIT ---------");
    }

    @Override
    public void start() throws Throwable {
    		logger.warning("---------- ExampleExtension START ---------");
    }

    @Override
    public void stop() throws Throwable {
		logger.warning("---------- ExampleExtension STOP ---------");
    }

    @Override
    public void shutdown() throws Throwable {
        gds.unregisterTransactionEventHandler(handler);
        logger.info("ExampleExtension: Shutdown");
    }
}
