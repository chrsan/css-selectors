/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors.selector;

import java.util.Objects;

/**
 * Represents a negation pseudo selector.
 * <p/>
 * See <a href="http://www.w3.org/TR/selectors/#negation">http://www.w3.org/TR/selectors/#negation</a>
 *
 * @author Christer Sandberg
 */
public class PseudoNegationSelector implements SimpleSelector {

    /** The simple selector to negate. */
    public final SimpleSelector selector;

    /**
     * Create a new negation pseudo selector.
     *
     * @param selector The simple selector to negate.
     */
    public PseudoNegationSelector(SimpleSelector selector) {
        this.selector = selector;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SimpleSelectorType getType() {
        return SimpleSelectorType.PSEUDO_NEGATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        PseudoNegationSelector that = (PseudoNegationSelector) other;
        return Objects.equals(selector, that.selector);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return selector.hashCode();
    }

}
