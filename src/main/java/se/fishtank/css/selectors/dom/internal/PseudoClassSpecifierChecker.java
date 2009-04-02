package se.fishtank.css.selectors.dom.internal;

import java.util.LinkedHashSet;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.fishtank.css.selectors.NodeSelectorException;
import se.fishtank.css.selectors.dom.DOMHelper;
import se.fishtank.css.selectors.specifier.PseudoClassSpecifier;
import se.fishtank.css.util.Assert;

/**
 * A {@link NodeTraversalChecker} that check if a node matches
 * the {@linkplain PseudoClassSpecifier pseudo-class specifier} set.
 * 
 * @author Christer Sandberg
 */
public class PseudoClassSpecifierChecker extends NodeTraversalChecker {
    
    /** The pseudo-class specifier to check against. */
    private final PseudoClassSpecifier specifier;
    
    /** The set of nodes to check. */
    private Set<Node> nodes;
    
    /** The result of the checks. */
    private Set<Node> result;
    
    /**
     * Create a new instance.
     * 
     * @param specifier The pseudo-class specifier to check against.
     */
    public PseudoClassSpecifierChecker(PseudoClassSpecifier specifier) {
        Assert.notNull(specifier, "specifier is null!");
        this.specifier = specifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Node> check(Set<Node> nodes) throws NodeSelectorException {
        Assert.notNull(nodes, "nodes is null!");
        this.nodes = nodes;
        result = new LinkedHashSet<Node>();
        String value = specifier.getValue();
        if ("empty".equals(value)) {
            getEmptyElements();
        } else if ("first-child".equals(value)) {
            getFirstChildElements();
        } else if ("first-of-type".equals(value)) {
            getFirstOfType();
        } else if ("last-child".equals(value)) {
            getLastChildElements();
        } else if ("last-of-type".equals(value)) {
            getLastOfType();
        } else if ("only-child".equals(value)) {
            getOnlyChildElements();
        } else if ("only-of-type".equals(value)) {
            getOnlyOfTypeElements();
        } else {
            throw new NodeSelectorException("Unknown pseudo class: " + value);
        }
        
        return result;
    }
    
    /**
     * Get {@code :empty} elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#empty-pseudo"><code>:empty</code> pseudo-class</a>
     */
    private void getEmptyElements() {
        for (Node node : nodes) {
            boolean empty = true;
            NodeList nl = node.getChildNodes();
            for (int i = 0; i < nl.getLength(); i++) {
                Node n = nl.item(i);
                if (n.getNodeType() == Node.ELEMENT_NODE) {
                    empty = false;
                    break;
                } else if (n.getNodeType() == Node.TEXT_NODE) {
                    // TODO: Should we trim the text and see if it's length 0?
                    String value = n.getNodeValue();
                    if (value.length() > 0) {
                        empty = false;
                        break;
                    }
                }
            }
            
            if (empty) {
                result.add(node);
            }
        }
    }
    
    /**
     * Get {@code :first-child} elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#first-child-pseudo"><code>:first-child</code> pseudo-class</a>
     */
    private void getFirstChildElements() {
        for (Node node : nodes) {
            if (DOMHelper.getPreviousSiblingElement(node) == null) {
                result.add(node);
            }
        }
    }
    
    /**
     * Get {@code :first-of-type} elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#first-of-type-pseudo"><code>:first-of-type</code> pseudo-class</a>
     */
    private void getFirstOfType() {
        for (Node node : nodes) {
            Node n = DOMHelper.getPreviousSiblingElement(node);
            while (n != null) {
                if (n.getNodeName().equals(node.getNodeName())) {
                    break;
                }
                
                n = DOMHelper.getPreviousSiblingElement(n);
            }
            
            if (n == null) {
                result.add(node);
            }
        }
    }

    /**
     * Get {@code :last-child} elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#last-child-pseudo"><code>:last-child</code> pseudo-class</a>
     */
    private void getLastChildElements() {
        for (Node node : nodes) {
            if (DOMHelper.getNextSiblingElement(node) == null) {
                result.add(node);
            }
        }
    }
    
    /**
     * Get {@code :last-of-type} elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#last-of-type-pseudo"><code>:last-of-type</code> pseudo-class</a>
     */
    private void getLastOfType() {
        for (Node node : nodes) {
            Node n = DOMHelper.getNextSiblingElement(node);
            while (n != null) {
                if (n.getNodeName().equals(node.getNodeName())) {
                    break;
                }
                
                n = DOMHelper.getNextSiblingElement(n);
            }
            
            if (n == null) {
                result.add(node);
            }
        }
    }
    
    /**
     * Get {@code :only-child} elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#only-child-pseudo"><code>:only-child</code> pseudo-class</a>
     */
    private void getOnlyChildElements() {
        for (Node node : nodes) {
            if (DOMHelper.getPreviousSiblingElement(node) == null &&
                    DOMHelper.getNextSiblingElement(node) == null) {
                result.add(node);
            }
        }
    }
    
    /**
     * Get {@code :only-of-type} elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#only-of-type-pseudo"><code>:only-of-type</code> pseudo-class</a>
     */
    private void getOnlyOfTypeElements() {
        for (Node node : nodes) {
            Node n = DOMHelper.getPreviousSiblingElement(node);
            while (n != null) {
                if (n.getNodeName().equals(node.getNodeName())) {
                    break;
                }
                
                n = DOMHelper.getPreviousSiblingElement(n);
            }
            
            if (n == null) {
                n = DOMHelper.getNextSiblingElement(node);
                while (n != null) {
                    if (n.getNodeName().equals(node.getNodeName())) {
                        break;
                    }
                    
                    n = DOMHelper.getNextSiblingElement(n);
                }
                
                if (n == null) {
                    result.add(node);
                }
            }
        }
    }

}
