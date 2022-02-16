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
package javasplit.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Stamp {
    private static String _fmt_date = "dd.MM.yyyy";
    private static String _fmt_time = "HH:mm:ss";
    private static String _fmt = String.format("%s %s", _fmt_date, _fmt_time);
    private static DateTimeFormatter fmt_date = DateTimeFormatter.ofPattern(_fmt_date);
    private static DateTimeFormatter fmt = DateTimeFormatter.ofPattern(_fmt);
    private LocalDateTime time;

    public Stamp() {
        this.setTime(java.time.LocalDateTime.now());
    }

    public Stamp(LocalDateTime time) {
        this.setTime(time);
    }

    public Stamp(String time_string) {
        this.setTime(time_string);
    }

    private static LocalDateTime fromString(String time_string) {
        LocalDateTime time;
        try {
            time = LocalDateTime.parse(time_string, fmt);
        } catch (DateTimeParseException e) {
            LocalDate tmp = LocalDate.parse(time_string, fmt_date);
            time = tmp.atStartOfDay();
        }

        return time;
    }

    public LocalDateTime getTime() {
        return this.time;
    }

    public void setTime(String time_string) {
        this.time = fromString(time_string);
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        LocalDate tmp = this.time.toLocalDate();
        if (this.time.isEqual(tmp.atStartOfDay())) {
            return tmp.format(Stamp.fmt_date);
        } else {
            return this.time.format(Stamp.fmt);
        }
    }
}