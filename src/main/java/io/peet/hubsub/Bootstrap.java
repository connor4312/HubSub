package io.peet.hubsub;

import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.typesafe.config.Config;
import io.peet.hubsub.pubsub.Pool;
import io.peet.hubsub.pubsub.PubsubPool;
import io.peet.hubsub.server.api.ApiServer;
import io.peet.hubsub.server.protocol.ProtocolServer;
import io.peet.hubsub.server.ServerFactory;

public class Bootstrap extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Override
    public void preStart() {
        Config config = getContext().system().settings().config();
        PubsubPool pool = new Pool();
        ServerFactory factory = new ServerFactory(getSelf());
        factory.create(
                getContext().actorOf(Props.create(ProtocolServer.class, pool)),
                config.getConfig("hubsub.protocol.addr")
        );
        factory.create(
                getContext().actorOf(Props.create(ApiServer.class, pool)),
                config.getConfig("hubsub.api.addr")
        );
    }

    @Override
    public void onReceive(Object msg) {

    }
}
