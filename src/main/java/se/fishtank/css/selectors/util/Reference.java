/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors.util;

/**
 * A reference object.
 *
 * @author Christer Sandberg
 */
public class Reference<T> {

    /** The object this reference refers to. */
    public T referent;

    /**
     * Create a new empty reference.
     */
    public Reference() {
        this(null);
    }

    /**
     * Create a new reference.
     *
     * @param referent The object the new reference will refer to.
     */
    public Reference(T referent) {
        this.referent = referent;
    }

}
