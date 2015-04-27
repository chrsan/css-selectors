/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors.matching;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import se.fishtank.css.selectors.dom.DOMNode;
import se.fishtank.css.selectors.selector.*;

/**
 * Selector matching
 *
 * @author Christer Sandberg
 */
public class SelectorMatcher<T extends DOMNode<T, ?>> {

    /** Matching result when matching compound selectors */
    private static enum MatchingResult {
        MATCHED, NOT_MATCHED, RESTART_FROM_CLOSEST_DESCENDANT, RESTART_FROM_CLOSEST_LATER_SIBLING
    }

    /** Space regex */
    public static final Pattern SPACE_REGEX = Pattern.compile("[ \\t\\r\\n\\f]+");

    /** Simple selector matcher for custom matching. */
    private final SimpleSelectorMatcher<T> simpleSelectorMatcher;

    /**
     * Create a selector matcher.
     *
     * @param simpleSelectorMatcher A simple selector matcher for custom matching or {@code null}
     */
    public SelectorMatcher(SimpleSelectorMatcher<T> simpleSelectorMatcher) {
        this.simpleSelectorMatcher = simpleSelectorMatcher;
    }

    /**
     * Create a selector matcher.
     */
    public SelectorMatcher() {
        this.simpleSelectorMatcher = null;
    }

    /**
     * Matches the given selectors against the given node.
     *
     * @param selectors The selectors
     * @param node The root node.
     * @return {@code true} or {@code false}
     */
    public boolean matchesSelectors(List<Selector> selectors, T node) {
        for (Selector selector : selectors) {
            if (matchesSelector(selector, node)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Matches the given selector against the given node.
     *
     * @param selector The selector
     * @param node The root node.
     * @return {@code true} or {@code false}
     */
    public boolean matchesSelector(Selector selector, T node) {
        return selector.pseudoElement == null &&
                matchesCompoundSelector(selector.compoundSelector, node) == MatchingResult.MATCHED;
    }

    /**
     * Matches the given simple selector against the given node.
     *
     * @param selector The simple selector.
     * @param node The root node.
     * @return {@code true} or {@code false}
     */
    public boolean matchesSimpleSelector(SimpleSelector selector, T node) {
        if (node.getType() == DOMNode.Type.DOCUMENT) {
            for (node = node.getFirstChild(); node != null; node = node.getNextSibling()) {
                if (node.getType() == DOMNode.Type.ELEMENT) {
                    break;
                }
            }
        }

        if (node == null || node.getType() != DOMNode.Type.ELEMENT) {
            return false;
        }

        if (selector instanceof LocalNameSelector) {
            return node.getData().equalsIgnoreCase(((LocalNameSelector) selector).name);
        } else if (selector instanceof AttributeSelector) {
            return matchesAttributeSelector((AttributeSelector) selector, node);
        } else if (selector instanceof PseudoNegationSelector) {
            return !matchesSimpleSelector(((PseudoNegationSelector) selector).selector, node);
        } else if (selector instanceof PseudoClassSelector) {
            if (matchesPseudoClassSelector((PseudoClassSelector) selector, node)) {
                return true;
            }
        } else if (selector instanceof PseudoNthSelector) {
            if (matchesPseudoNthSelector((PseudoNthSelector) selector, node)) {
                return true;
            }
        }

        return simpleSelectorMatcher != null && simpleSelectorMatcher.matches(selector, node);
    }

    /**
     * Matches the given compound selector against the given node.
     *
     * @param selector The compound selector.
     * @param node The root node.
     * @return A matching result.
     */
    private MatchingResult matchesCompoundSelector(CompoundSelector selector, T node) {
        for (SimpleSelector simpleSelector : selector.simpleSelectors) {
            if (!matchesSimpleSelector(simpleSelector, node)) {
                return MatchingResult.RESTART_FROM_CLOSEST_LATER_SIBLING;
            }
        }

        if (selector.previous == null) {
            return MatchingResult.MATCHED;
        }

        boolean siblings = false;
        MatchingResult candidateNotFound = MatchingResult.NOT_MATCHED;

        switch (selector.previous.first) {
        case NEXT_SIBLING:
        case LATER_SIBLING:
            siblings = true;
            candidateNotFound = MatchingResult.RESTART_FROM_CLOSEST_DESCENDANT;
        }

        while (true) {
            T nextNode;
            if (siblings) {
                nextNode = node.getPreviousSibling();
            } else {
                nextNode = node.getParentNode();
            }

            if (nextNode == null) {
                return candidateNotFound;
            } else {
                node = nextNode;
            }

            if (node.getType() == DOMNode.Type.ELEMENT) {
                MatchingResult result = matchesCompoundSelector(selector.previous.second, node);
                if (result == MatchingResult.MATCHED || result == MatchingResult.NOT_MATCHED) {
                    return result;
                }

                switch (selector.previous.first) {
                case CHILD:
                    return MatchingResult.RESTART_FROM_CLOSEST_DESCENDANT;
                case NEXT_SIBLING:
                    return result;
                case LATER_SIBLING:
                    if (result == MatchingResult.RESTART_FROM_CLOSEST_DESCENDANT) {
                        return result;
                    }
                }
            }
        }
    }

    /**
     * Matches the given attribute selector against the given node.
     *
     * @param selector The attribute selector.
     * @param node The node to match against.
     * @return {@code true} or {@code false}
     */
    private boolean matchesAttributeSelector(AttributeSelector selector, T node) {
        Map<String, String> attributes = node.getAttributes();
        if (attributes == null) {
            return false;
        }

        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            if (!entry.getKey().equals(selector.name)) {
                continue;
            }

            switch (selector.match) {
            case EXISTS:
                return true;
            case EQUALS:
                return entry.getValue().equals(selector.value);
            case INCLUDES:
                for (String v : SPACE_REGEX.split(entry.getValue())) {
                    if (v.equals(selector.value)) {
                        return true;
                    }
                }

                return false;
            case BEGINS:
                return entry.getValue().startsWith(selector.value);
            case ENDS:
                return entry.getValue().endsWith(selector.value);
            case CONTAINS:
                return entry.getValue().contains(selector.value);
            case HYPHENS:
                String v = entry.getValue();
                return v.equals(selector.value) || v.startsWith(selector.value + "-");
            }

            return false;
        }

        return false;
    }

    /**
     * Matches the given pseudo class selector against the given node.
     *
     * @param selector The pseudo class selector.
     * @param node The root node.
     * @return {@code true} or {@code false}
     */
    private boolean matchesPseudoClassSelector(PseudoClassSelector selector, T node) {
        switch (selector.value) {
            case "first-child":
                return matchesFirstOrLastChild(node, true);
            case "last-child":
                return matchesFirstOrLastChild(node, false);
            case "only-child":
                return matchesFirstOrLastChild(node, true) && matchesFirstOrLastChild(node, false);
            case "first-of-type":
                return matchesNthChild(node, 0, 1, true, false);
            case "last-of-type":
                return matchesNthChild(node, 0, 1, true, true);
            case "only-of-type":
                return matchesNthChild(node, 0, 1, true, false) && matchesNthChild(node, 0, 1, true, true);
            case "root":
                T parentNode = node.getParentNode();
                return parentNode != null && parentNode.getType() == DOMNode.Type.DOCUMENT;
            case "empty":
                for (T child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
                    switch (child.getType()) {
                    case ELEMENT:
                        return false;
                    case TEXT:
                        String data = child.getData();
                        if (data != null && !data.isEmpty()) {
                            return false;
                        }
                    }
                }

                return true;
            default:
                return false;
        }
    }

    /**
     * Matches the given {@code nth-*} pseudo class selector against the given node.
     *
     * @param selector The {@code nth-*} pseudo class selector.
     * @param node The root node.
     * @return {@code true} or {@code false}
     */
    private boolean matchesPseudoNthSelector(PseudoNthSelector selector, T node) {
        switch (selector.name) {
        case "nth-child":
            return matchesNthChild(node, selector.a, selector.b, false, false);
        case "nth-last-child":
            return matchesNthChild(node, selector.a, selector.b, false, true);
        case "nth-of-type":
            return matchesNthChild(node, selector.a, selector.b, true, false);
        case "nth-last-of-type":
            return matchesNthChild(node, selector.a, selector.b, true, true);
        default:
            return false;
        }
    }

    /**
     * Matches a first or last child.
     *
     * @param node The root node.
     * @param first If matching is performed against the first child.
     * @return {@code true} or {@code false}
     */
    private boolean matchesFirstOrLastChild(T node, boolean first) {
        while (true) {
            T n;
            if (first) {
                n = node.getPreviousSibling();
            } else {
                n = node.getNextSibling();
            }

            if (n == null) {
                n = node.getParentNode();
                return n != null && n.getType() != DOMNode.Type.DOCUMENT;
            } else {
                if (n.getType() == DOMNode.Type.ELEMENT) {
                    return false;
                }

                node = n;
            }
        }
    }

    /**
     * Matches the <i>nth</i> child.
     *
     * @param node The root node.
     * @param a The <i>A</i> argument.
     * @param b The <i>B</i> argument.
     * @param isOfType If the matching is performed for a {@code -of-type} selector.
     * @param fromEnd If matching is performed from the end.
     * @return {@code true} or {@code false}
     */
    private boolean matchesNthChild(T node, int a, int b, boolean isOfType, boolean fromEnd) {
        T parentNode = node.getParentNode();
        if (parentNode == null || parentNode.getType() == DOMNode.Type.DOCUMENT) {
            return false;
        }

        T n = node;
        int i = 1;
        while (true) {
            T sibling;
            if (fromEnd) {
                sibling = n.getNextSibling();
            } else {
                sibling = n.getPreviousSibling();
            }

            if (sibling == null) {
                break;
            }

            n = sibling;
            if (n.getType() == DOMNode.Type.ELEMENT) {
                if (isOfType) {
                    if (node.getData().equals(n.getData())) {
                        ++i;
                    }
                } else {
                    ++i;
                }
            }
        }

        if (a == 0) {
            return b == i;
        }

        return ((i - b) / a) >= 0 && ((i - b) %a) == 0;
    }

}
