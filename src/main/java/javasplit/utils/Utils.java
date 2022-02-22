package javasplit.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Utils {
    private Utils() {
    };

    public static <T> List<T> atLeast1D(T element) {
        ArrayList<T> recipients = new ArrayList<T>();
        recipients.add(element);
        return recipients;
    }

    public static <T> List<T> atLeast1D(List<T> element) {
        return element;
    }

    public static <T, R> List<R> convertAll(Collection<T> collection,
            Function<? super T, ? extends R> functor) {
        return collection.stream().map(functor).collect(Collectors.toList());
    }
}
