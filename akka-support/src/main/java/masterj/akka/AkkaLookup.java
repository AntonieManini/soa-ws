package masterj.akka;

import akka.actor.ActorSystem;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.japi.Creator;
import com.github.gkislin.common.LoggerWrapper;
import com.github.gkislin.common.config.RootConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * User: gkislin
 * Date: 27.02.14
 */
public class AkkaLookup {
    protected static final LoggerWrapper LOGGER = LoggerWrapper.get(AkkaLookup.class);

    private static final String AKKA_CONF = "akka/application.conf";

    private static ActorSystem system;

    private static void createSystem(String actorSystemName, String name) {
        system = ActorSystem.create(actorSystemName, RootConfig.getResourceConfig(AKKA_CONF).getConfig(name));
    }

    public static <T> List<T> startMaster(String actorSystemName, String configName, Class<T> typedClass, String[] pathArray) {
        LOGGER.info("Started Master " + actorSystemName + ":" + configName);
        List<T> nodes = new ArrayList<>(pathArray.length);
        try {
            createSystem(actorSystemName, configName);
            for (String path : pathArray) {
                LOGGER.info("Started Node Listener with path=" + path);
                nodes.add((T) TypedActor.get(system).typedActorOf(new TypedProps<T>(typedClass), system.actorFor(path)));
            }
        } catch (Throwable t) {
            throw LOGGER.getIllegalStateException("Akka initialization failed", t);
        }
        return nodes;
    }

    public static <T> void startNode(String actorSystemName, String nodeName, Class<T> typedClass, Creator<T> creator) {
        LOGGER.info("Started AkkaSystem " + actorSystemName + ":" + nodeName + ". Waiting for messages...");
        try {
            createSystem(actorSystemName, nodeName);
            TypedActor.get(system).typedActorOf(new TypedProps<>(typedClass, creator), nodeName);
        } catch (Throwable t) {
            throw LOGGER.getIllegalStateException("Akka initialization failed", t);
        }
    }

    public static void shutdown() {
        if(system!=null) {
        try {
                LOGGER.info("Akka system shutdown");
                system.shutdown();
        } catch (Throwable t) {
            LOGGER.error("Akka shutdown failed", t);
            throw t;
        }
        }
    }
}
