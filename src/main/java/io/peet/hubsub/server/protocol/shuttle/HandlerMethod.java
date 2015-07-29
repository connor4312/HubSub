package io.peet.hubsub.server.protocol.shuttle;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface HandlerMethod {
    String name() default "";
}
