package io.peet.hubsub;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.typesafe.config.Config;
import io.peet.hubsub.server.Server;

import java.net.InetSocketAddress;

public class Bootstrap extends UntypedActor {

    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    @Override
    public void preStart() {
        Config config = getContext().system().settings().config();
        String host = config.getString("hubsub.protocol.addr.hostname");
        int port = config.getInt("hubsub.protocol.addr.port");

        // create the greeter actor
        final ActorRef server = getContext().actorOf(
                Props.create(Server.class));
        final InetSocketAddress addr = new InetSocketAddress(host, port);
        server.tell(new Server.Open(addr), getSelf());

        log.info("Hubsub listening on {}", addr.toString());
    }

    @Override
    public void onReceive(Object msg) {

    }
}