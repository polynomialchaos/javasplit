package javasplit.utils;

import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class InputScanner {
    Scanner scanner;

    public InputScanner() {
        scanner = new Scanner(System.in);
    }

    public void finish() {
        scanner.close();
    }

    public <T> T get(String description, Function<? super String, ? extends T> functor) {
        return get(description, null, null, functor);
    }

    public <T> T get(String description, String default_value,
            Function<? super String, ? extends T> functor) {
        return get(description, default_value, null, functor);
    }

    public <T> T get(String description, String default_value,
            List<String> options, Function<? super String, ? extends T> functor) {

        String des_str = description;
        if (default_value != null) {
            des_str = String.format("%s [%s]", des_str, default_value);
        }

        if (options != null) {
            des_str = String.format("%s (%s)", des_str, String.join(",", Base.forEach_r(options, a -> a)));
        }

        System.out.print(String.format("%s: ", des_str));
        String user_input = scanner.nextLine(); // Read user input

        if (user_input.isBlank() && default_value == null) {
            throw new RuntimeException("Required input not provided!");
        }

        return functor.apply(user_input.isBlank() ? default_value : user_input);
    }
}
