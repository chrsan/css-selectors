/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors.selector;

import java.util.Objects;

/**
 * Represents an attribute selector.
 * <p/>
 * An attribute selector will also be used to represent class selectors and ID selectors.
 * <p/>
 * See <a href="http://www.w3.org/TR/selectors/#attribute-selectors">http://www.w3.org/TR/selectors/#attribute-selectors</a>
 *
 * @author Christer Sandberg
 */
public class AttributeSelector implements SimpleSelector {

    public static enum Match {
        EXISTS, EQUALS, INCLUDES, BEGINS, ENDS, CONTAINS, HYPHENS
    }

    /** How to match the attribute. */
    public final Match match;

    /** The attribute name. */
    public final String name;

    /** The attribute value. */
    public final String value;

    /**
     * Create a new attribute selector.
     *
     * @param match How to match the attribute.
     * @param name The attribute name.
     * @param value The attribute value.
     */
    public AttributeSelector(Match match, String name, String value) {
        this.match = match;
        this.name = name;
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SimpleSelectorType getType() {
        return SimpleSelectorType.ATTRIBUTE;
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

        AttributeSelector that = (AttributeSelector) other;
        return Objects.equals(match, that.match) &&
                Objects.equals(name, that.name) &&
                Objects.equals(value, that.value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(match, name, value);
    }

}
