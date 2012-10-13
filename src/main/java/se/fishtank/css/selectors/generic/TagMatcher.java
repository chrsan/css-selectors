package se.fishtank.css.selectors.generic;

import java.util.Collection;
import java.util.LinkedHashSet;

import se.fishtank.css.selectors.NodeSelectorException;
import se.fishtank.css.selectors.Selector;
import se.fishtank.css.util.Assert;


public class TagMatcher<JsonNode> extends AbstractChecker<JsonNode> {
   
    /** The selector to check against. */
    protected final Selector selector;
    
    /** The set of nodes to check. */
    protected Collection<JsonNode> nodes;
    
    /** The result of the checks. */
    protected Collection<JsonNode> result;
    
    /**
     * Create a new instance.
     * 
     * @param selector The selector to check against.
     */
    public TagMatcher(NodeHelper<JsonNode> helper, Selector selector) {
    	super(helper);
        Assert.notNull(selector, "selector is null!");
        this.selector = selector;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<JsonNode> check(Collection<JsonNode> nodes) {
        Assert.notNull(nodes, "nodes is null!");
        this.nodes = nodes;

        result = new LinkedHashSet<JsonNode>();
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
        for (JsonNode node : nodes) {
        	Collection<JsonNode> nodes = new LinkedHashSet<JsonNode>();
        	nodes.add(node);
        	nodes.addAll(helper.getDescendentNodes(node));
        	
        	for(JsonNode n : nodes) {
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
        for (JsonNode node : nodes) {
        	Collection<? extends JsonNode> childNodes = helper.getChildNodes(node);
        	for (JsonNode child : childNodes) {
                if (matchTag(child))
                	result.add(child);	
			}
        }
    }

	private boolean matchTag(JsonNode child) {
		String tag = selector.getTagName();
		return tagEquals(tag, helper.getName(child)) || tag.equals(Selector.UNIVERSAL_TAG);
	}

	/**
     * Add adjacent sibling elements.
     * 
     * @see <a href="http://www.w3.org/TR/css3-selectors/#adjacent-sibling-combinators">Adjacent sibling combinator</a>
     */
    private void addAdjacentSiblingElements() {
        for (JsonNode node : nodes) {
            JsonNode n = helper.getNextSibling(node);
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
        for (JsonNode node : nodes) {
            JsonNode n = helper.getNextSibling(node);
            while (n != null) {
                String tag = selector.getTagName();
                if (tagEquals(tag, helper.getName(n)) || tag.equals(Selector.UNIVERSAL_TAG)) {
                    result.add(n);
                }
                
                n = helper.getNextSibling(n);
            }
        }
    }

    /**
     * Determine if the two specified tag names are equal.
     *
     * @param tag1 A tag name.
     * @param tag2 A tag name.
     * @return <code>true</code> if the tag names are equal, <code>false</code> otherwise.
     */
    private boolean tagEquals(String tag1, String tag2) {
    	return helper.namesEqual(tag1, tag2);
    }

}
