/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors.util;

import java.util.Objects;

/**
 * A simple pair of values.
 *
 * @param <T> The type of the first value.
 * @param <U> The type of the second value.
 *
 * @author Christer Sandberg
 */
public class Pair<T, U> {

    /** The first value. */
    public final T first;

    /** The second value. */
    public final U second;

    /**
     * Create a new pair.
     *
     * @param first The first value.
     * @param second The second value.
     */
    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
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

        Pair<?, ?> pair = (Pair<?, ?>) other;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

}
