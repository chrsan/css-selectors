package se.fishtank.css.selectors;

import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import se.fishtank.css.selectors.dom.DOMNodeSelector;

public class NestedNodeSelectorTest {
        
    private final DOMNodeSelector nodeSelector;
    
    public NestedNodeSelectorTest() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = factory.newDocumentBuilder().parse(getClass().getResourceAsStream("/nested-test.html"));
        nodeSelector = new DOMNodeSelector(document);
    }

    @Test
    public void checkNestedSearch() throws NodeSelectorException {
        Node divFoo = nodeSelector.querySelector("#foo");
        Assert.assertEquals("foo", getAttr(divFoo, "id"));

        Node divBar = new DOMNodeSelector(divFoo).querySelector("#bar");
        Assert.assertEquals("bar", getAttr(divBar, "id"));
        
        Node alsoDivFoo = new DOMNodeSelector(divFoo).querySelector("#foo");
        Assert.assertEquals("foo", getAttr(alsoDivFoo, "id"));
    }
    
    @Test
    public void nestedDivCount() throws NodeSelectorException {
        Assert.assertEquals(1, nodeSelector.querySelectorAll("div#scene1").size());
        Assert.assertEquals(2, nodeSelector.querySelectorAll("div#scene1 div.dialog").size());
        Assert.assertEquals(2, nodeSelector.querySelectorAll("div#scene1 div.dialog div").size());
    }

    @Test
    public void checkFirstOfType() throws NodeSelectorException {
        Node head = nodeSelector.querySelector("head");
        Assert.assertNotNull(head);
        
        String selectScript = "script:first-of-type";
		Set<Node> scripts = nodeSelector.querySelectorAll(selectScript);
        Assert.assertEquals(1, scripts.size());;
        
        Set<Node> directScripts = new DOMNodeSelector(head).querySelectorAll(selectScript);
        Assert.assertEquals(1, directScripts.size());
    }

    private String getAttr(Node node, String key) {
    	Attr attr = (Attr) node.getAttributes().getNamedItem("id");
        return attr.getNodeValue().trim();
    }
}
