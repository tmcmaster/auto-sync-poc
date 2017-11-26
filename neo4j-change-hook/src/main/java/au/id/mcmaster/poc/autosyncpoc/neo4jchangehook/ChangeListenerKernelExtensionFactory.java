package au.id.mcmaster.poc.autosyncpoc.neo4jchangehook;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.config.Setting;
import org.neo4j.graphdb.factory.Description;
import org.neo4j.helpers.HostnamePort;
import org.neo4j.kernel.configuration.Config;
import org.neo4j.kernel.extension.KernelExtensionFactory;
import org.neo4j.kernel.impl.spi.KernelContext;
import org.neo4j.kernel.lifecycle.Lifecycle;

import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;


public class ChangeListenerKernelExtensionFactory extends KernelExtensionFactory<ChangeListenerKernelExtensionFactory.Dependencies> {

    public static final String SERVICE_NAME = "NEO4JCHANGEHOOK";

    private final static Logger logger = Logger.getLogger(ChangeListenerKernelExtensionFactory.class.getName());

    public ChangeListenerKernelExtensionFactory() {
        super(SERVICE_NAME);
        logger.warning("---------- Creating the Neo4j Extension ---------");
    }

    @Override
    public Lifecycle newInstance(KernelContext arg0, Dependencies dependencies) throws Throwable {
        logger.warning("---------- Creating the Neo4j Extension new instance ---------");
        
        return new ChangeListenerExtension(dependencies.getGraphDatabaseService());
    }

    public interface Dependencies {
        GraphDatabaseService getGraphDatabaseService();

        Config getConfig();
    }
}
