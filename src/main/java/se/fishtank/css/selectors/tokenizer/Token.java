/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors.tokenizer;

/**
 * Represents a token returned from the tokenizer.
 *
 * @author Christer Sandberg
 */
public class Token {

    /** The type of this token. */
    public final TokenType type;

    /** The position of this token. */
    public final int position;

    /** The string value of this token. */
    public final String value;

    /**
     * Create a new token.
     *
     * @param type The type of this token.
     * @param position The position of this token.
     * @param value The string value of this token.
     */
    public Token(TokenType type, int position, String value) {
        this.type = type;
        this.position = position;
        this.value = value;
    }

    /**
     * Specialization of a token that represents a Unicode range.
     */
    public static class UnicodeRange extends Token {

        /** The start of this Unicode range token. */
        public final int start;

        /** The end of this Unicode range token. */
        public final int end;

        /**
         * Create a new Unicode range token.
         *
         * @param position The position of this token.
         * @param start The start of this Unicode range token.
         * @param end The end of this Unicode range token.
         */
        public UnicodeRange(int position, int start, int end) {
            super(TokenType.UNICODE_RANGE, position, String.format("U+%04X-U+%04X", start, end));
            this.start = start;
            this.end = end;
        }

    }

    /**
     * Specialization of a token that represents a hash.
     */
    public static class Hash extends Token {

        /** If the type flag is of type <i>id</i>. */
        public final boolean id;

        /**
         * Create a new hash token.
         *
         * @param position The position of this token.
         * @param value The string value of this token.
         * @param id If the type flag is of type <i>id</i>..
         */
        public Hash(int position, String value, boolean id) {
            super(TokenType.HASH, position, value);
            this.id = id;
        }

    }

    /**
     * Specialization of a token that represents a number.
     */
    public static class Number extends Token {

        /** If the type flag is of type <i>integer</i>. */
        public final boolean integer;

        /**
         * Create a new number token.
         *
         * @param type The type of this token.
         * @param position The position of this token.
         * @param value The string value of this token.
         * @param integer If the type flag is of type <i>integer</i>.
         */
        protected Number(TokenType type, int position, String value, boolean integer) {
            super(type, position, value);
            this.integer = integer;
        }

        /**
         * Create a new number token of {@linkplain se.fishtank.css.selectors.tokenizer.TokenType#NUMBER}
         *
         * @param position The position of this token.
         * @param value The string value of this token.
         * @param integer If the type flag is of type <i>integer</i>.
         */
        public static Number number(int position, String value, boolean integer) {
            return new Number(TokenType.NUMBER, position, value, integer);
        }

        /**
         * Create a new number token of {@linkplain se.fishtank.css.selectors.tokenizer.TokenType#PERCENTAGE}
         *
         * @param position The position of this token.
         * @param value The string value of this token.
         * @param integer If the type flag is of type <i>integer</i>.
         */
        public static Number percentage(int position, String value, boolean integer) {
            return new Number(TokenType.PERCENTAGE, position, value, integer);
        }

    }

    /**
     * Specialization of a token that represents a dimension.
     */
    public static class Dimension extends Number {

        /** The unit of this dimension token. */
        public final String unit;

        /**
         * Create a new dimension token.
         *
         * @param position The position of this token.
         * @param value The string value of this token.
         * @param integer If the type flag is of type <i>integer</i>.
         * @param unit The unit of this dimension token.
         */
        public Dimension(int position, String value, boolean integer, String unit) {
            super(TokenType.DIMENSION, position, value, integer);
            this.unit = unit;
        }

    }

}
