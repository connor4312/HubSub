package io.peet.hubsub.pubsub;

import java.util.List;
import java.util.function.Consumer;

public interface Node<T> {

    /**
     * Returns whether the node patches the given pattern.
     * @param pattern a pubsub pattern
     * @return true if it matches
     */
    public boolean matches(String pattern);

    /**
     * Returns if the node matches the pattern in the event.
     * @param ev a pubsub event
     * @return true if it matches
     */
    public boolean matches(Event ev);

    /**
     * Returns whether the Node _exactly_ matches the pattern. Used for
     * grouping subscribed clients.
     * @param pattern a pubsub pattern
     * @return true if it matches
     */
    public boolean is(String pattern);

    /**
     * Adds a new element to the node.
     * @param item item to add
     * @return the number of subscribers after the operation
     */
    public int add(T item);

    /**
     * Removes an item from the node.
     * @param item the item to remove.
     * @return the number of subscribers after the operation
     */
    public int remove(T item);

    /**
     * Iterates over items.
     * @param fn foreach over the items
     */
    public void forEach(Consumer<T> fn);

    /**
     * Sets the underlying pattern.
     * @param pattern pattern string
     */
    public void setPattern(String pattern);

    /**
     * Sets the list of items.
     * @param items the items list
     */
    public void setItems(List<T> items);
}
