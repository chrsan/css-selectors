/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors.tokenizer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests the {@linkplain se.fishtank.css.selectors.tokenizer.Tokenizer}
 *
 * @author Christer Sandberg
 */
public class TokenizerTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testTokenizerSpecCompliance() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        TypeReference<List<Object>> typeRef = new TypeReference<List<Object>>() {};
        List<Object> list = objectMapper.readValue(getClass().getResource("/component_value_list.json"), typeRef);

        int n = 0;
        Iterator<Object> iterator = list.iterator();
        while (iterator.hasNext()) {
            ++n;

            String input = (String) iterator.next();
            LinkedList<Object> tokens = tokenize(input);
            List<Object> expected = (List<Object>) iterator.next();
            assertEquals("Size mismatch for test number " + n, expected.size(), tokens.size());

            int i = 0;
            for (Object obj : expected) {
                Object token = tokens.get(i);

                assertEquals(String.format("Value mismatch at array position %d for test %d: %s (%s != %s)",
                        i, n, input, token, obj), obj, token);
                ++i;
            }
        }
    }

    public LinkedList<Object> tokenize(String input) {
        LinkedList<Object> result = new LinkedList<Object>();
        Tokenizer tokenizer = new Tokenizer(input);
        while (true) {
            Object value;
            Token token = tokenizer.nextToken();
            switch (token.type) {
            case EOF:
                return result;
            case AT_KEYWORD:
                value = values("at-keyword", token.value);
                break;
            case BAD_STRING:
                value = values("error", "bad-string");
                break;
            case BAD_URL:
                value = values("error", "bad-url");
                break;
            case FUNCTION:
                value = values("function", token.value);
                break;
            case HASH:
                Token.Hash h = (Token.Hash) token;
                String hashFlag = h.id ? "id" : "unrestricted";
                value = values("hash", h.value, hashFlag);
                break;
            case IDENT:
                value = values("ident", token.value);
                break;
            case STRING:
                value = values("string", token.value);
                break;
            case URL:
                value = values("url", token.value);
                break;
            case WHITESPACE:
                value = " ";
                break;
            case DIMENSION:
                Token.Dimension d = (Token.Dimension) token;
                String dimensionFlag = d.integer ? "integer": "number";
                value = values("dimension", d.value, number(d.value, d.integer), dimensionFlag, d.unit);
                break;
            case NUMBER:
            case PERCENTAGE:
                Token.Number n = (Token.Number) token;
                String numberFlag = n.integer ? "integer": "number";
                String tokenType = token.type == TokenType.PERCENTAGE ? "percentage": "number";
                value = values(tokenType, n.value, number(n.value, n.integer), numberFlag);
                break;
            case UNICODE_RANGE:
                Token.UnicodeRange u = (Token.UnicodeRange) token;
                value = values("unicode-range", u.start, u.end);
                break;
            default:
                value = token.value;
            }

            result.add(value);
        }
    }

    private Object number(String value, boolean integer) {
        if (integer) {
            return Integer.parseInt(value);
        }

        double d = Double.parseDouble(value);
        if ((d % 1) == 0) {
            return (int) d;
        }

        return d;
    }

    private static List<Object> values(Object... values) {
        return Arrays.asList(values);
    }

}
