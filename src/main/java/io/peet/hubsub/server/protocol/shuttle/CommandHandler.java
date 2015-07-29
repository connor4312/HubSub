package io.peet.hubsub.server.protocol.shuttle;

import io.peet.hubsub.protocol.*;
import io.peet.hubsub.pubsub.Event;
import io.peet.hubsub.pubsub.Publishable;
import io.peet.hubsub.server.protocol.pool.PubsubCommand;
import io.peet.hubsub.server.protocol.pool.SubscribeCommand;
import io.peet.hubsub.server.protocol.pool.UnsubscribeCommand;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CommandHandler implements Handler {

    /**
     * Publishable to use for pubsub commands.
     */
    protected Publishable publishable;

    /**
     * Map of command names to handler functions, which take a command
     * and result a packet response.
     */
    protected Map<String, Function<Command, Response[]>> dispatchers;

    public CommandHandler(Publishable publishable) {
        this.publishable = publishable;
        this.buildHandlers();
    }

    protected void buildHandlers() {
        dispatchers = new HashMap<>();

        Class<HandlerMethod> annotationClass = HandlerMethod.class;
        Method[] methods = getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (!method.isAnnotationPresent(annotationClass)) {
                continue;
            }

            HandlerMethod annotation = method.getAnnotation(annotationClass);
            String name = annotation.name().equals("") ?
                    method.getName() : annotation.name();

            dispatchers.put(name, (Command cmd) -> {
                Object output = null;
                try {
                    output = method.invoke(this, cmd);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

                if (output instanceof Packet) {
                    return new Response[] { new PacketResponse((Packet) output) };
                } else if (output instanceof Response) {
                    return new Response[] { (Response) output };
                } else if (output instanceof Response[]) {
                    return (Response[]) output;
                }

                return new Response[0];
            });
        }
    }

    @HandlerMethod
    protected Response[] publish(Command cmd) {
        return new Response[]{
                new EventResponse(new Event(
                        cmd.arg(0).toString(),
                        ((BulkStringPacket) cmd.arg(1)).getData()
                )),
                // todo: replace 0 with the number of listeners
                new PacketResponse(new IntegerPacket(0))
        };
    }

    @HandlerMethod
    protected Response[] quit(Command cmd) {
        return new Response[] {
                new PacketResponse(new SimpleStringPacket("OK")),
                new CloseResponse()
        };
    }

    @HandlerMethod
    protected Response[] subscribe(Command cmd) {
        return baseSubscribe(cmd, true, false);
    }

    @HandlerMethod
    protected Response[] psubscribe(Command cmd) {
        return baseSubscribe(cmd, true, true);
    }

    @HandlerMethod
    protected Response[] unsubscribe(Command cmd) {
        return baseSubscribe(cmd, false, false);
    }

    @HandlerMethod
    protected Response[] punsubscribe(Command cmd) {
        return baseSubscribe(cmd, false, false);
    }

    protected Response[] baseSubscribe(Command cmd, boolean subscribed, boolean glob) {
        String pattern = cmd.arg(0).toString();
        PubsubCommand p;
        if (subscribed) {
            p = new SubscribeCommand(pattern, publishable, glob);
        } else {
            p = new UnsubscribeCommand(pattern, publishable, glob);
        }

        // todo: list the number of listeners in place of the `1`
        return new Response[] {
                new CommandResponse(p),
                new PacketResponse(new ArrayPacket()
                        .add(cmd.name()).add(pattern).add(1))
        };
    }

    /**
     * Dispatches an incoming command.
     * @param cmd the sent command
     * @return a list of Response instances
     * @throws UnhandledCommandException
     */
    @Override
    public Response[] handle(Command cmd) throws UnhandledCommandException {
        if (!dispatchers.containsKey(cmd.name())) {
            throw new UnhandledCommandException("ERR unknown command '" +
                    cmd.name() + "'");
        }

        return dispatchers.get(cmd.name()).apply(cmd);
    }
}
