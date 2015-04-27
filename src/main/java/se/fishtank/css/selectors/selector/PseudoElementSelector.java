/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors.selector;

import java.util.Objects;

/**
 * Represents a pseudo element selector.
 * <p/>
 * See <a href="http://www.w3.org/TR/selectors/#pseudo-elements">http://www.w3.org/TR/selectors/#pseudo-elements</a>
 *
 * @author Christer Sandberg
 */
public class PseudoElementSelector implements SimpleSelector {

    /** The pseudo element value. */
    public final String value;

    /**
     * Create a new pseudo element selector.
     *
     * @param value The pseudo element value.
     */
    public PseudoElementSelector(String value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SimpleSelectorType getType() {
        return SimpleSelectorType.PSEUDO_ELEMENT;
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

        PseudoElementSelector that = (PseudoElementSelector) other;
        return Objects.equals(value, that.value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
