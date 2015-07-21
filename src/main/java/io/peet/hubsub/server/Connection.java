package io.peet.hubsub.server;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.io.Tcp;
import akka.io.TcpMessage;
import akka.util.ByteString;
import io.peet.hubsub.server.handler.*;
import io.peet.hubsub.protocol.*;
import io.peet.hubsub.pubsub.Event;
import io.peet.hubsub.pubsub.Publishable;

import java.util.ArrayList;
import java.util.List;

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
     * Handler used for dispatching commands.
     */
    protected Handler handler;

    /**
     * The connection pool this belongs to.
     */
    protected ActorRef pool;

    public Connection(ActorRef connection, ActorRef pool) {
        this.connection = connection;
        this.decoder = new Decoder();
        this.pool = pool;
        this.handler = new CommandHandler(this);
    }

    /**
     * Queues a result packet to be sent back over the connection.
     * @param packet the packet to encode and write
     */
    protected void write(Packet packet) {
        connection.tell(TcpMessage.write(packet.encode()), getSelf());
    }

    /**
     * Tells the TCP connection to close.
     */
    protected void close() {
        connection.tell(TcpMessage.close(), getSelf());
    }

    /**
     * Sends an event to the pool.
     * @param ev the event to send
     */
    protected void send(Object ev) {
        pool.tell(ev, getSelf());
    }

    /**
     * Handles an incoming data packet and returns a response.
     * @param packet the incoming packet, which should be an ArrayPacket
     *               with a length of one or more.
     */
    protected void processPacket(Packet packet) {
        if (!(packet instanceof ArrayPacket)) {
            write(new ErrorPacket("ERR invalid command type."));
            return;
        }

        Command cmd = ((ArrayPacket) packet).asCommand();
        if (cmd == null) {
            write(new ErrorPacket("ERR invalid command format."));
            return;
        }

        try {
            Response[] responses = this.handler.handle(cmd);
            for (Response response : responses) {
                if (response instanceof PacketResponse) {
                    write(((PacketResponse) response).getPacket());
                } else if (response instanceof EventResponse) {
                    send(((EventResponse) response).getEvent());
                } else if (response instanceof CommandResponse) {
                    send(((CommandResponse) response).getCommand());
                } else if (response instanceof CloseResponse) {
                    close();
                }
            }
        } catch (UnhandledCommandException e) {
            write(e.getError());
        }
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
            processPacket(input);
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
    public void postStop() throws Exception {
        super.postStop();
        send(new DisconnectCommand(this));
    }

    @Override
    public void publish(Event ev) {
        // todo: replace this with some distributed magic
        write(ev.packet());
    }
}
