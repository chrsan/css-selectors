## Overview

A Java implementation of the [W3C][W3C] Selectors Specification.

This README covers the API for version `2.x`, see the wiki for the old API.

## Selector matching

Selectors are matched against a node in a [DOM][DOM].
More specifically they are matched against a `DOMNode` which provides an
abstraction over a concrete [DOM][DOM] implementation.

This library provides the `W3CNode` which wraps a `org.w3c.dom.Node`,
but you could always roll your own.

For simple one off selector matching it's easiest to specify the selectors
as a string:

```java
Selectors selectors = new Selectors(new W3CMode(document));
List<Node> result = selectors.querySelectorAll("head > :not(meta)");
Node firstDiv = selectors.querySelector("div");
```

When matching selectors more than once, you'd benefit by parsing the
selectors string into a selector list.

```java
List<Selector> selectorList = Selectors.parse("head > :not(meta)");
Selectors selectors = new Selectors(new W3CMode(document));
List<Node> result = selectors.querySelectorAll(selectorList);
Node firstDiv = selectors.querySelector(selectorList);
```

### Custom selector matching

This library provides the functionality that's specified in the spec, but
it's possible to hook into the matching machinery by using an implementation
of the `SimpleSelectorMatcher` interface. This implementation will then be
used when no node could be matched using the default matching machinery.

Let's say that we liked the `:contains()` functional pseudo class that has been
removed from the spec.

```java
SimpleSelectorMatcher<W3CNode> matcher = new SimpleSelectorMatcher<W3CNode>() {
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
};

Selectors selectors = new Selectors(new W3CMode(document), matcher);
List<Node> result = selectors.querySelectorAll(":contains('Boom')");
```

## Credit

Much of the inspiration for the `2.x` rewrite came from the excellent CSS libraries
that are used in [Servo][Servo].

[W3C]:http://www.w3.org/TR/selectors/
[DOM]:http://en.wikipedia.org/wiki/Document_Object_Model
[Servo]:https://github.com/servo/servo
