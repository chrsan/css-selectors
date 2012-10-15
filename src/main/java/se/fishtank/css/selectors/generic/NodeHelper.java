package se.fishtank.css.selectors.generic;

import java.util.Collection;
import java.util.List;

public interface NodeHelper<Node> {
    
    public class Index {
    	public final int index;
    	public final int size;
    	public Index(int index, int size) {
    		this.index = index;
    		this.size = size;
    	}
	}

    public String getValue(Node element);
	
	public Node getAttribute(Node element, String name);

	public Collection<Node> getAttributes(Node element);

	public Index getIndexInParent(Node node, boolean byType);

	public Node getRoot();

	public boolean isEmpty(Node node);
	
    public Collection<? extends Node> getDescendentNodes(Node node);
    
    public List<? extends Node> getChildNodes(Node node);

	public String getName(Node n);
    
    public Node getNextSibling(Node node);

    /**
     * Returns a case appropriate equality check for the name of the node.
     * Also returns true if name argument is Selector.UNIVERSAL_TAG
     * @param n
     * @param name
     * @return
     */
	public boolean nameMatches(Node n, String name);    
}
