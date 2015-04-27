/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors.matching;

import se.fishtank.css.selectors.dom.DOMNode;
import se.fishtank.css.selectors.selector.SimpleSelector;

/**
 * A simple selector matcher can be used to match a node which isn't matched
 * by the default matching machinery. I.e. to match custom selectors that isn't
 * covered by the specification.
 *
 * @author Christer Sandberg
 */
public interface SimpleSelectorMatcher<T extends DOMNode<T, ?>> {

    /**
     * Matches the simple selector against the node.
     *
     * @param simpleSelector Simple selector
     * @param node The root node.
     * @return {@code true} on success, {@code false} otherwise.
     */
    public boolean matches(SimpleSelector simpleSelector, T node);

}
