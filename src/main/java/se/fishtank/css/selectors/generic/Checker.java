package se.fishtank.css.selectors.generic;

import java.util.Collection;

public interface Checker<Node> {
    public abstract Collection<Node> check(Collection<Node> nodes);

}
