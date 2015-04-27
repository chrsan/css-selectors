/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors.selector;

/**
 * Represents a simple selector.
 * <p/>
 * See <a href="http://www.w3.org/TR/selectors/#simple-selectors">http://www.w3.org/TR/selectors/#simple-selectors</a>
 *
 * @author Christer Sandberg
 */
public interface SimpleSelector {

    /**
     * Returns the type of this simple selector.
     *
     * @return The simple selector type.
     */
    public SimpleSelectorType getType();

}
