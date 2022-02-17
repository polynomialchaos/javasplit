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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

import javasplit.utils.Stamp;

public class TestUtils {
    @Test
    void testStamp() {
        Stamp stamp = new Stamp();

        LocalDateTime tmp = LocalDateTime.now();
        stamp.setTime(tmp);

        String date_string = "23.02.2022";
        stamp.setTime(date_string);
        assertTrue(date_string.equals(stamp.toString()));

        date_string = "24.02.2022 00:30:00";
        stamp.setTime(date_string);
        assertTrue(date_string.equals(stamp.toString()));

        try {
            date_string = "abcd";
            stamp.setTime(date_string);
        } catch (DateTimeParseException e) {
            System.out.println(String.format("Catched: %s!", e.getClass().getName()));
        }
    }
}