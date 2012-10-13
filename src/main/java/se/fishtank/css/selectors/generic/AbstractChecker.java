package se.fishtank.css.selectors.generic;

import java.util.Collection;

import se.fishtank.css.util.Assert;


public abstract class AbstractChecker<Node> implements Checker<Node> {

	protected final NodeHelper<Node> helper;

    /** The set of nodes to check. */
    protected Collection<Node> nodes;
    
    /** The result of the checks. */
    protected Collection<Node> result;
    
	public AbstractChecker(NodeHelper<Node> helper) {
        Assert.notNull(helper, "helper is null!");
		this.helper = helper;
	}
}
