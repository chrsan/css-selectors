/**
 * Copyright (c) 2009-2015, Christer Sandberg
 */
package se.fishtank.css.selectors.parser;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import se.fishtank.css.selectors.tokenizer.Tokenizer;
import se.fishtank.css.selectors.util.Pair;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests the {@linkplain se.fishtank.css.selectors.parser.NthParser}
 *
 * @author Christer Sandberg
 */
public class NthParserTest {

    @SuppressWarnings("unchecked")
    @Test
    public void testNthParserSpecCompliance() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        TypeReference<List<Object>> typeRef = new TypeReference<List<Object>>() {};
        List<Object> list = objectMapper.readValue(getClass().getResource("/An+B.json"), typeRef);

        String args = null;
        Tokenizer tokenizer = null;

        int n = 0;
        for (Object obj : list) {
            if (n % 2 == 0) {
                args = (String) obj;
                tokenizer = new Tokenizer(args + ")");
            } else {
                if (obj == null) {
                    try {
                        NthParser.parse(tokenizer);
                        fail("Expected error for nth argument " + args + " at index " + n);
                    } catch (ParserException e) {
                        // Expected
                    }
                } else {
                    List<Integer> expected = (List<Integer>) obj;
                    Pair<Integer, Integer> nth = NthParser.parse(tokenizer);
                    assertEquals("Value mismatch for A: " + args, expected.get(0), nth.first);
                    assertEquals("Value mismatch for B: " + args, expected.get(1), nth.second);
                }
            }

            ++n;
        }
    }

}
