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
    private final Boolean debug;
    private final String somevar;
    private ChangeListenerEventHandler handler;

    private static String generateErrorMessage(String detail) {
        return "ExampleExtension: " + detail;
    }

    public ChangeListenerExtension(GraphDatabaseService gds, Boolean debug, String somevar) {
        this.gds = gds;
        this.debug = debug;
        this.somevar = somevar;
        
		logger.warning("---------- ExampleExtension CONSTRUCTOR ---------");
        // creating our kernel that will be making all the work
        handler = new ChangeListenerEventHandler(gds, debug, somevar);
        // registering our kernel event handler
        gds.registerTransactionEventHandler(handler);
        logger.info("ExampleExtension: Init");
    }

    @Override
    public void init() throws Throwable {
    		logger.warning("---------- ExampleExtension INIT ---------");
        // creating our kernel that will be making all the work
        //handler = new ChangeListenerEventHandler(gds, debug, somevar);
        // registering our kernel event handler
        //gds.registerTransactionEventHandler(handler);
        logger.info("ExampleExtension: Init");
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
