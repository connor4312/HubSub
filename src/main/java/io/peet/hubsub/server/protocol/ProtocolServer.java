package io.peet.hubsub.server.protocol;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.io.Tcp;
import akka.io.TcpMessage;
import io.peet.hubsub.pubsub.PubsubPool;
import io.peet.hubsub.server.Close;
import io.peet.hubsub.server.Open;
import io.peet.hubsub.server.Server;
import io.peet.hubsub.server.protocol.pool.ConnectionPool;

import java.net.InetSocketAddress;


/**
 * The server is a simple Redis server. It opens a TCP server,
 * and listens for incoming connections which it forwards to the pool.
 */
public class ProtocolServer extends UntypedActor implements Server {

    /**
     * The address the server should listen on. This is filled in when an
     * Open object is messaged.
     */
    protected InetSocketAddress addr;

    /**
     * The underlying TCP server.
     */
    protected ActorRef tcp;

    /**
     * A ConnectionPool actor that connections will be forwarded to.
     */
    protected ActorRef pool;

    /**
     * Logger used to notify about server state.
     */
    protected LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    /**
     * Creates a new instance of the Server, which can be used for receiving
     * connections and pubsub events.
     * @param pool The associated pubsub pool
     */
    public ProtocolServer(PubsubPool pool) {
        this.pool = getContext().actorOf(Props.create(
                ConnectionPool.class, pool));
    }

    /**
     * Opens the server.
     * @param event open event that contains the address the server should
     *              listen on.
     */
    private void open(Open event) {
        addr = event.getAddr();
        tcp = Tcp.get(getContext().system()).manager();
        tcp.tell(TcpMessage.bind(getSelf(), addr, 100), getSelf());
    }

    /**
     * Logs that the server is listening on the associated address.
     */
    private void logListening() {
        log.info("Protocol server listening on {}", addr.toString());
    }

    /**
     * Closes the TCP server if there is one active.
     */
    private void close() {
        if (tcp == null) {
            return;
        }

        tcp.tell(TcpMessage.close(), getSelf());
    }

    /**
     * Logs a message telling the client that the tcp server has stopped.
     */
    private void logClosed() {
        log.info("Protocol server has stopped listening.");
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof Tcp.Connected) {
            pool.tell(o, getSender());
        } else if (o instanceof Open) {
            open((Open) o);
        } else if (o instanceof Tcp.Bound) {
            logListening();
        } else if (o instanceof Close) {
            close();
        } else if (o instanceof Tcp.ConnectionClosed) {
            logClosed();
        } else {
            unhandled(o);
        }
    }
}
