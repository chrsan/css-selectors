/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors.dom;

/**
 * Visitor used while traversing a DOM.
 *
 * @param <T> The node type.
 *
 * @author Christer Sandberg
 */
public interface Visitor<T extends DOMNode<T, ?>> {

    /**
     * Visit the given node.
     *
     * @param node The node to visit.
     */
    public void visit(T node);

}
