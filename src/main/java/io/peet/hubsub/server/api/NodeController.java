package io.peet.hubsub.server.api;

import akka.actor.ActorContext;
import akka.http.javadsl.model.HttpEntity;
import akka.http.javadsl.model.HttpEntityDefault;
import akka.http.javadsl.server.RequestContext;
import akka.http.javadsl.server.RouteResult;

public class NodeController {

    protected ActorContext context;

    public NodeController(ActorContext context) {
        this.context = context;
    }

    /**
     * Lists nodes that are open, and their status information.
     * @param ctx request context
     * @return the http route result
     */
    public RouteResult nodes(RequestContext ctx) {
        ctx.
    }
}
