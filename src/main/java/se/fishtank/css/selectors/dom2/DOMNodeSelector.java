package se.fishtank.css.selectors.dom2;

import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Node;

import se.fishtank.css.selectors.generic.NodeSelector;
import se.fishtank.css.util.Assert;

public class DOMNodeSelector extends NodeSelector<Node> {
	
	    /**
	     * Create a new instance.
	     * 
	     * @param root The root node. Must be a document or element node.
	     */
	    public DOMNodeSelector(Node root) {
	        super(new DOMNodeHelper(root), root);
	        short nodeType = root.getNodeType();
	        Assert.isTrue(nodeType == Node.DOCUMENT_NODE ||
	                nodeType == Node.ELEMENT_NODE, "root must be a document or element node!");
	    }

		public Node querySelector(String selector) {
			return this.find(selector);
		}

		public Set<Node> querySelectorAll(String selector) {
			return new HashSet<Node>(this.findAll(selector));
		}
}
