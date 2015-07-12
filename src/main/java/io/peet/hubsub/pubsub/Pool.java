package io.peet.hubsub.pubsub;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Pool implements PubsubPool {

    protected List<Node<Publishable>> nodes;

    public Pool() {
        this.nodes = new LinkedList<>();
    }

    /**
     * Publishes an event to listening nodes.
     * @param ev Event
     */
    @Override
    public void publish(Event ev) {
        for (Node<Publishable> node : nodes) {
            if (node.matches(ev)) {
                node.forEach((Publishable p) -> p.publish(ev));
            }
        }
    }

    protected void subscribeBase(String pattern, Publishable item, Class<?> cls) {
        for (Node<Publishable> node : nodes) {
            if (node.is(pattern) && cls.isInstance(pattern)) {
                node.add(item);
                return;
            }
        }

        // Nothing found? Add a new node for the item.
        Node<Publishable> node;
        try {
            node = (Node<Publishable>) cls.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        node.setPattern(pattern);
        node.setItems(new LinkedList<>());
        node.add(item);
        nodes.add(node);
    }

    /**
     * Adds a new publishable listening on the basic pattern provided.
     * @param pattern basic pattern to subscribe to
     * @param item the "thing" to subscribe
     */
    @Override
    public void subscribe(String pattern, Publishable item) {
        subscribeBase(pattern, item, SimpleNode.class);
    }

    /**
     * Adds a new publishable listening on the glob-like pattern provided.
     * @param pattern glob pattern to subscribe to
     * @param item the "thing" to subscribe
     */
    @Override
    public void psubscribe(String pattern, Publishable item) {
        subscribeBase(pattern, item, PatternNode.class);
    }

    protected void unsubscribeBase(String pattern, Publishable item, Class<?> cls) {
        nodes = nodes.stream().filter((Node<Publishable> node) -> {
            if (node.is(pattern) && cls.isInstance(node)) {
                return node.remove(item) > 0;
            }

            return true;
        }).collect(Collectors.toList());
    }

    /**
     * Unsubscribes a given Publishable from a single plain pattern.
     * @param pattern pattern to unsubscribe from
     * @param item item to remove
     */
    @Override
    public void unsubscribe(String pattern, Publishable item) {
        unsubscribeBase(pattern, item, SimpleNode.class);
    }

    /**
     * Unsubscribes a given Publishable from a single glob pattern.
     * @param pattern pattern to unsubscribe from
     * @param item item to remove
     */
    @Override
    public void punsubscribe(String pattern, Publishable item) {
        unsubscribeBase(pattern, item, PatternNode.class);
    }

    /**
     * Removes the publishable from all subscription events.
     * @param item the item to remove
     */
    @Override
    public void unsubscribe(Publishable item) {
        nodes = nodes.stream()
                .filter((Node<Publishable> node) -> node.remove(item) > 0)
                .collect(Collectors.toList());
    }
}
