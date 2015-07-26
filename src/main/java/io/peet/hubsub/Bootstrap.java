package io.peet.hubsub;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.ActorRef;
import io.peet.hubsub.server.Server;

import java.net.InetSocketAddress;

public class Bootstrap extends UntypedActor {

    @Override
    public void preStart() {
        // create the greeter actor
        final ActorRef server = getContext().actorOf(
                Props.create(Server.class));
        final InetSocketAddress addr = new InetSocketAddress("localhost", 3221);
        server.tell(new Server.Open(addr), getSelf());

        System.out.println("HubSub has started.");
    }

    @Override
    public void onReceive(Object msg) {

    }
}