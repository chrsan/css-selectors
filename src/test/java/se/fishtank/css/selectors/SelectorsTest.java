/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors;

import java.util.List;

import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import se.fishtank.css.selectors.dom.W3CNode;
import se.fishtank.css.selectors.selector.Selector;

import static org.junit.Assert.*;

/**
 * Tests the simplified {@linkplain se.fishtank.css.selectors.Selectors selectors} API.
 *
 * @author Christer Sandberg
 */
public class SelectorsTest {

    private final W3CNode document = Support.getTestDocument();

    private final List<Selector> selectors = Selectors.parse("head > :not(meta)");

    @Test
    public void testQuerySelector() {
        Node node = new Selectors<>(document).querySelector(selectors);
        assertNotNull(node);
        assertEquals("script", node.getNodeName());

        NamedNodeMap attributes = node.getAttributes();
        assertNotNull(attributes);

        Attr attr = (Attr) attributes.getNamedItem("src");
        assertNotNull(attr);
        assertEquals("../frameworks/dummy.js", attr.getValue());
    }

    @Test
    public void testQuerySelectorAll() {
        List<Node> nodes = new Selectors<>(document).querySelectorAll(selectors);
        assertEquals(2, nodes.size());

        for (Node node : nodes) {
            assertEquals("script", node.getNodeName());
        }
    }

}
