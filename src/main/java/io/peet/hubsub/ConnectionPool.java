package io.peet.hubsub;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.io.Tcp;
import akka.io.TcpMessage;
import io.peet.hubsub.command.*;
import io.peet.hubsub.pubsub.Event;
import io.peet.hubsub.pubsub.Publishable;
import io.peet.hubsub.pubsub.PubsubPool;

import java.util.function.BiConsumer;

/**
 *
 */
public class ConnectionPool extends UntypedActor {

    /**
     * Underlying pubsub pool.
     */
    protected PubsubPool pool;

    public ConnectionPool(PubsubPool pubsubPool) {
        this.pool = pubsubPool;
    }

    /**
     * Called when a TCP connection is given from Akka's internal
     * TCP libraries. Creates and attaches a new Connection to the
     * pool.
     *
     * @param conn the tcp connection from akka
     */
    protected void genHandler(Tcp.Connected conn) {
        final ActorRef cnx = getContext().actorOf(Props.create(
                Connection.class, getSender(), getSelf()));
        getSender().tell(TcpMessage.register(cnx), getSelf());
    }

    /**
     * Takes an AbstractPubsubCommand, running plainFn with the pattern
     * and publishable if the command isn't a glob, globFn otherwise.
     *
     * @param cmd the incoming command
     * @param plainFn function to run if the command isn't a glob
     * @param globFn function to run if the command is a glob
     */
    protected void processCommand(PubsubCommand cmd,
                                  BiConsumer<String, Publishable> plainFn,
                                  BiConsumer<String, Publishable> globFn) {

        if (cmd.isGlob()) {
            plainFn.accept(cmd.getPattern(), cmd.getPublishable());
        } else {
            globFn.accept(cmd.getPattern(), cmd.getPublishable());
        }
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof Event) {
            pool.publish((Event) o);
        } else if (o instanceof SubscribeCommand) {
            processCommand((PubsubCommand) o,
                    pool::subscribe, pool::psubscribe);
        } else if (o instanceof UnsubscribeCommand) {
            processCommand((PubsubCommand) o,
                    pool::unsubscribe, pool::punsubscribe);
        } else if (o instanceof Tcp.Connected) {
            genHandler((Tcp.Connected) o);
        } else if (o instanceof DisconnectCommand) {
            pool.unsubscribe(((DisconnectCommand) o).getPublishable());
        } else {
            unhandled(o);
        }
    }
}
