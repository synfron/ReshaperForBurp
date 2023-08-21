package synfron.reshaper.burp.core.utils;

import lombok.Getter;

import java.io.Serializable;

public class CaseInsensitiveString implements Serializable {
    @Getter
    private final String value;
    @Getter
    private final String compareValue;

    public CaseInsensitiveString(String value) {
        this.value = value;
        this.compareValue = value.toLowerCase();
    }

    @Override
    public int hashCode() {
        return compareValue.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CaseInsensitiveString ? equals((CaseInsensitiveString)obj) : value.equals(obj);
    }

    public boolean equals(CaseInsensitiveString str) {
        return compareValue.equals(str.getCompareValue());
    }

    @Override
    public String toString() {
        return value;
    }

}
