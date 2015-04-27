/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors.selector;

import java.util.Objects;

/**
 * Represents a type selector.
 * <p/>
 * See <a href="http://www.w3.org/TR/selectors/#type-selectors">http://www.w3.org/TR/selectors/#type-selectors</a>
 *
 * @author Christer Sandberg
 */
public class LocalNameSelector implements SimpleSelector {

    /** The tag name. */
    public final String name;

    /**
     * Create a new type selector.
     *
     * @param name The tag name.
     */
    public LocalNameSelector(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SimpleSelectorType getType() {
        return SimpleSelectorType.LOCAL_NAME;
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

        LocalNameSelector that = (LocalNameSelector) other;
        return Objects.equals(name, that.name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
