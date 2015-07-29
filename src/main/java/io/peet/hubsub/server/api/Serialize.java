package io.peet.hubsub.server.api;

import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.RequestContext;
import akka.http.javadsl.server.RouteResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Serialize {

    private static ObjectMapper mapper = new ObjectMapper();

    public static RouteResult toJSON(RequestContext ctx, Object o) {
        ObjectWriter writer = mapper.writer();
        String value;
        try {
            value = writer.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ctx.completeWithStatus(500);
        }

        return ctx.
    }
}
