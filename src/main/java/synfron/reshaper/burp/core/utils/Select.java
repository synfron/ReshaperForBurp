package synfron.reshaper.burp.core.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

public class Select<T> {

    @Getter
    private final List<T> options;
    @Getter @Setter
    private T selectedOption;

    public Select(List<T> options, T selectedOption) {
        this.options = Collections.unmodifiableList(options);
        this.selectedOption = selectedOption != null ? selectedOption : options.stream().findFirst().orElse(null);
    }

}
