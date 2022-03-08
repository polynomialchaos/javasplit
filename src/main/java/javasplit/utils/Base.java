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

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

/**
 * Base class defining required package class methods.
 */
public class Base {
    private TimeStamp stamp;

    /**
     * Initialize a Base object.
     */
    public Base() {
        stamp = new TimeStamp();
    }

    /**
     * Serializes the object.
     *
     * @return A LinkedHashMap of type String and Object.
     */
    protected LinkedHashMap<String, Object> serialize() {
        LinkedHashMap<String, Object> hash_map = new LinkedHashMap<String, Object>();
        return hash_map;
    }

    /**
     * Sets the time from a string.
     */
    public void setTime(String time_string) {
        stamp.setTime(time_string);
    }

    /**
     * Sets the time from a DateTime object.
     */
    public void setTime(LocalDateTime time) {
        stamp.setTime(time);
    }

    /**
     * Converts to an equivalent dictionary.
     *
     * @return A LinkedHashMap of type String and Object.
     */
    public final LinkedHashMap<String, Object> toDict() {
        LinkedHashMap<String, Object> tmp = serialize();
        tmp.put("stamp", stamp.toString());
        return tmp;
    }

    /**
     * Converts to an equivalent string.
     *
     * @return A string.
     */
    @Override
    public String toString() {
        return String.format("<%s stamp=%s>", getClass().getName(), stamp.toString());
    }
}