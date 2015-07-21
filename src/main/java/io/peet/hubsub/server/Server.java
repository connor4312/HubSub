package io.peet.hubsub.server;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.io.Tcp;
import akka.io.TcpMessage;
import au.com.ds.ef.EasyFlow;
import au.com.ds.ef.EventEnum;
import au.com.ds.ef.StateEnum;
import au.com.ds.ef.StatefulContext;
import io.peet.hubsub.pubsub.Pool;

import java.net.InetSocketAddress;

import static au.com.ds.ef.FlowBuilder.from;
import static au.com.ds.ef.FlowBuilder.on;


/**
 * The server is a simple Redis server. It opens a TCP server,
 * and listens for incoming connections which it forwards to the pool.
 */
public class Server extends UntypedActor {

    /**
     * A list of states that the server can be in.
     */
    public enum State implements StateEnum {
        // There has not yet been any event telling it to open a server.
        IDLE,
        // It got an event asking to open the server, and is waiting for the
        // tcp connection to bind.
        OPENING,
        // It's open and currently accepting connections.
        OPEN,
        // It's been told to tear down and is waiting for the tcp server to
        // close connections.
        CLOSING,
        // The TCP server and all connections are closed
        CLOSED
    }

    /**
     * Events, triggered internally, to modify those states.
     */
    protected enum Event implements EventEnum {
        OPEN, CLOSE, LISTENING, CLOSED
    }

    /**
     * fsm used for managing state
     */
    protected EasyFlow<StatefulContext> fsm;

    /**
     * fsm's state context
     */
    protected StatefulContext context;

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
     * Creates a new instance of the Server, which can be used for receiving
     * connections and pubsub events.
     */
    public Server() {
        this.pool = getContext().actorOf(
                Props.create(ConnectionPool.class, new Pool()));

        fsm = from(State.IDLE).transit(
                on(Event.OPEN).to(State.OPENING).transit(
                        on(Event.LISTENING).to(State.OPEN).transit(
                                on(Event.CLOSE).to(State.CLOSING).transit(
                                        on(Event.CLOSED).finish(State.CLOSED)
                                )
                        )
                )
        );

        fsm.whenEnter(State.OPENING, context1 -> {
            tcp = Tcp.get(getContext().system()).manager();
            tcp.tell(TcpMessage.bind(getSelf(), addr, 100), getSelf());
        });

        fsm.whenEnter(State.CLOSING, context1 -> {
            tcp.tell(TcpMessage.close(), getSelf());
        });

        fsm.start(context = new StatefulContext());
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof Tcp.Connected) {
            pool.tell(o, getSender());
        } else if (o instanceof Open) {
            addr = ((Open) o).getAddr();
            fsm.trigger(Event.OPEN, context);
        } else if (o instanceof Tcp.Bound) {
            fsm.trigger(Event.LISTENING, context);
        } else if (o instanceof Close) {
            fsm.trigger(Event.CLOSE, context);
        } else if (o instanceof Tcp.ConnectionClosed) {
            fsm.trigger(Event.CLOSED, context);
        } else {
            unhandled(o);
        }
    }

    /**
     * The Open class can be sent to the Server to start it listening for
     * incoming events.
     */
    public static class Open {
        private InetSocketAddress addr;

        /**
         * Specify the port to open the connection on.
         *
         * @param addr the address to listen on
         */
        public Open(InetSocketAddress addr) {
            this.addr = addr;
        }

        /**
         * Returns the socket address we intend to open on.
         */
        public InetSocketAddress getAddr() {
            return addr;
        }
    }

    /**
     * The Close can be sent to the server stop listening and disconnect
     * all clients.
     */
    public static class Close {
    }
}
