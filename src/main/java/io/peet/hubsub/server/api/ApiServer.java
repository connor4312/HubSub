package io.peet.hubsub.server.api;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.server.*;
import akka.io.Tcp;
import akka.io.TcpMessage;
import io.peet.hubsub.pubsub.PubsubPool;
import io.peet.hubsub.server.Close;
import io.peet.hubsub.server.Open;
import io.peet.hubsub.server.Server;

import java.net.InetSocketAddress;

import static akka.http.javadsl.server.Directives.*;


/**
 * The server is an HTTP server, which provides routes that can be used
 * to view and modify state of the hubsub cluster.
 */
public class ApiServer extends UntypedActor implements Server {

    /**
     * The address the server should listen on. This is filled in when an
     * Open object is messaged.
     */
    protected InetSocketAddress addr;

    /**
     * A PubSub pool that connections can be found in.
     */
    protected PubsubPool pool;

    /**
     * Logger used to notify about server state.
     */
    protected LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    /**
     * Creates a new instance of the Server, which can be used for receiving
     * connections and pubsub events.
     * @param pool The associated pubsub pool
     */
    public ApiServer(PubsubPool pool) {
        this.pool = pool;
    }

    private Route getRoutes() {
        NodeController nodeCtrl = new NodeController(getContext());
        log.info("API server listening on {}", addr.toString());

        return route(
                path("nodes").route(handleReflectively(nodeCtrl, "nodes"))
        );
    }

    /**
     * Opens the server.
     * @param event open event that contains the address the server should
     *              listen on.
     */
    private void open(Open event) {
        addr = event.getAddr();
        HttpService.bindRoute(
                addr.getHostName(), addr.getPort(),
                getRoutes(), getContext().system()
        );
    }
    /**
     * Closes the TCP server if there is one active.
     */
    private void close() {
        log.info("API server has stopped listening.");
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof Open) {
            open((Open) o);
        } else if (o instanceof Close) {
            close();
        } else {
            unhandled(o);
        }
    }
}
