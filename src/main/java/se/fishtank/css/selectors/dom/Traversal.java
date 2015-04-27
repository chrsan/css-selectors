/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors.dom;

/**
 * DOM traversal
 *
 * @author Christer Sandberg
 */
public class Traversal {

    private Traversal() {
    }

    /**
     * Traverse all element nodes starting at {@code root}.
     *
     * @param root The root node.
     * @param visitor The visitor that will be called for each element node.
     */
    public static <T extends DOMNode<T, ?>> void traverseElements(T root, Visitor<T> visitor) {
        if (root.getType() == DOMNode.Type.ELEMENT) {
            visitor.visit(root);
        }

        for (T child = root.getFirstChild(); child != null; child = child.getNextSibling()) {
            traverseElements(child, visitor);
        }
    }

}
