package se.fishtank.css.selectors.generic;

import java.util.Collection;

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
	
	public boolean hasAttribute(Node element, String name);

	public Collection<Node> getAttributes(Node element);

	public Index getIndexInParent(Node node, boolean byType);

	public Node getRoot(Node node);


    public Collection<? extends Node> getDescendentNodes(Node node);
    
    public Collection<? extends Node> getChildNodes(Node node);

	public String getName(Node n);
    
    public Node getNextSibling(Node node);

    /**
     * This can be a case-sensitive or insensitive match.
     * 
     * @param tag1
     * @param tag2
     * @return
     */
	public boolean namesEqual(String tag1, String tag2);    
}
