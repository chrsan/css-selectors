/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors.tokenizer;

import java.util.regex.Pattern;

/**
 * A CSS tokenizer according to <a href="http://www.w3.org/TR/css-syntax-3/">http://www.w3.org/TR/css-syntax-3/</a>
 *
 * @author Christer Sandberg
 */
public class Tokenizer {

    /** Replacement code point. */
    public static final char REPLACEMENT_CHAR = '\uFFFD';

    /** End of file code point. */
    public static final int EOF = -1;

    /** End of file token. */
    public static final Token EOF_TOKEN = new Token(TokenType.EOF, EOF, "");

    /** Regex used to preprocess the input (see http://www.w3.org/TR/css-syntax-3/#input-preprocessing). */
    public static final Pattern PREPROCESS_REGEX = Pattern.compile("\\f|\\r\\n?");

    /** The input to tokenize. */
    public final String input;

    /** The current position. */
    private int pos = 0;

    /** The current mark. */
    private int mark = 0;

    /**
     * Create a new tokenizer.
     *
     * @param input The input to tokenize.
     */
    public Tokenizer(String input) {
        this.input = PREPROCESS_REGEX.matcher(input).replaceAll("\n").replace('\u0000', REPLACEMENT_CHAR);
    }

    /**
     * Returns the current position in the input.
     *
     * @return The current position.
     */
    public int getPosition() {
        return pos;
    }

    /**
     * Resets the position to {@code 0}
     */
    public void reset() {
        this.pos = 0;
        this.mark = 0;
    }

    /**
     * Returns whether the given code point matches <code>[a-zA-Z]</code>
     *
     * @param c Code point to check
     * @return {@code true} or {@code false}
     */
    public static boolean isAlpha(int c) {
        return (c | 0x20) >= 'a' && (c | 0x20) <= 'z';
    }

    /**
     * Returns whether the given code point matches <code>[0-9]</code>
     *
     * @param c Code point to check
     * @return {@code true} or {@code false}
     */
    public static boolean isDigit(int c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Returns whether the given code point matches <code>[0-9a-fA-F]</code>
     *
     * @param c Code point to check
     * @return {@code true} or {@code false}
     */
    public static boolean isHexDigit(int c) {
        return isDigit(c) || ((c | 0x20) >= 'a' && (c | 0x20) <= 'f');
    }

    /**
     * Returns whether the given code point matches <code>[ \t\r\n\f]</code>
     *
     * @param c Code point to check
     * @return {@code true} or {@code false}
     */
    public static boolean isSpace(int c) {
        return c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == '\f';
    }

    /**
     * Returns whether the given code point is a <code>name-start</code> code point
     * as of http://www.w3.org/TR/css-syntax-3/#name-start-code-point
     *
     * @param c Code point to check
     * @return {@code true} or {@code false}
     */
    public static boolean isNameStart(int c) {
        return c == '_' || c >= 0x80 || isAlpha(c);
    }

    /**
     * Returns whether the given code point is a <code>name</code> code point
     * as of http://www.w3.org/TR/css-syntax-3/#name-code-point
     *
     * @param c Code point to check
     * @return {@code true} or {@code false}
     */
    public static boolean isName(int c) {
        return c == '-' || isNameStart(c) || isDigit(c);
    }

    /**
     * Returns whether the given code point is a <code>non-printable</code> code point
     * as of http://www.w3.org/TR/css-syntax-3/#non-printable-code-point
     *
     * @param c Code point to check
     * @return {@code true} or {@code false}
     */
    public static boolean isNonPrintable(int c) {
        return (c >= 0x00 && c <= 0x08) || c == 0x0B || (c >= 0x0E && c <= 0x1F) || c == 0x7F;
    }

    /**
     * Returns whether the two code points are a valid <code>escape</code>
     * as of http://www.w3.org/TR/css-syntax-3/#check-if-two-code-points-are-a-valid-escape
     *
     * @param c1 Code point to check
     * @param c2 Code point to check
     * @return {@code true} or {@code false}
     */
    public static boolean isValidEscape(int c1, int c2) {
        return c1 == '\\' && c2 != '\n';
    }

    /**
     * Convert the given code point to its numeric value.
     *
     * @param c The code point to convert.
     * @return The numeric value for the code point.
     */
    public static int hexValue(int c) {
        if (c < 'A') {
            return c - '0';
        }

        return (c - 'A' + 10) & 0xF;
    }

    /**
     * Returns whether all code points in the input have been consumed.
     *
     * @return {@code true} or {@code false}
     */
    public boolean isEof() {
        return this.pos >= this.input.length();
    }

    /**
     * Returns the next token.
     *
     * @return The next token.
     */
    public Token nextToken() {
        if (isEof()) {
            return EOF_TOKEN;
        }

        skipComments();
        if (isEof()) {
            return EOF_TOKEN;
        }

        int p = this.pos;
        int n = skipSpace();
        if (n > 0) {
            return new Token(TokenType.WHITESPACE, p, "");
        }

        mark();
        int c = next();
        switch (c) {
        case '"':
            setPositionToMark();
            return consumeStringToken(false);
        case '#':
            if (isIdentStart()) {
                return new Token.Hash(p, consumeName(), true);
            }

            int[] d = peek2();
            if (isName(d[0]) || isValidEscape(d[0], d[1])) {
                return new Token.Hash(p, consumeName(), false);
            }

            return new Token(TokenType.DELIM, p, "#");
        case '$':
            if (peek() == '=') {
                next();
                return new Token(TokenType.SUFFIX_MATCH, p, "$=");
            }

            return new Token(TokenType.DELIM, p, "$");
        case '\'':
            setPositionToMark();
            return consumeStringToken(true);
        case '(':
            return new Token(TokenType.LEFT_PAREN, p, "(");
        case ')':
            return new Token(TokenType.RIGHT_PAREN, p, ")");
        case '*':
            if (peek() == '=') {
                next();
                return new Token(TokenType.SUBSTRING_MATCH, p, "*=");
            }

            return new Token(TokenType.DELIM, p, "*");
        case '+':
            setPositionToMark();
            if (isNumberStart()) {
                return consumeNumericToken();
            }

            next();
            return new Token(TokenType.DELIM, p, "+");
        case ',':
            return new Token(TokenType.COMMA, p, ",");
        case '-':
            setPositionToMark();
            if (isNumberStart()) {
                return consumeNumericToken();
            }

            if (isIdentStart()) {
                return consumeIdentLikeToken();
            }

            if (consume("-->")) {
                return new Token(TokenType.CDC, p, "-->");
            }

            next();
            return new Token(TokenType.DELIM, p, "-");
        case '.':
            setPositionToMark();
            if (isNumberStart()) {
                return consumeNumericToken();
            }

            next();
            return new Token(TokenType.DELIM, p, ".");
        case ':':
            return new Token(TokenType.COLON, p, ":");
        case ';':
            return new Token(TokenType.SEMICOLON, p, ";");
        case '<':
            if (consume("!--")) {
                return new Token(TokenType.CDO, p, "<!--");
            }

            return new Token(TokenType.DELIM, p, "<");
        case '@':
            if (isIdentStart()) {
                return new Token(TokenType.AT_KEYWORD, p, consumeName());
            }

            return new Token(TokenType.DELIM, p, "@");
        case '[':
            return new Token(TokenType.LEFT_SQUARE_BRACKET, p, "[");
        case ']':
            return new Token(TokenType.RIGHT_SQUARE_BRACKET, p, "]");
        case '\\':
            if (isValidEscape('\\', peek())) {
                setPositionToMark();
                return consumeIdentLikeToken();
            }

            return new Token(TokenType.DELIM, p, "\\");
        case '^':
            if (peek() == '=') {
                next();
                return new Token(TokenType.PREFIX_MATCH, p, "^=");
            }

            return new Token(TokenType.DELIM, p, "^");
        case '{':
            return new Token(TokenType.LEFT_CURLY_BRACKET, p, "{");
        case '}':
            return new Token(TokenType.RIGHT_CURLY_BRACKET, p, "}");
        case '|':
            int x = peek();
            switch (x) {
            case '=':
                next();
                return new Token(TokenType.DASH_MATCH, p, "|=");
            case '|':
                next();
                return new Token(TokenType.COLUMN, p, "||");
            }

            return new Token(TokenType.DELIM, p, "|");
        case '~':
            if (peek() == '=') {
                next();
                return new Token(TokenType.INCLUDE_MATCH, p, "~=");
            }

            return new Token(TokenType.DELIM, p, "~");
        }

        if (isDigit(c)) {
            setPositionToMark();
            return consumeNumericToken();
        }

        if (c == 'U' || c == 'u') {
            int[] e = peek2();
            if (e[0] == '+' && (e[1] == '?' || isHexDigit(e[1]))) {
                next(); // Consume the '+'
                return consumeUnicodeRangeToken();
            }

            setPositionToMark();
            return consumeIdentLikeToken();
        }

        if (isNameStart(c)) {
            setPositionToMark();
            return consumeIdentLikeToken();
        }

        return new Token(TokenType.DELIM, p, String.copyValueOf(Character.toChars(c)));
    }

    /**
     * Mark the current position in the input.
     */
    private void mark() {
        this.mark = this.pos;
    }

    /**
     * Sets the position to the marked position in the input.
     */
    private void setPositionToMark() {
        this.pos = this.mark;
    }

    /**
     * Consumes and returns the next code point in the input.
     *
     * @return The next code point in the input.
     */
    private int next() {
        if (isEof()) {
            return EOF;
        }

        int c = this.input.codePointAt(this.pos);
        this.pos += Character.charCount(c);
        return c;
    }

    /**
     * Returns the next code point in the input without consuming it.
     *
     * @return The next code point in the input.
     */
    private int peek() {
        int p = this.pos;
        int c = next();
        this.pos = p;
        return c;
    }

    /**
     * Returns the next two code points in the input without consuming them.
     *
     * @return The next two code points in the input.
     */
    private int[] peek2() {
        int p = this.pos;
        int[] c = new int[] { next(), next() };
        this.pos = p;
        return c;
    }

    /**
     * Returns the next three code points in the input without consuming them.
     *
     * @return The next three code points in the input.
     */
    private int[] peek3() {
        int p = this.pos;
        int[] c = new int[] { next(), next(), next() };
        this.pos = p;
        return c;
    }

    /**
     * Skip comments at the current position.
     */
    private void skipComments() {
        if (consume("/*")) {
            while (true) {
                int c = next();
                if (c == EOF) {
                    return;
                }

                if (c == '*' && peek() == '/') {
                    next(); // Consume the '/'
                    return;
                }
            }
        }
    }

    /**
     * Skip whitespace at the current position.
     *
     * @return The number of whitespace code points skipped.
     */
    private int skipSpace() {
        int n = 0;
        while (isSpace(peek())) {
            n += 1;
            next();
        }

        return n;
    }

    /**
     * Tries to consume the string {@code str} at the current position.
     *
     * @param str The string to consume.
     * @return {@code true} on success consuming {@code str}
     */
    private boolean consume(String str) {
        if (!isEof() && this.input.startsWith(str, this.pos)) {
            this.pos += str.length();
            return true;
        }

        return false;
    }

    /**
     * Returns whether the tokenizer could match an identifier at the current position.
     * <p/>
     * See http://www.w3.org/TR/css-syntax-3/#would-start-an-identifier
     *
     * @return {@code true} or {@code false}
     */
    private boolean isIdentStart() {
        if (isEof()) {
            return false;
        }

        int[] c = peek3();
        return isNameStart(c[0]) || isValidEscape(c[0], c[1]) ||
                (c[0] == '-' && (isNameStart(c[1]) || isValidEscape(c[1], c[2])));
    }

    /**
     * Returns whether the tokenizer could match a number at the current position.
     * <p/>
     * See http://www.w3.org/TR/css-syntax-3/#starts-with-a-number
     *
     * @return {@code true} or {@code false}
     */
    private boolean isNumberStart() {
        if (isEof()) {
            return false;
        }

        int[] c = peek3();
        if (isDigit(c[0]) || (c[0] == '.' && isDigit(c[1]))) {
            return true;
        }

        if (c[0] == '+' || c[0] == '-') {
            if (isDigit(c[1])) {
                return true;
            }

            if (c[1] == '.' && isDigit(c[2])) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns whether the next code point at the current position start the exponential part of a number.
     *
     * @return {@code true} or {@code false}
     */
    private boolean isValidExponent() {
        if (isEof()) {
            return false;
        }

        try {
            mark();
            int c = next();
            if (c != 'e' && c != 'E') {
                return false;
            }

            c = next();
            if (c == '+' || c == '-') {
                return isDigit(next());
            }

            return isDigit(c);
        } finally {
            setPositionToMark();
        }
    }

    /**
     * Consume an escaped code point.
     * <p/>
     * It is assumed that the U+005C REVERSE SOLIDUS (\) has already been consumed
     * and that the next code point in the input has been verified to not be a newline.
     * <p/>
     * See http://www.w3.org/TR/css-syntax-3/#consume-an-escaped-code-point
     *
     * @return The consumed code point.
     */
    private int consumeEscape() {
        if (isEof()) {
            return REPLACEMENT_CHAR;
        }

        if (isHexDigit(peek())) {
            int uc = 0;
            int len = 6;
            while (len > 0 && isHexDigit(peek())) {
                uc = (uc << 4) + hexValue(next());
                --len;
            }

            if (uc == 0 || uc > Character.MAX_CODE_POINT || (uc >= 0xD800 && uc <= 0xDFFF)) {
                uc = REPLACEMENT_CHAR;
            }

            if (isSpace(peek())) {
                next();
            }

            return uc;
        }

        return next();
    }

    /**
     * Consume a name.
     * <p/>
     * It is assumed that the current position of the tokenizer represents a name.
     * <p/>
     * See http://www.w3.org/TR/css-syntax-3/#consume-a-name
     *
     * @return The consumed name.
     */
    private String consumeName() {
        StringBuilder sb = new StringBuilder();
        while (true) {
            mark();
            int c = next();
            if (isName(c)) {
                sb.appendCodePoint(c);
            } else if (isValidEscape(c, peek())) {
                sb.appendCodePoint(consumeEscape());
            } else {
                setPositionToMark();
                break;
            }
        }

        return sb.toString();
    }

    /**
     * Consume a number.
     * <p/>
     * It is assumed that the current position of the tokenizer represents a number token.
     *
     * @return The consumed number token.
     */
    private Token.Number consumeNumber() {
        StringBuilder sb = new StringBuilder();

        int p = this.pos;
        int c = peek();
        if (c == '+' || c == '-') {
            sb.appendCodePoint(next());
        }

        while (isDigit(peek())) {
            sb.appendCodePoint(next());
        }

        mark();
        boolean integer = true;
        int c1 = next();
        int c2 = next();
        if (c1 == '.' && isDigit(c2)) {
            sb.appendCodePoint(c1).appendCodePoint(c2);
            while (isDigit(peek())) {
                sb.appendCodePoint(next());
            }

            integer = false;
        } else {
            setPositionToMark();
        }

        if (isValidExponent()) {
            integer = false;
            sb.appendCodePoint(next()).appendCodePoint(next());
            while (isDigit(peek())) {
                sb.appendCodePoint(next());
            }
        }

        return Token.Number.number(p, sb.toString(), integer);
    }

    /**
     * Consume a numeric token.
     * <p/>
     * It is assumed that the current position of the tokenizer represents a number token.
     * <p/>
     * See http://www.w3.org/TR/css-syntax-3/#consume-a-numeric-token
     *
     * @return The consumed numeric token.
     */
    private Token consumeNumericToken() {
        Token.Number token = consumeNumber();
        if (peek() == '%') {
            next();
            return Token.Number.percentage(token.position, token.value, token.integer);
        }

        if (isIdentStart()) {
            return new Token.Dimension(token.position, token.value, token.integer, consumeName());
        }

        return token;
    }

    /**
     * Consume an ident-like token.
     * <p/>
     * See http://www.w3.org/TR/css-syntax-3/#consume-an-ident-like-token
     *
     * @return The consumed token.
     */
    private Token consumeIdentLikeToken() {
        int p = this.pos;
        String name = consumeName();
        TokenType type = TokenType.IDENT;
        if (peek() == '(') {
            next(); // Consume the '('
            if ("url".equalsIgnoreCase(name)) {
                return consumeUrlToken();
            } else {
                type = TokenType.FUNCTION;
            }
        }

        return new Token(type, p, name);
    }

    /**
     * Consume a string token.
     * <p/>
     * See http://www.w3.org/TR/css-syntax-3/#consume-a-string-token
     *
     * @param apostrophe If the string contents is surrounded by apostrophes.
     * @return The consumed string token.
     */
    private Token consumeStringToken(boolean apostrophe) {
        StringBuilder sb = new StringBuilder();

        int p = this.pos;
        next(); // Consume the quote
        while (true) {
            mark();
            int c = next();
            if (c == EOF || (c == '\'' && apostrophe) || (c == '"' && !apostrophe)) {
                break;
            }

            if (c == '\n') {
                setPositionToMark();
                return new Token(TokenType.BAD_STRING, p, "");
            }

            if (c == '\\') {
                int d = peek();
                if (d != EOF) {
                    if (d == '\n') {
                        next(); // Consume the newline
                    } else {
                        sb.appendCodePoint(consumeEscape());
                    }
                }
            } else {
                sb.appendCodePoint(c);
            }
        }

        return new Token(TokenType.STRING, p, sb.toString());
    }

    /**
     * Consume a URL token.
     * <p/>
     * It is assumed that the current position of the tokenizer represents a URL token.
     * <p/>
     * See http://www.w3.org/TR/css-syntax-3/#consume-a-url-token
     *
     * @return The consumed URL token.
     */
    private Token consumeUrlToken() {
        skipSpace();
        int p = this.pos;
        if (isEof()) {
            return new Token(TokenType.URL, p, "");
        }

        int c = peek();
        if (c == '\'' || c == '"') {
            Token token = consumeStringToken(c != '"');
            if (token.type == TokenType.BAD_STRING) {
                p = this.pos;
                consumeBadUrl();
                return new Token(TokenType.BAD_URL, p, token.value);
            } else {
                skipSpace();
                c = peek();
                if (c == ')' || c == EOF) {
                    if (c == ')') {
                        next(); // Consume the ')'
                    }

                    return new Token(TokenType.URL, p, token.value);
                }

                p = this.pos;
                consumeBadUrl();
                return new Token(TokenType.BAD_URL, p, token.value);
            }
        }

        StringBuilder sb = new StringBuilder();
        boolean spaceSeen = false;
        while (true) {
            c = next();
            if (c == ')' || c == EOF) {
                return new Token(TokenType.URL, p, sb.toString());
            }

            if (isSpace(c)) {
                spaceSeen = true;
                skipSpace();
                continue;
            }

            if (spaceSeen) {
                p = this.pos;
                consumeBadUrl();
                return new Token(TokenType.BAD_URL, p, "");
            }

            if (c == '\'' || c == '"' || c == '(' || isNonPrintable(c)) {
                p = this.pos;
                consumeBadUrl();
                return new Token(TokenType.BAD_URL, p, "");
            }

            if (c == '\\') {
                if (isValidEscape(c, peek())) {
                    sb.appendCodePoint(consumeEscape());
                } else {
                    p = this.pos;
                    consumeBadUrl();
                    return new Token(TokenType.BAD_URL, p, "");
                }
            } else {
                sb.appendCodePoint(c);
            }
        }
    }

    /**
     * Consume a unicode range.
     * <p/>
     * Is is assumed that the initial {@code u+} has already been consumed and that
     * the next input code point has been verified to be a hex digit or a {@code ?}.
     *
     * @return The consumed Unicode range token.
     */
    private Token.UnicodeRange consumeUnicodeRangeToken() {
        int p = this.pos;
        int start = 0;
        int length = 0;
        while (isHexDigit(peek()) && length < 6) {
            start = (start << 4) + hexValue(next());
            ++length;
        }

        int q = 0;
        if (length < 6) {
            while (peek() == '?' && length < 6) {
                next();
                ++length;
                ++q;
            }
        }

        if (q != 0) {
            int end = start;
            for (int i = 0; i < q; ++i) {
                start = start << 4;
                end = (end << 4) + 15;
            }

            return new Token.UnicodeRange(p, start, end);
        }

        int end = 0;
        int[] c = peek2();
        if (c[0] == '-' && isHexDigit(c[1])) {
            next(); // Consume the '-'
            length = 0;
            while (isHexDigit(peek()) && length < 6) {
                end = (end << 4) + hexValue(next());
                ++length;
            }
        } else {
            end = start;
        }

        return new Token.UnicodeRange(p, start, end);
    }

    /**
     * Consume the remnants of a bad URL.
     * <p/>
     * See http://www.w3.org/TR/css-syntax-3/#consume-the-remnants-of-a-bad-url
     */
    private void consumeBadUrl() {
        while (true) {
            int c = next();
            if (c == ')' || c == EOF) {
                break;
            }

            if (isValidEscape(c, peek())) {
                consumeEscape();
            }
        }
    }

}
