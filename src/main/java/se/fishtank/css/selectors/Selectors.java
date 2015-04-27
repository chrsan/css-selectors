/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors;

import java.util.LinkedList;
import java.util.List;

import se.fishtank.css.selectors.dom.DOMNode;
import se.fishtank.css.selectors.dom.Traversal;
import se.fishtank.css.selectors.dom.Visitor;
import se.fishtank.css.selectors.matching.SelectorMatcher;
import se.fishtank.css.selectors.matching.SimpleSelectorMatcher;
import se.fishtank.css.selectors.parser.ParserException;
import se.fishtank.css.selectors.parser.SelectorParser;
import se.fishtank.css.selectors.selector.Selector;
import se.fishtank.css.selectors.util.Reference;

/**
 * Simplified selectors API.
 *
 * @author Christer Sandberg
 */
public class Selectors<T, U extends DOMNode<U, T>> {

    /** The root node. */
    private final U rootNode;

    /** The selectors matcher. */
    private final SelectorMatcher<U> selectorMatcher;

    /**
     * Create a new instance.
     *
     * @param rootNode The root node.
     */
    public Selectors(U rootNode) {
        this(rootNode, null);
    }

    /**
     * Create a new instance.
     *
     * @param rootNode The root node.
     * @param simpleSelectorMatcher A simple selector matcher for custom matching.
     */
    public Selectors(U rootNode, SimpleSelectorMatcher<U> simpleSelectorMatcher) {
        this.rootNode = rootNode;
        this.selectorMatcher = new SelectorMatcher<>(simpleSelectorMatcher);
    }

    /**
     * Returns the root node.
     *
     * @return The root node.
     */
    public U getRootNode() {
        return rootNode;
    }

    /**
     * Returns the first matching node or {@code null} if match was found.
     *
     * @param selectors A list of selectors.
     * @return The first matching node or {@code null}
     */
    public T querySelector(final List<Selector> selectors) {
        final Reference<T> ref = new Reference<>();
        final RuntimeException done = new RuntimeException();
        try {
            Traversal.traverseElements(rootNode, new Visitor<U>() {
                @Override
                public void visit(U node) {
                    if (selectorMatcher.matchesSelectors(selectors, node)) {
                        ref.referent = node.getUnderlying();
                        throw done;
                    }
                }
            });
        } catch (RuntimeException e) {
            if (e != done) {
                throw e;
            }
        }

        return ref.referent;
    }


    /**
     * Returns the first matching node or {@code null} if match was found.
     *
     * @param selectors A selectors string.
     * @return The first matching node or {@code null}
     * @throws ParserException On errors parsing the given selectors string.
     */
    public T querySelector(String selectors) throws ParserException {
        return querySelector(parse(selectors));
    }

    /**
     * Returns a list of all the matching nodes.
     *
     * @param selectors A list of selectors.
     * @return A list of all the matching nodes.
     */
    public List<T> querySelectorAll(final List<Selector> selectors) {
        final LinkedList<T> result = new LinkedList<>();
        Traversal.traverseElements(rootNode, new Visitor<U>() {
            @Override
            public void visit(U node) {
                if (selectorMatcher.matchesSelectors(selectors, node)) {
                    result.add(node.getUnderlying());
                }
            }
        });

        return result;
    }

    /**
     * Returns a list of all the matching nodes.
     *
     * @param selectors A selectors string.
     * @return A list of all the matching nodes.
     * @throws ParserException On errors parsing the given selectors string.
     */
    public List<T> querySelectorAll(String selectors) throws ParserException {
        return querySelectorAll(parse(selectors));
    }

    /**
     * Parses the given selectors string and returns a selector list.
     *
     * @param selectors The selectors string to parse.
     * @return A selector list.
     * @throws ParserException On parsing errors.
     */
    public static List<Selector> parse(String selectors) throws ParserException {
        return SelectorParser.parse(selectors);
    }

}
