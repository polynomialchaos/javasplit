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
package jvsplit.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Base {
    private Stamp stamp;

    public Base() {
        stamp = new Stamp();
    }

    public static <T, R> List<R> apply(Collection<T> coll, Function<? super T, ? extends R> mapper) {
        return coll.stream().map(mapper).collect(Collectors.toList());
    }

    public static List<String> as_list(String element) {
        ArrayList<String> recipients = new ArrayList<String>();
        recipients.add(element);
        return recipients;
    }

    public void setTime(String time_string) {
        this.stamp.setTime(time_string);
    }

    public void setTime(LocalDateTime time) {
        this.stamp.setTime(time);
    }

    public LinkedHashMap<String, Object> serialize() {
        throw new UnsupportedOperationException();
    }

    public LinkedHashMap<String, Object> toDict() {
        LinkedHashMap<String, Object> tmp = serialize();
        tmp.put("stamp", this.stamp.toString());
        return tmp;
    }
}