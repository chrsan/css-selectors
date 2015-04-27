/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors.tokenizer;

/**
 * Identifies the type of a token.
 *
 * @author Christer Sandberg
 */
public enum TokenType {

    AT_KEYWORD,
    BAD_STRING, BAD_URL,
    CDC, CDO, COLON, COLUMN, COMMA,
    DASH_MATCH, DELIM, DIMENSION,
    EOF,
    FUNCTION,
    HASH, IDENT, INCLUDE_MATCH,
    LEFT_CURLY_BRACKET, LEFT_PAREN, LEFT_SQUARE_BRACKET,
    NUMBER,
    PERCENTAGE, PREFIX_MATCH,
    RIGHT_CURLY_BRACKET, RIGHT_PAREN, RIGHT_SQUARE_BRACKET,
    SEMICOLON, STRING, SUBSTRING_MATCH, SUFFIX_MATCH,
    UNICODE_RANGE, URL,
    WHITESPACE

}
