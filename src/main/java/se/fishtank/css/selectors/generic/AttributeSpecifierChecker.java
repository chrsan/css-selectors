package se.fishtank.css.selectors.generic;

import java.util.Collection;
import java.util.LinkedHashSet;

import se.fishtank.css.selectors.specifier.AttributeSpecifier;
import se.fishtank.css.util.Assert;


public class AttributeSpecifierChecker<Node> extends AbstractChecker<Node> {

	/** The attribute specifier to check against. */
	protected final AttributeSpecifier specifier;

	public AttributeSpecifierChecker(NodeHelper<Node> helper, AttributeSpecifier specifier) {
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
	    Collection<Node> result = new LinkedHashSet<Node>();
	    for (Node node : nodes) {
	        String name = specifier.getName();
	        if (!helper.hasAttribute(node, name))
	        	continue;
			String attribute = helper.getAttribute(node, name);
			
			// It just have to be present.
			if (specifier.getValue() == null && attribute!=null) {
				result.add(node);
	            continue;
	        }

        	String spec = specifier.getValue();
            switch (specifier.getMatch()) {
            case EXACT:
                if (attribute.equals(spec)) {
                    result.add(node);
                }
                
                break;
            case HYPHEN:
                if (attribute.equals(spec) || attribute.startsWith(spec + '-')) {
                    result.add(node);
                }
                
                break;
            case PREFIX:
                if (attribute.startsWith(spec)) {
                    result.add(node);
                }
                
                break;
            case SUFFIX:
                if (attribute.endsWith(spec)) {
                    result.add(node);
                }
                
                break;
            case CONTAINS:
                if (attribute.contains(spec)) {
                    result.add(node);
                }
                
                break;
            case LIST:
                for (String v : attribute.split("\\s+")) {
                    if (v.equals(spec)) {
                        result.add(node);
                    }
                }
                
                break;
            }
	    }
	    
	    return result;
	}

}