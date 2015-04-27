/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors.matching;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import se.fishtank.css.selectors.Support;
import se.fishtank.css.selectors.dom.W3CNode;
import se.fishtank.css.selectors.dom.Traversal;
import se.fishtank.css.selectors.dom.Visitor;
import se.fishtank.css.selectors.parser.SelectorParser;
import se.fishtank.css.selectors.selector.PseudoFunctionSelector;
import se.fishtank.css.selectors.selector.Selector;
import se.fishtank.css.selectors.selector.SimpleSelector;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@linkplain se.fishtank.css.selectors.matching.SelectorMatcher}
 *
 * @author Christer Sandberg
 */
public class SelectorMatcherTest {

    private static final W3CNode ROOT = Support.getTestDocument();

    @Test
    public void testSelectorMatching() {
        final SelectorMatcher<W3CNode> selectorMatcher = new SelectorMatcher<>();
        for (Map.Entry<String, Integer> entry : createTestSelectorsMap().entrySet()) {
            String selector = entry.getKey();
            int expectedCount = entry.getValue();

            List<Selector> selectors = SelectorParser.parse(selector);
            CountingVisitor visitor = new CountingVisitor(selectorMatcher, selectors);
            Traversal.traverseElements(ROOT, visitor);
            assertEquals(selector, expectedCount, visitor.count);
        }
    }

    @Test
    public void testSelectorMatcherWithSimpleSelectorMatcher() {
        SelectorMatcher<W3CNode> selectorMatcher = new SelectorMatcher<>(new ContainsMatcher());

        CountingVisitor visitor1 = new CountingVisitor(selectorMatcher, SelectorParser.parse("h3:contains('palace')"));
        Traversal.traverseElements(ROOT, visitor1);
        assertEquals(1, visitor1.count);

        CountingVisitor visitor2 = new CountingVisitor(selectorMatcher, SelectorParser.parse(":contains('Boom')"));
        Traversal.traverseElements(ROOT, visitor2);
        assertEquals(0, visitor2.count);
    }

    private static LinkedHashMap<String, Integer> createTestSelectorsMap() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();

        map.put(":root", 1);
        map.put(":empty", 2);
        map.put("div:first-child", 51);
        map.put("div:nth-child(even)", 106);
        map.put("div:nth-child(2n)", 106);
        map.put("div:nth-child(odd)", 137);
        map.put("div:nth-child(2n+1)", 137);
        map.put("div:nth-child(n)", 243);
        map.put("script:first-of-type", 1);
        map.put("div:last-child", 53);
        map.put("script:last-of-type", 1);
        map.put("script:nth-last-child(odd)", 1);
        map.put("script:nth-last-child(even)", 1);
        map.put("script:nth-last-child(5)", 0);
        map.put("script:nth-of-type(2)", 1);
        map.put("script:nth-last-of-type(n)", 2);
        map.put("div:only-child", 22);
        map.put("meta:only-of-type", 1);
        map.put("div > div", 242);
        map.put("div + div", 190);
        map.put("div ~ div", 190);
        map.put("body", 1);
        map.put("body div", 243);
        map.put("div", 243);
        map.put("div div", 242);
        map.put("div div div", 241);
        map.put("div, div, div", 243);
        map.put("div, a, span",  243);
        map.put(".dialog", 51);
        map.put("div.dialog", 51);
        map.put("div .dialog", 51);
        map.put("div.character, div.dialog", 99);
        map.put("#speech5", 1);
        map.put("div#speech5", 1);
        map.put("div #speech5", 1);
        map.put("div.scene div.dialog",49);
        map.put("div#scene1 div.dialog div", 142);
        map.put("#scene1 #speech1", 1);
        map.put("div[class]", 103);
        map.put("div[class=dialog]", 50);
        map.put("div[class^=dia]", 51);
        map.put("div[class$=log]", 50);
        map.put("div[class*=sce]", 1);
        map.put("div[class|=dialog]", 50);
        map.put("div[class~=dialog]", 51);
        map.put("head > :not(meta)", 2);
        map.put("head > :not(:last-child)", 2);

        return map;
    }

    static class CountingVisitor implements Visitor<W3CNode> {
        final SelectorMatcher<W3CNode> selectorMatcher;
        final List<Selector> selectors;

        int count = 0;

        CountingVisitor(SelectorMatcher<W3CNode> selectorMatcher, List<Selector> selectors) {
            this.selectorMatcher = selectorMatcher;
            this.selectors = selectors;
        }

        @Override
        public void visit(W3CNode node) {
            if (selectorMatcher.matchesSelectors(selectors, node)) {
                ++count;
            }
        }
    }

    static class ContainsMatcher implements SimpleSelectorMatcher<W3CNode> {
        @Override
        public boolean matches(SimpleSelector simpleSelector, W3CNode node) {
            if (!(simpleSelector instanceof PseudoFunctionSelector)) {
                return false;
            }

            PseudoFunctionSelector selector = (PseudoFunctionSelector) simpleSelector;
            if (!selector.name.equals("contains")) {
                return false;
            }

            String text = node.getUnderlying().getTextContent();
            return text != null && text.contains(selector.arguments);
        }
    }

}
