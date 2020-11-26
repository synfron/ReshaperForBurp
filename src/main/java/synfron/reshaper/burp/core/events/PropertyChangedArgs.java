package synfron.reshaper.burp.core.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PropertyChangedArgs {
    private final Object source;
    private final String name;
    private final Object value;
}
