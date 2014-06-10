/**
 * Copyright (c) 2009-2012, Christer Sandberg
 */
package se.fishtank.css.selectors.scanner;

import java.util.LinkedList;
import java.util.List;

import se.fishtank.css.selectors.Selector;
import se.fishtank.css.selectors.Specifier;
import se.fishtank.css.selectors.specifier.AttributeSpecifier;
import se.fishtank.css.selectors.specifier.NegationSpecifier;
import se.fishtank.css.selectors.specifier.PseudoContainsSpecifier;
import se.fishtank.css.selectors.specifier.PseudoClassSpecifier;
import se.fishtank.css.selectors.specifier.PseudoNthSpecifier;
import se.fishtank.css.util.Assert;

/**
 * A selectors scanner as defined by
 * <a href="http://www.w3.org/TR/css3-selectors/#w3cselgrammar">Selectors Level 3 specification</a>.
 * <p/>
 * This implementation uses the <a href="http://www.complang.org/ragel/">Ragel State Machine Compiler</a>.
 * <p/>
 * Use the following command to generate the Java code for the scanner:
 * <br/>
 * <pre>
 * ragel -J Scanner.java.rl -o ../java/se/fishtank/css/selectors/scanner/Scanner.java
 * </pre>
 * 
 * @author Christer Sandberg
 */
public class Scanner {
  
	/** The input to scan. */
	private final String input;
	
	/**
	 * Create a new scanner instance with the specified {@code input}.
	 */
	public Scanner(CharSequence input) {
	    Assert.notNull(input, "input is null!");
	    this.input = input.toString();
	}
	
%%{
	machine Scanner;
	
	action attr {
	    AttributeSpecifier specifier;
	    if (attributeValue != null) {
	        specifier = new AttributeSpecifier(attributeName, attributeValue, attributeMatch);
	    } else {
	        specifier = new AttributeSpecifier(attributeName);
	    }
	    
	    specifiers.add(specifier);
	}
	
	action attr_name {
	    attributeName = getSlice(mark, p);
	}
	
	action attr_match {
	    String m = getSlice(mark, p);
	    if ("=".equals(m)) {
	        attributeMatch = AttributeSpecifier.Match.EXACT;
	    } else if ("~=".equals(m)) {
	        attributeMatch = AttributeSpecifier.Match.LIST;
	    } else if ("|=".equals(m)) {
	        attributeMatch = AttributeSpecifier.Match.HYPHEN;
	    } else if ("^=".equals(m)) {
	        attributeMatch = AttributeSpecifier.Match.PREFIX;
	    } else if ("$=".equals(m)) {
	        attributeMatch = AttributeSpecifier.Match.SUFFIX;
	    } else if ("*=".equals(m)) {
	        attributeMatch = AttributeSpecifier.Match.CONTAINS;
	    }
	}
	
	action attr_value {
	    String value = getSlice(mark, p);
	    if (value.charAt(0) == '"' || value.charAt(0) == '\'') {
	        value = value.substring(1, value.length() - 1); 
	    }
	        
	    attributeValue = value;
	}
	
	action clazz {
	    specifiers.add(new AttributeSpecifier("class",
	        getSlice(mark, p), AttributeSpecifier.Match.LIST));
	}
	
	action combinator {
	    switch (data[p]) {
	    case ' ':
	        combinator = Selector.Combinator.DESCENDANT;
	        break;
	    case '>':
	        combinator = Selector.Combinator.CHILD;
	        break;
	    case '+':
	        combinator = Selector.Combinator.ADJACENT_SIBLING;
	        break;
	    case '~':
	        combinator = Selector.Combinator.GENERAL_SIBLING;
	        break;
	    }
	}
	
	action _group {
	    parts = new LinkedList<Selector>();
	}
	
	action group {
	    selectors.add(parts);
	}
	
	action id {
	    specifiers.add(new AttributeSpecifier("id",
	        getSlice(mark, p), AttributeSpecifier.Match.EXACT));
	}
	
	action mark {
	    mark = p;
	}
	
	action _negation {
	    isNegation = true;
	}
	
	action negation {
	    specifiers.add(new NegationSpecifier(negationSelector));
	    isNegation = false;
	}
	
	action pseudo_class {
        specifiers.add(new PseudoClassSpecifier(getSlice(mark, p)));	  
	}
	
	action pseudo_nth_arg {
	    specifiers.add(new PseudoNthSpecifier(pseudoNthClass, getSlice(mark, p)));
	}
	
	action pseudo_contains_arg {
		specifiers.add(new PseudoContainsSpecifier(getSlice(mark, p)));
	}
	
	action pseudo_nth_class {
	    pseudoNthClass = getSlice(mark, p);
	}	
	
	action sel {
	    Selector selector;
	    List<Specifier> list = specifiers.isEmpty() ? null : specifiers;
	    if (isNegation) {
	        negationSelector = new Selector(negationTagName, list);
	    } else {
	        if (combinator == null) {
	            selector = new Selector(tagName, list);
	        } else {
	            selector = new Selector(tagName, combinator, list);
	        }
	        
	        parts.add(selector);
	        tagName = Selector.UNIVERSAL_TAG;
	        combinator = null;
	    }
	    
	    negationTagName = Selector.UNIVERSAL_TAG;
	    attributeName = null;
	    attributeValue = null;
	    attributeMatch = null;
	    pseudoNthClass = null;
	    specifiers = new LinkedList<Specifier>();
	}
	
	action tag {
	    if (isNegation) {
	        negationTagName = getSlice(mark, p);
	    } else {
	        tagName = getSlice(mark, p);
	    }
	}
			
	include ScannerCommon "ScannerCommon.rl";
	
	write data;
	
	main := space* selectors space*;
}%%
	
	/**
	 * Scan the {@link #input}.
	 * 
	 * @return A list of selector groups that contain a list of {@link Selector}s scanned.
	 * @throws ScannerException If the input is invalid.
	 */
	public List<List<Selector>> scan() throws ScannerException {
		char[] data = input.toCharArray();
		int cs;
		int top;
		int[] stack = new int[32];
		int eof = data.length;
		int p = 0;
		int pe = eof;
		
	    int mark = 0;
	    
	    LinkedList<List<Selector>> selectors = new LinkedList<List<Selector>>();
            List<Selector> parts = null;

	    String tagName = Selector.UNIVERSAL_TAG;
	    String negationTagName = Selector.UNIVERSAL_TAG;
	    Selector.Combinator combinator = null;
	    List<Specifier> specifiers = new LinkedList<Specifier>();
		
		String attributeName = null;
		String attributeValue = null;
		AttributeSpecifier.Match attributeMatch = null;

		String pseudoNthClass = null;
		
		boolean isNegation = false;
		Selector negationSelector = null;
		
		%% write init;
		%% write exec;
		
		if (cs < Scanner_first_final && p != pe) {
		    // TODO: Better error reporting ;)
			throw new ScannerException("Bad input!");
		}
		
		return selectors;
	}

	/**
	 * Get a slice from the {@linkplain #input scanner input}.
	 * 
	 * @param start The start offset.
	 * @param end The end offset.
	 * @return A substring starting at {@code start} and ending in {@code end}.
	 */
	private String getSlice(int start, int end) {
		return input.substring(start, end);
	}
	
}
