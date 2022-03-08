// MIT License
//
// Copyright (c) 2022 Florian Eigentler
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
package javasplit.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * TimeStamp class for storing time information.
 */
public class TimeStamp {
    private static String _fmt_date = "dd.MM.yyyy";
    private static String _fmt_time = "HH:mm:ss";
    private static String _fmt = String.format("%s %s", _fmt_date, _fmt_time);
    private static DateTimeFormatter fmt_date = DateTimeFormatter.ofPattern(_fmt_date);
    private static DateTimeFormatter fmt = DateTimeFormatter.ofPattern(_fmt);
    private LocalDateTime time;

    /**
     * Initialize a TimeStamp object.</summary>
     *
     */
    public TimeStamp() {
        setTime(java.time.LocalDateTime.now());
    }

    /**
     * Initialize a TimeStamp object from DateTime object.
     */
    public TimeStamp(LocalDateTime time) {
        setTime(time);
    }

    /**
     * Initialize a TimeStamp object from time string.
     */
    public TimeStamp(String time_string) {
        setTime(time_string);
    }

    /**
     * Gets the DateTime object.
     *
     * @return A DateTime object.
     */
    public LocalDateTime getTime() {
        return time;
    }

    /**
     * Sets the time from a string.
     */
    public void setTime(String time_string) {
        try {
            time = LocalDateTime.parse(time_string, fmt);
        } catch (DateTimeParseException e) {
            LocalDate tmp = LocalDate.parse(time_string, fmt_date);
            time = tmp.atStartOfDay();
        }
    }

    /**
     * Sets the time from a DateTime object.
     */
    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    /**
     * Converts to an equivalent string.
     *
     * @return A string.
     */
    @Override
    public String toString() {
        LocalDate tmp = time.toLocalDate();
        if (time.isEqual(tmp.atStartOfDay())) {
            return tmp.format(TimeStamp.fmt_date);
        } else {
            return time.format(TimeStamp.fmt);
        }
    }
}