/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors.selector;

import java.util.Objects;

/**
 * Represents a functional pseudo class selector that is not of type {@code :nth-*} or {@code :not}
 * <p/>
 * See <a href="http://www.w3.org/TR/selectors/#w3cselgrammar">http://www.w3.org/TR/selectors/#w3cselgrammar</a>
 *
 * @author Christer Sandberg
 */
public class PseudoFunctionSelector implements SimpleSelector {

    /** The name of this selector. */
    public final String name;

    /** The arguments of this selector. */
    public final String arguments;

    /**
     * Create a new functional pseudo class selector.
     *
     * @param name The name of the selector.
     * @param arguments The arguments.
     */
    public PseudoFunctionSelector(String name, String arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SimpleSelectorType getType() {
        return SimpleSelectorType.PSEUDO_FUNCTION;
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

        PseudoFunctionSelector that = (PseudoFunctionSelector) other;
        return Objects.equals(name, that.name) &&
                Objects.equals(arguments, that.arguments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, arguments);
    }

}
