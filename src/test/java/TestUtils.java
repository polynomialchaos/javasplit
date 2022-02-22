// MIT License
//
// Copyright (c) 2021 Florian
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javasplit.utils.Base;
import javasplit.utils.Currency;
import javasplit.utils.InputScanner;
import javasplit.utils.Stamp;
import javasplit.utils.Utils;

public class TestUtils {
    private static String date_string = "23.02.2022";
    private static String date_time_string = String.format("%s 00:30:00", date_string);

    @Test
    void testBase() {
        // Test: construction
        Base base = new Base();

        // Test: set the time
        base.setTime(LocalDateTime.now());
        base.setTime(date_time_string);
        base.setTime(date_string);

        // Test: toDict()
        LinkedHashMap<String, Object> tmp = base.toDict();
        assertTrue(tmp.containsKey("stamp"));

        // Test: toString()
        System.out.println(base.toString());
    }

    @Test
    void testCurrency() {
        // Test: iteration, name and valueOf comparison
        for (Currency currency : Currency.values()) {
            System.out.println(currency.name());
            Currency.valueOf(currency.name());
        }
    }

    @Test
    void testInputScanner() {
        String test_input = "4\n\n\n";
        InputStream test_stream = new ByteArrayInputStream(test_input.getBytes());

        // Test: overloaded construction with stream
        InputScanner scanner = new InputScanner(test_stream);

        // Test: parse with given value an default value
        Double result = scanner.get("Test description", "2",
                Utils.atLeast1D("2"), a -> Double.parseDouble(a));
        System.out.println(result);

        // Test: parse without given value an default value
        Double result_2 = scanner.get("Test description", "2",
                a -> Double.parseDouble(a));
        System.out.println(result_2);

        // Test: parse without given an default value
        try {
            scanner.get("Test description", a -> Double.parseDouble(a));
        } catch (RuntimeException e) {
            System.out.println(String.format("Catched: %s!", e.getClass().getName()));
        }

        scanner.close();
    }

    @Test
    void testStamp() {
        // Test: overload construction
        LocalDateTime now = LocalDateTime.now();

        Stamp stamp_1 = new Stamp();
        Stamp stamp_2 = new Stamp(now);
        Stamp stamp_3 = new Stamp(date_time_string);

        // Test: set short date string
        System.out.println(stamp_1);
        stamp_1.setTime(date_string);
        System.out.println(stamp_1);

        // Test: get time object
        LocalDateTime time = stamp_2.getTime();
        assertTrue(time.equals(now));

        // Test: toString()
        assertTrue(date_string.equals(stamp_1.toString()));
        assertTrue(date_time_string.equals(stamp_3.toString()));

        // Test: raise DateTimeParseException
        try {
            String false_date_string = "01.02.22";
            stamp_2.setTime(false_date_string);
            throw new AssertionError("Exception not captured!");
        } catch (DateTimeParseException e) {
            System.out.println(String.format("Catched: %s!", e.getClass().getName()));
        }
    }

    @Test
    void testUtils() {
        // Test: atLeast1D
        Double value = 2.0;
        List<Double> tmp = Utils.atLeast1D(value);
        tmp = Utils.atLeast1D(tmp);

        // Test: convertAll
        List<Double> tmp_2 = Utils.convertAll(tmp, a -> a + value);
        System.out.println(tmp_2);
        assertTrue(tmp_2.get(0) == value + value);
    }
}