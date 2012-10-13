package se.fishtank.css.selectors.generic;

import java.util.Collection;
import java.util.LinkedHashSet;

import se.fishtank.css.selectors.generic.NodeHelper.Index;
import se.fishtank.css.selectors.specifier.PseudoClassSpecifier;
import se.fishtank.css.util.Assert;


public class PseudoClassSpecifierChecker<Node> extends AbstractChecker<Node> {

	/** The pseudo-class specifier to check against. */
    protected final PseudoClassSpecifier specifier;
    
    /**
     * Create a new instance.
     * 
     * @param specifier The pseudo-class specifier to check against.
     */
    public PseudoClassSpecifierChecker(NodeHelper<Node> helper, PseudoClassSpecifier specifier) {
    	super(helper);
        Assert.notNull(specifier, "specifier is null!");
        this.specifier = specifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Node> check(Collection<Node> nodes) {
        Assert.notNull(nodes, "nodes is null!");
        this.nodes = nodes;

        this.result = new LinkedHashSet<Node>();

        String value = specifier.getValue();
        if ("empty".equals(value)) {
            addEmptyElements();
        } else if ("first-child".equals(value)) {
            addFirstChildElements();
        } else if ("first-of-type".equals(value)) {
            addFirstOfType();
        } else if ("last-child".equals(value)) {
            addLastChildElements();
        } else if ("last-of-type".equals(value)) {
            addLastOfType();
        } else if ("only-child".equals(value)) {
            addOnlyChildElements();
        } else if ("only-of-type".equals(value)) {
            addOnlyOfTypeElements();
        } else if ("root".equals(value)) {
            addRootElement();
        } else {
            throw new RuntimeException("Unknown pseudo class: " + value);
        }
        
        return result;
    }

    /**
     * Add {@code :empty} elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#empty-pseudo"><code>:empty</code> pseudo-class</a>
     */
    private void addEmptyElements() {
        for (Node node : nodes) {
        	if (helper.getChildNodes(node).isEmpty())
        		result.add(node);
        }
    }

    /**
     * Add {@code :first-child} elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#first-child-pseudo"><code>:first-child</code> pseudo-class</a>
     */
    private void addFirstChildElements() {
        for (Node node : nodes) {
        	Index index = helper.getIndexInParent(node, false);
        	if (index.index == 0)
                result.add(node);
        }
    }
    
    /**
     * Add {@code :first-of-type} elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#first-of-type-pseudo"><code>:first-of-type</code> pseudo-class</a>
     */
    private void addFirstOfType() {
        for (Node node : nodes) {
        	Index index = helper.getIndexInParent(node, true);
        	if (index.index == 0)
                result.add(node);
        }
    }

    /**
     * Add {@code :last-child} elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#last-child-pseudo"><code>:last-child</code> pseudo-class</a>
     */
    private void addLastChildElements() {
        for (Node node : nodes) {
        	Index index = helper.getIndexInParent(node, false);
        	if (index.index == (index.size-1))
                result.add(node);
        }
    }
    
    /**
     * Add {@code :last-of-type} elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#last-of-type-pseudo"><code>:last-of-type</code> pseudo-class</a>
     */
    private void addLastOfType() {
        for (Node node : nodes) {
        	Index index = helper.getIndexInParent(node, true);
        	if (index.index == (index.size-1))
                result.add(node);
        }
    }
    
    /**
     * Add {@code :only-child} elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#only-child-pseudo"><code>:only-child</code> pseudo-class</a>
     */
    private void addOnlyChildElements() {
        for (Node node : nodes) {
        	Index index = helper.getIndexInParent(node, false);
        	if (index.size==1)
                result.add(node);
        }
    }
    
    /**
     * Add {@code :only-of-type} elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#only-of-type-pseudo"><code>:only-of-type</code> pseudo-class</a>
     */
    private void addOnlyOfTypeElements() {
        for (Node node : nodes) {
        	Index index = helper.getIndexInParent(node, true);
        	if (index.size==1)
                result.add(node);
        }
    }

    /**
     * Add the {@code :root} element.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#root-pseudo"><code>:root</code> pseudo-class</a>
     */
    private void addRootElement() {
        for (Node node : nodes) {
        	Node root = helper.getRoot(node);
        	
        	if (root != null)
        		result.add(root);
        }
    }
}
