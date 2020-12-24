package synfron.reshaper.burp.core.utils;

import java.util.Objects;

public class Value<T> implements IValue<T> {

    private final T value;

    public Value(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
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
        return TextUtils.toString(getValue());
    }
}
