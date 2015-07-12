package io.peet.hubsub;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.io.Tcp;
import akka.io.TcpMessage;
import akka.util.ByteString;
import io.peet.hubsub.command.SubscribeCommand;
import io.peet.hubsub.packet.*;
import io.peet.hubsub.pubsub.Event;
import io.peet.hubsub.pubsub.Publishable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Connection extends UntypedActor implements Publishable {

    /**
     * The associated TCP connection.
     */
    protected ActorRef connection;

    /**
     * Atom decode.
     */
    protected Decoder decoder;

    /**
     * Map of command names to handler functions, which take a command
     * and result a packet response.
     */
    protected Map<String, Function<Command, Packet>> dispatchers;

    /**
     * The connection pool this belongs to.
     */
    protected ActorRef pool;

    public Connection(ActorRef connection, ActorRef pool) {
        this.connection = connection;
        this.decoder = new Decoder();
        this.pool = pool;

        dispatchers = new HashMap<>();

        dispatchers.put("publish", (Command cmd) -> {
            pool.tell(new Event(
                    cmd.arg(0).toString(),
                    ((BulkStringPacket) cmd.arg(1)).getData()
            ), getSelf());

            return null;
        });
        dispatchers.put("subscribe", (Command cmd) -> baseSubscribe(cmd, false));
        dispatchers.put("psubscribe", (Command cmd) -> baseSubscribe(cmd, true));
    }

    protected Packet baseSubscribe(Command cmd, boolean glob) {
        String pattern = cmd.arg(0).toString();
        pool.tell(new SubscribeCommand(pattern, this, glob), getSelf());

        return makeReply(cmd.name(), pattern);
    }

    /**
     * Creates a packet to reply to a subscription event.
     * @param event command (subscribe/psubscribe)
     * @param pattern pattern the subscribe was to
     * @return a packet response
     */
    protected Packet makeReply(String event, String pattern) {
        // todo: list the number of listeners in place of the `1`
        return new ArrayPacket().add(event).add(pattern).add(1);
    }

    /**
     * Queues a result packet to be sent back over the connection.
     * @param packet the packet to encode and write
     */
    protected void write(Packet packet) {
        this.connection.tell(TcpMessage.write(packet.encode()), getSelf());
    }

    /**
     * Handles an incoming data packet and returns a response.
     * @param packet the incoming packet, which should be an ArrayPacket
     *               with a length of one or more.
     * @return a response packet
     */
    protected Packet processPacket(Packet packet) {
        if (!(packet instanceof ArrayPacket)) {
            return new ErrorPacket("ERR invalid command type.");
        }

        Command cmd = ((ArrayPacket) packet).asCommand();
        if (cmd == null) {
            return new ErrorPacket("ERR invalid command format.");
        }

        String name = cmd.name();
        if (dispatchers.containsKey(name)) {
            return dispatchers.get(name).apply(cmd);
        }

        return new ErrorPacket("ERR unknown command '" + cmd.name() + "'");
    }

    /**
     * Writes data to the decoder, and tries to decode any packets
     * it's able to out of there.
     * @param data the byte data to push
     */
    protected void pushData(ByteString data) {
        this.decoder.write(data);

        List<Packet> todo = new ArrayList<>();
        while (true) {
            try {
                todo.add(decoder.packet());
            } catch (PacketIncompleteException e) {
                break;
            }
        }

        for (Packet input : todo) {
            Packet reply = processPacket(input);
            if (reply != null) {
                write(reply);
            }
        }
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof Tcp.Received) {
            pushData(((Tcp.Received) o).data());
        } else if (o instanceof Tcp.ConnectionClosed) {
            getContext().stop(getSelf());
        } else {
            unhandled(o);
        }
    }

    @Override
    public void publish(Event ev) {
        // todo: replace this with some distributed magic
        write(ev.packet());
    }
}
