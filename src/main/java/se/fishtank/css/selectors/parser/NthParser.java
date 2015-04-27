/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors.parser;

import se.fishtank.css.selectors.tokenizer.Token;
import se.fishtank.css.selectors.tokenizer.TokenType;
import se.fishtank.css.selectors.tokenizer.Tokenizer;
import se.fishtank.css.selectors.util.Pair;

/**
 * Parses {@code An+B} notation.
 *
 * @author Christer Sandberg
 */
public class NthParser {

    /** The tokenizer used when parsing. */
    private final Tokenizer tokenizer;

    /** Exception thrown on parsing errors. */
    private final ParserException error;

    /**
     * Create a new parser.
     *
     * @param tokenizer The tokenizer to use when parsing.
     */
    private NthParser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        this.error = new ParserException("Invalid nth arguments at position " + tokenizer.getPosition());
    }

    /**
     * Parses {@code An+B} notation at the current position in the given tokenizer.
     *
     * @param tokenizer The tokenizer to use when parsing.
     * @return The values for <i>A</i> and <i>B</i>.
     */
    public static Pair<Integer, Integer> parse(Tokenizer tokenizer) {
        NthParser parser = new NthParser(tokenizer);
        try {
            String str;
            Token token = parser.skipWhitespace();
            switch (token.type) {
            case NUMBER:
                Token.Number n = (Token.Number) token;
                if (!n.integer || !parser.matchClosingParen()) {
                    throw parser.error;
                }

                return new Pair<>(0, Integer.parseInt(n.value));
            case DIMENSION:
                Token.Dimension d = (Token.Dimension) token;
                if (!d.integer) {
                    throw parser.error;
                }

                int a = Integer.parseInt(d.value);
                str = d.unit.toLowerCase();
                switch (str) {
                case "n":
                    return new Pair<>(a, parser.parseB());
                case "n-":
                    return new Pair<>(a, parser.parseSignlessB(-1));
                }

                try {
                    return new Pair<>(a, parser.parseNDashDigits(str));
                } finally {
                    parser.mustMatchClosingParen();
                }
            case IDENT:
                str = token.value.toLowerCase();
                switch (str) {
                case "even":
                    parser.mustMatchClosingParen();
                    return new Pair<>(2, 0);
                case "odd":
                    parser.mustMatchClosingParen();
                    return new Pair<>(2, 1);
                case "n":
                    return new Pair<>(1, parser.parseB());
                case "-n":
                    return new Pair<>(-1, parser.parseB());
                case "n-":
                    return new Pair<>(1, parser.parseSignlessB(-1));
                case "-n-":
                    return new Pair<>(-1, parser.parseSignlessB(-1));
                }

                try {
                    if (str.startsWith("-")) {
                        return new Pair<>(-1, parser.parseNDashDigits(str.substring(1)));
                    } else {
                        return new Pair<>(1, parser.parseNDashDigits(str));
                    }
                } finally {
                    parser.mustMatchClosingParen();
                }
            case DELIM:
                if (!"+".equals(token.value)) {
                    throw parser.error;
                }

                token = tokenizer.nextToken();
                if (token.type != TokenType.IDENT) {
                    throw parser.error;
                }

                str = token.value.toLowerCase();
                switch (str) {
                case "n":
                    return new Pair<>(1, parser.parseB());
                case "n-":
                    return new Pair<>(1, parser.parseSignlessB(-1));
                }

                try {
                    return new Pair<>(1, parser.parseNDashDigits(str));
                } finally {
                    parser.mustMatchClosingParen();
                }
            default:
                throw parser.error;
            }
        } catch (NumberFormatException e) {
            throw parser.error;
        }
    }

    /**
     * Parse a <i>B</i> value.
     *
     * @return The number parsed.
     */
    private int parseB() {
        Token token = skipWhitespace();
        switch (token.type) {
        case RIGHT_PAREN:
            return 0;
        case DELIM:
            switch (token.value) {
            case "+":
                return parseSignlessB(1);
            case "-":
                return parseSignlessB(-1);
            }

            break;
        case NUMBER:
            Token.Number n = (Token.Number) token;
            if (n.integer && hasSignPrefix(n.value) && matchClosingParen()) {
                return Integer.parseInt(n.value);
            }

            break;
        }

        throw error;
    }

    /**
     * Parse a <i>B</i> value returning {@code B * sign}
     *
     * @param sign The sign.
     * @return The number parsed.
     */
    private int parseSignlessB(int sign) {
        Token token = skipWhitespace();
        if (token instanceof Token.Number) {
            Token.Number n = (Token.Number) token;
            if (n.integer && !hasSignPrefix(n.value) && matchClosingParen()) {
                return Integer.parseInt(n.value) * sign;
            }
        }

        throw error;
    }

    /**
     * Parses the digit(s) that is prefixed by {@code n-}
     *
     * @param str The string to parse digits from.
     * @return The number parsed.
     */
    private int parseNDashDigits(String str) {
        if (str.length() >= 3 && str.startsWith("n-")) {
            return Integer.parseInt(str.substring(1));
        }

        throw error;
    }

    /**
     * Returns whether the given string starts with {@code +} or {@code -}
     *
     * @param str The string to check.
     * @return {@code true} or {@code false}
     */
    private boolean hasSignPrefix(String str) {
        char c = str.charAt(0);
        return c == '+' || c == '-';
    }

    /**
     * Returns whether the next non-whitespace token is a right parent.
     *
     * @return {@code true} or {@code false}
     */
    private boolean matchClosingParen() {
        return skipWhitespace().type == TokenType.RIGHT_PAREN;
    }

    /**
     * Throws a parser error exception if the next non-whitespace token isn't a right paren.
     */
    private void mustMatchClosingParen() {
        if (!matchClosingParen()) {
            throw error;
        }
    }

    /**
     * Returns the next non-whitespace token from the given tokenizer.
     *
     * @return The next non-whitespace token.
     */
    private Token skipWhitespace() {
        while (true) {
            Token token = tokenizer.nextToken();
            if (token.type != TokenType.WHITESPACE) {
                return token;
            }
        }
    }

}
