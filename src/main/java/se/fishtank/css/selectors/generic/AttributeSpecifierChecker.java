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
			Node attribute = helper.getAttribute(node, name);
			
			if (attribute==null)
				continue;

			// It just have to be present.
			if (specifier.getValue() == null && attribute!=null) {
				result.add(node);
	            continue;
	        }

        	String value = helper.getValue(attribute);
            String spec = specifier.getValue();
            switch (specifier.getMatch()) {
            case EXACT:
                if (value.equals(spec)) {
                    result.add(node);
                }
                
                break;
            case HYPHEN:
                if (value.equals(spec) || value.startsWith(spec + '-')) {
                    result.add(node);
                }
                
                break;
            case PREFIX:
                if (value.startsWith(spec)) {
                    result.add(node);
                }
                
                break;
            case SUFFIX:
                if (value.endsWith(spec)) {
                    result.add(node);
                }
                
                break;
            case CONTAINS:
                if (value.contains(spec)) {
                    result.add(node);
                }
                
                break;
            case LIST:
                for (String v : value.split("\\s+")) {
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