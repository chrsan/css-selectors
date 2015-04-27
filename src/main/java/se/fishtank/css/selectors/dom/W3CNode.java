/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors.dom;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * {@linkplain se.fishtank.css.selectors.dom.DOMNode} implementation for a {@linkplain org.w3c.dom.Node}
 *
 * @author Christer Sandberg
 */
public class W3CNode implements DOMNode<W3CNode, Node> {

    /** The underlying node. */
    private final Node node;

    /**
     * Create a new node.
     *
     * @param node The underlying node.
     */
    public W3CNode(Node node) {
        this.node = node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Node getUnderlying() {
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Type getType() {
        switch (node.getNodeType()) {
        case Node.DOCUMENT_NODE:
            return Type.DOCUMENT;
        case Node.ELEMENT_NODE:
            return Type.ELEMENT;
        case Node.TEXT_NODE:
            return Type.TEXT;
        default:
            return Type.OTHER;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getData() {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            return node.getNodeName();
        }

        return node.getNodeValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getAttributes() {
        NamedNodeMap namedNodeMap = node.getAttributes();
        if (namedNodeMap == null) {
            return null;
        }

        HashMap<String, String> attrs = new HashMap<>();

        int len = namedNodeMap.getLength();
        for (int i = 0; i < len; ++i) {
            Attr attr = (Attr) namedNodeMap.item(i);
            attrs.put(attr.getName(), attr.getValue());
        }

        return attrs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public W3CNode getFirstChild() {
        return wrap(node.getFirstChild());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public W3CNode getPreviousSibling() {
        return wrap(node.getPreviousSibling());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public W3CNode getNextSibling() {
        return wrap(node.getNextSibling());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public W3CNode getParentNode() {
        return wrap(node.getParentNode());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        W3CNode that = (W3CNode) other;
        return Objects.equals(node, that.node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(node);
    }

    private W3CNode wrap(Node n) {
        return n == null ? null : new W3CNode(n);
    }

}
