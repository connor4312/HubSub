package io.peet.hubsub.pubsub;

import java.util.LinkedList;
import java.util.List;

public class SimpleNode<T> extends AbstractNode<T> {

    public SimpleNode(String pattern, List<T> items) {
        setPattern(pattern);
        this.items = items;
    }

    public SimpleNode(String pattern) {
        setPattern(pattern);
        this.items = new LinkedList<>();
    }

    public SimpleNode() {
        this.items = new LinkedList<>();
    }

    @Override
    public boolean matches(String pattern) {
        return this.pattern.equals(pattern);
    }
}
