/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors.selector;

import java.util.Objects;

/**
 * Represents a pseudo class selector.
 * <p/>
 * See <a href="http://www.w3.org/TR/selectors/#pseudo-classes">http://www.w3.org/TR/selectors/#pseudo-classes</a>
 *
 * @author Christer Sandberg
 */
public class PseudoClassSelector implements SimpleSelector {

    /** The pseudo class value. */
    public final String value;

    /**
     * Create a new pseudo class selector.
     *
     * @param value The pseudo class value.
     */
    public PseudoClassSelector(String value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SimpleSelectorType getType() {
        return SimpleSelectorType.PSEUDO_CLASS;
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

        PseudoClassSelector that = (PseudoClassSelector) other;
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
