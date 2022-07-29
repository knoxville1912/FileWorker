package program;

import java.util.List;

public interface WriteMapper<T> {
    String[] map(T t);
}
