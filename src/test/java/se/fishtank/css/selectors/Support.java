/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;

import se.fishtank.css.selectors.dom.W3CNode;

/**
 * Some supporting test methods.
 *
 * @author Christer Sandberg
 */
public class Support {

    public static W3CNode getTestDocument() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try (InputStream in = Support.class.getResourceAsStream("/test.html")) {
            return new W3CNode(factory.newDocumentBuilder().parse(in));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
