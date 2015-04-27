/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors.selector;

import java.util.Objects;

/**
 * Represents a {@code nth-*} pseudo class selector.
 * <p/>
 * See <a href="http://www.w3.org/TR/selectors/#pseudo-classes">http://www.w3.org/TR/selectors/#pseudo-classes</a>
 *
 * @author Christer Sandberg
 */
public class PseudoNthSelector implements SimpleSelector {

    /** The name of this selector. */
    public final String name;

    /** The <i>a</i> argument of this selector. */
    public final int a;

    /** The <i>b</i> argument of this selector. */
    public final int b;

    /**
     * Create a new {@code nth-*} pseudo class selector.
     *
     * @param name The name of the selector.
     * @param a The <i>a</i> argument value.
     * @param b The <i>b</i> argument value.
     */
    public PseudoNthSelector(String name, int a, int b) {
        this.name = name;
        this.a = a;
        this.b = b;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SimpleSelectorType getType() {
        return SimpleSelectorType.PSEUDO_NTH;
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

        PseudoNthSelector that = (PseudoNthSelector) other;
        return Objects.equals(a, that.a) &&
                Objects.equals(b, that.b) &&
                Objects.equals(name, that.name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, a, b);
    }

}
