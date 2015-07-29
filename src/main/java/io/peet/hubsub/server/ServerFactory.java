package io.peet.hubsub.server;

import akka.actor.ActorRef;
import akka.event.LoggingAdapter;
import com.typesafe.config.Config;

import java.net.InetSocketAddress;

public class ServerFactory {

    protected ActorRef manager;

    /**
     * Creates a new server factory.
     * @param manager The created servers' managing actor.
     */
    public ServerFactory(ActorRef manager) {
        this.manager = manager;
    }

    /**
     * Creates a new server with the specified config.
     * Additionally, `open` will be
     * @param server The server class to create
     * @param config The config is expected to have a "hostname" and "port",
     *               which the server will be started with.
     */
    public void create(ActorRef server, Config config) {
        String host = config.getString("hostname");
        int port = config.getInt("port");
        InetSocketAddress addr = new InetSocketAddress(host, port);

        server.tell(new Open(addr), manager);
    }
}
