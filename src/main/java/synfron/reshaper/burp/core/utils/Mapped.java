package synfron.reshaper.burp.core.utils;

import java.util.Objects;
import java.util.function.Supplier;

public class Mapped<T> implements IValue<T> {

    private final Supplier<T> supplier;

    public Mapped(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T getValue() {
        return supplier.get();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getValue());
    }

    @Override
    public boolean equals(Object obj) {
        return Objects.equals(getValue(), obj);
    }

    @Override
    public String toString() {
       return Objects.toString(getValue());
    }
}
