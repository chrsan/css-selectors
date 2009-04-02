package se.fishtank.css.selectors.dom;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Helper methods for DOM operations.
 * 
 * @author Christer Sandberg
 */
public class DOMHelper {

    /**
     * Private CTOR.
     */
    private DOMHelper() {
    }
    
    /**
     * Get the next sibling element.
     * 
     * @param node The start node.
     * @return The next sibling element or {@code null}.
     */
    public static final Element getNextSiblingElement(Node node) {
        Node n = node.getNextSibling();
        while (n != null && n.getNodeType() != Node.ELEMENT_NODE) {
            n = n.getNextSibling();
        }
        
        return (Element) n;
    }
    
    /**
     * Get the previous sibling element.
     * 
     * @param node The start node.
     * @return The previous sibling element or {@code null}.
     */
    public static final Element getPreviousSiblingElement(Node node) {
        Node n = node.getPreviousSibling();
        while (n != null && n.getNodeType() != Node.ELEMENT_NODE) {
            n = n.getPreviousSibling();
        }
        
        return (Element) n;
    }
    
}
