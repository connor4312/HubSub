package io.peet.hubsub.pubsub;

import java.util.List;
import java.util.function.Consumer;

abstract public class AbstractNode<T> implements Node<T> {

    protected List<T> items;
    protected String pattern;

    @Override
    public boolean is(String pattern) {
        return this.pattern.equals(pattern);
    }

    @Override
    public int add(T item) {
        items.add(item);
        return items.size();
    }

    @Override
    public int remove(T item) {
        items.remove(item);
        return items.size();
    }

    @Override
    public void forEach(Consumer<T> fn) {
        items.forEach(fn);
    }

    @Override
    public boolean matches(Event event) {
        return matches(event.getPattern());
    }

    @Override
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public void setItems(List<T> items) {
        this.items = items;
    }
}
