/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors.dom;

import java.util.Map;

/**
 * DOM node abstraction.
 *
 * @param <T> The actual node type.
 *
 * @author Christer Sandberg
 */
public interface DOMNode<T extends DOMNode, U> {

    /** Node type. */
    public static enum Type {
        DOCUMENT, ELEMENT, TEXT, OTHER
    }

    /**
     * Returns the underlying node instance.
     *
     * @return The underlying node instance.
     */
    public U getUnderlying();

    /**
     * Returns the type for this node.
     *
     * @return The node type.
     */
    public Type getType();

    /**
     * Returns the data for this node.
     * <p/>
     * For element nodes the tag name will be returned and for text nodes its content.
     *
     * @return The data for this node or {@code null}
     */
    public String getData();

    /**
     * Returns the attributes for this node.
     *
     * @return The attributes for this node or {@code null}
     */
    public Map<String, String> getAttributes();

    /**
     * Returns the first child of this node.
     *
     * @return The first child or {@code null}
     */
    public T getFirstChild();

    /**
     * Returns the node immediately preceding this node.
     *
     * @return The previous sibling or {@code null}
     */
    public T getPreviousSibling();

    /**
     * Returns the node immediately following this node.
     *
     * @return The next sibling or {@code null}
     */
    public T getNextSibling();

    /**
     * Returns the parent of this node.
     *
     * @return The parent or {@code null}
     */
    public T getParentNode();

}
