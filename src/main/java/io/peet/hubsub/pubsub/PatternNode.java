package io.peet.hubsub.pubsub;

import java.util.LinkedList;
import java.util.List;

public class PatternNode<T> extends AbstractNode<T> {

    protected Pattern matcher;

    public PatternNode(String pattern, List<T> items) {
        setPattern(pattern);
        this.items = items;
    }

    public PatternNode(String pattern) {
        setPattern(pattern);
        this.items = new LinkedList<>();
    }

    public PatternNode() {
        this.items = new LinkedList<>();
    }

    @Override
    public boolean matches(String pattern) {
        return matcher.matches(pattern);
    }

    @Override
    public void setPattern(String pattern) {
        super.setPattern(pattern);
        this.matcher = new Pattern(pattern);
    }
}
