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

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

/**
 * InputScanner class to retrieve user input.
 */
public final class InputScanner {
    private Scanner scanner;

    /**
     * Initialize a Base object.
     */
    public InputScanner() {
        scanner = new Scanner(System.in);
    }

    /**
     * Initialize a Base object with a given input stream.
     */
    public InputScanner(InputStream input) {
        scanner = new Scanner(input);
    }

    /**
     * Close the Inputscanner object.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Gets the user input for a given description.
     *
     * @return The user input converted by the functor of type T.
     */
    public <T> T get(String description, Function<? super String, ? extends T> functor) {
        return get(description, null, null, functor);
    }

    /**
     * Gets the user input for a given description and default value.
     *
     * @return The user input converted by the functor of type T.
     */
    public <T> T get(String description, String default_value,
            Function<? super String, ? extends T> functor) {
        return get(description, default_value, null, functor);
    }

    /**
     * Gets the user input for a given description,
     * default value and possible options.
     *
     * @return The user input converted by the functor of type T.
     */
    public <T> T get(String description, String default_value,
            List<String> options, Function<? super String, ? extends T> functor) {

        String des_str = description;
        if (default_value != null) {
            des_str = String.format("%s [%s]", des_str, default_value);
        }

        if (options != null) {
            des_str = String.format("%s (%s)", des_str, String.join(",", Utils.convertAll(options, a -> a)));
        }

        System.out.print(String.format("%s: ", des_str));
        String user_input = scanner.nextLine(); // Read user input

        if (user_input.isBlank() && default_value == null) {
            throw new RuntimeException("Required input not provided!");
        }

        return functor.apply(user_input.isBlank() ? default_value : user_input);
    }
}
