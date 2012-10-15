package se.fishtank.css.selectors.generic;

import java.util.Collection;
import java.util.LinkedHashSet;

import se.fishtank.css.selectors.NodeSelectorException;
import se.fishtank.css.selectors.Selector;
import se.fishtank.css.util.Assert;


public class TagChecker<Node> extends AbstractChecker<Node> {
   
    /** The selector to check against. */
    protected final Selector selector;
    
    /** The set of nodes to check. */
    protected Collection<Node> nodes;
    
    /** The result of the checks. */
    protected Collection<Node> result;
    
    /**
     * Create a new instance.
     * 
     * @param selector The selector to check against.
     */
    public TagChecker(NodeHelper<Node> helper, Selector selector) {
    	super(helper);
        Assert.notNull(selector, "selector is null!");
        this.selector = selector;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Node> check(Collection<Node> nodes) {
        Assert.notNull(nodes, "nodes is null!");
        this.nodes = nodes;

        result = new LinkedHashSet<Node>();
        switch (selector.getCombinator()) {
        case DESCENDANT:
            addDescendantElements();
            break;
        case CHILD:
            addChildElements();
            break;
        case ADJACENT_SIBLING:
            addAdjacentSiblingElements();
            break;
        case GENERAL_SIBLING:
            addGeneralSiblingElements();
            break;
        }
        
        return result;
    }
    
    /**
     * Add descendant elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#descendant-combinators">Descendant combinator</a>
     * 
     * @throws NodeSelectorException If one of the nodes have an illegal type.
     */
    private void addDescendantElements() {
        for (Node node : nodes) {
        	Collection<Node> nodes = new LinkedHashSet<Node>();

        	nodes.addAll(helper.getDescendentNodes(node));
        	
        	for(Node n : nodes) {
        		if (matchTag(n))
        			result.add(n);
        	}
        }
    }

	/**
     * Add child elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#child-combinators">Child combinators</a>
     */
    private void addChildElements() {
        for (Node node : nodes) {
        	Collection<? extends Node> childNodes = helper.getChildNodes(node);
        	for (Node child : childNodes) {
                if (matchTag(child))
                	result.add(child);	
			}
        }
    }

	private boolean matchTag(Node child) {
		String tag = selector.getTagName();
		return helper.nameMatches(child, tag);
	}

	/**
     * Add adjacent sibling elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#adjacent-sibling-combinators">Adjacent sibling combinator</a>
     */
    private void addAdjacentSiblingElements() {
        for (Node node : nodes) {
            Node n = helper.getNextSibling(node);
            if (n != null) {
                if (matchTag(n))
                	result.add(n);	
            }
        }
    }
    
	/**
     * Add general sibling elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#general-sibling-combinators">General sibling combinator</a>
     */
    private void addGeneralSiblingElements() {
        for (Node node : nodes) {
            Node n = helper.getNextSibling(node);
            while (n != null) {
                String tag = selector.getTagName();
                if (helper.nameMatches(n, tag)) {
                    result.add(n);
                }
                
                n = helper.getNextSibling(n);
            }
        }
    }
}
